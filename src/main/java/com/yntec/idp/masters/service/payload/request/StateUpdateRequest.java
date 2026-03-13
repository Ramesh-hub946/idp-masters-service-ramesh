package com.yntec.idp.masters.service.payload.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StateUpdateRequest {

    @NotBlank(message = "State name is required")
    @Size(max = 100)
    private String stateName;

    @Size(max = 10)
    private String stateCode;
}
