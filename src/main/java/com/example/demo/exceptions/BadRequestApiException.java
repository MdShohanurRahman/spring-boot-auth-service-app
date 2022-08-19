/**
 * Created By shoh@n
 * Date: 8/14/2022
 */

package com.example.demo.exceptions;

import org.springframework.http.HttpStatus;

public class BadRequestApiException extends ApiException {

    public BadRequestApiException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }

    public BadRequestApiException() {
        this("Request could not be understood");
    }
}
