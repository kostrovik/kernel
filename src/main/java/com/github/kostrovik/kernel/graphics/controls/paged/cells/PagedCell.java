package com.github.kostrovik.kernel.graphics.controls.paged.cells;

import com.github.kostrovik.kernel.graphics.controls.common.cells.CellBase;
import com.github.kostrovik.kernel.graphics.controls.common.columns.CommonColumn;
import com.github.kostrovik.kernel.graphics.controls.paged.table.PagedTable;
import javafx.scene.control.Skin;

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
public class PagedCell<E, V> extends CellBase<E> {
    private CommonColumn<E, V> column;
    private boolean isHeaderCell;
    private PagedTable<E> table;

    public PagedCell(PagedTable<E> table, CommonColumn<E, V> column, E item, boolean isHeaderCell) {
        super(item);

        Objects.requireNonNull(table);
        this.table = table;

        Objects.requireNonNull(column);
        this.column = column;
        setCellValueFactory(column.getCellValueFactory());

        this.isHeaderCell = isHeaderCell;
    }

    public PagedCell(PagedTable<E> table, CommonColumn<E, V> column, boolean isHeaderCell) {
        this(table, column, null, isHeaderCell);
    }

    public CommonColumn<E, V> getColumn() {
        return column;
    }

    public boolean isHeaderCell() {
        return isHeaderCell;
    }

    public PagedTable<E> getTable() {
        return table;
    }

    @Override
    protected Skin<?> createDefaultSkin() {
        return new PagedCellSkin<>(this);
    }
}
