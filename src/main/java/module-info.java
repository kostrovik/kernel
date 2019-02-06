import com.github.kostrovik.kernel.builders.SceneBuilder;
import com.github.kostrovik.kernel.settings.Configurator;

/**
 * project: kernel
 * author:  kostrovik
 * date:    18/07/2018
 * github:  https://github.com/kostrovik/kernel
 */
module kernel {
    requires java.logging;
    requires javafx.graphics;
    requires javafx.controls;
    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.dataformat.yaml;
    requires com.github.kostrovik.useful.utils;

    exports com.github.kostrovik.kernel.common;
    exports com.github.kostrovik.kernel.dictionaries;
    exports com.github.kostrovik.kernel.exceptions;
    exports com.github.kostrovik.kernel.interfaces;
    exports com.github.kostrovik.kernel.interfaces.controls;
    exports com.github.kostrovik.kernel.interfaces.models;
    exports com.github.kostrovik.kernel.interfaces.views;

    exports com.github.kostrovik.kernel.graphics.builders;
    exports com.github.kostrovik.kernel.graphics.common;
    exports com.github.kostrovik.kernel.graphics.common.icons;
    exports com.github.kostrovik.kernel.graphics.controls.common;
    exports com.github.kostrovik.kernel.graphics.controls.common.columns;
    exports com.github.kostrovik.kernel.graphics.controls.dropdown;
    exports com.github.kostrovik.kernel.graphics.controls.field;
    exports com.github.kostrovik.kernel.graphics.controls.form;
    exports com.github.kostrovik.kernel.graphics.controls.image;
    exports com.github.kostrovik.kernel.graphics.controls.notification;
    exports com.github.kostrovik.kernel.graphics.controls.paged;
    exports com.github.kostrovik.kernel.graphics.controls.panel;
    exports com.github.kostrovik.kernel.graphics.controls.progress;

    exports com.github.kostrovik.kernel.models;
    exports com.github.kostrovik.kernel.models.controls;

    uses com.github.kostrovik.kernel.interfaces.ModuleConfiguratorInterface;
    uses com.github.kostrovik.kernel.interfaces.views.ViewEventListenerInterface;
    uses com.github.kostrovik.useful.interfaces.LoggerConfigInterface;

    provides com.github.kostrovik.kernel.interfaces.ModuleConfiguratorInterface with Configurator;
    provides com.github.kostrovik.kernel.interfaces.views.ViewEventListenerInterface with SceneBuilder;
}