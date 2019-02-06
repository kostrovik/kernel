package com.github.kostrovik.kernel.exceptions;

/**
 * project: kernel
 * author:  kostrovik
 * date:    2019-01-11
 * github:  https://github.com/kostrovik/kernel
 */
public class SaveEntityException extends Exception {
    public SaveEntityException() {
        super();
    }

    public SaveEntityException(String message) {
        super(message);
    }

    public SaveEntityException(String message, Throwable cause) {
        super(message, cause);
    }

    public SaveEntityException(Throwable cause) {
        super(cause);
    }
}
