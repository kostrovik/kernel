package com.github.kostrovik.kernel.models;

import com.github.kostrovik.kernel.dictionaries.SortDirection;
import com.github.kostrovik.kernel.interfaces.controls.ListFilterAndSorterInterface;
import com.github.kostrovik.useful.models.AbstractObservable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * project: kernel
 * author:  kostrovik
 * date:    2018-09-17
 * github:  https://github.com/kostrovik/kernel
 */
public class EmptyListFilter extends AbstractObservable implements ListFilterAndSorterInterface {
    @Override
    public Map<String, SortDirection> getSortBy() {
        return new HashMap<>();
    }

    @Override
    public void setSortBy(Map<String, SortDirection> sortBy) {
        // Обект фильтра является заглушкой.
    }

    @Override
    public List<Map<String, Object>> getFilters() {
        return new ArrayList<>();
    }

    @Override
    public void clear() {
        // Обект фильтра является заглушкой.
    }
}
