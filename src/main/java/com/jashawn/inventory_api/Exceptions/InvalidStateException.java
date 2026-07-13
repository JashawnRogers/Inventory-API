package com.jashawn.inventory_api.Exceptions;

import org.springframework.http.HttpStatus;

public class InvalidStateException extends BaseDomainException {
    public InvalidStateException(String noun, String field, String value) {
        super(
                HttpStatus.CONFLICT,
                ErrorCode.INVALID_STATE_VIOLATION.name(),
                noun + " cannot perform the requested operation while " + field + " is '" + value + "'."
        );
    }
}
