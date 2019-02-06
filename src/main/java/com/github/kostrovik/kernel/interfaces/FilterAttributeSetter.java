package com.github.kostrovik.kernel.interfaces;

/**
 * project: kernel
 * author:  kostrovik
 * date:    2019-01-11
 * github:  https://github.com/kostrovik/kernel
 */
public interface FilterAttributeSetter<V> {
    String getAttributeName();

    Object prepareValue(V value);
}
