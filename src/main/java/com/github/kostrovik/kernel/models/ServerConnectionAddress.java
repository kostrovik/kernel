package com.github.kostrovik.kernel.models;

import java.time.LocalDateTime;

/**
 * project: glcmtx
 * author:  kostrovik
 * date:    26/07/2018
 * github:  https://github.com/kostrovik/glcmtx
 */
public class ServerConnectionAddress {
    private String url;
    private LocalDateTime lastUsage;
    private Boolean isDefault;

    public ServerConnectionAddress(String url) {
        this.url = url;
        this.isDefault = false;
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

    public Boolean isDefault() {
        return isDefault;
    }

    public void setDefault(boolean isDefault) {
        this.isDefault = isDefault;
    }
}
