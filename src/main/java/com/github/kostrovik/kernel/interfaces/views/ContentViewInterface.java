package com.github.kostrovik.kernel.interfaces.views;

import javafx.scene.layout.Region;

import java.util.EventObject;

/**
 * project: kernel
 * author:  kostrovik
 * date:    24/07/2018
 * github:  https://github.com/kostrovik/kernel
 */
public interface ContentViewInterface {
    void initView(EventObject event);

    Region getView();
}
