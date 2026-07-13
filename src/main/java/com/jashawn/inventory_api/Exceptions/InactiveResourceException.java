package com.jashawn.inventory_api.Exceptions;

import org.springframework.http.HttpStatus;

public class InactiveResourceException extends BaseDomainException {
    public InactiveResourceException(String noun, String field) {
        super(
                HttpStatus.CONFLICT,
                ErrorCode.INACTIVE_RESOURCE_VIOLATION.name(),
                noun + " with " + field + " is inactive."
        );
    }
}
