package com.github.kostrovik.kernel.graphics.controls.panel;

import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;

import java.util.Map;

/**
 * project: kernel
 * author:  kostrovik
 * date:    24/08/2018
 * github:  https://github.com/kostrovik/kernel
 */
public class ListControlPanel extends Control {
    private ObservableMap<String, Button> buttons;

    public ListControlPanel() {
        this.buttons = FXCollections.observableHashMap();

        getStyleClass().add("list-control-panel");
        getStylesheets().add(this.getClass().getResource("/com/github/kostrovik/styles/controls/list-control-panel.css").toExternalForm());
    }

    public ListControlPanel(Map<String, Button> buttons) {
        this();
        this.buttons.clear();
        this.buttons.putAll(buttons);
    }

    public ObservableMap<String, Button> getButtons() {
        return buttons;
    }

    public void setButtons(ObservableMap<String, Button> buttons) {
        this.buttons = buttons;
    }

    @Override
    protected Skin<?> createDefaultSkin() {
        return new ListControlPanelSkin(this);
    }
}
