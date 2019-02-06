package com.github.kostrovik.kernel.graphics.controls.paged.rows;

import com.github.kostrovik.kernel.graphics.controls.paged.cells.PagedCell;
import com.github.kostrovik.kernel.graphics.controls.common.columns.CommonColumn;
import com.github.kostrovik.kernel.graphics.controls.paged.table.PagedTable;
import javafx.collections.ListChangeListener;
import javafx.scene.control.SkinBase;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

import java.util.List;

/**
 * project: kernel
 * author:  kostrovik
 * date:    2019-01-20
 * github:  https://github.com/kostrovik/kernel
 */
public abstract class RowBaseSkin<E> extends SkinBase<RowBase<E>> {
    protected PagedTable<E> table;
    protected HBox row;

    protected RowBaseSkin(RowBase<E> control) {
        super(control);
        this.table = control.getTable();
        createSkin();
        table.getColumns().addListener((ListChangeListener<CommonColumn<E, ?>>) c -> createCells(row, table.getColumns()));
    }

    protected void createSkin() {
        row = new HBox();
        createCells(row, table.getColumns());
        getChildren().addAll(row);
    }

    protected void createCells(HBox row, List<CommonColumn<E, ?>> columns) {
        row.getChildren().clear();

        columns.forEach(column -> {
            PagedCell<E, ?> cell = getCell(column);
            HBox.setHgrow(cell, Priority.ALWAYS);
            cell.minWidthProperty().bind(column.minWidthProperty());
            cell.prefWidthProperty().bind(column.prefWidthProperty());
            cell.minHeightProperty().bind(row.heightProperty());
            cell.prefHeightProperty().bind(row.heightProperty());

            row.getChildren().add(cell);
        });
    }

    protected void updateCells(E item) {
        row.getChildren().forEach(node -> ((PagedCell<E, ?>) node).setItem(item));
    }

    protected abstract PagedCell<E, ?> getCell(CommonColumn<E, ?> column);
}
