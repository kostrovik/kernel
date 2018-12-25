package com.github.kostrovik.kernel.graphics.controls.dropdown;

import com.github.kostrovik.kernel.dictionaries.SortDirection;
import com.github.kostrovik.kernel.models.EmptyListFilter;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * project: kernel
 * author:  kostrovik
 * date:    30/08/2018
 * github:  https://github.com/kostrovik/kernel
 */
public class DropDownListFilter extends EmptyListFilter {
    private Map<String, SortDirection> sortBy;
    private Map<String, SortDirection> defaultSortBy;
    private String attribute;
    private Map<String, Object> filters;

    private Map<String, Object> valueFilter;

    public DropDownListFilter(String attribute) {
        super();
        this.sortBy = new HashMap<>();
        this.defaultSortBy = new HashMap<>();
        this.attribute = attribute;
        this.filters = new HashMap<>();
        this.valueFilter = new HashMap<>();
    }

    @Override
    public Map<String, SortDirection> getSortBy() {
        if (sortBy.isEmpty()) {
            sortBy.put(attribute, SortDirection.ASC);
            defaultSortBy.put(attribute, SortDirection.ASC);
        }
        return sortBy;
    }

    @Override
    public void setSortBy(Map<String, SortDirection> sortBy) {
        if (Objects.nonNull(sortBy)) {
            this.sortBy = sortBy;
            notifyListeners(this);
        }
    }

    @Override
    public Map<String, Object> getFilter() {
        return filters;
    }

    @Override
    public void clear() {
        this.sortBy = defaultSortBy;
        this.filters.clear();
        this.valueFilter.clear();

        notifyListeners(this);
    }

    public void setValueFilter(String value) {
        if (Objects.isNull(value) || value.isEmpty()) {
            filters.remove(attribute);
        } else {
            filters.put(attribute, value);
        }
        notifyListeners(this);
    }

    public String getValueFilter() {
        return (String) filters.getOrDefault(attribute, "");
    }
}
