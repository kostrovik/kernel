import com.github.kostrovik.kernel.settings.Configurator;

/**
 * project: kernel
 * author:  kostrovik
 * date:    18/07/2018
 * github:  https://github.com/kostrovik/kernel
 */
module kernel {
    requires javafx.graphics;
    requires javafx.controls;
    requires java.logging;

    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.dataformat.yaml;

    exports com.github.kostrovik.kernel.common;
    exports com.github.kostrovik.kernel.interfaces;
    exports com.github.kostrovik.kernel.interfaces.controls;
    exports com.github.kostrovik.kernel.interfaces.views;

    exports com.github.kostrovik.kernel.graphics.controls.notification;
    exports com.github.kostrovik.kernel.graphics.controls.field;
    exports com.github.kostrovik.kernel.graphics.controls.table;
    exports com.github.kostrovik.kernel.graphics.controls.dropdown;
    exports com.github.kostrovik.kernel.graphics.controls.progress;
    exports com.github.kostrovik.kernel.graphics.common;
    exports com.github.kostrovik.kernel.graphics.common.icons;

    exports com.github.kostrovik.kernel.models;
    exports com.github.kostrovik.kernel.dictionaries;

    uses com.github.kostrovik.kernel.interfaces.ModuleConfiguratorInterface;
    uses com.github.kostrovik.kernel.interfaces.views.ViewEventListenerInterface;
    uses com.github.kostrovik.kernel.interfaces.controls.ControlBuilderFacadeInterface;
    uses com.github.kostrovik.kernel.interfaces.ApplicationLoggerInterface;

    provides com.github.kostrovik.kernel.interfaces.ModuleConfiguratorInterface with Configurator;
    provides com.github.kostrovik.kernel.interfaces.ServerConnectionInterface with com.github.kostrovik.kernel.common.ServerConnector;
    provides com.github.kostrovik.kernel.interfaces.views.ViewEventListenerInterface with com.github.kostrovik.kernel.builders.SceneFactory;
    provides com.github.kostrovik.kernel.interfaces.controls.ControlBuilderFacadeInterface with com.github.kostrovik.kernel.graphics.common.ControlBuilderFacade;
}