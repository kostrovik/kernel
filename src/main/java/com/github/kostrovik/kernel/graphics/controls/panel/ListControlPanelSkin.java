package com.github.kostrovik.kernel.graphics.controls.panel;

import com.github.kostrovik.kernel.graphics.builders.ButtonBuilder;
import com.github.kostrovik.kernel.graphics.common.icons.SolidIcons;
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
    private ButtonBuilder builder;

    protected ListControlPanelSkin(ListControlPanel control) {
        super(control);
        builder = new ButtonBuilder();
        createSkin();

        getSkinnable().getButtons().addListener((MapChangeListener<String, Button>) change -> panel.getChildren().setAll(change.getMap().values()));

        addDefaultButtons();
        panel.getChildren().setAll(getSkinnable().getButtons().values());
    }

    private void createSkin() {
        panel = new HBox(1);

        getChildren().addAll(panel);
    }

    private void addDefaultButtons() {
        getSkinnable().getDefaultButtons().forEach(buttonType -> {
            Button button = null;
            switch (buttonType) {
                case ADD:
                    button = builder.createButton(SolidIcons.PLUS, "", true);
                    break;
                case REMOVE:
                    button = builder.createButton(SolidIcons.MINUS, "", true);
                    break;
                case EDIT:
                    button = builder.createButton(SolidIcons.PEN, "", true);
                    break;
                default:
                    break;
            }
            if (button != null) {
                button.setFocusTraversable(false);
                button.prefWidthProperty().bind(button.heightProperty());
                button.setPrefHeight(18);
                button.setMinWidth(0);

                getSkinnable().addButton(buttonType.getButtonKey(), button);
            }
        });
    }
}
