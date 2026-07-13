package com.jashawn.inventory_api.Exceptions;

import org.springframework.http.HttpStatus;

public class ResourceNotFoundException extends BaseDomainException{

    public ResourceNotFoundException(String noun, String field, String value) {
        super(
                HttpStatus.NOT_FOUND,
                ErrorCode.RESOURCE_NOT_FOUND_VIOLATION.name(),
                noun + " with " + field + ": " + value + " was not found."
        );
    }
}
