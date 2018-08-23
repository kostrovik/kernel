package com.github.kostrovik.kernel.graphics.controls.notification;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;

/**
 * project: kernel
 * author:  kostrovik
 * date:    21/07/2018
 * github:  https://github.com/kostrovik/kernel
 */
public class Notification extends Control {
    private final ObjectProperty<Boolean> isVisible;
    private final ObjectProperty<String> message;
    private final ObjectProperty<NotificationType> type;

    public Notification() {
        this.isVisible = new SimpleObjectProperty<>();
        this.message = new SimpleObjectProperty<>();
        this.type = new SimpleObjectProperty<>();
        setFocusTraversable(false);
    }

    @Override
    protected Skin<?> createDefaultSkin() {
        return new NotificationSkin(this);
    }

    // свойсто видимости
    public ObjectProperty<Boolean> showProperty() {
        return isVisible;
    }

    public boolean getIsVisible() {
        return isVisible.get();
    }

    public void setIsVisible(boolean visible) {
        isVisible.set(visible);
    }
    // -- свойсто видимости --

    // свойсто текст
    public ObjectProperty<String> messageProperty() {
        return message;
    }

    public String getMessage() {
        return message.get();
    }

    public void setMessage(String text) {
        message.set(text);
        isVisible.set(true);
    }
    // -- свойсто текст --

    // свойсто тип
    public ObjectProperty<NotificationType> typeProperty() {
        return type;
    }

    public NotificationType getType() {
        return type.get();
    }

    public void setType(NotificationType notificationType) {
        type.set(notificationType);
    }
    // -- свойсто тип --
}
