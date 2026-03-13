package com.yntec.idp.masters.service.payload.response;


import lombok.*;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DropdownCountryResponse {

    private UUID countryId;
    private String countryName;
    private List<DropdownStateResponse> states;
}
