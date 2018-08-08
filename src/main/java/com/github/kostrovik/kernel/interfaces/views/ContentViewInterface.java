package com.github.kostrovik.kernel.interfaces.views;

import javafx.scene.layout.Region;

import java.util.EventObject;

/**
 * project: glcmtx
 * author:  kostrovik
 * date:    24/07/2018
 * github:  https://github.com/kostrovik/glcmtx
 */
public interface ContentViewInterface {
    void initView(EventObject event);

    Region getView();
}
