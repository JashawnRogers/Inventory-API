package com.jashawn.inventory_api.Exceptions;

import org.springframework.http.HttpStatus;

public class BusinessRuleViolationException extends BaseDomainException {
    public BusinessRuleViolationException(String noun, String field, String value) {
        super(
                HttpStatus.BAD_REQUEST,
                ErrorCode.BUSINESS_RULE_VIOLATION.name(),
                "Business rule violated for " + noun + ":" + field + " " + value + " is not allowed."
                );
    }
}
