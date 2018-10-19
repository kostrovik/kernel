package com.github.kostrovik.kernel.graphics.controls.panel;

import com.github.kostrovik.kernel.dictionaries.ControlPanelButtons;
import com.github.kostrovik.kernel.settings.Configurator;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

/**
 * project: kernel
 * author:  kostrovik
 * date:    24/08/2018
 * github:  https://github.com/kostrovik/kernel
 */
public class ListControlPanel extends Control {
    private final ObjectProperty<ObservableMap<String, Button>> buttons;
    private final ObjectProperty<List<ControlPanelButtons>> defaultButtons;

    public ListControlPanel() {
        this.defaultButtons = new SimpleObjectProperty<>(prepareDefaultButtons());
        this.buttons = new SimpleObjectProperty<>(FXCollections.observableHashMap());

        getStyleClass().add("list-control-panel");
        setControlStyles();
    }

    public ListControlPanel(List<ControlPanelButtons> defaultButtons) {
        this.defaultButtons = new SimpleObjectProperty<>(defaultButtons);
        this.buttons = new SimpleObjectProperty<>(FXCollections.observableHashMap());

        getStyleClass().add("list-control-panel");
        setControlStyles();
    }

    // свойсто список кнопок панели
    public ObjectProperty<ObservableMap<String, Button>> buttonsProperty() {
        return buttons;
    }

    public ObservableMap<String, Button> getButtons() {
        return buttonsProperty().get();
    }

    public void setButtons(ObservableMap<String, Button> buttons) {
        buttonsProperty().set(buttons);
    }

    public void addButton(String buttonKey, Button button) {
        getButtons().put(buttonKey, button);
    }

    public void clearButtons() {
        getButtons().clear();
    }
    // -- свойсто список кнопок панели --

    // свойсто список кнопок по умолчанию
    public ObjectProperty<List<ControlPanelButtons>> defaultButtonsProperty() {
        return defaultButtons;
    }

    public List<ControlPanelButtons> getDefaultButtons() {
        return defaultButtonsProperty().get();
    }

    public void setDefaultButtons(List<ControlPanelButtons> buttons) {
        defaultButtonsProperty().set(buttons);
    }
    // -- свойсто список кнопок по умолчанию --

    @Override
    protected Skin<?> createDefaultSkin() {
        return new ListControlPanelSkin(this);
    }

    private List<ControlPanelButtons> prepareDefaultButtons() {
        List<ControlPanelButtons> list = new ArrayList<>();
        list.add(ControlPanelButtons.ADD);
        list.add(ControlPanelButtons.REMOVE);
        list.add(ControlPanelButtons.EDIT);
        return list;
    }

    private void setControlStyles() {
        try {
            getStylesheets().add(Class.forName(this.getClass().getName()).getResource("/com/github/kostrovik/styles/controls/list-control-panel.css").toExternalForm());
        } catch (ClassNotFoundException error) {
            Configurator.getConfig().getLogger(ListControlPanel.class.getName()).log(Level.WARNING, "Ошибка загрузки стилей.", error);
        }
    }
}
