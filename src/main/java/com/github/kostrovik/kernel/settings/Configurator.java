package com.github.kostrovik.kernel.settings;

import com.github.kostrovik.kernel.dictionaries.ViewTypeDictionary;
import com.github.kostrovik.kernel.interfaces.ModuleConfiguratorInterface;
import com.github.kostrovik.kernel.interfaces.views.MenuBuilderInterface;
import com.github.kostrovik.kernel.interfaces.views.ViewEventListenerInterface;
import com.github.kostrovik.kernel.views.DropDownDialog;
import com.github.kostrovik.kernel.views.menu.MenuBuilder;
import com.github.kostrovik.useful.utils.InstanceLocatorUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.ServiceLoader;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * project: kernel
 * author:  kostrovik
 * date:    23/07/2018
 * github:  https://github.com/kostrovik/kernel
 */
public final class Configurator implements ModuleConfiguratorInterface {
    private static Logger logger = InstanceLocatorUtil.getLocator().getLogger(Configurator.class.getName());
    private static volatile Configurator configurator;
    private static Map<String, Class<?>> views = new HashMap<>();

    private Configurator() {
    }

    public static Configurator provider() {
        return getConfig();
    }

    public static Configurator getConfig() {
        if (configurator == null) {
            synchronized (Configurator.class) {
                if (configurator == null) {
                    configurator = new Configurator();
                }
            }
        }
        return configurator;
    }

    @Override
    public MenuBuilderInterface getMenuBuilder() {
        return new MenuBuilder();
    }

    @Override
    public Map<String, Class<?>> getModuleViews() {
        if (views.isEmpty()) {
            synchronized (Configurator.class) {
                if (views.isEmpty()) {
                    views.put(ViewTypeDictionary.DROPDOWN_DIALOG.name(), DropDownDialog.class);
                }
            }
        }

        return views;
    }

    @Override
    public ViewEventListenerInterface getEventListener() {
        Optional<ViewEventListenerInterface> applicationSettings = getFirstLoadedImplementation(ViewEventListenerInterface.class);

        if (applicationSettings.isPresent()) {
            return applicationSettings.get();
        }
        logger.log(Level.SEVERE, String.format("Не найден контейнер view приложения. Модуль: %s", this.getClass().getModule().getName()));

        return null;
    }

    private <E> Optional<E> getFirstLoadedImplementation(Class<E> type) {
        return ServiceLoader.load(ModuleLayer.boot(), type).findFirst();
    }
}
