package com.yntec.idp.masters.service.service;

import com.yntec.idp.masters.service.payload.request.*;
import com.yntec.idp.masters.service.payload.response.CityResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface CityMasterService {

    CityResponse create(CityCreateRequest request);

    CityResponse update(UUID id, CityUpdateRequest request);

    CityResponse findById(UUID id);

    List<CityResponse> findAll();

    Page<CityResponse> search(String name, UUID stateId, Pageable pageable);

    CityResponse updateStatus(UUID id, Boolean status);

    List<CityResponse> bulkCreate(CityBulkRequest request);

    List<CityResponse> findByStateId(UUID stateId);
}
