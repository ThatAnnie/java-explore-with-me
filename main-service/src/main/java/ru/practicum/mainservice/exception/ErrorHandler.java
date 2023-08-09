package ru.practicum.mainservice.exception;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ValidationException;
import java.time.LocalDateTime;

import static ru.practicum.mainservice.common.CommonConstants.DATE_TIME_FORMATTER;

@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler(value = {ValidationException.class, BadRequestException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidationException(Exception e) {
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.BAD_REQUEST.toString(),
                "Incorrectly made request.",
                e.getMessage(),
                LocalDateTime.now().format(DATE_TIME_FORMATTER));
        return errorResponse;
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFoundException(NotFoundException e) {
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.NOT_FOUND.toString(),
                "The required object was not found.",
                e.getMessage(),
                LocalDateTime.now().format(DATE_TIME_FORMATTER));
        return errorResponse;
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleConstraintViolationException(DataIntegrityViolationException e) {
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.CONFLICT.toString(),
                "Integrity constraint has been violated.",
                e.getMessage(),
                LocalDateTime.now().format(DATE_TIME_FORMATTER));
        return errorResponse;
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleConflictException(ConflictException e) {
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.CONFLICT.toString(),
                "For the requested operation the conditions are not met.",
                e.getMessage(),
                LocalDateTime.now().format(DATE_TIME_FORMATTER));
        return errorResponse;
    }
}
