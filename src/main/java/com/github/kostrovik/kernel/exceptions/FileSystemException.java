package com.github.kostrovik.kernel.exceptions;

/**
 * project: kernel
 * author:  kostrovik
 * date:    2018-12-17
 * github:  https://github.com/kostrovik/kernel
 */
public class FileSystemException extends RuntimeException {
    public FileSystemException() {
    }

    public FileSystemException(String message) {
        super(message);
    }

    public FileSystemException(String message, Throwable cause) {
        super(message, cause);
    }

    public FileSystemException(Throwable cause) {
        super(cause);
    }
}
