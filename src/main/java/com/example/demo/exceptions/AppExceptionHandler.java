/**
 * Created By shoh@n
 * Date: 8/13/2022
 */

package com.example.demo.exceptions;

import com.example.demo.models.ErrorModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestControllerAdvice
@Slf4j
public class AppExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(ApiException.class)
    ResponseEntity<?> handleApiException(ApiException ex, WebRequest request) {
        ex.printStackTrace();
        ErrorModel model = ErrorModel.builder().status(ex.getHttpStatusCode()).message(ex.getMessage()).build();
        return new ResponseEntity<>(model.response(), ex.getHttpStatus());
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        List<Map<String, Object>> errors = new ArrayList<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.add(Map.of(error.getField(), Optional.ofNullable(error.getDefaultMessage())));
        }
        for (ObjectError error : ex.getBindingResult().getGlobalErrors()) {
            errors.add(Map.of(error.getObjectName(), Optional.ofNullable(error.getDefaultMessage())));
        }
        ErrorModel model = ErrorModel.builder().status(HttpStatus.BAD_REQUEST.value()).errors(errors).build();
        return new ResponseEntity<>(model.errorsResponse(), HttpStatus.BAD_REQUEST);
    }

    @Override
    protected ResponseEntity<Object> handleNoHandlerFoundException(NoHandlerFoundException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        ErrorModel model = ErrorModel.builder().status(status.value()).message(ex.getMessage()).build();
        return new ResponseEntity<>(model.response(), status);
    }
}
