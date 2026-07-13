package com.jashawn.inventory_api.Exceptions;

import org.springframework.http.HttpStatus;

public abstract class BaseDomainException extends RuntimeException{

    private final HttpStatus status;
    private final String errorCode;

    public BaseDomainException(HttpStatus status, String message, String errorCode) {
        super(message);
        this.status = status;
        this.errorCode = errorCode;
    }

    public HttpStatus getStatus() {return status;}
    public String getErrorCode() {return errorCode;}
}
