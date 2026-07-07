package com.jashawn.inventory_api.Exceptions;

import org.springframework.http.HttpStatus;

public class InactiveResourceException extends BaseDomainException {
    public InactiveResourceException(String message) {
        super(HttpStatus.BAD_REQUEST, message);
    }
}
