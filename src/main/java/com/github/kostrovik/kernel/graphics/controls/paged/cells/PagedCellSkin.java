package com.github.kostrovik.kernel.graphics.controls.paged.cells;

import com.github.kostrovik.kernel.graphics.controls.common.cells.CellBaseSkin;
import javafx.geometry.Pos;
import javafx.scene.Node;

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
public class PagedCellSkin<E, V> extends CellBaseSkin<E, PagedCell<E, V>> {
    public PagedCellSkin(PagedCell<E, V> control) {
        super(control);

        cell.getStyleClass().setAll("paged-table-cell");
    }

    @Override
    protected Object getCellContent() {
        E item = getSkinnable().getItem();
        Object content = getSkinnable().isHeaderCell() ? getSkinnable().getColumn().getColumnName() : "";
        if (Objects.nonNull(item) && !getSkinnable().isHeaderCell()) {
            content = Objects.requireNonNullElse(getSkinnable().getColumn().getCellValueFactory().call(item), "");
        }

        return content;
    }

    @Override
    protected void setContainerSize(Node node) {
        super.setContainerSize(node);
        if (Objects.nonNull(node)) {
            getSkinnable().getColumn().setMinWidth(Math.max(node.minWidth(1) + paddingH.get(), getSkinnable().getColumn().getPrefWidth()));
            getSkinnable().getColumn().setPrefWidth(Math.max(node.prefWidth(1) + paddingH.get(), getSkinnable().getColumn().getPrefWidth()));

            getSkinnable().getTable().setRowHeight((int) Math.max(node.prefHeight(1) + paddingV.get(), getSkinnable().getTable().getRowHeight()));
        }
    }

    @Override
    protected void setCellAlignment() {
        if (getSkinnable().isHeaderCell()) {
            cell.setAlignment(Pos.CENTER);
        } else {
            cell.alignmentProperty().bind(getSkinnable().getColumn().alignmentProperty());
        }
    }
}
