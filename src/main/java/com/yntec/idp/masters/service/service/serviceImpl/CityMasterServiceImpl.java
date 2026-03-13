package com.yntec.idp.masters.service.service.serviceImpl;

import com.yntec.idp.masters.service.entity.CityMaster;
import com.yntec.idp.masters.service.entity.StateMaster;
import com.yntec.idp.masters.service.exception.LocationMasterNotFoundException;
import com.yntec.idp.masters.service.mapper.CityMasterMapper;
import com.yntec.idp.masters.service.payload.request.*;
import com.yntec.idp.masters.service.payload.response.CityResponse;
import com.yntec.idp.masters.service.repository.CityRepository;
import com.yntec.idp.masters.service.repository.StateRepository;
import com.yntec.idp.masters.service.service.CityMasterService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * City master business implementation.
 * Enforces state-scoped uniqueness for city names/codes and supports bulk ingestion safely.
 */
@Service
public class CityMasterServiceImpl implements CityMasterService {

    private final CityRepository cityRepository;
    private final StateRepository stateRepository;
    private final CityMasterMapper cityMapper;

    public CityMasterServiceImpl(
            CityRepository cityRepository,
            StateRepository stateRepository,
            CityMasterMapper cityMapper
    ) {
        this.cityRepository = cityRepository;
        this.stateRepository = stateRepository;
        this.cityMapper = cityMapper;
    }

    /**
     * Creates a city under an existing state.
     */
    @Override
    @Transactional
    public CityResponse create(CityCreateRequest request) {

        StateMaster state = stateRepository.findById(request.getStateId())
                .orElseThrow(() ->
                        new LocationMasterNotFoundException(
                                "State not found with id: " + request.getStateId()));

        request.setCityName(normalizeName(request.getCityName()));
        request.setCityCode(normalizeCode(request.getCityCode()));

        validateCityUniqueness(
                request.getCityName(),
                request.getCityCode(),
                request.getStateId(),
                null
        );

        CityMaster entity = cityMapper.toEntity(request);
        entity.setState(state);

        return cityMapper.toResponse(cityRepository.save(entity));
    }

    /**
     * Read all cities.
     */
    @Override
    @Transactional(readOnly = true)
    public List<CityResponse> findAll() {
        return cityRepository.findAll()
                .stream()
                .map(cityMapper::toResponse)
                .toList();
    }

    /**
     * Filtered search by city name and optional state.
     */
    @Override
    @Transactional(readOnly = true)
    public Page<CityResponse> search(String name, UUID stateId, Pageable pageable) {

        String safeName = name == null ? "" : name;

        if (stateId != null) {
            return cityRepository
                    .findByCityNameContainingIgnoreCaseAndStateStateId(safeName, stateId, pageable)
                    .map(cityMapper::toResponse);
        }

        return cityRepository
                .findByCityNameContainingIgnoreCase(safeName, pageable)
                .map(cityMapper::toResponse);
    }

    /**
     * City detail lookup by UUID.
     */
    @Override
    @Transactional(readOnly = true)
    public CityResponse findById(UUID id) {

        CityMaster city = cityRepository.findById(id)
                .orElseThrow(() ->
                        new LocationMasterNotFoundException("City not found with id: " + id));

        return cityMapper.toResponse(city);
    }

    /**
     * Updates city details with state-scoped duplicate checks.
     */
    @Override
    @Transactional
    public CityResponse update(UUID id, CityUpdateRequest request) {

        CityMaster existing = cityRepository.findById(id)
                .orElseThrow(() ->
                        new LocationMasterNotFoundException("City not found with id: " + id));

        request.setCityName(normalizeName(request.getCityName()));
        request.setCityCode(normalizeCode(request.getCityCode()));

        validateCityUniqueness(
                request.getCityName(),
                request.getCityCode(),
                existing.getState().getStateId(),
                id
        );

        cityMapper.updateEntityFromRequest(request, existing);

        return cityMapper.toResponse(cityRepository.save(existing));
    }

    /**
     * Status toggle for city records.
     */
    @Override
    @Transactional
    public CityResponse updateStatus(UUID id, Boolean status) {

        CityMaster existing = cityRepository.findById(id)
                .orElseThrow(() ->
                        new LocationMasterNotFoundException("City not found with id: " + id));

        existing.setStatus(status);

        return cityMapper.toResponse(cityRepository.save(existing));
    }

    /**
     * Bulk city create with in-request and database duplicate guards.
     */
    @Override
    @Transactional
    public List<CityResponse> bulkCreate(CityBulkRequest request) {

        List<CityCreateRequest> cityRequests = request.getCities();

        Set<UUID> stateIds = cityRequests.stream()
                .map(CityCreateRequest::getStateId)
                .collect(Collectors.toSet());

        Map<UUID, StateMaster> stateMap = stateRepository.findAllById(stateIds)
                .stream()
                .collect(Collectors.toMap(StateMaster::getStateId, s -> s));

        Map<UUID, Set<String>> namesByState = new HashMap<>();
        Map<UUID, Set<String>> codesByState = new HashMap<>();

        List<CityMaster> entities = new ArrayList<>();

        for (CityCreateRequest cityRequest : cityRequests) {

            UUID stateId = cityRequest.getStateId();
            StateMaster state = stateMap.get(stateId);

            if (state == null) {
                throw new LocationMasterNotFoundException(
                        "State not found with id: " + stateId);
            }

            cityRequest.setCityName(normalizeName(cityRequest.getCityName()));
            cityRequest.setCityCode(normalizeCode(cityRequest.getCityCode()));

            String nameKey = cityRequest.getCityName().toLowerCase();
            namesByState.computeIfAbsent(stateId, k -> new HashSet<>());
            if (!namesByState.get(stateId).add(nameKey)) {
                throw new IllegalArgumentException("Duplicate city name in request for state " + stateId + ": " + cityRequest.getCityName());
            }

            if (cityRequest.getCityCode() != null) {
                String codeKey = cityRequest.getCityCode().toLowerCase();
                codesByState.computeIfAbsent(stateId, k -> new HashSet<>());
                if (!codesByState.get(stateId).add(codeKey)) {
                    throw new IllegalArgumentException("Duplicate city code in request for state " + stateId + ": " + cityRequest.getCityCode());
                }
            }

            validateCityUniqueness(
                    cityRequest.getCityName(),
                    cityRequest.getCityCode(),
                    stateId,
                    null
            );

            CityMaster entity = cityMapper.toEntity(cityRequest);
            entity.setState(state);

            entities.add(entity);
        }

        List<CityMaster> saved = cityRepository.saveAll(entities);

        return cityMapper.toResponseList(saved);
    }

    /**
     * Returns all cities under the requested state.
     */
    @Override
    @Transactional(readOnly = true)
    public List<CityResponse> findByStateId(UUID stateId) {

        if (!stateRepository.existsById(stateId)) {
            throw new LocationMasterNotFoundException(
                    "State not found with id: " + stateId);
        }

        List<CityMaster> cities =
                cityRepository.findByStateStateId(stateId);

        return cityMapper.toResponseList(cities);
    }

    /**
     * Shared uniqueness guard used by create/update/bulk flows.
     */
    private void validateCityUniqueness(
            String name,
            String code,
            UUID stateId,
            UUID currentId
    ) {

        Optional<CityMaster> existingByName =
                cityRepository.findByCityNameIgnoreCaseAndStateStateId(name, stateId);

        if (existingByName.isPresent()) {
            CityMaster city = existingByName.get();
            if (currentId == null || !city.getCityId().equals(currentId)) {
                throw new IllegalArgumentException(
                        "City name already exists in this state");
            }
        }

        if (code != null) {
            Optional<CityMaster> existingByCode =
                    cityRepository.findByCityCodeIgnoreCaseAndStateStateId(code, stateId);

            if (existingByCode.isPresent()) {
                CityMaster city = existingByCode.get();
                if (currentId == null || !city.getCityId().equals(currentId)) {
                    throw new IllegalArgumentException(
                            "City code already exists in this state");
                }
            }
        }
    }

    /**
     * Trims incoming city names.
     */
    private String normalizeName(String value) {
        return value == null ? null : value.trim();
    }

    /**
     * Upper-cases city code and converts blank values to null.
     */
    private String normalizeCode(String value) {
        if (value == null) {
            return null;
        }

        String normalized = value.trim().toUpperCase();
        return normalized.isEmpty() ? null : normalized;
    }
}
