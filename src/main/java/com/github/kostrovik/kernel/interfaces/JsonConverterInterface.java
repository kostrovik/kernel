package com.github.kostrovik.kernel.interfaces;

import java.util.Map;

/**
 * project: kernel
 * author:  kostrovik
 * date:    2019-01-11
 * github:  https://github.com/kostrovik/kernel
 */
public interface JsonConverterInterface<E> {
    String toJSON(E entity);

    E fromJSON(String json);

    E fromMap(Map dataMap);
}
