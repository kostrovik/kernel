package com.github.kostrovik.kernel.builders;

import com.github.kostrovik.kernel.interfaces.ModuleConfiguratorInterface;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.ServiceLoader;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;

/**
 * project: kernel
 * author:  kostrovik
 * date:    23/07/2018
 * github:  https://github.com/kostrovik/kernel
 * <p>
 * Конструктор общей конфигурации подключенных модулей. Собирает представленные, в пространстве модулей, реализации
 * интерфейса ModuleConfiguratorInterface и формирует из них общую карту. Используется в конструкторе отображений.
 * Реализует один из вариантов потокобезопасного Singleton.
 */
public class ModulesConfigBuilder {
    private static volatile ModulesConfigBuilder builder;
    /**
     * Общая конфигурация подключенных модулей.
     */
    private Map<String, ModuleConfiguratorInterface> modulesConfig;

    /**
     * Constructor for Modules config builder.
     */
    private ModulesConfigBuilder() {
        this.modulesConfig = prepareConfig();
    }

    /**
     * Gets instance.
     *
     * @return the instance
     */
    public static synchronized ModulesConfigBuilder getInstance() {
        if (Objects.isNull(builder)) {
            builder = new ModulesConfigBuilder();
        }
        return builder;
    }

    /**
     * Gets config for module.
     *
     * @param moduleName the module name
     *
     * @return the config for module
     */
    public ModuleConfiguratorInterface getConfigForModule(String moduleName) {
        return modulesConfig.getOrDefault(moduleName, null);
    }

    /**
     * Module keys list.
     *
     * @return the list
     */
    public List<String> moduleKeys() {
        return new ArrayList<>(modulesConfig.keySet());
    }

    /**
     * Gets modules config.
     *
     * @return the modules config
     */
    public List<ModuleConfiguratorInterface> getModulesConfig() {
        return new ArrayList<>(modulesConfig.values());
    }

    /**
     * Собирает экспортированные модулями реализации интерфейса ModuleConfiguratorInterface. Формирует из них
     * сортированное дерево. Это необходимо для того чтобы соблюдать корректный порядок пунктов меню при каждом
     * запуске приложения. В итоге формирует ConcurrentMap общей конфигурации.
     *
     * @return the map
     */
    private static Map<String, ModuleConfiguratorInterface> prepareConfig() {
        TreeMap<Integer, ModuleConfiguratorInterface> configTree = new TreeMap<>();
        for (ModuleConfiguratorInterface item : ServiceLoader.load(ModuleLayer.boot(), ModuleConfiguratorInterface.class)) {
            configTree.put(item.getModuleOrder(), item);
        }
        Map<String, ModuleConfiguratorInterface> config = new ConcurrentHashMap<>();
        configTree.values().forEach(moduleConfig -> config.put(moduleConfig.getClass().getModule().getName(), moduleConfig));

        return config;
    }
}
