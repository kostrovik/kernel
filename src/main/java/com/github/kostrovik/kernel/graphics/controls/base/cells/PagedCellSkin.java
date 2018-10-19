package com.github.kostrovik.kernel.graphics.controls.base.cells;

import com.github.kostrovik.kernel.graphics.controls.base.columns.PagedColumn;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.SkinBase;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;

import java.util.Objects;

/**
 * project: kernel
 * author:  kostrovik
 * date:    2018-09-08
 * github:  https://github.com/kostrovik/kernel
 *
 * @param <E> Тип сущности значение которой будет выведена в ячейке (E - entity)
 * @param <V> Тип значения сущности (V - value)
 */
public class PagedCellSkin<E, V> extends SkinBase<PagedCell<E, V>> {
    private StackPane cell;
    private PagedColumn<E, V> column;
    private boolean isHeaderCell;

    public PagedCellSkin(PagedCell<E, V> control, PagedColumn<E, V> column, boolean isHeaderCell) {
        super(control);
        this.column = column;
        this.isHeaderCell = isHeaderCell;

        createSkin();
    }

    private void createSkin() {
        cell = new StackPane();
        Rectangle cellMask = new Rectangle();

        if (!isHeaderCell) {
            cell.setAlignment(column.getAlignment());
            cell.alignmentProperty().bind(column.alignmentProperty());
        } else {
            cell.setAlignment(Pos.CENTER);
        }
        cell.paddingProperty().bind(column.paddingProperty());

        updateContent();

        getSkinnable().itemProperty().addListener((observable, oldValue, newValue) -> updateContent());

        cellMask.widthProperty().bind(getSkinnable().widthProperty());
        cellMask.heightProperty().bind(getSkinnable().heightProperty());

        cell.setClip(cellMask);

        if (column.getColumnMaxWidth() > 0) {
            getSkinnable().setMaxWidth(column.getColumnMaxWidth());
        }

        getChildren().addAll(cell);
    }

    private void updateContent() {
        Object content;
        if (Objects.isNull(getSkinnable().getItem())) {
            content = isHeaderCell ? column.getColumnName() : "";
        } else {
            content = isHeaderCell ? column.getColumnName() : Objects.requireNonNullElse(column.getCellValueFactory().call(getSkinnable().getItem()), "");
        }

        if (content instanceof String) {
            content = ((String) content).replaceAll("\r\n", "\n");
        }

        Node cellContent = (content instanceof Node) ? (Node) content : new Label(content.toString());

        if (cellContent instanceof Label) {
            ((Label) cellContent).setWrapText(true);
        }

        cellContent.addEventHandler(ScrollEvent.ANY, event -> getSkinnable().fireEvent(event.copyFor(getSkinnable(), getSkinnable())));

        cell.getChildren().setAll(cellContent);
    }
}
