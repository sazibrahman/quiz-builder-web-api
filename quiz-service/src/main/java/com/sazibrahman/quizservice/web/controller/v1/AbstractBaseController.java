package com.sazibrahman.quizservice.web.controller.v1;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import com.sazibrahman.quizservice.data.model.v1.ApiError;
import com.sazibrahman.quizservice.exception.InvalidInputException;
import com.sazibrahman.quizservice.exception.NotFoundException;

public class AbstractBaseController {

    @ExceptionHandler({ AuthenticationException.class })
    public ResponseEntity<ApiError> handleAuthenticationException(AuthenticationException ex, WebRequest request) {
        ApiError apiError = ApiError.build(HttpStatus.UNAUTHORIZED, ex.getMessage());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new ResponseEntity<>(apiError, headers, apiError.getStatusEnum());
    }

    @ExceptionHandler({ InvalidInputException.class })
    public ResponseEntity<ApiError> handleInvalidInputException(InvalidInputException ex, WebRequest request) {
        ApiError apiError = ApiError.build(HttpStatus.CONFLICT, ex.getMessage());
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new ResponseEntity<>(apiError, headers, apiError.getStatusEnum());
    }

    @ExceptionHandler({ NotFoundException.class })
    public ResponseEntity<ApiError> handleNotFoundException(NotFoundException ex, WebRequest request) {
        ApiError apiError = ApiError.build(HttpStatus.NOT_FOUND, ex.getMessage());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new ResponseEntity<>(apiError, headers, apiError.getStatusEnum());
    }

    @ExceptionHandler({ Exception.class })
    public ResponseEntity<ApiError> handleException(Exception ex, WebRequest request) {
        ApiError apiError = ApiError.build(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new ResponseEntity<>(apiError, headers, apiError.getStatusEnum());
    }

}
