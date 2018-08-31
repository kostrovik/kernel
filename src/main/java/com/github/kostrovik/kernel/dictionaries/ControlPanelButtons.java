package com.github.kostrovik.kernel.dictionaries;

/**
 * project: kernel
 * author:  kostrovik
 * date:    24/08/2018
 * github:  https://github.com/kostrovik/kernel
 */
public enum ControlPanelButtons {
    ADD() {
        public String getButtonKey() {
            return "add-button";
        }
    },
    REMOVE() {
        public String getButtonKey() {
            return "remove-button";
        }
    },
    EDIT() {
        public String getButtonKey() {
            return "edit-button";
        }
    };

    public abstract String getButtonKey();
}