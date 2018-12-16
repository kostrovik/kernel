package com.github.kostrovik.kernel.exceptions;

/**
 * project: kernel
 * author:  kostrovik
 * date:    2018-12-16
 * github:  https://github.com/kostrovik/kernel
 */
public class SceneBuilderException extends RuntimeException {
    public SceneBuilderException() {
    }

    public SceneBuilderException(String message) {
        super(message);
    }

    public SceneBuilderException(String message, Throwable cause) {
        super(message, cause);
    }

    public SceneBuilderException(Throwable cause) {
        super(cause);
    }
}
