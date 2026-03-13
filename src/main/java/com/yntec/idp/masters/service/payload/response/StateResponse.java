package com.yntec.idp.masters.service.payload.response;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StateResponse {

    private UUID stateId;
    private String stateName;
    private String stateCode;
    private Boolean status;

    private UUID countryId;
}
