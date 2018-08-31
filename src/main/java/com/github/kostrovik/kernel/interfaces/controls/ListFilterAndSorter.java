package com.github.kostrovik.kernel.interfaces.controls;

import com.github.kostrovik.kernel.interfaces.Observable;

import java.util.List;
import java.util.Map;

/**
 * project: kernel
 * author:  kostrovik
 * date:    23/08/2018
 * github:  https://github.com/kostrovik/kernel
 */
public interface ListFilterAndSorter extends Observable {
    String getSortBy();

    void setSortBy(String sortBy);

    String getSortDirection();

    void setSortDirection(String sortDirection);

    List<Map<String, Object>> getFilters();

    void clear();
}