package com.github.kostrovik.kernel.common;

import com.github.kostrovik.kernel.dictionaries.ColorThemeDictionary;
import com.github.kostrovik.kernel.exceptions.FileSystemException;
import com.github.kostrovik.kernel.models.ServerAddress;
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
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * project: kernel
 * author:  kostrovik
 * date:    26/07/2018
 * github:  https://github.com/kostrovik/kernel
 */
public class ApplicationSettings extends AbstractObservable {
    private static Logger logger = InstanceLocatorUtil.getLocator().getLogger(ApplicationSettings.class);
    private static volatile ApplicationSettings settings;

    private static final String DEFAULT_HOST_PROPERTY = "defaultHost";
    private static final String HOSTS_PROPERTY = "hosts";
    private static final String VERSION_PROPERTY = "version";
    private static final String COLOR_THEME_PROPERTY = "colorTheme";
    private static final String SHOW_MEMORY_USAGE_PROPERTY = "showMemoryUsage";
    private static final String HOST_LAST_USAGE_PROPERTY = "lastUsage";

    private ConfigParser parser;
    private FileSystemUtil fsUtil;
    private ServerAddress defaultHost;
    private Path configFile;

    private ApplicationSettings() {
        this.defaultHost = new ServerAddress("");
        this.fsUtil = new FileSystemUtil();
        this.configFile = getConfigPath();
        this.parser = new ConfigParser(configFile);
    }

    public static synchronized ApplicationSettings getInstance() {
        if (settings == null) {
            settings = new ApplicationSettings();
        }
        return settings;
    }

    public List<ServerAddress> getHosts() {
        Object defaultAddress = parser.getConfigProperty(DEFAULT_HOST_PROPERTY);
        List<ServerAddress> addresses = new ArrayList<>();

        Object savedHosts = parser.getConfigProperty(HOSTS_PROPERTY);
        if (Objects.nonNull(savedHosts)) {
            List<Map> hosts = (List<Map>) savedHosts;

            addresses = hosts.stream()
                    .map(map -> prepareAddress(map, defaultAddress))
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
        }

        return addresses;
    }

    private ServerAddress prepareAddress(Map map, Object defaultAddress) {
        String url = (String) map.getOrDefault("url", null);
        String lastUsage = (String) map.getOrDefault(HOST_LAST_USAGE_PROPERTY, null);

        if (Objects.nonNull(url) && !url.trim().isEmpty()) {
            ServerAddress address = new ServerAddress(url);
            if (Objects.nonNull(lastUsage) && !lastUsage.equalsIgnoreCase("null") && !lastUsage.trim().isEmpty()) {
                address.setLastUsage(LocalDateTime.parse(lastUsage));
            }
            checkDefaultAddress(defaultAddress, address);
            return address;
        }

        return null;
    }

    private void checkDefaultAddress(Object defaultAddress, ServerAddress address) {
        if (defaultAddress instanceof ServerAddress
                && !((ServerAddress) defaultAddress).getUriString().isBlank()
                && ((ServerAddress) defaultAddress).getUriString().equals(address.getUriString())) {
            address.setDefaultAddress(true);
        }
        if (defaultAddress instanceof String && !((String) defaultAddress).isBlank() && address.getUriString().equals(defaultAddress)) {
            address.setDefaultAddress(true);
        }
    }

    public ServerAddress getDefaultHost() {
        if (Objects.isNull(defaultHost) || Objects.isNull(defaultHost.getUri()) || defaultHost.getUriString().isBlank()) {
            ServerAddress host = getHosts()
                    .stream()
                    .filter(ServerAddress::isDefaultAddress)
                    .findFirst()
                    .orElse(null);

            if (Objects.isNull(host)) {
                logger.log(Level.SEVERE, "Не найдено настроек для подключения к серверу.");
            } else {
                defaultHost = host;
            }
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

    public void saveHostsList(List<ServerAddress> hosts) {
        Map<String, Object> config = parser.getConfig();
        config.remove(DEFAULT_HOST_PROPERTY);
        defaultHost = null;

        List<Map<String, String>> preparedHosts = prepareHostsForSave(hosts, false);

        hosts.stream()
                .filter(ServerAddress::isDefaultAddress)
                .findFirst()
                .ifPresent(host -> {
                    config.put(DEFAULT_HOST_PROPERTY, host);
                    defaultHost = host;
                });

        config.put(HOSTS_PROPERTY, preparedHosts);
        writeSettings(config);
    }

    public void updateHostLastUsage() {
        Map<String, Object> config = parser.getConfig();
        List<Map<String, String>> preparedHosts = prepareHostsForSave(getHosts(), true);
        config.put(HOSTS_PROPERTY, preparedHosts);
        writeSettings(config);
    }

    private List<Map<String, String>> prepareHostsForSave(List<ServerAddress> hosts, boolean updateLastUsage) {
        return hosts
                .stream()
                .map(serverConnectionAddress -> {
                    if (serverConnectionAddress.isDefaultAddress()) {
                        if (updateLastUsage) {
                            serverConnectionAddress.setLastUsage(LocalDateTime.now());
                        }
                        defaultHost = serverConnectionAddress;
                    }

                    return prepareHostForSave(serverConnectionAddress);
                })
                .collect(Collectors.toList());
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

    private Map<String, String> prepareHostForSave(ServerAddress host) {
        Map<String, String> preparedHost = new HashMap<>();
        preparedHost.put("url", host.getUriString());
        if (Objects.nonNull(host.getLastUsage())) {
            preparedHost.put(HOST_LAST_USAGE_PROPERTY, host.getLastUsage().toString());
        }

        return preparedHost;
    }

    private void writeSettings(Map<String, Object> config) {
        parser.writeSettings(config, configFile);
        notifyListeners(getInstance());
    }

    private Path getConfigPath() {
        try {
            Path applicationDirectory = fsUtil.getCurrentDirectory(ApplicationSettings.class);
            applicationDirectory = Paths.get(applicationDirectory.toString(), "settings", "application.yaml");

            if (Files.notExists(applicationDirectory.getParent())) {
                Files.createDirectories(applicationDirectory.getParent());
            }
            if (Files.notExists(applicationDirectory)) {
                Files.createFile(applicationDirectory);
            }
            return applicationDirectory;
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Нет возможно создать директорию с настройками.", e);
            throw new FileSystemException(e);
        }
    }
}
