package com.github.kostrovik.kernel.interfaces;

import java.util.EventObject;

/**
 * project: kernel
 * author:  kostrovik
 * date:    18/07/2018
 * github:  https://github.com/kostrovik/kernel
 */
public interface EventListenerInterface {
    void handle(EventObject event);
}
