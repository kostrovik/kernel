package com.github.kostrovik.kernel.interfaces;

import java.util.logging.Logger;

/**
 * project: kernel
 * author:  kostrovik
 * date:    31/07/2018
 * github:  https://github.com/kostrovik/kernel
 */
public interface ApplicationLoggerInterface {
    Logger getLogger(String className);
}
