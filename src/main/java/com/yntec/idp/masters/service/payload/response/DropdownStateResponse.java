package com.yntec.idp.masters.service.payload.response;

import lombok.*;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DropdownStateResponse {

    private UUID stateId;
    private String stateName;
    private List<DropdownCityResponse> cities;
}
