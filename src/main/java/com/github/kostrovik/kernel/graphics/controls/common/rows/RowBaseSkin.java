package com.github.kostrovik.kernel.graphics.controls.common.rows;

import com.github.kostrovik.kernel.graphics.controls.common.cells.CellBase;
import com.github.kostrovik.kernel.graphics.controls.common.columns.CommonColumn;
import javafx.collections.ListChangeListener;
import javafx.scene.control.SkinBase;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

import java.util.List;

/**
 * project: kernel
 * author:  kostrovik
 * date:    2019-02-03
 * github:  https://github.com/kostrovik/kernel
 */
public abstract class RowBaseSkin<E, V extends RowBase<E>> extends SkinBase<V> {
    protected HBox row;

    public RowBaseSkin(V control) {
        super(control);
        createSkin();
        getSkinnable().getColumns().addListener((ListChangeListener<CommonColumn<E, ?>>) c -> createCells(row, getSkinnable().getColumns()));
    }

    protected void createSkin() {
        row = new HBox();
        createCells(row, getSkinnable().getColumns());
        getChildren().addAll(row);
    }

    protected void createCells(HBox row, List<CommonColumn<E, ?>> columns) {
        row.getChildren().clear();

        columns.forEach(column -> {
            CellBase<E> cell = getCell(column);
            HBox.setHgrow(cell, Priority.ALWAYS);
            setCellConfig(column, cell);

            row.getChildren().add(cell);
        });
    }

    protected void setCellConfig(CommonColumn<E, ?> column, CellBase<E> cell) {
        cell.minWidthProperty().bind(column.minWidthProperty());
        cell.prefWidthProperty().bind(column.prefWidthProperty());
        cell.minHeightProperty().bind(row.heightProperty());
        cell.prefHeightProperty().bind(row.heightProperty());
    }

    protected void updateCells(E item) {
        row.getChildren().forEach(node -> ((CellBase<E>) node).setItem(item));
    }

    protected abstract <C extends CellBase<E>> C getCell(CommonColumn<E, ?> column);
}