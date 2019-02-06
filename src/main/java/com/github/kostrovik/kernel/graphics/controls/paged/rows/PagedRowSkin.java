package com.github.kostrovik.kernel.graphics.controls.paged.rows;

import com.github.kostrovik.kernel.graphics.controls.paged.cells.PagedCell;
import com.github.kostrovik.kernel.graphics.controls.common.columns.CommonColumn;
import com.github.kostrovik.kernel.graphics.controls.paged.selection.TableSelectionModel;
import javafx.collections.ListChangeListener;
import javafx.scene.input.MouseEvent;

/**
 * project: kernel
 * author:  kostrovik
 * date:    2018-09-09
 * github:  https://github.com/kostrovik/kernel
 */
public class PagedRowSkin<E> extends RowBaseSkin<E> {
    private TableSelectionModel<E> selectionModel;
    private PagedRow<E> control;

    public PagedRowSkin(PagedRow<E> control) {
        super(control);
        this.control = control;
        this.selectionModel = table.getSelectionModel();

        row.prefHeightProperty().bind(table.rowHeightProperty());
        row.maxHeightProperty().bind(table.rowHeightProperty());

        setListeners();
    }

    private void setListeners() {
        control.itemProperty().addListener((observable, oldValue, newValue) -> {
            updateCells(newValue);
            control.setSelected(selectionModel.getSelectedItems().contains(newValue));
        });
        control.selectedProperty().addListener((observable, oldValue, newValue) -> setStyleClass(newValue));

        row.addEventFilter(MouseEvent.MOUSE_CLICKED, event -> {
            if (table.isSelectable()) {
                E item = control.getItem();
                boolean setSelected = true;
                if (event.isShortcutDown()) {
                    setSelected = !selectionModel.getSelectedItems().contains(item);
                }

                if (setSelected) {
                    selectionModel.addItem(item);
                } else {
                    selectionModel.removeItem(item);
                }
            }
            getSkinnable().fireEvent(event.copyFor(getSkinnable(), getSkinnable()));
        });

        selectionModel.getSelectedItems().addListener((ListChangeListener<E>) c -> {
            E item = control.getItem();
            control.setSelected(selectionModel.getSelectedItems().contains(item));
        });
    }

    @Override
    protected PagedCell<E, ?> getCell(CommonColumn<E, ?> column) {
        return new PagedCell<>(table, column, ((PagedRow<E>) getSkinnable()).getItem(), false);
    }

    private void setStyleClass(boolean set) {
        if (set) {
            control.getStyleClass().add("selected");
        } else {
            control.getStyleClass().remove("selected");
        }
    }
}
