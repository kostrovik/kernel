package com.github.kostrovik.kernel.builders;

import com.github.kostrovik.kernel.settings.Configurator;
import com.github.kostrovik.kernel.interfaces.ModuleConfiguratorInterface;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

/**
 * project: kernel
 * author:  kostrovik
 * date:    23/07/2018
 * github:  https://github.com/kostrovik/kernel
 */
public class ModulesConfigBuilder {
    private static Logger logger = Configurator.getConfig().getLogger(ModulesConfigBuilder.class.getName());

    private static volatile ModulesConfigBuilder builder;
    private static Map<String, ModuleConfiguratorInterface> modulesConfig;

    private ModulesConfigBuilder() {
        modulesConfig = prepareConfig();
    }

    public static ModulesConfigBuilder getInstance() {
        if (builder == null) {
            synchronized (SceneFactory.class) {
                if (builder == null) {
                    builder = new ModulesConfigBuilder();
                }
            }
        }
        return builder;
    }

    public ModuleConfiguratorInterface getConfigForModule(String moduleName) {
        return modulesConfig.getOrDefault(moduleName, null);
    }

    public List<String> moduleKeys() {
        return new ArrayList<>(modulesConfig.keySet());
    }

    private Map<String, ModuleConfiguratorInterface> prepareConfig() {
        Map<String, ModuleConfiguratorInterface> config = new ConcurrentHashMap<>();

        ServiceLoader<ModuleConfiguratorInterface> serviceLoader = ServiceLoader.load(ModuleLayer.boot(), ModuleConfiguratorInterface.class);
        Iterator<ModuleConfiguratorInterface> iterator = serviceLoader.iterator();
        while (iterator.hasNext()) {
            ModuleConfiguratorInterface item = iterator.next();
            config.put(item.getClass().getModule().getName(), item);
        }

        return config;
    }
}
