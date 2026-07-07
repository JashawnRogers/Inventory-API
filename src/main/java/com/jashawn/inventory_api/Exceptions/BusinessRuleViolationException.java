package com.jashawn.inventory_api.Exceptions;

import org.springframework.http.HttpStatus;

public class BusinessRuleViolationException extends BaseDomainException {
    public BusinessRuleViolationException(String message) {
        super(HttpStatus.BAD_REQUEST, message);
    }
}
