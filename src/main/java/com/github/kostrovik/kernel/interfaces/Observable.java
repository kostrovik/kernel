package com.github.kostrovik.kernel.interfaces;

/**
 * project: kernel
 * author:  kostrovik
 * date:    27/08/2018
 * github:  https://github.com/kostrovik/kernel
 */
public interface Observable {
    void addListener(EventListenerInterface listener);

    void removeListener(EventListenerInterface listener);
}
