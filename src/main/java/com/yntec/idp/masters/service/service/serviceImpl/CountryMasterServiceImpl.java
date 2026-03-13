package com.yntec.idp.masters.service.service.serviceImpl;

import com.yntec.idp.masters.service.entity.CityMaster;
import com.yntec.idp.masters.service.entity.CountryMaster;
import com.yntec.idp.masters.service.entity.StateMaster;
import com.yntec.idp.masters.service.exception.LocationMasterNotFoundException;
import com.yntec.idp.masters.service.mapper.CityMasterMapper;
import com.yntec.idp.masters.service.mapper.CountryMasterMapper;
import com.yntec.idp.masters.service.mapper.StateMasterMapper;
import com.yntec.idp.masters.service.payload.request.CountryBulkRequest;
import com.yntec.idp.masters.service.payload.request.CountryCreateRequest;
import com.yntec.idp.masters.service.payload.request.CountryUpdateRequest;
import com.yntec.idp.masters.service.payload.response.*;
import com.yntec.idp.masters.service.repository.CityRepository;
import com.yntec.idp.masters.service.repository.CountryRepository;
import com.yntec.idp.masters.service.repository.StateRepository;
import com.yntec.idp.masters.service.service.CountryMasterService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Country master business implementation.
 * Handles normalization, uniqueness validation, and country-centric location
 * read models.
 */
@Service
public class CountryMasterServiceImpl implements CountryMasterService {

    private final CountryRepository countryRepository;
    private final StateRepository stateRepository;
    private final CountryMasterMapper countryMapper;
    private final StateMasterMapper stateMapper;
    private final CityRepository cityRepository;
    private final CityMasterMapper cityMapper;

    public CountryMasterServiceImpl(
            CountryRepository countryRepository,
            StateRepository stateRepository,
            CountryMasterMapper countryMapper,
            StateMasterMapper stateMapper,
            CityRepository cityRepository,
            CityMasterMapper cityMapper) {
        this.countryRepository = countryRepository;
        this.stateRepository = stateRepository;
        this.countryMapper = countryMapper;
        this.stateMapper = stateMapper;
        this.cityRepository = cityRepository;
        this.cityMapper = cityMapper;
    }

    /**
     * Creates a country after canonical normalization and uniqueness checks.
     */
    @Override
    @Transactional
    public CountryResponse create(CountryCreateRequest request) {

        request.setCountryName(normalizeName(request.getCountryName()));
        request.setCountryCode(normalizeCountryCode(request.getCountryCode()));

        validateCountryUniqueness(request.getCountryName(), request.getCountryCode(), null);

        CountryMaster entity = countryMapper.toEntity(request);
        CountryMaster saved = countryRepository.save(entity);

        return countryMapper.toResponse(saved);
    }

    /**
     * Read all country records.
     */
    @Override
    @Transactional(readOnly = true)
    public List<CountryResponse> findAll() {

        return countryRepository.findAll()
                .stream()
                .map(countryMapper::toResponse)
                .toList();
    }

    /**
     * Filtered search by country name and optional status.
     */
    @Override
    @Transactional(readOnly = true)
    public Page<CountryResponse> search(String name, Boolean status, Pageable pageable) {

        String safeName = name == null ? "" : name;

        if (status != null) {
            return countryRepository
                    .findByCountryNameContainingIgnoreCaseAndStatus(safeName, status, pageable)
                    .map(countryMapper::toResponse);
        }

        return countryRepository
                .findByCountryNameContainingIgnoreCase(safeName, pageable)
                .map(countryMapper::toResponse);
    }

    /**
     * Country detail lookup by UUID.
     */
    @Override
    @Transactional(readOnly = true)
    public CountryResponse findById(UUID id) {

        CountryMaster entity = countryRepository.findById(id)
                .orElseThrow(() -> new LocationMasterNotFoundException("Country not found with id: " + id));

        return countryMapper.toResponse(entity);
    }

    /**
     * Updates country fields with duplicate-safe validation.
     */
    @Override
    @Transactional
    public CountryResponse update(UUID id, CountryUpdateRequest request) {

        CountryMaster existing = countryRepository.findById(id)
                .orElseThrow(() -> new LocationMasterNotFoundException("Country not found with id: " + id));

        request.setCountryName(normalizeName(request.getCountryName()));
        request.setCountryCode(normalizeCountryCode(request.getCountryCode()));

        validateCountryUniqueness(request.getCountryName(), request.getCountryCode(), id);

        countryMapper.updateEntityFromRequest(request, existing);

        CountryMaster saved = countryRepository.save(existing);

        return countryMapper.toResponse(saved);
    }

    /**
     * Soft status toggle endpoint support.
     */
    @Override
    @Transactional
    public CountryResponse updateStatus(UUID id, Boolean status) {

        CountryMaster existing = countryRepository.findById(id)
                .orElseThrow(() -> new LocationMasterNotFoundException("Country not found with id: " + id));

        existing.setStatus(status);

        return countryMapper.toResponse(countryRepository.save(existing));
    }

    /**
     * Bulk country create with in-request duplicate guards.
     */
    @Override
    @Transactional
    public List<CountryResponse> bulkCreate(CountryBulkRequest request) {

        Set<String> nameSet = new HashSet<>();
        Set<String> codeSet = new HashSet<>();

        List<CountryMaster> entities = request.getCountries()
                .stream()
                .map(countryRequest -> {
                    countryRequest.setCountryName(normalizeName(countryRequest.getCountryName()));
                    countryRequest.setCountryCode(normalizeCountryCode(countryRequest.getCountryCode()));

                    String nameKey = countryRequest.getCountryName().toLowerCase();
                    if (!nameSet.add(nameKey)) {
                        throw new IllegalArgumentException(
                                "Duplicate country name in request: " + countryRequest.getCountryName());
                    }

                    String codeKey = countryRequest.getCountryCode().toLowerCase();
                    if (!codeSet.add(codeKey)) {
                        throw new IllegalArgumentException(
                                "Duplicate country code in request: " + countryRequest.getCountryCode());
                    }

                    validateCountryUniqueness(countryRequest.getCountryName(), countryRequest.getCountryCode(), null);
                    return countryMapper.toEntity(countryRequest);
                })
                .toList();

        List<CountryMaster> saved = countryRepository.saveAll(entities);

        return countryMapper.toResponseList(saved);
    }

    /**
     * Shared uniqueness rule used by create/update/bulk flows.
     */
    private void validateCountryUniqueness(String name, String code, UUID currentId) {

        countryRepository.findByCountryNameIgnoreCase(name)
                .ifPresent(existing -> {
                    if (currentId == null || !existing.getCountryId().equals(currentId)) {
                        throw new IllegalArgumentException("Country name already exists");
                    }
                });

        countryRepository.findByCountryCodeIgnoreCase(code)
                .ifPresent(existing -> {
                    if (currentId == null || !existing.getCountryId().equals(currentId)) {
                        throw new IllegalArgumentException("Country code already exists");
                    }
                });
    }

    /**
     * Trims user input to a canonical name form.
     */
    private String normalizeName(String value) {
        return value == null ? null : value.trim();
    }

    /**
     * Upper-cases ISO code values and preserves dialing-code format (e.g., +91).
     */
    private String normalizeCountryCode(String value) {
        if (value == null) {
            return null;
        }

        String normalized = value.trim();
        if (!normalized.startsWith("+")) {
            normalized = normalized.toUpperCase();
        }
        return normalized;
    }

    /**
     * Country -> state -> city tree payload for dependent UI trees.
     */
    @Override
    @Transactional(readOnly = true)
    public List<LocationTreeCountryResponse> getLocationTree() {

        List<CountryMaster> countries = countryRepository.findAll();

        return countries.stream().map(country -> {

            List<LocationTreeStateResponse> stateResponses = country.getStates().stream().map(state -> {

                List<LocationTreeCityResponse> cityResponses = state.getCities().stream()
                        .map(city -> new LocationTreeCityResponse(
                                city.getCityId(),
                                city.getCityName(),
                                city.getCityCode(),
                                city.getStatus()))
                        .toList();

                return new LocationTreeStateResponse(
                        state.getStateId(),
                        state.getStateName(),
                        state.getStatus(),
                        cityResponses);
            }).toList();

            return new LocationTreeCountryResponse(
                    country.getCountryId(),
                    country.getCountryName(),
                    country.getCountryCode(),
                    country.getStatus(),
                    stateResponses);
        }).toList();
    }

    /**
     * Lightweight dropdown payload for country/state/city selectors.
     */
    @Override
    @Transactional(readOnly = true)
    public List<DropdownCountryResponse> getDropdownData() {

        List<CountryMaster> countries = countryRepository.findAll();

        return countries.stream().map(country -> {

            List<DropdownStateResponse> stateOptions = country.getStates().stream().map(state -> {

                List<DropdownCityResponse> cityOptions = state.getCities().stream()
                        .map(city -> new DropdownCityResponse(
                                city.getCityId(),
                                city.getCityName()))
                        .toList();

                return new DropdownStateResponse(
                        state.getStateId(),
                        state.getStateName(),
                        cityOptions);
            }).toList();

            return new DropdownCountryResponse(
                    country.getCountryId(),
                    country.getCountryName(),
                    stateOptions);
        }).toList();
    }

    /**
     * Returns a single city enriched with parent state/country hierarchy.
     */
    @Override
    @Transactional(readOnly = true)
    public CityHierarchyResponse getCityWithHierarchy(UUID cityId) {

        CityMaster city = cityRepository.findById(cityId)
                .orElseThrow(() -> new LocationMasterNotFoundException(
                        "City not found with id: " + cityId));

        StateMaster state = city.getState();
        CountryMaster country = state.getCountry();

        return new CityHierarchyResponse(
                city.getCityId(),
                city.getCityName(),
                city.getCityCode(),
                city.getStatus(),
                state.getStateId(),
                state.getStateName(),
                country.getCountryId(),
                country.getCountryName(),
                country.getCountryCode());
    }
}
