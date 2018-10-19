package com.github.kostrovik.kernel.graphics.controls.dropdown;

import com.github.kostrovik.kernel.dictionaries.SortDirection;
import com.github.kostrovik.kernel.models.AbstractListFilter;

import java.util.*;

/**
 * project: kernel
 * author:  kostrovik
 * date:    30/08/2018
 * github:  https://github.com/kostrovik/kernel
 */
public class DropDownListFilter extends AbstractListFilter {
    private Map<String, SortDirection> sortBy;
    private Map<String, SortDirection> defaultSortBy;
    private String attribute;
    private Map<String, Map<String, Object>> filters;

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
            notifyListeners();
        }
    }

    @Override
    public List<Map<String, Object>> getFilters() {
        return new ArrayList<>(filters.values());
    }

    @Override
    public void clear() {
        this.sortBy = defaultSortBy;
        this.filters.clear();
        this.valueFilter.clear();

        notifyListeners();
    }

    public void setValueFilter(String value) {
        if (value == null || value.isEmpty()) {
            filters.remove(getFilterKey(attribute));
        } else {
            valueFilter.put(attribute, value);
            filters.put(getFilterKey(attribute), valueFilter);
        }
        notifyListeners();
    }

    public String getValueFilter() {
        return (String) filters.getOrDefault(getFilterKey(attribute), new HashMap<>()).getOrDefault(attribute, "");
    }

    private String getFilterKey(String attribute) {
        return attribute + "Filter";
    }
}
