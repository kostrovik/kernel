package com.github.kostrovik.kernel.interfaces;

import com.github.kostrovik.kernel.dictionaries.SortDirection;
import com.github.kostrovik.useful.interfaces.Observable;

import java.util.Map;

/**
 * project: kernel
 * author:  kostrovik
 * date:    23/08/2018
 * github:  https://github.com/kostrovik/kernel
 */
public interface ListFilterAndSorterInterface extends Observable {
    Map<String, SortDirection> getSortBy();

    void setSortBy(Map<String, SortDirection> sortBy);

    Map<String, Object> getFilter();

    void clear();
}