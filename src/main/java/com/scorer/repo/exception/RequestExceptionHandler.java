package com.scorer.repo.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.scorer.repo.response.ErrorResponse;

import io.swagger.v3.oas.annotations.Hidden;

@Hidden
@RestControllerAdvice(basePackages = "com.scorer.repo")
public class RequestExceptionHandler {
    @ExceptionHandler(InvalidRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleInvalidRequestValidations(InvalidRequestException ex) {
    	System.out.println(ex);
    	return new ErrorResponse(ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleAllException(Exception ex) {
    	System.out.println(ex);
        return new ErrorResponse(ex.getMessage());
    }
}
