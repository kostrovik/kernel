package com.github.kostrovik.kernel.graphics.controls.panel;

import javafx.collections.MapChangeListener;
import javafx.scene.control.Button;
import javafx.scene.control.SkinBase;
import javafx.scene.layout.HBox;

/**
 * project: kernel
 * author:  kostrovik
 * date:    24/08/2018
 * github:  https://github.com/kostrovik/kernel
 */
public class ListControlPanelSkin extends SkinBase<ListControlPanel> {
    private HBox panel;

    protected ListControlPanelSkin(ListControlPanel control) {
        super(control);
        createSkin();

        getSkinnable().getButtons().addListener((MapChangeListener<String, Button>) change -> panel.getChildren().setAll(getSkinnable().getButtons().values()));
    }

    private void createSkin() {
        panel = new HBox(1);
        panel.getChildren().setAll(getSkinnable().getButtons().values());
        getChildren().addAll(panel);
    }
}
