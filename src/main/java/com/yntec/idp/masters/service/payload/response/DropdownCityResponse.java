package com.yntec.idp.masters.service.payload.response;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DropdownCityResponse {

    private UUID cityId;
    private String cityName;
}
