package com.github.kostrovik.kernel.models;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * project: kernel
 * author:  kostrovik
 * date:    26/07/2018
 * github:  https://github.com/kostrovik/kernel
 */
public class ServerAddress {
    private ObjectProperty<URI> uri;
    private LocalDateTime lastUsage;
    private BooleanProperty defaultAddress;

    public ServerAddress(String url) {
        Objects.requireNonNull(url);
        this.uri = new SimpleObjectProperty<>(URI.create(url));
        this.defaultAddress = new SimpleBooleanProperty(false);
    }

    public URI getUri() {
        return uri.get();
    }

    public String getUriString() {
        return getUri().getPath();
    }

    public ObjectProperty<URI> uriProperty() {
        return uri;
    }

    public void setUri(URI uri) {
        this.uri.set(uri);
    }

    public LocalDateTime getLastUsage() {
        return lastUsage;
    }

    public void setLastUsage(LocalDateTime lastUsage) {
        this.lastUsage = lastUsage;
    }

    public boolean isDefaultAddress() {
        return defaultAddress.get();
    }

    public BooleanProperty defaultAddressProperty() {
        return defaultAddress;
    }

    public void setDefaultAddress(boolean defaultAddress) {
        this.defaultAddress.set(defaultAddress);
    }
}
