package com.doubleA.crypto.filter;

/**
 * trigger for bad request exception
 */
public class BadRequestException extends RuntimeException {
    public BadRequestException(String msg, Throwable t) {
        super(msg, t);
    }

    public BadRequestException(String msg) {
        super(msg);
    }

}