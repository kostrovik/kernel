package com.github.kostrovik.kernel.graphics.controls.dropdown;

import com.github.kostrovik.kernel.settings.Configurator;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ObjectPropertyBase;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;
import javafx.util.Callback;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * project: kernel
 * author:  kostrovik
 * date:    22/08/2018
 * github:  https://github.com/kostrovik/kernel
 */
public class SearchableDropDownField<T extends Comparable> extends Control {
    private static Logger logger = Configurator.getConfig().getLogger(SearchableDropDownField.class.getName());

    private final ObjectProperty<String> label;
    private final ObjectProperty<List<T>> items;
    private final ObjectProperty<Boolean> showLabel;
    private final ObjectProperty<Callback<T, String>> listLabelCallback;
    private final ObjectProperty<ObservableList<T>> selectedItems;

    private final Callback<T, String> defaultCallBack = param -> param.toString();

    private final EventHandler<ActionEvent> defauldAction = event -> {
    };

    private ObjectProperty<EventHandler<ActionEvent>> onOpenDictionaryAction = new ObjectPropertyBase<>() {
        @Override
        protected void invalidated() {
            setEventHandler(ActionEvent.ACTION, get());
        }

        @Override
        public Object getBean() {
            return SearchableDropDownField.this;
        }

        @Override
        public String getName() {
            return "onOpenDictionaryAction";
        }
    };

    private final ObjectProperty<Boolean> isMultiple;

    public SearchableDropDownField(String label) {
        this.label = new SimpleObjectProperty<>("");
        this.items = new SimpleObjectProperty<>(FXCollections.<T>observableArrayList());
        this.showLabel = new SimpleObjectProperty<>(true);
        this.listLabelCallback = new SimpleObjectProperty<>(defaultCallBack);
        this.selectedItems = new SimpleObjectProperty<>(FXCollections.<T>observableArrayList());
        this.isMultiple = new SimpleObjectProperty<>(true);

        setOnOpenDictionaryAction(defauldAction);
        setLabel(label);
        getStyleClass().add("drop-down");

        try {
            getStylesheets().add(Class.forName(this.getClass().getName()).getResource("/com/github/kostrovik/styles/controls/searchable-dropdown.css").toExternalForm());
        } catch (ClassNotFoundException error) {
            logger.log(Level.WARNING, "Ошибка загрузки стилей.", error);
        }
    }

    public SearchableDropDownField(String label, boolean showLabel) {
        this.label = new SimpleObjectProperty<>();
        this.items = new SimpleObjectProperty<>();
        this.showLabel = new SimpleObjectProperty<>(showLabel);
        this.listLabelCallback = new SimpleObjectProperty<>(defaultCallBack);
        this.selectedItems = new SimpleObjectProperty<>(FXCollections.<T>observableArrayList());
        this.isMultiple = new SimpleObjectProperty<>(true);

        setOnOpenDictionaryAction(defauldAction);
        setLabel(label);
        getStyleClass().add("drop-down");

        try {
            getStylesheets().add(Class.forName(this.getClass().getName()).getResource("/com/github/kostrovik/styles/controls/searchable-dropdown.css").toExternalForm());
        } catch (ClassNotFoundException error) {
            logger.log(Level.WARNING, "Ошибка загрузки стилей.", error);
        }
    }

    // свойсто название поля
    public ObjectProperty<String> labelProperty() {
        return label;
    }

    public String getLabel() {
        return label.get();
    }

    public void setLabel(String labelValue) {
        label.set(labelValue);
    }
    // -- свойсто название поля --

    // свойсто список значений
    public ObjectProperty<List<T>> itemsProperty() {
        return items;
    }

    public List<T> getItems() {
        return items.get();
    }

    public void setItems(List<T> items) {
        this.items.set(items);
    }
    // -- свойсто список значений --

    // свойсто выводить подпись
    public ObjectProperty<Boolean> showLabelProperty() {
        return showLabel;
    }

    public boolean isShowLabel() {
        return showLabel.get();
    }

    public void setShowLabel(boolean show) {
        this.showLabel.set(show);
    }
    // -- свойсто выводить подпись --

    // свойсто callback для получения строки для выпадающего списка
    public ObjectProperty<Callback<T, String>> listLabelCallbackProperty() {
        return listLabelCallback;
    }

    public Callback<T, String> getListLabelCallback() {
        return listLabelCallback.get();
    }

    public void setListLabelCallback(Callback<T, String> callback) {
        this.listLabelCallback.set(callback);
    }
    // -- свойсто выводить подпись --

    // свойсто список выбранных значений
    public ObjectProperty<ObservableList<T>> selectedItemsProperty() {
        return selectedItems;
    }

    public ObservableList<T> getSelectedItems() {
        return selectedItems.get();
    }

    public void setSelectedItems(ObservableList<T> items) {
        this.selectedItems.set(items);
    }

    public void addSelectedItem(T item) {
        getSelectedItems().add(item);
    }

    public void clearSelectedItems() {
        getSelectedItems().clear();
    }
    // -- свойсто список выбранных значений --

    // свойсто множественный выбор
    public ObjectProperty<Boolean> isMultipleProperty() {
        return isMultiple;
    }

    public boolean isMultiple() {
        return isMultiple.get();
    }

    public void setIsMultiple(boolean multiple) {
        this.isMultiple.set(multiple);
    }

    // -- свойсто выводить подпись --
    public final ObjectProperty<EventHandler<ActionEvent>> onOpenDictionaryActionProperty() {
        return onOpenDictionaryAction;
    }

    public final void setOnOpenDictionaryAction(EventHandler<ActionEvent> value) {
        onOpenDictionaryActionProperty().set(value);
    }

    public final EventHandler<ActionEvent> getOnOpenDictionaryAction() {
        return onOpenDictionaryActionProperty().get();
    }

    @Override
    protected Skin<?> createDefaultSkin() {
        return new SearchableDropDownFieldSkin<T>(this);
    }
}