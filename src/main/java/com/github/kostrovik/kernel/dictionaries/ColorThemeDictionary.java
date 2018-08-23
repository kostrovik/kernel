package com.github.kostrovik.kernel.dictionaries;

/**
 * project: kernel
 * author:  kostrovik
 * date:    27/07/2018
 * github:  https://github.com/kostrovik/kernel
 */
public enum ColorThemeDictionary {
    LIGHT() {
        public String getThemeName() {
            return "light-theme.css";
        }
    },
    DARK_ADMIN() {
        public String getThemeName() {
            return "admin-theme.css";
        }
    };

    public abstract String getThemeName();
}
