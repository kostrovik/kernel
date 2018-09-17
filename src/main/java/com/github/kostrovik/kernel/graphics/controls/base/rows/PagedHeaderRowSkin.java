package com.github.kostrovik.kernel.graphics.controls.base.rows;

import com.github.kostrovik.kernel.graphics.controls.base.cells.PagedCell;
import com.github.kostrovik.kernel.graphics.controls.base.columns.PagedColumn;
import com.github.kostrovik.kernel.graphics.controls.base.table.PagedTable;
import javafx.collections.ListChangeListener;
import javafx.scene.control.SkinBase;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.shape.Rectangle;

/**
 * project: kernel
 * author:  kostrovik
 * date:    2018-09-09
 * github:  https://github.com/kostrovik/kernel
 */
public class PagedHeaderRowSkin<E> extends SkinBase<PagedHeaderRow<E>> {
    private HBox row;
    private PagedTable<E> table;

    public PagedHeaderRowSkin(PagedHeaderRow<E> control) {
        super(control);
        table = control.getTable();

        row = new HBox();
        createRowCells();

        Pane container = new Pane();
        Rectangle mask = new Rectangle();
        mask.widthProperty().bind(getSkinnable().widthProperty());
        mask.heightProperty().bind(getSkinnable().heightProperty());
        container.setClip(mask);

        container.minHeightProperty().bind(row.heightProperty());

        row.layoutXProperty().bind(getSkinnable().layoutXPosProperty());
        row.prefWidthProperty().bind(getSkinnable().rowWidthProperty());

        container.getChildren().addAll(row);
        getChildren().addAll(container);

        table.getColumns().addListener((ListChangeListener<PagedColumn<E, ?>>) c -> createRowCells());
    }

    private void createRowCells() {
        row.getChildren().clear();
        table.getColumns().forEach(column -> {
            PagedCell<E, String> cell = new PagedCell<>((PagedColumn<E, String>) column, true);
            HBox.setHgrow(cell, Priority.ALWAYS);
            cell.prefWidthProperty().bind(column.columnWidthProperty());
            cell.minWidthProperty().bind(column.columnMinWidthProperty());

            cell.widthProperty().addListener((observable, oldValue, newValue) -> {
                if (column.getColumnWidth() < newValue.doubleValue()) {
                    column.setColumnWidth(newValue.doubleValue());
                }
            });

            cell.skinProperty().addListener((observable, oldValue, newValue) -> cell.setMinHeight(table.getDefaultHeaderCellHeight()));
            row.getChildren().add(cell);
        });
    }
}