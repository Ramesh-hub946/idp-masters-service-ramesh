package com.yntec.idp.masters.service.service;

import com.yntec.idp.masters.service.payload.request.*;
import com.yntec.idp.masters.service.payload.response.StateResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface StateMasterService {

    StateResponse create(StateCreateRequest request);

    StateResponse update(UUID id, StateUpdateRequest request);

    StateResponse findById(UUID id);

    List<StateResponse> findAll();

    Page<StateResponse> search(String name, UUID countryId, Pageable pageable);

    StateResponse updateStatus(UUID id, Boolean status);

    List<StateResponse> bulkCreate(StateBulkRequest request);

    List<StateResponse> findByCountryId(UUID countryId);
}
