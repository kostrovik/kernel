package com.github.kostrovik.kernel.graphics.controls.paged.rows;

import com.github.kostrovik.kernel.graphics.controls.paged.table.PagedTable;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.Skin;

/**
 * project: kernel
 * author:  kostrovik
 * date:    2018-09-09
 * github:  https://github.com/kostrovik/kernel
 */
public class PagedRow<E> extends RowBase<E> {
    private ObjectProperty<E> item;
    private BooleanProperty selected;

    public PagedRow(PagedTable<E> table, E item) {
        super(table);
        getStyleClass().setAll("paged-table-row");
        this.item = new SimpleObjectProperty<>(item);
        this.selected = new SimpleBooleanProperty(false);
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
