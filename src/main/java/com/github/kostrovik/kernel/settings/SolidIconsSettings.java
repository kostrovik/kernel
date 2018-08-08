package com.github.kostrovik.kernel.settings;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * project: glcmtx
 * author:  kostrovik
 * date:    20/07/2018
 * github:  https://github.com/kostrovik/glcmtx
 */
final public class SolidIconsSettings {
    private static Logger logger = Configurator.getConfig().getLogger(SolidIconsSettings.class.getName());

    private static volatile SolidIconsSettings settings;
    private static Properties config;
    private final static String defaultConfigFilePath = "/icons/control_icons.properties";

    private SolidIconsSettings() {
        config = getDefaultConfig();
    }

    public static SolidIconsSettings getInstance() {
        if (settings == null) {
            synchronized (SolidIconsSettings.class) {
                if (settings == null) {
                    settings = new SolidIconsSettings();
                }
            }
        }
        return settings;
    }

    private Properties getDefaultConfig() {
        Properties result = new Properties();

        try (InputStream inputStream = Class.forName(SolidIconsSettings.class.getName()).getResourceAsStream(defaultConfigFilePath)) {
            if (inputStream != null) {
                result.load(inputStream);

                result.setProperty("icons.font.path", preparePathForDefaultResource(result.getProperty("icons.font.path")));
            }

        } catch (FileNotFoundException error) {
            logger.log(Level.SEVERE, "Не найден файл конфигурации по умолчанию", error);
        } catch (IOException error) {
            logger.log(Level.SEVERE, "Не возможно загрузить настройки умолчанию", error);
        } catch (ClassNotFoundException error) {
            logger.log(Level.SEVERE, "Не возможно найти класс", error);
        }

        return result;
    }

    private String preparePathForDefaultResource(String path) throws FileNotFoundException, ClassNotFoundException {
        URL resource = Class.forName(SolidIconsSettings.class.getName()).getResource(path);

        if (resource == null) {
            throw new FileNotFoundException("resources path: " + path);
        }

        return resource.toExternalForm();
    }

    public String getFontPath() {
        return (String) config.getOrDefault("icons.font.path", "");
    }

    public double getDefaultIconsFontSize() {
        return Double.parseDouble((String) config.getOrDefault("icons.font.size", 10));
    }
}
