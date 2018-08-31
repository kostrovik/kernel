package com.github.kostrovik.kernel.graphics.helper;

import com.github.kostrovik.kernel.interfaces.controls.ListFilterAndSorter;

/**
 * project: kernel
 * author:  kostrovik
 * date:    28/08/2018
 * github:  https://github.com/kostrovik/kernel
 */
public class PageInfo {
    private int offset;
    private int pageSize;
    private ListFilterAndSorter filter;
    private boolean hasNextPage;

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

    public ListFilterAndSorter getFilter() {
        return filter;
    }

    public void setFilter(ListFilterAndSorter filter) {
        this.filter = filter;
    }

    public boolean isHasNextPage() {
        return hasNextPage;
    }

    public void setHasNextPage(boolean hasNextPage) {
        this.hasNextPage = hasNextPage;
    }
}
