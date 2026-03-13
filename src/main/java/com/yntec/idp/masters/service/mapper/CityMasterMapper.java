package com.yntec.idp.masters.service.mapper;

import com.yntec.idp.masters.service.entity.CityMaster;
import com.yntec.idp.masters.service.payload.request.CityCreateRequest;
import com.yntec.idp.masters.service.payload.request.CityUpdateRequest;
import com.yntec.idp.masters.service.payload.response.CityResponse;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)public interface CityMasterMapper {

    @Mapping(target = "cityId", ignore = true)
    @Mapping(target = "state", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "modifiedBy", ignore = true)
    @Mapping(target = "modifiedAt", ignore = true)
    @Mapping(target = "status", ignore = true)
    CityMaster toEntity(CityCreateRequest request);

    @Mapping(target = "cityId", ignore = true)
    @Mapping(target = "state", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "modifiedBy", ignore = true)
    @Mapping(target = "modifiedAt", ignore = true)
    @Mapping(target = "status", ignore = true)
    void updateEntityFromRequest(CityUpdateRequest request,
                                 @MappingTarget CityMaster entity);

    @Mapping(source = "state.stateId", target = "stateId")
    CityResponse toResponse(CityMaster entity);

    List<CityResponse> toResponseList(List<CityMaster> entities);
}
