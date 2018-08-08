package com.github.kostrovik.kernel.views.menu;

import com.github.kostrovik.kernel.common.ConfigParser;
import com.github.kostrovik.kernel.interfaces.views.MenuBuilderInterface;
import com.github.kostrovik.kernel.settings.Configurator;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.MenuItem;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * project: glcmtx
 * author:  kostrovik
 * date:    23/07/2018
 * github:  https://github.com/kostrovik/glcmtx
 */
public class MenuBuilder implements MenuBuilderInterface {
    private static Logger logger = Configurator.getConfig().getLogger(MenuBuilder.class.getName());
    private ConfigParser parser;

    public MenuBuilder() {
        this.parser = new ConfigParser(getPath());
    }

    @Override
    public List<MenuItem> getMenuList() {
        Object menuList = parser.getConfigProperty("items");
        List<MenuItem> menuItems = new ArrayList<>();

        if (menuList != null && menuList instanceof Map) {
            for (Object menuKey : ((Map) menuList).keySet()) {
                Map menuObject = (Map) ((Map) menuList).get(menuKey);
                MenuItem item = new MenuItem((String) menuObject.get("title"));
                item.setOnAction(prepareAction((String) menuObject.get("action")));
                menuItems.add(item);
            }
        }

        return menuItems;
    }

    @Override
    public String getModuleMenuName() {
        return "Основное";
    }

    private EventHandler<ActionEvent> prepareAction(String actionClassName) {
        EventHandler<ActionEvent> action = null;
        Class<?> actionClass;
        try {
            actionClass = Class.forName(actionClassName);
            Constructor<?> constructor = actionClass.getDeclaredConstructor();
            action = (EventHandler<ActionEvent>) constructor.newInstance();
        } catch (ClassNotFoundException e) {
            logger.log(Level.SEVERE, String.format("Для пункта меню не найден класс action %s.", actionClassName), e);
        } catch (NoSuchMethodException e) {
            logger.log(Level.SEVERE, "Не задан конструктор для action с необходимымой сигнатурой getDeclaredConstructor().", e);
        } catch (IllegalAccessException e) {
            logger.log(Level.SEVERE, "Конструктор для action не доступен.", e);
        } catch (InstantiationException | InvocationTargetException e) {
            logger.log(Level.SEVERE, String.format("Не возможно создать объект action %s.", actionClassName), e);
        }

        return action;
    }

    private URI getPath() {
        URI applicationConfigPath = null;
        try {
            URI applicationDirectory = MenuBuilder.class.getProtectionDomain().getCodeSource().getLocation().toURI();
            if (Paths.get(applicationDirectory).getParent().toString().equals("/")) {
                applicationDirectory = URI.create(System.getProperty("java.home"));
            }

            applicationConfigPath = new URI(applicationDirectory + "configurations/menu_config.yaml");
        } catch (URISyntaxException e) {
            logger.log(Level.SEVERE, "Нет доступа к директории с настройками.", e);
        }

        return applicationConfigPath;
    }
}
