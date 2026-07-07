package com.jashawn.inventory_api.Exceptions;

import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

public record ApiErrorResponse(int status, String message, String error, LocalDateTime timestamp) {}
