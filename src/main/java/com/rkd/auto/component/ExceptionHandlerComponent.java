package com.rkd.auto.component;

import com.rkd.auto.dto.ExceptionDto;
import com.rkd.auto.exception.NotFoundException;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;

import static com.rkd.auto.type.ExceptionType.INVALID_INPUT_FIELD;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@RestControllerAdvice
public class ExceptionHandlerComponent {

    @ExceptionHandler(WebExchangeBindException.class)
    public ResponseEntity<ExceptionDto> handleValidationException(WebExchangeBindException ex) {

        var message = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .findFirst()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .orElse(INVALID_INPUT_FIELD.message());

        var exceptionDto = new ExceptionDto(INVALID_INPUT_FIELD.code(), message);
        return new ResponseEntity<>(exceptionDto, BAD_REQUEST);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ExceptionDto> handleNotFoundException(NotFoundException ex) {
        var exceptionDto = new ExceptionDto(ex.getCode(), ex.getDescription());
        return new ResponseEntity<>(exceptionDto, NOT_FOUND);
    }
}