package com.example.websitepro.Config;

import org.springframework.http.HttpStatus;

public class WebException extends RuntimeException{

    private final HttpStatus status;

    public WebException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }

    public WebException(String message) {
        super(message);
        this.status = HttpStatus.OK;
    }

    public HttpStatus getStatus() {
        return status;
    }

}
