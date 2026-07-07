package com.jashawn.inventory_api.Exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {


//   Handles all custom exceptions
    @ExceptionHandler(BaseDomainException.class)
    public ResponseEntity<ApiErrorResponse> handleDomainExceptions(BaseDomainException ex) {

        ApiErrorResponse error = new ApiErrorResponse(
                ex.getStatus().value(),
                ex.getMessage(),
                ex.getStatus().getReasonPhrase(),
                LocalDateTime.now()
        );

        return ResponseEntity.status(ex.getStatus()).body(error);
    }

//    Catch all for any non-custom errors
    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGeneralError(Exception ex) {

        ApiErrorResponse error = new ApiErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                ex.getMessage(),
                HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                LocalDateTime.now()
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

}
