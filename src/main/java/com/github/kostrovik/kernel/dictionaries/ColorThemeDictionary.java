package com.github.kostrovik.kernel.dictionaries;

/**
 * project: glcmtx
 * author:  kostrovik
 * date:    27/07/2018
 * github:  https://github.com/kostrovik/glcmtx
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
