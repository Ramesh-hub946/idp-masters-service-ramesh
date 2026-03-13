package com.yntec.idp.masters.service.service.serviceImpl;

import com.yntec.idp.masters.service.entity.CountryMaster;
import com.yntec.idp.masters.service.entity.StateMaster;
import com.yntec.idp.masters.service.exception.LocationMasterNotFoundException;
import com.yntec.idp.masters.service.mapper.StateMasterMapper;
import com.yntec.idp.masters.service.payload.request.StateBulkRequest;
import com.yntec.idp.masters.service.payload.request.StateCreateRequest;
import com.yntec.idp.masters.service.payload.request.StateUpdateRequest;
import com.yntec.idp.masters.service.payload.response.StateResponse;
import com.yntec.idp.masters.service.repository.CountryRepository;
import com.yntec.idp.masters.service.repository.StateRepository;
import com.yntec.idp.masters.service.service.StateMasterService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * State master business implementation.
 * Maintains country-scoped uniqueness and bulk integrity checks for state data.
 */
@Service
public class StateMasterServiceImpl implements StateMasterService {

    private final StateRepository stateRepository;
    private final CountryRepository countryRepository;
    private final StateMasterMapper stateMapper;

    public StateMasterServiceImpl(
            StateRepository stateRepository,
            CountryRepository countryRepository,
            StateMasterMapper stateMapper) {
        this.stateRepository = stateRepository;
        this.countryRepository = countryRepository;
        this.stateMapper = stateMapper;
    }

    /**
     * Creates a state under an existing country.
     */
    @Override
    @Transactional
    public StateResponse create(StateCreateRequest request) {

        CountryMaster country = countryRepository.findById(request.getCountryId())
                .orElseThrow(() -> new LocationMasterNotFoundException(
                        "Country not found with id: " + request.getCountryId()));

        request.setStateName(normalizeName(request.getStateName()));
        request.setStateCode(normalizeCode(request.getStateCode()));

        validateStateUniqueness(
                request.getStateName(),
                request.getStateCode(),
                request.getCountryId(),
                null);

        StateMaster entity = stateMapper.toEntity(request);
        entity.setCountry(country);

        return stateMapper.toResponse(stateRepository.save(entity));
    }

    /**
     * Read all states.
     */
    @Override
    @Transactional(readOnly = true)
    public List<StateResponse> findAll() {
        return stateRepository.findAll()
                .stream()
                .map(stateMapper::toResponse)
                .toList();
    }

    /**
     * Filtered search by state name and optional country.
     */
    @Override
    @Transactional(readOnly = true)
    public Page<StateResponse> search(String name, UUID countryId, Pageable pageable) {

        String safeName = name == null ? "" : name;

        if (countryId != null) {
            return stateRepository
                    .findByStateNameContainingIgnoreCaseAndCountryCountryId(safeName, countryId, pageable)
                    .map(stateMapper::toResponse);
        }

        return stateRepository
                .findByStateNameContainingIgnoreCase(safeName, pageable)
                .map(stateMapper::toResponse);
    }

    /**
     * State detail lookup by UUID.
     */
    @Override
    @Transactional(readOnly = true)
    public StateResponse findById(UUID id) {

        StateMaster state = stateRepository.findById(id)
                .orElseThrow(() -> new LocationMasterNotFoundException("State not found with id: " + id));

        return stateMapper.toResponse(state);
    }

    /**
     * Updates mutable state fields.
     */
    @Override
    @Transactional
    public StateResponse update(UUID id, StateUpdateRequest request) {

        StateMaster existing = stateRepository.findById(id)
                .orElseThrow(() -> new LocationMasterNotFoundException("State not found with id: " + id));

        request.setStateName(normalizeName(request.getStateName()));
        request.setStateCode(normalizeCode(request.getStateCode()));

        validateStateUniqueness(
                request.getStateName(),
                request.getStateCode(),
                existing.getCountry().getCountryId(),
                id);

        stateMapper.updateEntityFromRequest(request, existing);

        return stateMapper.toResponse(stateRepository.save(existing));
    }

    /**
     * Status toggle for state master entries.
     */
    @Override
    @Transactional
    public StateResponse updateStatus(UUID id, Boolean status) {

        StateMaster existing = stateRepository.findById(id)
                .orElseThrow(() -> new LocationMasterNotFoundException("State not found with id: " + id));

        existing.setStatus(status);

        return stateMapper.toResponse(stateRepository.save(existing));
    }

    /**
     * Bulk state create with duplicate prevention and parent-country existence
     * checks.
     */
    @Override
    @Transactional
    public List<StateResponse> bulkCreate(StateBulkRequest request) {

        List<StateCreateRequest> stateRequests = request.getStates();

        Set<UUID> countryIds = stateRequests.stream()
                .map(StateCreateRequest::getCountryId)
                .collect(Collectors.toSet());

        Map<UUID, CountryMaster> countryMap = countryRepository.findAllById(countryIds)
                .stream()
                .collect(Collectors.toMap(CountryMaster::getCountryId, c -> c));

        Map<UUID, Set<String>> namesByCountry = new HashMap<>();
        Map<UUID, Set<String>> codesByCountry = new HashMap<>();

        List<StateMaster> entities = new ArrayList<>();

        for (StateCreateRequest stateRequest : stateRequests) {

            UUID countryId = stateRequest.getCountryId();
            CountryMaster country = countryMap.get(countryId);

            if (country == null) {
                throw new LocationMasterNotFoundException(
                        "Country not found with id: " + countryId);
            }

            stateRequest.setStateName(normalizeName(stateRequest.getStateName()));
            stateRequest.setStateCode(normalizeCode(stateRequest.getStateCode()));

            String nameKey = stateRequest.getStateName().toLowerCase();
            namesByCountry.computeIfAbsent(countryId, k -> new HashSet<>());
            if (!namesByCountry.get(countryId).add(nameKey)) {
                throw new IllegalArgumentException("Duplicate state name in request for country " + countryId + ": "
                        + stateRequest.getStateName());
            }

            if (stateRequest.getStateCode() != null) {
                String codeKey = stateRequest.getStateCode().toLowerCase();
                codesByCountry.computeIfAbsent(countryId, k -> new HashSet<>());
                if (!codesByCountry.get(countryId).add(codeKey)) {
                    throw new IllegalArgumentException("Duplicate state code in request for country " + countryId + ": "
                            + stateRequest.getStateCode());
                }
            }

            validateStateUniqueness(
                    stateRequest.getStateName(),
                    stateRequest.getStateCode(),
                    countryId,
                    null);

            StateMaster entity = stateMapper.toEntity(stateRequest);
            entity.setCountry(country);

            entities.add(entity);
        }

        List<StateMaster> saved = stateRepository.saveAll(entities);

        return stateMapper.toResponseList(saved);
    }

    /**
     * Returns all states under the requested country.
     */
    @Override
    @Transactional(readOnly = true)
    public List<StateResponse> findByCountryId(UUID countryId) {

        if (!countryRepository.existsById(countryId)) {
            throw new LocationMasterNotFoundException(
                    "Country not found with id: " + countryId);
        }

        List<StateMaster> states = stateRepository.findByCountryCountryIdOrderByStateNameAsc(countryId);

        return stateMapper.toResponseList(states);
    }

    /**
     * Shared uniqueness guard used by create/update/bulk flows.
     */
    private void validateStateUniqueness(
            String name,
            String code,
            UUID countryId,
            UUID currentId) {

        Optional<StateMaster> existingByName = stateRepository.findByStateNameIgnoreCaseAndCountryCountryId(name,
                countryId);

        if (existingByName.isPresent()) {
            StateMaster state = existingByName.get();
            if (currentId == null || !state.getStateId().equals(currentId)) {
                throw new IllegalArgumentException(
                        "State name already exists in this country");
            }
        }

        if (code != null) {
            Optional<StateMaster> existingByCode = stateRepository.findByStateCodeIgnoreCaseAndCountryCountryId(code,
                    countryId);

            if (existingByCode.isPresent()) {
                StateMaster state = existingByCode.get();
                if (currentId == null || !state.getStateId().equals(currentId)) {
                    throw new IllegalArgumentException(
                            "State code already exists in this country");
                }
            }
        }
    }

    /**
     * Trims incoming state names.
     */
    private String normalizeName(String value) {
        return value == null ? null : value.trim();
    }

    /**
     * Upper-cases state code and converts blank values to null.
     */
    private String normalizeCode(String value) {
        if (value == null) {
            return null;
        }

        String normalized = value.trim().toUpperCase();
        return normalized.isEmpty() ? null : normalized;
    }
}
