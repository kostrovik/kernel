package com.github.kostrovik.kernel.graphics.controls.dropdown;

import com.github.kostrovik.kernel.graphics.controls.common.SelectionModel;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;
import javafx.util.Callback;

import java.util.Objects;

/**
 * project: kernel
 * author:  kostrovik
 * date:    2019-01-23
 * github:  https://github.com/kostrovik/kernel
 */
class InputControl<E> extends Control {
    private ObjectProperty<Callback<E, String>> callback;
    private SelectionModel<E> selectionModel;
    private EventHandler<ActionEvent> openDialog;
    private EventHandler<ActionEvent> openList;
    private EventHandler<ActionEvent> clear;

    InputControl(SelectionModel<E> selectionModel, EventHandler<ActionEvent> openDialog, EventHandler<ActionEvent> openList, EventHandler<ActionEvent> clear) {
        this.selectionModel = selectionModel;
        this.openDialog = openDialog;
        this.openList = openList;
        this.clear = clear;
        this.callback = new SimpleObjectProperty<>(Objects::toString);
    }

    public Callback<E, String> getCallback() {
        return callback.get();
    }

    public ObjectProperty<Callback<E, String>> callbackProperty() {
        return callback;
    }

    public void setCallback(Callback<E, String> callback) {
        this.callback.set(callback);
    }

    public SelectionModel<E> getSelectionModel() {
        return selectionModel;
    }

    EventHandler<ActionEvent> getOpenDialog() {
        return openDialog;
    }

    EventHandler<ActionEvent> getOpenList() {
        return openList;
    }

    EventHandler<ActionEvent> getClear() {
        return clear;
    }

    @Override
    protected Skin<?> createDefaultSkin() {
        return new InputControlSkin<>(this);
    }
}