package com.github.kostrovik.kernel.interfaces.controls;

import com.github.kostrovik.kernel.models.PagedList;

/**
 * project: kernel
 * author:  kostrovik
 * date:    23/08/2018
 * github:  https://github.com/kostrovik/kernel
 */
public interface PaginationServiceInterface<T> {
    PagedList<T> getFilteredList(int offset, int pageSize, ListFilterAndSorter conditions);
}
