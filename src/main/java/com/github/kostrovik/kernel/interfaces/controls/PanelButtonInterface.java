package com.github.kostrovik.kernel.interfaces.controls;

import javafx.scene.control.Button;

/**
 * project: kernel
 * author:  kostrovik
 * date:    2019-01-21
 * github:  https://github.com/kostrovik/kernel
 */
public interface PanelButtonInterface {
    String getButtonKey();

    Button getButton();

    int getOrder();
}
