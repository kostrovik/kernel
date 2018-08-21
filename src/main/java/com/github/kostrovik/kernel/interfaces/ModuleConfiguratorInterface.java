package com.github.kostrovik.kernel.interfaces;

import com.github.kostrovik.kernel.interfaces.controls.ControlBuilderFacadeInterface;
import com.github.kostrovik.kernel.interfaces.views.MenuBuilderInterface;
import com.github.kostrovik.kernel.interfaces.views.ViewEventListenerInterface;

import java.util.Map;
import java.util.logging.Logger;

/**
 * project: glcmtx
 * author:  kostrovik
 * date:    24/07/2018
 * github:  https://github.com/kostrovik/glcmtx
 */
public interface ModuleConfiguratorInterface {
    MenuBuilderInterface getMenuBuilder();

    Map<String, Class<?>> getModuleViews();

    ViewEventListenerInterface getEventListener();

    ControlBuilderFacadeInterface getControlBuilder();

    Logger getLogger(String className);
}
