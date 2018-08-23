package com.github.kostrovik.kernel.graphics.common.icons;

import com.github.kostrovik.kernel.interfaces.controls.IconInterface;
import com.github.kostrovik.kernel.settings.SolidIconsSettings;
import javafx.scene.text.Font;

/**
 * Словарь иконок.
 * Использует иконочный шрифт font-awesome (https://fontawesome.com).
 * <p>
 * project: kernel
 * author:  kostrovik
 * date:    20/07/2018
 * github:  https://github.com/kostrovik/kernel
 */
public enum SolidIcons implements IconInterface {
    CARET_DOWN("\uf0d7"),
    SERVER("\uf233"),
    DATA_BASE("\uf1c0"),
    PALETTE("\uf53f"),
    ELIPSIS("\uf141"),
    CROSS("\uf00d");

    private final String character;
    private final Font font;
    private final SolidIconsSettings settings;

    private SolidIcons(String character) {
        this.character = character;
        this.settings = SolidIconsSettings.getInstance();
        this.font = prepareFont(settings);
    }

    @Override
    public String getSymbol() {
        return character;
    }

    @Override
    public Font getFont() {
        return font;
    }

    @Override
    public String getFontPath() {
        return settings.getFontPath();
    }

    private Font prepareFont(SolidIconsSettings settings) {
        return Font.loadFont(settings.getFontPath(), settings.getDefaultIconsFontSize());
    }
}
