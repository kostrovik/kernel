package com.github.kostrovik.kernel.graphics.controls.dropdown.list;

import com.github.kostrovik.kernel.graphics.controls.common.SelectionModel;
import com.github.kostrovik.kernel.graphics.controls.common.columns.CommonColumn;
import com.github.kostrovik.kernel.graphics.controls.common.rows.RowBase;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.ObservableList;
import javafx.scene.control.Skin;

import java.util.Objects;

/**
 * project: kernel
 * author:  kostrovik
 * date:    2019-02-03
 * github:  https://github.com/kostrovik/kernel
 */
public class ListRow<E> extends RowBase<E> {
    private SelectionModel<E> selectionModel;
    private BooleanProperty selected;

    public ListRow(ObservableList<CommonColumn<E, ?>> commonColumns, E item, SelectionModel<E> selectionModel) {
        super(commonColumns, item);
        Objects.requireNonNull(selectionModel);
        this.selectionModel = selectionModel;
        this.selected = new SimpleBooleanProperty(false);

        getStyleClass().setAll("list-row");
    }

    public ListRow(ObservableList<CommonColumn<E, ?>> commonColumns, SelectionModel<E> selectionModel) {
        super(commonColumns);
        Objects.requireNonNull(selectionModel);
        this.selectionModel = selectionModel;
        this.selected = new SimpleBooleanProperty(false);

        getStyleClass().setAll("list-row");
    }

    public SelectionModel<E> getSelectionModel() {
        return selectionModel;
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
        return new ListRowSkin<>(this);
    }
}
