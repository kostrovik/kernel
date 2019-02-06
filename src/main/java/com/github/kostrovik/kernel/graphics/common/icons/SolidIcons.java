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
    EMPTY_ICON(""){
        @Override
        public String getFontPath() {
            return settings.getFontPath();
        }
    },
    CARET_DOWN("\uf0d7") {
        @Override
        public String getFontPath() {
            return settings.getFontPath();
        }
    }, // стрелка вниз для выпадающего списка
    SERVER("\uf233") {
        @Override
        public String getFontPath() {
            return settings.getFontPath();
        }
    },
    DATA_BASE("\uf1c0") {
        @Override
        public String getFontPath() {
            return settings.getFontPath();
        }
    },
    PALETTE("\uf53f") {
        @Override
        public String getFontPath() {
            return settings.getFontPath();
        }
    },
    ELIPSIS("\uf141") {
        @Override
        public String getFontPath() {
            return settings.getFontPath();
        }
    }, // три горизонтальные точки
    CROSS("\uf00d") {
        @Override
        public String getFontPath() {
            return settings.getFontPath();
        }
    }, // крест для кнопки очистить
    PLUS("\uf067") {
        @Override
        public String getFontPath() {
            return settings.getFontPath();
        }
    },
    MINUS("\uf068") {
        @Override
        public String getFontPath() {
            return settings.getFontPath();
        }
    },
    PEN("\uf304") {
        @Override
        public String getFontPath() {
            return settings.getFontPath();
        }
    }, // карандаш для кнопки редактировать
    WRENCH("\uf0ad") {
        @Override
        public String getFontPath() {
            return settings.getFontPath();
        }
    }, // гаечный ключ
    ARROW_ALT_RIGHT_LIGHT("\uf356") {
        @Override
        public String getFontPath() {
            return settings.getLightFontPath();
        }

        @Override
        public String getSolidFontPath() {
            return settings.getSolidFontPath();
        }

        @Override
        public Font getFont() {
            return prepareFont(FONT_LIGHT);
        }
    },
    ARROW_ALT_RIGHT_SOLID("\uf356", SolidIcons.FONT_SOLID) {
        @Override
        public String getFontPath() {
            return settings.getSolidFontPath();
        }

        @Override
        public String getSolidFontPath() {
            return settings.getSolidFontPath();
        }

        @Override
        public Font getFont() {
            return prepareFont(FONT_SOLID);
        }
    };

    private final String character;
    private final String type;
    private static final SolidIconsSettings settings = SolidIconsSettings.getInstance();
    private static final String FONT_LIGHT = "light";
    private static final String FONT_SOLID = "solid";

    private SolidIcons(String character) {
        this.character = character;
        this.type = "default";
    }

    private SolidIcons(String character, String type) {
        this.character = character;
        this.type = type;
    }

    @Override
    public String getSymbol() {
        return character;
    }

    @Override
    public Font getFont() {
        return prepareFont("default");
    }

    @Override
    public Font getSolidFont() {
        return prepareFont(FONT_SOLID);
    }

    @Override
    public Font getLightFont() {
        return prepareFont(FONT_LIGHT);
    }

    protected Font prepareFont(String type) {
        switch (type) {
            case FONT_SOLID:
                return Font.loadFont(getSolidFontPath(), settings.getDefaultIconsFontSize());
            case FONT_LIGHT:
                return Font.loadFont(getLightFontPath(), settings.getDefaultIconsFontSize());
            default:
                return Font.loadFont(getFontPath(), settings.getDefaultIconsFontSize());
        }
    }

    @Override
    public String getSolidFontPath() {
        return settings.getFontPath();
    }

    @Override
    public String getLightFontPath() {
        return settings.getFontPath();
    }
}
