package com.yntec.idp.masters.service.util;

import com.yntec.idp.masters.service.payload.response.ApiResponse;

import java.util.Collections;
import java.util.List;

public class ResponseBuilder {

    // Success response with data
    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(false, 200, message, Collections.emptyList(), data);
    }

    // Success response without data
    public static ApiResponse<Object> success(String message) {
        return new ApiResponse<>(false, 200, message, Collections.emptyList(), null);
    }

    // Error response with list of errors
    public static <T> ApiResponse<T> error(int statusCode, String message, List<String> isError) {
        return new ApiResponse<>(true, statusCode, message, isError, null);
    }

    // Error response with single error message
    public static <T> ApiResponse<T> error(int statusCode, String message, String isError) {
        return new ApiResponse<>(true, statusCode, message, List.of(isError), null);
    }

    // Optional: Error response with data included (useful for validation)
    public static <T> ApiResponse<T> errorWithData(int statusCode, String message, List<String> errors, T data) {
        return new ApiResponse<>(true, statusCode, message, errors, data);
    }
}
