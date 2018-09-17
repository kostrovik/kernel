package com.github.kostrovik.kernel.common;

import com.github.kostrovik.kernel.builders.SceneFactory;
import com.github.kostrovik.kernel.dictionaries.ColorThemeDictionary;
import com.github.kostrovik.kernel.interfaces.EventListenerInterface;
import com.github.kostrovik.kernel.interfaces.Observable;
import com.github.kostrovik.kernel.models.ServerConnectionAddress;
import com.github.kostrovik.kernel.settings.Configurator;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * project: kernel
 * author:  kostrovik
 * date:    26/07/2018
 * github:  https://github.com/kostrovik/kernel
 */
public class ApplicationSettings implements Observable {
    private static Logger logger = Configurator.getConfig().getLogger(ApplicationSettings.class.getName());
    private static volatile ApplicationSettings settings;

    private static final String DEFAULT_HOST_PROPERTY = "defaultHost";
    private static final String HOSTS_PROPERTY = "hosts";

    private Path applicationConfigPath;
    private ConfigParser parser;
    private ServerConnectionAddress defaultHost;
    private List<EventListenerInterface> listeners;

    private ApplicationSettings() {
        setPath();
        this.parser = new ConfigParser(applicationConfigPath.toUri());
        this.listeners = new ArrayList<>();
    }

    public static ApplicationSettings getInstance() {
        if (settings == null) {
            synchronized (SceneFactory.class) {
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
        List<ServerConnectionAddress> addreses = new ArrayList<>();

        List<String> hosts = Arrays.asList(((String) config.getOrDefault(HOSTS_PROPERTY, "")).split(","));

        for (String host : hosts) {
            ServerConnectionAddress address = createAddress(host);

            if (!defaultAddress.isEmpty() && address.getUrl().equals(defaultAddress)) {
                address.setDefault(true);
            }

            if (!address.getUrl().isEmpty()) {
                addreses.add(address);
            }
        }

        return addreses;
    }

    public ServerConnectionAddress getDefaultHost() {
        if (defaultHost == null) {
            Optional<ServerConnectionAddress> host = getHosts().stream().filter(ServerConnectionAddress::isDefault).findFirst();
            host.ifPresent(serverConnectionAddress -> defaultHost = serverConnectionAddress);
        }

        if (defaultHost == null) {
            logger.log(Level.SEVERE, "Не найдено настроек для подключения к серверу.");
        }

        return defaultHost;
    }

    public void saveHostsList(List<ServerConnectionAddress> hosts) {
        Map<String, Object> config = parser.getConfig();
        config.remove(DEFAULT_HOST_PROPERTY);
        defaultHost = null;
        config.put(HOSTS_PROPERTY, String.join(",", hosts.stream().map(host -> {
            if (host.isDefault()) {
                config.put(DEFAULT_HOST_PROPERTY, host.getUrl());
                defaultHost = host;
            }
            return String.format("%s@%s", host.getUrl(), host.getLastUsage());
        }).collect(Collectors.toList())));

        writeSettings(config);
    }

    public void updateHostLastUsage() {
        Map<String, Object> config = parser.getConfig();
        config.put(HOSTS_PROPERTY, String.join(",", getHosts().stream().map(host -> {
            if (host.isDefault()) {
                host.setLastUsage(LocalDateTime.now());
                defaultHost = host;
            }
            return String.format("%s@%s", host.getUrl(), host.getLastUsage());
        }).collect(Collectors.toList())));

        writeSettings(config);
    }

    public String getDefaultColorTheme() {
        Object colorTheme = parser.getConfigProperty("colorTheme");
        return colorTheme != null ? (String) colorTheme : ColorThemeDictionary.LIGHT.getThemeName();
    }

    public void saveDefaultColorTheme(String theme) {
        Map<String, Object> config = parser.getConfig();
        config.put("colorTheme", theme);
        writeSettings(config);
    }

    public boolean showMemoryUsage() {
        return (boolean) Objects.requireNonNullElse(parser.getConfigProperty("showMemoryUsage"), false);
    }

    public void saveShowMemoryUsage(boolean isShow) {
        Map<String, Object> config = parser.getConfig();
        config.put("showMemoryUsage", isShow);
        writeSettings(config);
    }

    private ServerConnectionAddress createAddress(String host) {
        String[] hostParams = host.split("@");
        ServerConnectionAddress address = new ServerConnectionAddress(hostParams[0]);

        if (hostParams.length > 1 && !hostParams[1].equals("null")) {
            address.setLastUsage(LocalDateTime.parse(hostParams[1]));
        }

        return address;
    }

    private void writeSettings(Map<String, Object> config) {
        parser.writeSettings(config);
        notifyListeners();
    }

    private void setPath() {
        try {
            URI applicationDirectory = ApplicationSettings.class.getProtectionDomain().getCodeSource().getLocation().toURI();

            if (Paths.get(applicationDirectory).getParent().toString().equals("/")) {
                applicationDirectory = URI.create(System.getProperty("java.home"));
                applicationConfigPath = Paths.get(applicationDirectory.getPath());
            } else {
                applicationConfigPath = Paths.get(applicationDirectory.getPath()).getParent();
            }

            applicationConfigPath = Paths.get(applicationConfigPath + "/settings", "application.yaml");

            if (Files.notExists(applicationConfigPath.getParent())) {
                Files.createDirectory(applicationConfigPath.getParent());
                Files.createFile(applicationConfigPath);
            }
            if (Files.notExists(applicationConfigPath)) {
                Files.createFile(applicationConfigPath);
            }
        } catch (URISyntaxException e) {
            logger.log(Level.SEVERE, "Нет доступа к директории с настройками.", e);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Нет возможно создать директорию с настройками.", e);
        }
    }

    @Override
    public void addListener(EventListenerInterface listener) {
        listeners.add(listener);
    }

    @Override
    public void removeListener(EventListenerInterface listener) {
        listeners.remove(listener);
    }

    private void notifyListeners() {
        listeners.forEach(listener -> listener.handle(new EventObject(getInstance())));
    }
}
