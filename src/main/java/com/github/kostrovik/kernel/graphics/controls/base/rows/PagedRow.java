package com.github.kostrovik.kernel.graphics.controls.base.rows;

import com.github.kostrovik.kernel.graphics.controls.base.table.PagedTable;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;

/**
 * project: kernel
 * author:  kostrovik
 * date:    2018-09-09
 * github:  https://github.com/kostrovik/kernel
 */
public class PagedRow<E> extends Control {
    private PagedTable<E> table;
    private ObjectProperty<E> item;
    private ObjectProperty<Integer> rowIndex;

    private BooleanProperty selected;

    public PagedRow(PagedTable<E> table, E item) {
        getStyleClass().setAll("paged-table-row");

        this.table = table;
        this.item = new SimpleObjectProperty<>(item);
        this.rowIndex = new SimpleObjectProperty<>(0);
        this.selected = new SimpleBooleanProperty(false);
    }

    public PagedTable<E> getTable() {
        return table;
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

    public Integer getRowIndex() {
        return rowIndex.get();
    }

    public ObjectProperty<Integer> rowIndexProperty() {
        return rowIndex;
    }

    public void setRowIndex(Integer rowIndex) {
        this.rowIndex.set(rowIndex);
    }

    public boolean isSelected() {
        return selected.get();
    }

    public BooleanProperty selectedProperty() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected.set(selected);
    }

    @Override
    protected Skin<?> createDefaultSkin() {
        return new PagedRowSkin<>(this);
    }
}
