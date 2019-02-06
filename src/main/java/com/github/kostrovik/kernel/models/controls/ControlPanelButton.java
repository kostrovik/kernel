package com.github.kostrovik.kernel.models.controls;

import com.github.kostrovik.kernel.graphics.builders.ButtonBuilder;
import com.github.kostrovik.kernel.graphics.common.icons.SolidIcons;
import com.github.kostrovik.kernel.interfaces.controls.PanelButtonInterface;
import javafx.scene.control.Button;

import java.util.Objects;

/**
 * project: kernel
 * author:  kostrovik
 * date:    2019-01-21
 * github:  https://github.com/kostrovik/kernel
 */
public class ControlPanelButton implements PanelButtonInterface {
    private ButtonBuilder buttonBuilder;
    private int order;
    private String key;
    private String label;
    private SolidIcons icon;

    public ControlPanelButton(String key, int order) {
        this.buttonBuilder = new ButtonBuilder();
        Objects.requireNonNull(key);
        this.key = key;
        this.order = order;
        this.label = "";
        this.icon = SolidIcons.EMPTY_ICON;
    }

    public ControlPanelButton(String key, int order, String label, SolidIcons icon) {
        this(key, order);
        this.label = Objects.requireNonNullElse(label, "");
        this.icon = Objects.requireNonNullElse(icon, SolidIcons.EMPTY_ICON);
    }

    @Override
    public String getButtonKey() {
        return key;
    }

    @Override
    public Button getButton() {
        return buttonBuilder.createButton(icon, label, true);
    }

    @Override
    public int getOrder() {
        return order;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ControlPanelButton)) return false;
        ControlPanelButton that = (ControlPanelButton) o;
        return order == that.order &&
                key.equals(that.key) &&
                label.equals(that.label) &&
                icon.equals(that.icon);
    }

    @Override
    public int hashCode() {
        return Objects.hash(order, key, label, icon);
    }
}