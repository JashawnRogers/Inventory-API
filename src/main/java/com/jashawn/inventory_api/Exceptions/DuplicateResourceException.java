package com.jashawn.inventory_api.Exceptions;

import org.springframework.http.HttpStatus;

public class DuplicateResourceException extends BaseDomainException {
    public DuplicateResourceException(String message) {
        super(HttpStatus.CONFLICT, message);
    }
}
