package com.yntec.idp.masters.service.mapper;

import com.yntec.idp.masters.service.entity.StateMaster;
import com.yntec.idp.masters.service.payload.request.StateCreateRequest;
import com.yntec.idp.masters.service.payload.request.StateUpdateRequest;
import com.yntec.idp.masters.service.payload.response.StateResponse;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface StateMasterMapper {

    @Mapping(target = "stateId", ignore = true)
    @Mapping(target = "country", ignore = true)
    @Mapping(target = "cities", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "modifiedBy", ignore = true)
    @Mapping(target = "modifiedAt", ignore = true)
    @Mapping(target = "status", ignore = true)
    StateMaster toEntity(StateCreateRequest request);

    @Mapping(target = "stateId", ignore = true)
    @Mapping(target = "country", ignore = true)
    @Mapping(target = "cities", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "modifiedBy", ignore = true)
    @Mapping(target = "modifiedAt", ignore = true)
    @Mapping(target = "status", ignore = true)
    void updateEntityFromRequest(StateUpdateRequest request,
                                 @MappingTarget StateMaster entity);

    @Mapping(source = "country.countryId", target = "countryId")
    StateResponse toResponse(StateMaster entity);

    List<StateResponse> toResponseList(List<StateMaster> entities);

    @AfterMapping
    default void normalize(@MappingTarget StateMaster entity) {
        if (entity.getStateCode() == null) {
            return;
        }

        String normalized = entity.getStateCode().trim().toUpperCase();
        entity.setStateCode(normalized.isEmpty() ? null : normalized);
    }
}
