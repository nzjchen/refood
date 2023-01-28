package org.seng302.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Custom exception status to be thrown when an image extension is invalid.
 */
@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class InvalidImageExtensionException extends Exception{

    public InvalidImageExtensionException (String message) {
        super(message);
    }

    public InvalidImageExtensionException(String message, Throwable t) {
        super(message, t);
    }

}
