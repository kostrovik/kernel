package com.github.kostrovik.kernel.graphics.controls.base.rows;

import com.github.kostrovik.kernel.graphics.controls.base.cells.PagedCell;
import com.github.kostrovik.kernel.graphics.controls.base.columns.PagedColumn;
import com.github.kostrovik.kernel.graphics.controls.base.table.PagedTable;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.scene.control.SkinBase;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

import java.util.List;

/**
 * project: kernel
 * author:  kostrovik
 * date:    2018-09-09
 * github:  https://github.com/kostrovik/kernel
 */
public class PagedRowSkin<E> extends SkinBase<PagedRow<E>> {
    private PagedTable<E> table;
    private HBox row;

    public PagedRowSkin(PagedRow<E> control) {
        super(control);
        table = control.getTable();

        row = new HBox();
        createCells();
        getChildren().addAll(row);
        control.setPrefHeight(table.getDefaultCellHeight());

        table.getColumns().addListener((ListChangeListener<PagedColumn<E, ?>>) c -> createCells());
        getSkinnable().itemProperty().addListener((observable, oldValue, newValue) -> {
            updateCells();

            List<E> selectedItems = table.getSelectionModel().getSelectedItems();
            getSkinnable().setSelected(selectedItems.contains(newValue));
        });
        getSkinnable().selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                getSkinnable().getStyleClass().add("selected");
            } else {
                getSkinnable().getStyleClass().remove("selected");
            }
        });

        getSkinnable().addEventFilter(MouseEvent.MOUSE_CLICKED, event -> {
            if (table.isSelectable()) {
                PagedRow<E> selectedRow = (PagedRow<E>) event.getSource();
                boolean setSelected = true;

                if (event.isShortcutDown()) {
                    setSelected = !getSkinnable().isSelected();
                }

                getSkinnable().setSelected(setSelected);

                if (setSelected) {
                    table.getSelectionModel().addSelectedItem(selectedRow.getItem());
                } else {
                    table.getSelectionModel().removeSelectedItem(selectedRow.getItem());
                }
            }

            getSkinnable().requestFocus();

            table.fireEvent(event.copyFor(getSkinnable(), getSkinnable()));
        });

        table.getSelectionModel().getSelectedItems().addListener((ListChangeListener<E>) c -> getSkinnable().selectedProperty().set(c.getList().contains(getSkinnable().getItem())));

        getSkinnable().addEventHandler(MouseEvent.ANY, event -> table.fireEvent(event.copyFor(getSkinnable(), getSkinnable())));
    }

    private void createCells() {
        row.getChildren().clear();

        table.getColumns().forEach(column -> {
            PagedCell<E, ?> cell = new PagedCell<>(column, getSkinnable().getItem(), false);
            HBox.setHgrow(cell, Priority.ALWAYS);
            cell.prefWidthProperty().bind(column.columnWidthProperty());
            cell.minWidthProperty().bind(column.columnMinWidthProperty());

            if (column.getColumnMaxWidth() > 0) {
                cell.setMaxWidth(column.getColumnMaxWidth());
            }

            column.columnMaxWidthProperty().addListener(new ChangeListener<Number>() {
                @Override
                public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                    if (newValue.doubleValue() > 0) {
                        cell.setMaxWidth(newValue.doubleValue());
                    }
                }
            });

            cell.widthProperty().addListener((observable, oldValue, newValue) -> {
                if (column.getColumnWidth() < newValue.doubleValue()) {
                    column.setColumnWidth(newValue.doubleValue());
                }
            });

            row.getChildren().add(cell);
        });
    }

    private void updateCells() {
        row.getChildren().forEach(node -> {
            PagedCell<E, ?> cell = (PagedCell<E, ?>) node;
            cell.setItem(getSkinnable().getItem());
        });
    }
}
