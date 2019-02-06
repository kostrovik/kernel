package com.github.kostrovik.kernel.graphics.controls.paged.rows;

import com.github.kostrovik.kernel.graphics.controls.paged.table.PagedTable;
import javafx.scene.control.Control;

/**
 * project: kernel
 * author:  kostrovik
 * date:    2019-01-20
 * github:  https://github.com/kostrovik/kernel
 */
public abstract class RowBase<E> extends Control {
    private PagedTable<E> table;

    public RowBase(PagedTable<E> table) {
        this.table = table;
    }

    public PagedTable<E> getTable() {
        return table;
    }
}
