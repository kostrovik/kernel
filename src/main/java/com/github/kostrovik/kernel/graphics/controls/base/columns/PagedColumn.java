package com.github.kostrovik.kernel.graphics.controls.base.columns;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.util.Callback;

import java.util.Objects;

/**
 * project: kernel
 * author:  kostrovik
 * date:    2018-09-09
 * github:  https://github.com/kostrovik/kernel
 *
 * @param <E> Тип сущности значение которой будет выведена в ячейке (E - entity)
 * @param <V> Тип значения сущности (V - value)
 */
public class PagedColumn<E, V> {
    private String columnName;
    private Callback<E, ?> cellValueFactory;
    private DoubleProperty columnWidth;
    private DoubleProperty columnMinWidth;
    private DoubleProperty columnMaxWidth;
    private ObjectProperty<Pos> alignment;
    private ObjectProperty<Insets> padding;

    public PagedColumn(String columnName) {
        this.columnName = columnName;
        this.columnWidth = new SimpleDoubleProperty(0);
        this.columnMinWidth = new SimpleDoubleProperty(0);
        this.columnMaxWidth = new SimpleDoubleProperty(-1);
        this.alignment = new SimpleObjectProperty<>(Pos.CENTER);
        this.padding = new SimpleObjectProperty<>(Insets.EMPTY);
    }

    public String getColumnName() {
        return columnName;
    }

    public Callback<E, V> getCellValueFactory() {
        if (Objects.isNull(cellValueFactory)) {
            cellValueFactory = (Callback<E, Object>) param -> Objects.nonNull(param) ? param.toString() : "";
        }

        return (Callback<E, V>) cellValueFactory;
    }

    public void setCellValueFactory(Callback<E, V> cellValueFactory) {
        this.cellValueFactory = cellValueFactory;
    }

    public double getColumnWidth() {
        return columnWidth.get();
    }

    public DoubleProperty columnWidthProperty() {
        return columnWidth;
    }

    public void setColumnWidth(double columnWidth) {
        this.columnWidth.set(columnWidth);
    }

    public double getColumnMinWidth() {
        return columnMinWidth.get();
    }

    public DoubleProperty columnMinWidthProperty() {
        return columnMinWidth;
    }

    public void setColumnMinWidth(double columnMinWidth) {
        this.columnMinWidth.set(columnMinWidth);
    }

    public double getColumnMaxWidth() {
        return columnMaxWidth.get();
    }

    public DoubleProperty columnMaxWidthProperty() {
        return columnMaxWidth;
    }

    public void setColumnMaxWidth(double columnMaxWidth) {
        this.columnMaxWidth.set(columnMaxWidth);
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

    public Insets getPadding() {
        return padding.get();
    }

    public ObjectProperty<Insets> paddingProperty() {
        return padding;
    }

    public void setPadding(Insets padding) {
        this.padding.set(padding);
    }
}
