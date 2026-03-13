package com.yntec.idp.masters.service.payload.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CityUpdateRequest {

    @NotBlank(message = "City name is required")
    @Size(max = 100)
    private String cityName;

    @Size(max = 20)
    private String cityCode;
}
