package com.github.kostrovik.kernel.graphics.controls.notification;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;

/**
 * project: kernel
 * author:  kostrovik
 * date:    21/07/2018
 * github:  https://github.com/kostrovik/kernel
 */
public class Notification extends Control {
    private StringProperty message;
    private ObjectProperty<NotificationType> type;

    public Notification() {
        this.message = new SimpleStringProperty("");
        this.type = new SimpleObjectProperty<>(NotificationType.INFO);
        setFocusTraversable(false);
        setVisible(false);
    }

    public String getMessage() {
        return message.get();
    }

    public StringProperty messageProperty() {
        return message;
    }

    public void setMessage(String message) {
        this.message.set(message);
    }

    public NotificationType getType() {
        return type.get();
    }

    public ObjectProperty<NotificationType> typeProperty() {
        return type;
    }

    public void setType(NotificationType type) {
        this.type.set(type);
    }

    @Override
    protected Skin<?> createDefaultSkin() {
        return new NotificationSkin(this);
    }
}
