package com.github.kostrovik.kernel.graphics.controls.common.cells;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Pos;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;
import javafx.util.Callback;

/**
 * project: kernel
 * author:  kostrovik
 * date:    2019-01-23
 * github:  https://github.com/kostrovik/kernel
 */
public class CellBase<E> extends Control {
    private ObjectProperty<E> item;
    private ObjectProperty<Callback<E, ?>> cellValueFactory;
    private ObjectProperty<Pos> alignment;

    public CellBase(E item) {
        this.item = new SimpleObjectProperty<>(item);
        this.cellValueFactory = new SimpleObjectProperty<>(param -> null);
        this.alignment = new SimpleObjectProperty<>(Pos.CENTER);
    }

    public E getItem() {
        return item.get();
    }

    public ObjectProperty<E> itemProperty() {
        return item;
    }

    public void setItem(E item) {
        this.item.set(item);
    }

    public Callback<E, ?> getCellValueFactory() {
        return cellValueFactory.get();
    }

    public ObjectProperty<Callback<E, ?>> cellValueFactoryProperty() {
        return cellValueFactory;
    }

    public void setCellValueFactory(Callback<E, ?> cellValueFactory) {
        this.cellValueFactory.set(cellValueFactory);
    }

    public Pos getAlignment() {
        return alignment.get();
    }

    public ObjectProperty<Pos> alignmentProperty() {
        return alignment;
    }

    public void setAlignment(Pos alignment) {
        this.alignment.set(alignment);
    }

    @Override
    protected Skin<?> createDefaultSkin() {
        return new CellBaseSkin<>(this);
    }
}
