package com.yntec.idp.masters.service.payload.response;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CountryResponse {

    private UUID countryId;
    private String countryName;
    private String countryCode;
    private Boolean status;
}
