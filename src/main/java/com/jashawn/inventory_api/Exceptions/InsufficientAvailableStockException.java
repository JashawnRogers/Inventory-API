package com.jashawn.inventory_api.Exceptions;

import org.springframework.http.HttpStatus;

import java.util.UUID;

public class InsufficientAvailableStockException extends BaseDomainException {
    public InsufficientAvailableStockException(UUID stockItemId, int requestedQuantity, int availableQuantity) {
        super(
                HttpStatus.CONFLICT,
                ErrorCode.INSUFFICIENT_STOCK.name(),
                "Stock item '" + stockItemId

                        + "' has insufficient available stock. Requested: "

                        + requestedQuantity

                        + ", available: "

                        + availableQuantity

                        + "."
        );
    }
}
