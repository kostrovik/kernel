package com.github.kostrovik.kernel.builders;

import com.github.kostrovik.kernel.interfaces.ModuleConfiguratorInterface;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.ServiceLoader;
import java.util.stream.Collectors;

/**
 * project: kernel
 * author:  kostrovik
 * date:    23/07/2018
 * github:  https://github.com/kostrovik/kernel
 */
public class ModulesConfigBuilder {
    private static volatile ModulesConfigBuilder builder;
    private Map<String, ModuleConfiguratorInterface> modulesConfig = prepareConfig();

    private ModulesConfigBuilder() {
    }

    public static synchronized ModulesConfigBuilder getInstance() {
        if (builder == null) {
            builder = new ModulesConfigBuilder();
        }
        return builder;
    }

    public ModuleConfiguratorInterface getConfigForModule(String moduleName) {
        return modulesConfig.getOrDefault(moduleName, null);
    }

    public List<String> moduleKeys() {
        return new ArrayList<>(modulesConfig.keySet());
    }

    private static Map<String, ModuleConfiguratorInterface> prepareConfig() {
        return ServiceLoader
                .load(ModuleLayer.boot(), ModuleConfiguratorInterface.class)
                .stream()
                .map(ServiceLoader.Provider::get)
                .filter(Objects::nonNull)
                .collect(Collectors.toConcurrentMap(o -> o.getClass().getModule().getName(), item -> item));
    }
}
