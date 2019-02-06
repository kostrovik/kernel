package com.github.kostrovik.kernel.exceptions;

/**
 * project: kernel
 * author:  kostrovik
 * date:    2019-02-07
 * github:  https://github.com/kostrovik/kernel
 */
public class SceneControlException extends RuntimeException {
    public SceneControlException() {
    }

    public SceneControlException(String message) {
        super(message);
    }

    public SceneControlException(String message, Throwable cause) {
        super(message, cause);
    }

    public SceneControlException(Throwable cause) {
        super(cause);
    }

    public SceneControlException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
