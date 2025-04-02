package com.gridnine.testing.exceptions;

public class InvalidDateTimeFormatException extends IllegalArgumentException {
    public InvalidDateTimeFormatException(String message, Throwable cause) {
        super(message, cause);
    }
}
