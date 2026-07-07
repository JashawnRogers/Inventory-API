package com.jashawn.inventory_api.Exceptions;

import org.springframework.http.HttpStatus;

public class ResourceNotFoundException extends BaseDomainException{

    public ResourceNotFoundException(String message) {
        super( HttpStatus.NOT_FOUND, message);
    }
}
