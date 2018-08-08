package com.github.kostrovik.kernel.interfaces.views;

/**
 * project: glcmtx
 * author:  kostrovik
 * date:    24/07/2018
 * github:  https://github.com/kostrovik/glcmtx
 */
public interface ViewEventInterface {
    String getModuleName();

    String getViewName();

    Object getEventData();

    LayoutType getLayoutType();
}
