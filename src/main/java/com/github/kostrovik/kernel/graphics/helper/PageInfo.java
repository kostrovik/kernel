package com.github.kostrovik.kernel.graphics.helper;

import com.github.kostrovik.kernel.dictionaries.SortDirection;
import com.github.kostrovik.kernel.interfaces.controls.ListFilterAndSorterInterface;
import com.github.kostrovik.kernel.models.AbstractListFilter;

import java.util.*;

/**
 * project: kernel
 * author:  kostrovik
 * date:    28/08/2018
 * github:  https://github.com/kostrovik/kernel
 */
public class PageInfo {
    private int offset;
    private int pageSize;
    private ListFilterAndSorterInterface filter;
    private ListFilterAndSorterInterface defaultFilter;
    private boolean hasNextPage;
    private int pageNumber;

    public PageInfo() {
        this.offset = 0;
        this.pageSize = 0;
        this.filter = createDefaultFilter();
        this.hasNextPage = false;
        this.pageNumber = 0;
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
        this.filter = Objects.requireNonNullElse(filter, createDefaultFilter());
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

    private ListFilterAndSorterInterface createDefaultFilter() {
        if (defaultFilter == null) {
            return new AbstractListFilter() {
                @Override
                public Map<String, SortDirection> getSortBy() {
                    return new HashMap<>();
                }

                @Override
                public void setSortBy(Map<String, SortDirection> sortBy) {
                    // Обект фильтра является заглушкой. Поэтому реализация метода отсутсвует.
                }

                @Override
                public List<Map<String, Object>> getFilters() {
                    return new ArrayList<>();
                }

                @Override
                public void clear() {
                    // Обект фильтра является заглушкой. Поэтому реализация метода отсутсвует.
                }
            };
        }
        return defaultFilter;
    }
}
