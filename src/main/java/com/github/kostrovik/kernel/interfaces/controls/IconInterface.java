package com.github.kostrovik.kernel.interfaces.controls;

import javafx.scene.text.Font;

/**
 * project: kernel
 * author:  kostrovik
 * date:    30/07/2018
 * github:  https://github.com/kostrovik/kernel
 */
public interface IconInterface {
    String getSymbol();

    Font getFont();

    Font getSolidFont();

    Font getLightFont();

    String getFontPath();

    String getSolidFontPath();

    String getLightFontPath();
}
