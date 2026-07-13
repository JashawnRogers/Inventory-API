package com.jashawn.inventory_api.Exceptions;

import org.springframework.http.HttpStatus;

public class DuplicateResourceException extends BaseDomainException {
    public DuplicateResourceException(String noun, String field, String value) {
        super(
                HttpStatus.CONFLICT,
                ErrorCode.DUPLICATE_RESOURCE_VIOLATION.name(),
                noun + " with " + field + " '" + value + "' already exists."
        );
    }
}
