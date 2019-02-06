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
import java.util.Objects;
import java.util.ServiceLoader;
import java.util.concurrent.ConcurrentHashMap;
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
    private Map<String, Class<?>> views;

    private Configurator() {
        this.views = prepareViews();
    }

    public static Configurator provider() {
        return getConfig();
    }

    public static synchronized Configurator getConfig() {
        if (Objects.isNull(configurator)) {
            configurator = new Configurator();
        }
        return configurator;
    }

    @Override
    public MenuBuilderInterface getMenuBuilder() {
        return new MenuBuilder();
    }

    @Override
    public Map<String, Class<?>> getModuleViews() {
        return new ConcurrentHashMap<>(views);
    }

    @Override
    public ViewEventListenerInterface getEventListener() {
        ViewEventListenerInterface settings = ServiceLoader.load(ModuleLayer.boot(), ViewEventListenerInterface.class).findFirst().orElse(null);

        if (Objects.isNull(settings)) {
            logger.severe(String.format("Не найден контейнер view приложения. Модуль: %s", this.getClass().getModule().getName()));
        }

        return settings;
    }

    @Override
    public int getModuleOrder() {
        return 0;
    }

    private Map<String, Class<?>> prepareViews() {
        Map<String, Class<?>> viewsMap = new HashMap<>();
        viewsMap.put(ViewTypeDictionary.DROPDOWN_DIALOG.name(), DropDownDialog.class);

        return viewsMap;
    }
}
