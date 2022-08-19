/**
 * Created By shoh@n
 * Date: 8/14/2022
 */

package com.example.demo.exceptions;

import org.springframework.http.HttpStatus;

public class NotFoundApiException extends ApiException {

    public NotFoundApiException(String message) {
        super(message, HttpStatus.NOT_FOUND);
    }

    public NotFoundApiException() {
        this("Requested resource could not be found");
    }
}
