package com.jashawn.inventory_api.Exceptions;

import org.springframework.http.HttpStatus;

public class InvalidFieldException extends BaseDomainException{

    public InvalidFieldException(String noun, String field, String value) {
        super(
                HttpStatus.BAD_REQUEST,
                ErrorCode.INVALID_FIELD_VIOLATION.name(),
                "The value '" + value + "' is invalid for " + noun + "." + field + "."
        );
    }
}
