package com.example.fortbyte_conglomerate.exceptions;

public class EmptyRepositoryException extends RuntimeException {
    public EmptyRepositoryException() {
    }

    public EmptyRepositoryException(String message) {
        super(message);
    }

    public EmptyRepositoryException(String message, Throwable cause) {
        super(message, cause);
    }

    public EmptyRepositoryException(Throwable cause) {
        super(cause);
    }

    public EmptyRepositoryException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
