package com.yntec.idp.masters.service.payload.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StateStatusUpdateRequest {

    @NotNull(message = "Status is required")
    private Boolean status;
}
