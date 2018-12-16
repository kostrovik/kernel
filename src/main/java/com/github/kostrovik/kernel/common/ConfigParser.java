package com.github.kostrovik.kernel.common;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.github.kostrovik.kernel.exceptions.ParseException;
import com.github.kostrovik.useful.utils.InstanceLocatorUtil;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * project: kernel
 * author:  kostrovik
 * date:    08/08/2018
 * github:  https://github.com/kostrovik/kernel
 */
public class ConfigParser {
    private Logger logger = InstanceLocatorUtil.getLocator().getLogger(ConfigParser.class);

    private Path filePath;
    private Map<String, Object> config;
    private ObjectMapper mapper;

    public ConfigParser(Path filePath) {
        this.filePath = filePath;
        this.mapper = new ObjectMapper(new YAMLFactory());
        this.config = parseConfig();
    }

    public Map<String, Object> getConfig() {
        return new HashMap<>(config);
    }

    public Object getConfigProperty(String property) {
        return findProperty(property, config);
    }

    public void writeSettings(Map<String, Object> config) {
        try {
            mapper.writeValue(filePath.toFile(), config);
            this.config = parseConfig();
        } catch (IOException error) {
            logger.log(Level.SEVERE, "Ошибка записи конфигурации", error);
            throw new ParseException(error);
        }
    }

    private Map<String, Object> parseConfig() {
        try {
            return mapper.readValue(filePath.toFile(), Map.class);
        } catch (MismatchedInputException error) {
            logger.log(Level.WARNING, "Пустой файл конфигурации", error);
            return new HashMap<>();
        } catch (IOException error) {
            logger.log(Level.SEVERE, "Ошибка чтения конфигурации", error);
            throw new ParseException(error);
        }
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