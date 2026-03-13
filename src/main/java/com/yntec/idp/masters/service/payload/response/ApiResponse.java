package com.yntec.idp.masters.service.payload.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class ApiResponse<T> {

    @JsonProperty("IsError")
    private boolean isError;

    private int statusCode;
    private String message;
    private List<String> errors;
    private T data;
}
