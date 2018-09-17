package com.github.kostrovik.kernel.graphics.controls.base.cells;

import com.github.kostrovik.kernel.graphics.controls.base.columns.PagedColumn;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;

/**
 * project: kernel
 * author:  kostrovik
 * date:    2018-09-08
 * github:  https://github.com/kostrovik/kernel
 *
 * @param <E> Тип сущности значение которой будет выведена в ячейке (E - entity)
 * @param <V> Тип значения сущности (V - value)
 */
public class PagedCell<E, V> extends Control {
    private PagedColumn<E, V> column;
    private ObjectProperty<E> item;
    private boolean isHeaderCell;

    public PagedCell(PagedColumn<E, V> column, E item, boolean isHeaderCell) {
        getStyleClass().setAll("paged-table-cell");

        this.item = new SimpleObjectProperty<>(item);
        this.column = column;
        this.isHeaderCell = isHeaderCell;
    }

    public PagedCell(PagedColumn<E, V> column, boolean isHeaderCell) {
        this(column, null, isHeaderCell);
    }

    public ObjectProperty<E> itemProperty() {
        return item;
    }

    public E getItem() {
        return itemProperty().get();
    }

    public void setItem(E item) {
        itemProperty().set(item);
    }

    @Override
    protected Skin<?> createDefaultSkin() {
        return new PagedCellSkin(this, column, isHeaderCell);
    }
}
