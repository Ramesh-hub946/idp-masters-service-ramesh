package com.yntec.idp.masters.service.mapper;

import com.yntec.idp.masters.service.entity.CountryMaster;
import com.yntec.idp.masters.service.payload.request.CountryCreateRequest;
import com.yntec.idp.masters.service.payload.request.CountryUpdateRequest;
import com.yntec.idp.masters.service.payload.response.CountryResponse;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CountryMasterMapper {

    @Mapping(target = "countryId", ignore = true)
    @Mapping(target = "states", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "modifiedBy", ignore = true)
    @Mapping(target = "modifiedAt", ignore = true)
    @Mapping(target = "status", ignore = true)
    CountryMaster toEntity(CountryCreateRequest request);

    @Mapping(target = "countryId", ignore = true)
    @Mapping(target = "states", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "modifiedBy", ignore = true)
    @Mapping(target = "modifiedAt", ignore = true)
    @Mapping(target = "status", ignore = true)
    void updateEntityFromRequest(CountryUpdateRequest request,
                                 @MappingTarget CountryMaster entity);

    CountryResponse toResponse(CountryMaster entity);

    List<CountryResponse> toResponseList(List<CountryMaster> entities);

    @AfterMapping
    default void normalize(@MappingTarget CountryMaster entity) {
        if (entity.getCountryCode() == null) {
            return;
        }

        String normalized = entity.getCountryCode().trim();
        if (!normalized.startsWith("+")) {
            normalized = normalized.toUpperCase();
        }
        entity.setCountryCode(normalized);
    }
}
