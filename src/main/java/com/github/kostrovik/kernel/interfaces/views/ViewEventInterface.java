package com.github.kostrovik.kernel.interfaces.views;

import com.github.kostrovik.kernel.dictionaries.LayoutType;

/**
 * project: kernel
 * author:  kostrovik
 * date:    24/07/2018
 * github:  https://github.com/kostrovik/kernel
 */
public interface ViewEventInterface {
    String getModuleName();

    String getViewName();

    Object getEventData();

    LayoutType getLayoutType();
}
