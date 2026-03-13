package com.yntec.idp.masters.service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class LocationMasterNotFoundException extends RuntimeException {
    public LocationMasterNotFoundException(String message) {
        super(message);
    }
}
