package com.potus.app.user.exception;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * For HTTP 404 errros
 */
@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException() {
        super();
    }

    public ResourceNotFoundException(String object, String id) {
        super("%s (%s) not found".formatted(object, id));
    }

    public ResourceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public ResourceNotFoundException(String message) {
        super(message);
    }

    public ResourceNotFoundException(Throwable cause) {
        super(cause);
    }

}