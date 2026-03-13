package com.yntec.idp.masters.service.payload.response;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LocationTreeCityResponse {

    private UUID cityId;
    private String cityName;
    private String cityCode;
    private Boolean status;
}
