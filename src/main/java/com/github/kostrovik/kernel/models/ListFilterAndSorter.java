package com.github.kostrovik.kernel.models;

import com.github.kostrovik.kernel.dictionaries.SortDirection;
import com.github.kostrovik.kernel.interfaces.FilterAttributeSetter;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * project: kernel
 * author:  kostrovik
 * date:    2019-01-11
 * github:  https://github.com/kostrovik/kernel
 */
public class ListFilterAndSorter extends EmptyListFilter {
    private SortDirection sortDirection;
    private Map<String, SortDirection> sortBy;
    private Map<String, Object> filters;

    private Map<String, FilterAttributeSetter> setters;

    public ListFilterAndSorter(String attribute) {
        this.sortDirection = SortDirection.ASC;
        this.sortBy = new HashMap<>();
        this.sortBy.put(attribute, sortDirection);

        this.filters = new HashMap<>();
        this.setters = new HashMap<>();
    }

    @Override
    public Map<String, SortDirection> getSortBy() {
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
        this.filters.clear();
        notifyListeners(this);
    }

    public void addFilterAttribute(FilterAttributeSetter<?> attributeSetter) {
        Objects.requireNonNull(attributeSetter);
        if (Objects.nonNull(attributeSetter.getAttributeName())) {
            setters.putIfAbsent(attributeSetter.getAttributeName(), attributeSetter);
        }
    }

    public void setFilterAttributeValue(String name, Object value) {
        FilterAttributeSetter setter = setters.get(name);
        Objects.requireNonNull(setter);
        Object filterValue = setter.prepareValue(value);
        if (Objects.isNull(filterValue)) {
            filters.remove(setter.getAttributeName());
        } else {
            filters.put(setter.getAttributeName(), filterValue);
        }
        notifyListeners(this);
    }

    public Object getFilterAttribute(String name) {
        return filters.get(name);
    }
}