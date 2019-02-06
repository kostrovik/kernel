package com.github.kostrovik.kernel.graphics.controls.common.columns;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Pos;
import javafx.scene.control.Control;
import javafx.util.Callback;

/**
 * project: kernel
 * author:  kostrovik
 * date:    2018-09-09
 * github:  https://github.com/kostrovik/kernel
 *
 * @param <E> Тип сущности значение которой будет выведено в ячейке (E - entity)
 * @param <V> Тип значения сущности (V - value)
 */
public class CommonColumn<E, V> extends Control {
    private String columnName;
    private ObjectProperty<Callback<E, ?>> cellValueFactory;
    private ObjectProperty<Pos> alignment;

    public CommonColumn(String columnName) {
        this.columnName = columnName;
        this.alignment = new SimpleObjectProperty<>(Pos.CENTER);
        this.cellValueFactory = new SimpleObjectProperty<>(param -> null);
    }

    public String getColumnName() {
        return columnName;
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
}
