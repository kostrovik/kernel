package com.github.kostrovik.kernel.settings;

import com.github.kostrovik.kernel.interfaces.ApplicationLoggerInterface;
import com.github.kostrovik.kernel.interfaces.ModuleConfiguratorInterface;
import com.github.kostrovik.kernel.interfaces.controls.ControlBuilderFacadeInterface;
import com.github.kostrovik.kernel.interfaces.views.MenuBuilderInterface;
import com.github.kostrovik.kernel.interfaces.views.ViewEventListenerInterface;
import com.github.kostrovik.kernel.views.menu.MenuBuilder;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.ServiceLoader;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * project: glcmtx
 * author:  kostrovik
 * date:    23/07/2018
 * github:  https://github.com/kostrovik/glcmtx
 */
public final class Configurator implements ModuleConfiguratorInterface {
    private static volatile Configurator configurator;

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
        return new HashMap<>();
    }

    @Override
    public ViewEventListenerInterface getEventListener() {
        Optional<ViewEventListenerInterface> applicationSettings = getFirstLoadedImplementation(ViewEventListenerInterface.class);

        if (applicationSettings.isPresent()) {
            return applicationSettings.get();
        }
        getLogger(Configurator.class.getName()).log(Level.SEVERE, String.format("Не найден контейнер view приложения. Модуль: %s", this.getClass().getModule().getName()));

        return null;
    }

    @Override
    public ControlBuilderFacadeInterface getControlBuilder() {
        Optional<ControlBuilderFacadeInterface> controlBuilderFacade = getFirstLoadedImplementation(ControlBuilderFacadeInterface.class);

        if (controlBuilderFacade.isPresent()) {
            return controlBuilderFacade.get();
        }
        getLogger(Configurator.class.getName()).log(Level.SEVERE, String.format("Не найден фасад для построения элементов интерфейса. Модуль: %s", this.getClass().getModule().getName()));

        return null;
    }

    @Override
    public Logger getLogger(String className) {
        Optional<ApplicationLoggerInterface> applicationLogger = getFirstLoadedImplementation(ApplicationLoggerInterface.class);

        return applicationLogger.map(applicationLoggerInterface -> applicationLoggerInterface.getLogger(className)).orElse(Logger.getLogger(className));
    }

    private <E> Optional<E> getFirstLoadedImplementation(Class<E> type) {
        return ServiceLoader.load(ModuleLayer.boot(), type).findFirst();
    }
}
