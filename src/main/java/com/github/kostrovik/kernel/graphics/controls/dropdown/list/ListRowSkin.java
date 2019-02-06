package com.github.kostrovik.kernel.graphics.controls.dropdown.list;

import com.github.kostrovik.kernel.graphics.controls.common.cells.CellBase;
import com.github.kostrovik.kernel.graphics.controls.common.columns.CommonColumn;
import com.github.kostrovik.kernel.graphics.controls.common.rows.RowBaseSkin;
import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;

/**
 * project: kernel
 * author:  kostrovik
 * date:    2019-02-03
 * github:  https://github.com/kostrovik/kernel
 */
public class ListRowSkin<E> extends RowBaseSkin<E, ListRow<E>> {
    private ListRow<E> control;

    public ListRowSkin(ListRow<E> control) {
        super(control);
        this.control = control;
        setListeners();
    }

    private void setListeners() {
        control.itemProperty().addListener((observable, oldValue, newValue) -> {
            updateCells(newValue);
            control.setSelected(control.getSelectionModel().getItems().contains(newValue));
        });
        control.selectedProperty().addListener((observable, oldValue, newValue) -> setStyleClass(newValue));

        row.addEventFilter(MouseEvent.MOUSE_CLICKED, event -> {
            E item = control.getItem();
            boolean setSelected = true;
            if (event.isShortcutDown()) {
                setSelected = !control.getSelectionModel().getItems().contains(item);
            }

            if (setSelected) {
                control.getSelectionModel().addItem(item);
            } else {
                control.getSelectionModel().removeItem(item);
            }
            getSkinnable().fireEvent(event.copyFor(getSkinnable(), getSkinnable()));
        });

        control.getSelectionModel().getItems().addListener((ListChangeListener<E>) c -> {
            E item = control.getItem();
            control.setSelected(control.getSelectionModel().getItems().contains(item));
        });
    }

    @Override
    protected <C extends CellBase<E>> C getCell(CommonColumn<E, ?> column) {
        C cell = (C) new CellBase<>(getSkinnable().getItem());
        cell.alignmentProperty().bind(column.alignmentProperty());
        cell.cellValueFactoryProperty().bind(column.cellValueFactoryProperty());

        Platform.runLater(() -> {
            Node cellContainer = cell.lookup(".cell-container");
            row.setPrefHeight(Math.max(cellContainer.prefHeight(1) + column.getPadding().getTop() + column.getPadding().getBottom(), row.getMinHeight()));
            row.setMinWidth(cellContainer.prefWidth(1) + column.getPadding().getLeft() + column.getPadding().getRight());
        });

        return cell;
    }

    private void setStyleClass(boolean set) {
        if (set) {
            getSkinnable().getStyleClass().add("selected");
        } else {
            getSkinnable().getStyleClass().removeAll("selected");
        }
    }
}