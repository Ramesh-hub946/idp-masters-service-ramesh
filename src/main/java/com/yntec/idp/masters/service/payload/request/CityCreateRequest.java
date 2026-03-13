package com.yntec.idp.masters.service.payload.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CityCreateRequest {

    @NotNull(message = "State ID is required")
    private UUID stateId;

    @NotBlank(message = "City name is required")
    @Size(max = 100)
    private String cityName;

    @Size(max = 20)
    private String cityCode;
}
