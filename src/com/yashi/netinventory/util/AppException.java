package com.yashi.netinventory.util;

/**
 * Application-level checked exception, used to translate low-level SQLExceptions
 * into clean, human-readable errors for the UI layer.
 */
public class AppException extends Exception {
    public AppException(String message) {
        super(message);
    }

    public AppException(String message, Throwable cause) {
        super(message, cause);
    }
}
