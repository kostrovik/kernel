package com.github.kostrovik.kernel.graphics.helper;

import com.github.kostrovik.kernel.interfaces.controls.ListFilterAndSorterInterface;
import com.github.kostrovik.kernel.models.EmptyListFilter;

import java.util.Objects;

/**
 * project: kernel
 * author:  kostrovik
 * date:    28/08/2018
 * github:  https://github.com/kostrovik/kernel
 */
public class PageInfo {
    private int offset;
    private int pageNumber;
    private int pageSize;
    private boolean hasNextPage;
    private ListFilterAndSorterInterface filter;
    private ListFilterAndSorterInterface defaultFilter;

    public PageInfo() {
        this.offset = 0;
        this.pageSize = 0;
        this.pageNumber = 0;
        this.hasNextPage = false;
        this.filter = getDefaultFilter();
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public ListFilterAndSorterInterface getFilter() {
        return filter;
    }

    public void setFilter(ListFilterAndSorterInterface filter) {
        this.filter = Objects.requireNonNullElse(filter, getDefaultFilter());
        defaultFilter = filter;
    }

    public boolean isHasNextPage() {
        return hasNextPage;
    }

    public void setHasNextPage(boolean hasNextPage) {
        this.hasNextPage = hasNextPage;
    }

    public int getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
    }

    private ListFilterAndSorterInterface getDefaultFilter() {
        if (defaultFilter == null) {
            return new EmptyListFilter();
        }
        return defaultFilter;
    }
}
