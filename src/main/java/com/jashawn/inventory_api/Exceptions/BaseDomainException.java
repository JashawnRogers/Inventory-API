package com.jashawn.inventory_api.Exceptions;

import org.springframework.http.HttpStatus;

public abstract class BaseDomainException extends RuntimeException{

    private final HttpStatus status;

    public BaseDomainException(HttpStatus status, String message) {
        super(message);
        this.status = status;
    }

    public HttpStatus getStatus() {return status;}
}
