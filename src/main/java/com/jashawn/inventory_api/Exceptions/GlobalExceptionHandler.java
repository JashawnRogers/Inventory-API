package com.jashawn.inventory_api.Exceptions;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;
import java.time.Instant;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private final HttpServletRequest request;

    public GlobalExceptionHandler(HttpServletRequest request) {
        this.request = request;
    }


//   Handles all custom exceptions
    @ExceptionHandler(BaseDomainException.class)
    public ResponseEntity<ProblemDetail> handleDomainExceptions(BaseDomainException ex) {
        ProblemDetail problem = ProblemDetail
                .forStatusAndDetail(ex.getStatus(), ex.getMessage());
        problem.setTitle(ex.getStatus().getReasonPhrase());
        problem.setInstance(URI.create(request.getRequestURI()));
        problem.setProperty("errorCode", ex.getErrorCode());
        problem.setProperty("timestamp", Instant.now());

        return ResponseEntity.status(ex.getStatus()).body(problem);
    }

//    Catch all for any non-custom errors
@ExceptionHandler(Exception.class)
    public ResponseEntity<ProblemDetail> handleUnexpected(Exception ex) {
        ProblemDetail problem = ProblemDetail
                .forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR,
                        "An unexpected error occurred");
        problem.setProperty("timestamp", Instant.now());

        return ResponseEntity.internalServerError().body(problem);
    }
}
