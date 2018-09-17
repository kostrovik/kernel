package com.github.kostrovik.kernel.models;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

import java.time.LocalDateTime;

/**
 * project: kernel
 * author:  kostrovik
 * date:    26/07/2018
 * github:  https://github.com/kostrovik/kernel
 */
public class ServerConnectionAddress {
    private String url;
    private LocalDateTime lastUsage;
    private BooleanProperty isDefault;

    public ServerConnectionAddress(String url) {
        this.url = url;
        this.isDefault = new SimpleBooleanProperty(false);
    }

    public String getUrl() {
        return url;
    }

    public LocalDateTime getLastUsage() {
        return lastUsage;
    }

    public void setLastUsage(LocalDateTime lastUsage) {
        this.lastUsage = lastUsage;
    }

    public boolean isDefault() {
        return isDefault.get();
    }

    public BooleanProperty defaultProperty() {
        return isDefault;
    }

    public void setDefault(boolean isDefault) {
        this.isDefault.set(isDefault);
    }
}
