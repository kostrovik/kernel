package com.github.kostrovik.kernel.graphics.controls.base.rows;

import com.github.kostrovik.kernel.graphics.controls.base.table.PagedTable;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;

/**
 * project: kernel
 * author:  kostrovik
 * date:    2018-09-09
 * github:  https://github.com/kostrovik/kernel
 */
public class PagedHeaderRow<E> extends Control {
    private PagedTable<E> table;
    private DoubleProperty layoutXPos;
    private DoubleProperty rowWidth;

    public PagedHeaderRow(PagedTable<E> table) {
        this.table = table;
        getStyleClass().setAll("paged-table-header-row");
        this.layoutXPos = new SimpleDoubleProperty(0);
        this.rowWidth = new SimpleDoubleProperty(0);
    }

    public PagedTable<E> getTable() {
        return table;
    }

    public double getLayoutXPos() {
        return layoutXPos.get();
    }

    public DoubleProperty layoutXPosProperty() {
        return layoutXPos;
    }

    public void setLayoutXPos(double layoutXPos) {
        this.layoutXPos.set(layoutXPos);
    }

    public double getRowWidth() {
        return rowWidth.get();
    }

    public DoubleProperty rowWidthProperty() {
        return rowWidth;
    }

    public void setRowWidth(double rowWidth) {
        this.rowWidth.set(rowWidth);
    }

    @Override
    protected Skin<?> createDefaultSkin() {
        return new PagedHeaderRowSkin<>(this);
    }
}
