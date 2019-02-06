package com.github.kostrovik.kernel.graphics.controls.paged.rows;

import com.github.kostrovik.kernel.graphics.controls.paged.table.PagedTable;
import javafx.scene.control.Skin;

/**
 * project: kernel
 * author:  kostrovik
 * date:    2018-09-09
 * github:  https://github.com/kostrovik/kernel
 */
public class PagedHeaderRow<E> extends RowBase<E> {
    public PagedHeaderRow(PagedTable<E> table) {
        super(table);
        getStyleClass().setAll("paged-table-header-row");
    }

    @Override
    protected Skin<?> createDefaultSkin() {
        return new PagedHeaderRowSkin<>(this);
    }
}
