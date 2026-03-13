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
public class StateCreateRequest {

    @NotNull(message = "Country ID is required")
    private UUID countryId;

    @NotBlank(message = "State name is required")
    @Size(max = 100)
    private String stateName;

    @Size(max = 10)
    private String stateCode;
}
