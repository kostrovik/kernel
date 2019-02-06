package com.github.kostrovik.kernel.common;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.github.kostrovik.kernel.exceptions.ParseException;
import com.github.kostrovik.useful.utils.InstanceLocatorUtil;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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

    private Map<String, Object> config;
    private ObjectMapper mapper;
    private Object lock = new Object();

    public ConfigParser(Path filePath) {
        this.mapper = new ObjectMapper(new YAMLFactory());
        this.config = parseConfig(filePath);
    }

    public ConfigParser(InputStream stream) {
        this.mapper = new ObjectMapper(new YAMLFactory());
        this.config = parseConfig(stream);
    }

    public Map<String, Object> getConfig() {
        synchronized (lock) {
            return new HashMap<>(config);
        }
    }

    public Object getConfigProperty(String property) {
        synchronized (lock) {
            return findProperty(property, config);
        }
    }

    public void writeSettings(Map<String, Object> config, Path path) {
        synchronized (lock) {
            try {
                mapper.writeValue(path.toFile(), config);
                this.config = config;
            } catch (IOException error) {
                logger.log(Level.SEVERE, "Ошибка записи конфигурации", error);
                throw new ParseException(error);
            }
        }
    }

    public void writeSettings(Map<String, Object> config, OutputStream stream) {
        synchronized (lock) {
            try {
                mapper.writeValue(stream, config);
                this.config = config;
            } catch (IOException error) {
                logger.log(Level.SEVERE, "Ошибка записи конфигурации", error);
                throw new ParseException(error);
            }
        }
    }

    private Map<String, Object> parseConfig(Path path) {
        try {
            return mapper.readValue(path.toFile(), new TypeReference<Map>() {
            });
        } catch (MismatchedInputException error) {
            logger.log(Level.WARNING, "Пустой файл конфигурации", error);
            return new HashMap<>();
        } catch (IOException error) {
            logger.log(Level.SEVERE, "Ошибка чтения конфигурации", error);
            throw new ParseException(error);
        }
    }

    private Map<String, Object> parseConfig(InputStream stream) {
        try {
            return mapper.readValue(stream, new TypeReference<Map>() {
            });
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