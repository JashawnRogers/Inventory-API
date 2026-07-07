package com.jashawn.inventory_api.Exceptions;

import org.springframework.http.HttpStatus;

public class InvalidStateException extends BaseDomainException{

    public InvalidStateException(String message) {
        super(HttpStatus.BAD_REQUEST, message);
    }
}
