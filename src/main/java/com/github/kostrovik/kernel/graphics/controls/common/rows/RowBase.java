package com.github.kostrovik.kernel.graphics.controls.common.rows;

import com.github.kostrovik.kernel.graphics.controls.common.columns.CommonColumn;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;
import javafx.scene.control.Control;

import java.util.Objects;

/**
 * project: kernel
 * author:  kostrovik
 * date:    2019-02-03
 * github:  https://github.com/kostrovik/kernel
 */
public abstract class RowBase<E> extends Control {
    private ObservableList<CommonColumn<E, ?>> columns;
    private ObjectProperty<E> item;
    private BooleanProperty empty;

    public RowBase(ObservableList<CommonColumn<E, ?>> columns, E item) {
        this(columns);
        setItem(item);
    }

    public RowBase(ObservableList<CommonColumn<E, ?>> columns) {
        Objects.requireNonNull(columns);
        this.columns = columns;
        this.item = new SimpleObjectProperty<>();
        this.empty = new SimpleBooleanProperty(true);
        this.empty.bind(item.isNull());
    }

    public ObservableList<CommonColumn<E, ?>> getColumns() {
        return columns;
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

    public boolean isEmpty() {
        return empty.get();
    }

    public BooleanProperty emptyProperty() {
        return empty;
    }
}
