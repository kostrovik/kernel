package com.github.kostrovik.kernel.exceptions;

/**
 * project: kernel
 * author:  kostrovik
 * date:    2018-12-16
 * github:  https://github.com/kostrovik/kernel
 */
public class ParseException extends RuntimeException {
    public ParseException() {
    }

    public ParseException(String message) {
        super(message);
    }

    public ParseException(String message, Throwable cause) {
        super(message, cause);
    }

    public ParseException(Throwable cause) {
        super(cause);
    }
}
