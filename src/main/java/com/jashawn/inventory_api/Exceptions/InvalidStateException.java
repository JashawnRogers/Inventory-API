package com.jashawn.inventory_api.Exceptions;

public class InvalidStateException extends RuntimeException{

    public InvalidStateException(String message) {
        super(message);
    }
}
