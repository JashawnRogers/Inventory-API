package com.jashawn.inventory_api.Exceptions;

import org.springframework.http.HttpStatus;

public class InvalidStockMovementException extends BaseDomainException {
    public InvalidStockMovementException(String operation, String reason) {
        super(
                HttpStatus.BAD_REQUEST,
                ErrorCode.INVALID_STOCK_MOVEMENT.name(),
                "Cannot " + operation + ": " + reason + "."
        );
    }
}
