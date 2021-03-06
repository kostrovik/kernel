package com.github.kostrovik.kernel.settings;

import com.github.kostrovik.kernel.exceptions.ParseException;
import com.github.kostrovik.useful.utils.InstanceLocatorUtil;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * project: kernel
 * author:  kostrovik
 * date:    20/07/2018
 * github:  https://github.com/kostrovik/kernel
 */
public final class SolidIconsSettings {
    private static Logger logger = InstanceLocatorUtil.getLocator().getLogger(SolidIconsSettings.class);

    private static volatile SolidIconsSettings settings;

    private Properties config;
    private static final String DEFAULT_CONFIG_FILE_PATH = "/com/github/kostrovik/icons/control_icons.properties";
    private static final String ICONS_FONT_PATH = "icons.font.path";
    private static final String ICONS_LIGHT_FONT_PATH = "icons.font.path.light";
    private static final String ICONS_SOLID_FONT_PATH = "icons.font.path.solid";
    private static final String ICONS_REGULAR_FONT_PATH = "icons.font.path.regular";
    private static final String ICONS_BRAND_FONT_PATH = "icons.font.path.brand";

    private SolidIconsSettings() {
        config = getDefaultConfig();
    }

    public static synchronized SolidIconsSettings getInstance() {
        if (settings == null) {
            settings = new SolidIconsSettings();
        }
        return settings;
    }

    private Properties getDefaultConfig() {
        Properties result = new Properties();
        try (InputStream inputStream = this.getClass().getResourceAsStream(DEFAULT_CONFIG_FILE_PATH)) {
            if (inputStream != null) {
                result.load(inputStream);

                result.setProperty(ICONS_FONT_PATH, preparePathForDefaultResource(result.getProperty(ICONS_FONT_PATH)));
                result.setProperty(ICONS_LIGHT_FONT_PATH, preparePathForDefaultResource(result.getProperty(ICONS_LIGHT_FONT_PATH)));
                result.setProperty(ICONS_SOLID_FONT_PATH, preparePathForDefaultResource(result.getProperty(ICONS_SOLID_FONT_PATH)));
                result.setProperty(ICONS_REGULAR_FONT_PATH, preparePathForDefaultResource(result.getProperty(ICONS_REGULAR_FONT_PATH)));
                result.setProperty(ICONS_BRAND_FONT_PATH, preparePathForDefaultResource(result.getProperty(ICONS_BRAND_FONT_PATH)));
            }
            return result;
        } catch (FileNotFoundException error) {
            logger.log(Level.SEVERE, "Не найден файл конфигурации по умолчанию", error);
            throw new ParseException(error);
        } catch (IOException error) {
            logger.log(Level.SEVERE, "Не возможно загрузить настройки умолчанию", error);
            throw new ParseException(error);
        }
    }

    private String preparePathForDefaultResource(String path) throws FileNotFoundException {
        URL resource = this.getClass().getResource(path);

        if (resource == null) {
            throw new FileNotFoundException("resources path: " + path);
        }

        return resource.toExternalForm();
    }

    public String getFontPath() {
        return (String) config.getOrDefault(ICONS_FONT_PATH, "");
    }

    public String getBrandFontPath() {
        return (String) config.getOrDefault(ICONS_BRAND_FONT_PATH, "");
    }

    public String getLightFontPath() {
        return (String) config.getOrDefault(ICONS_LIGHT_FONT_PATH, "");
    }

    public String getRegularFontPath() {
        return (String) config.getOrDefault(ICONS_REGULAR_FONT_PATH, "");
    }

    public String getSolidFontPath() {
        return (String) config.getOrDefault(ICONS_SOLID_FONT_PATH, "");
    }

    public double getDefaultIconsFontSize() {
        return Double.parseDouble((String) config.getOrDefault("icons.font.size", 10));
    }
}
