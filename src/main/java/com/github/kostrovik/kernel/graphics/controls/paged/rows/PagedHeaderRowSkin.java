package com.github.kostrovik.kernel.graphics.controls.paged.rows;

import com.github.kostrovik.kernel.graphics.controls.paged.cells.PagedCell;
import com.github.kostrovik.kernel.graphics.controls.common.columns.CommonColumn;

/**
 * project: kernel
 * author:  kostrovik
 * date:    2018-09-09
 * github:  https://github.com/kostrovik/kernel
 */
public class PagedHeaderRowSkin<E> extends RowBaseSkin<E> {
    public PagedHeaderRowSkin(PagedHeaderRow<E> control) {
        super(control);
        row.minHeightProperty().bind(table.headerRowHeightProperty());
        row.prefHeightProperty().bind(table.headerRowHeightProperty());
    }

    @Override
    protected PagedCell<E, ?> getCell(CommonColumn<E, ?> column) {
        return new PagedCell<>(table, (CommonColumn<E, String>) column, true);
    }
}