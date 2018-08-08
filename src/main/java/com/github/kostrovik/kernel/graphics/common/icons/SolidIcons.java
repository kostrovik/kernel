package com.github.kostrovik.kernel.graphics.common.icons;

import com.github.kostrovik.kernel.interfaces.controls.IconInterface;
import com.github.kostrovik.kernel.settings.SolidIconsSettings;
import javafx.scene.text.Font;

/**
 * Словарь иконок.
 * Использует иконочный шрифт font-awesome (https://fontawesome.com).
 * <p>
 * project: glcmtx
 * author:  kostrovik
 * date:    20/07/2018
 * github:  https://github.com/kostrovik/glcmtx
 */
public enum SolidIcons implements IconInterface {
    CARET_DOWN("\uf0d7"),
    SERVER("\uf233"),
    DATA_BASE("\uf1c0"),
    PALETTE("\uf53f");

    private final String character;
    private final Font font;
    private final SolidIconsSettings settings;

    private SolidIcons(String character) {
        this.character = character;
        this.settings = SolidIconsSettings.getInstance();
        this.font = prepareFont(settings);
    }

    public String getSymbol() {
        return character;
    }

    public Font getFont() {
        return font;
    }

    private Font prepareFont(SolidIconsSettings settings) {
        return Font.loadFont(settings.getFontPath(), settings.getDefaultIconsFontSize());
    }
}
