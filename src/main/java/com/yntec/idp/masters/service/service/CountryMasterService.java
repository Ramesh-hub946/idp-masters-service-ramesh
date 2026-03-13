package com.yntec.idp.masters.service.service;

import com.yntec.idp.masters.service.payload.request.*;
import com.yntec.idp.masters.service.payload.response.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface CountryMasterService {

    CountryResponse create(CountryCreateRequest request);

    CountryResponse update(UUID id, CountryUpdateRequest request);

    CountryResponse findById(UUID id);

    List<CountryResponse> findAll();

    Page<CountryResponse> search(String name, Boolean status, Pageable pageable);

    CountryResponse updateStatus(UUID id, Boolean status);

    List<CountryResponse> bulkCreate(CountryBulkRequest request);

    List<LocationTreeCountryResponse> getLocationTree();

    List<DropdownCountryResponse> getDropdownData();

    CityHierarchyResponse getCityWithHierarchy(UUID cityId);

}
