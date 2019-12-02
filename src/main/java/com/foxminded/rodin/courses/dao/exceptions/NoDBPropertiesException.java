package com.foxminded.rodin.courses.dao.exceptions;

@SuppressWarnings("serial")
public class NoDBPropertiesException extends RuntimeException {

    public NoDBPropertiesException(String message, Throwable cause) {
        super(message, cause);
    }


}
