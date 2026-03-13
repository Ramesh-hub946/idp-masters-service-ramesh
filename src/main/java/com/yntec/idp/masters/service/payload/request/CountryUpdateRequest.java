package com.yntec.idp.masters.service.payload.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CountryUpdateRequest {

    @NotBlank(message = "Country name is required")
    @Size(max = 100)
    private String countryName;

    @NotBlank(message = "Country code is required")
    @Size(max = 4)
    @Pattern(
            regexp = "^(?:[A-Za-z]{2,3}|\\+[1-9]\\d?)$",
            message = "Country code must be 2-3 letters or dialing code like +91"
    )
    private String countryCode;
}
