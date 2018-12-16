package com.github.kostrovik.kernel.common;

import com.github.kostrovik.kernel.builders.SceneBuilder;
import com.github.kostrovik.kernel.dictionaries.ColorThemeDictionary;
import com.github.kostrovik.kernel.models.ServerConnectionAddress;
import com.github.kostrovik.useful.models.AbstractObservable;
import com.github.kostrovik.useful.utils.FileSystemUtil;
import com.github.kostrovik.useful.utils.InstanceLocatorUtil;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * project: kernel
 * author:  kostrovik
 * date:    26/07/2018
 * github:  https://github.com/kostrovik/kernel
 */
public class ApplicationSettings extends AbstractObservable {
    private static Logger logger = InstanceLocatorUtil.getLocator().getLogger(ApplicationSettings.class.getName());
    private static volatile ApplicationSettings settings;

    private static final String DEFAULT_HOST_PROPERTY = "defaultHost";
    private static final String HOSTS_PROPERTY = "hosts";
    private static final String VERSION_PROPERTY = "version";
    private static final String COLOR_THEME_PROPERTY = "colorTheme";
    private static final String SHOW_MEMORY_USAGE_PROPERTY = "showMemoryUsage";
    private static final String HOST_LAST_USAGE_PROPERTY = "lastUsage";

    private Path applicationConfigPath;
    private ConfigParser parser;
    private ServerConnectionAddress defaultHost;

    private FileSystemUtil fsUtil;

    private ApplicationSettings() {
        this.defaultHost = new ServerConnectionAddress("");
        this.fsUtil = new FileSystemUtil();
        getConfigPath();
        this.parser = new ConfigParser(applicationConfigPath);
    }

    public static ApplicationSettings getInstance() {
        if (settings == null) {
            synchronized (SceneBuilder.class) {
                if (settings == null) {
                    settings = new ApplicationSettings();
                }
            }
        }
        return settings;
    }

    public List<ServerConnectionAddress> getHosts() {
        Map<String, Object> config = parser.getConfig();
        String defaultAddress = (String) config.getOrDefault(DEFAULT_HOST_PROPERTY, "");
        List<ServerConnectionAddress> addresses = new ArrayList<>();


        List<Map> savedHosts = (List<Map>) config.getOrDefault(HOSTS_PROPERTY, new ArrayList<>());
        savedHosts.forEach(savedHost -> {
            Map<String, String> host = savedHost;
            String url = host.getOrDefault("url", null);
            String lastUsage = host.getOrDefault(HOST_LAST_USAGE_PROPERTY, null);

            if (Objects.nonNull(url) && !url.trim().isEmpty()) {
                ServerConnectionAddress address = new ServerConnectionAddress(host.get("url"));
                if (Objects.nonNull(lastUsage) && !lastUsage.equalsIgnoreCase("null") && !lastUsage.trim().isEmpty()) {
                    address.setLastUsage(LocalDateTime.parse(host.get(HOST_LAST_USAGE_PROPERTY)));
                }

                if (!defaultAddress.trim().isEmpty() && address.getUrl().equals(defaultAddress)) {
                    address.setDefault(true);
                }
                addresses.add(address);
            }
        });

        return addresses;
    }

    public ServerConnectionAddress getDefaultHost() {
        if (Objects.isNull(defaultHost) || Objects.isNull(defaultHost.getUrl()) || defaultHost.getUrl().isEmpty()) {
            Optional<ServerConnectionAddress> host = getHosts().stream().filter(ServerConnectionAddress::isDefault).findFirst();
            host.ifPresentOrElse(serverConnectionAddress -> defaultHost = serverConnectionAddress, () -> logger.log(Level.SEVERE, "Не найдено настроек для подключения к серверу."));
        }

        return defaultHost;
    }

    public String getVersion() {
        Object version = parser.getConfigProperty(VERSION_PROPERTY);
        return Objects.nonNull(version) ? (String) version : "";
    }

    public void saveVersion(String version) {
        Map<String, Object> config = parser.getConfig();
        config.put(VERSION_PROPERTY, version);
        writeSettings(config);
    }

    public void saveHostsList(List<ServerConnectionAddress> hosts) {
        Map<String, Object> config = parser.getConfig();
        config.remove(DEFAULT_HOST_PROPERTY);
        defaultHost = null;

        List<Map<String, String>> preparedHosts = new ArrayList<>();
        hosts.forEach(host -> {
            if (host.isDefault()) {
                config.put(DEFAULT_HOST_PROPERTY, host.getUrl());
                defaultHost = host;
            }
            preparedHosts.add(prepareHostForSave(host));
        });
        config.put(HOSTS_PROPERTY, preparedHosts);
        writeSettings(config);
    }

    public void updateHostLastUsage() {
        Map<String, Object> config = parser.getConfig();

        List<Map<String, String>> preparedHosts = new ArrayList<>();
        getHosts().forEach(host -> {
            if (host.isDefault()) {
                host.setLastUsage(LocalDateTime.now());
                defaultHost = host;
            }

            preparedHosts.add(prepareHostForSave(host));
        });
        config.put(HOSTS_PROPERTY, preparedHosts);
        writeSettings(config);
    }

    public String getDefaultColorTheme() {
        Object colorTheme = parser.getConfigProperty(COLOR_THEME_PROPERTY);
        return Objects.nonNull(colorTheme) ? (String) colorTheme : ColorThemeDictionary.LIGHT.getThemeName();
    }

    public void saveDefaultColorTheme(String theme) {
        Map<String, Object> config = parser.getConfig();
        config.put(COLOR_THEME_PROPERTY, theme);
        writeSettings(config);
    }

    public boolean showMemoryUsage() {
        return (boolean) Objects.requireNonNullElse(parser.getConfigProperty(SHOW_MEMORY_USAGE_PROPERTY), false);
    }

    public void saveShowMemoryUsage(boolean isShow) {
        Map<String, Object> config = parser.getConfig();
        config.put(SHOW_MEMORY_USAGE_PROPERTY, isShow);
        writeSettings(config);
    }

    private Map<String, String> prepareHostForSave(ServerConnectionAddress host) {
        Map<String, String> preparedHost = new HashMap<>();
        if (Objects.nonNull(host.getUrl())) {
            preparedHost.put("url", host.getUrl());
        }
        if (Objects.nonNull(host.getLastUsage())) {
            preparedHost.put(HOST_LAST_USAGE_PROPERTY, host.getLastUsage().toString());
        }

        return preparedHost;
    }

    private void writeSettings(Map<String, Object> config) {
        parser.writeSettings(config);
        notifyLlisteners(getInstance());
    }

    private void getConfigPath() {
        try {
            Path applicationDirectory = fsUtil.getCurrentDirectory(ApplicationSettings.class);
            applicationConfigPath = Paths.get(applicationDirectory.toString(), "settings", "application.yaml");

            if (Files.notExists(applicationConfigPath.getParent())) {
                Files.createDirectories(applicationConfigPath.getParent());
            }
            if (Files.notExists(applicationConfigPath)) {
                Files.createFile(applicationConfigPath);
            }
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Нет возможно создать директорию с настройками.", e);
        }
    }
}
