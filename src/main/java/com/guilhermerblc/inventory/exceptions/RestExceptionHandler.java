package com.guilhermerblc.inventory.exceptions;

import io.jsonwebtoken.ExpiredJwtException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.NoSuchElementException;

@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(NoSuchElementException.class)
    protected ResponseEntity<ApiError> handleUserNotFound(NoSuchElementException ex) {
        return buildResponseEntity(new ApiError(HttpStatus.NOT_FOUND, "Item not found.", ex));
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    protected ResponseEntity<ApiError> handleUsernameNotFound(UsernameNotFoundException ex) {
        return buildResponseEntity(new ApiError(HttpStatus.NOT_FOUND, "User not found by given username.", ex));
    }

    @ExceptionHandler(IdentificationNotEqualsException.class)
    protected ResponseEntity<ApiError> handleIdentificationNotEqualsException(IdentificationNotEqualsException ex) {
        return buildResponseEntity(new ApiError(HttpStatus.UNPROCESSABLE_ENTITY, "IDs must be the same in path and body.", ex));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    protected ResponseEntity<ApiError> handleIllegalArgumentException(IllegalArgumentException ex) {
        return buildResponseEntity(new ApiError(HttpStatus.UNPROCESSABLE_ENTITY, "Some arguments has invalid data.", ex));
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST, "Invalid data.", ex);
        return new ResponseEntity<>(apiError, apiError.getStatus());
    }

    @ExceptionHandler(CredentialsExpiredException.class)
    protected ResponseEntity<ApiError> handleCredentialsExpiredException(CredentialsExpiredException ex) {
        return buildResponseEntity(new ApiError(HttpStatus.FORBIDDEN, "Credential expired.", ex));
    }

    private ResponseEntity<ApiError> buildResponseEntity(ApiError apiError) {
        return new ResponseEntity<>(apiError, apiError.getStatus());
    }

}
