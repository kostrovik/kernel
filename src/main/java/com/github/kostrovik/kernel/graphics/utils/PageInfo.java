package com.github.kostrovik.kernel.graphics.utils;

import com.github.kostrovik.kernel.interfaces.ListFilterAndSorterInterface;
import com.github.kostrovik.kernel.models.EmptyListFilter;

import java.util.Objects;

/**
 * project: kernel
 * author:  kostrovik
 * date:    28/08/2018
 * github:  https://github.com/kostrovik/kernel
 */
public class PageInfo {
    private final int offset;
    private final int pageSize;
    private ListFilterAndSorterInterface filter;

    public PageInfo(int offset, int pageSize, ListFilterAndSorterInterface filter) {
        this(offset, pageSize);
        this.filter = Objects.requireNonNullElse(filter, getDefaultFilter());
    }

    public PageInfo(int offset, int pageSize) {
        this.offset = offset < 0 ? 0 : offset;
        this.pageSize = pageSize < 0 ? 0 : pageSize;
        this.filter = getDefaultFilter();
    }

    public int getOffset() {
        return offset;
    }

    public int getPageSize() {
        return pageSize;
    }

    public ListFilterAndSorterInterface getFilter() {
        return filter;
    }

    private ListFilterAndSorterInterface getDefaultFilter() {
        return new EmptyListFilter();
    }
}
