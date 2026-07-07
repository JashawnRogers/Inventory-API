package com.jashawn.inventory_api.Exceptions;

public class InactiveResourceException extends RuntimeException {
    public InactiveResourceException(String message) {
        super(message);
    }
}
