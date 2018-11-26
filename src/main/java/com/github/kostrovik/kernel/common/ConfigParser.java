package com.github.kostrovik.kernel.common;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.github.kostrovik.useful.utils.InstanceLocatorUtil;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * project: kernel
 * author:  kostrovik
 * date:    08/08/2018
 * github:  https://github.com/kostrovik/kernel
 */
public class ConfigParser {
    private static Logger logger = InstanceLocatorUtil.getLocator().getLogger(ConfigParser.class.getName());

    private Path filePath;
    private Map<String, Object> config;

    public ConfigParser(Path filePath) {
        this.filePath = filePath;
        this.config = parseConfig();
    }

    public Map<String, Object> getConfig() {
        return config;
    }

    public Object getConfigProperty(String property) {
        return findProperty(property, config);
    }

    public void writeSettings(Map<String, Object> config) {
        try {
            ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
            mapper.writeValue(new File(filePath.toString()), config);
            this.config = parseConfig();
        } catch (IOException error) {
            logger.log(Level.SEVERE, "Ошибка записи конфигурации", error);
        }
    }

    private Map<String, Object> parseConfig() {
        ConcurrentHashMap result = new ConcurrentHashMap<>();

        try {
            ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
            result = mapper.readValue(new File(filePath.toString()), ConcurrentHashMap.class);
        } catch (MismatchedInputException error) {
            logger.log(Level.WARNING, "Пустой файл конфигурации", error);
        } catch (IOException error) {
            logger.log(Level.WARNING, "Ошибка чтения конфигурации", error);
        }

        return result;
    }

    private Object findProperty(String property, Map properties) {
        if (Objects.isNull(property) || properties.isEmpty()) {
            logger.log(Level.WARNING, "Не найден ключ, либо конфигурация не задана: {0}", String.format("ключ = %s, конфигурация пуста = %b", property, properties.isEmpty()));
            return null;
        }

        if (properties.containsKey(property)) {
            return properties.get(property);
        }

        for (Object value : properties.values()) {
            if (value instanceof Map) {
                return findProperty(property, (Map) value);
            }
        }

        logger.log(Level.WARNING, "Не найден ключ конфигурации: {0}", property);

        return null;
    }
}