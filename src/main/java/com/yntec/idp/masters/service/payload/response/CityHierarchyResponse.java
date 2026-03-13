package com.yntec.idp.masters.service.payload.response;


import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CityHierarchyResponse {

    private UUID cityId;
    private String cityName;
    private String cityCode;
    private Boolean status;

    private UUID stateId;
    private String stateName;

    private UUID countryId;
    private String countryName;
    private String countryCode;
}
