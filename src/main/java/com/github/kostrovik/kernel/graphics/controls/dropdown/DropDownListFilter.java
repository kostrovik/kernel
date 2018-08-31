package com.github.kostrovik.kernel.graphics.controls.dropdown;

import com.github.kostrovik.kernel.interfaces.EventListenerInterface;
import com.github.kostrovik.kernel.interfaces.controls.ListFilterAndSorter;

import java.util.*;

/**
 * project: kernel
 * author:  kostrovik
 * date:    30/08/2018
 * github:  https://github.com/kostrovik/kernel
 */
public class DropDownListFilter implements ListFilterAndSorter {
    private String sortDirection;
    private String sortBy;
    private String defaultSortBy;
    private String attribute;
    private Map<String, Map<String, Object>> filters;

    private Map<String, Object> valueFilter;

    private List<EventListenerInterface> listeners;

    public DropDownListFilter(String sortBy, String attribute) {
        this.sortBy = sortBy;
        this.defaultSortBy = sortBy;
        this.attribute = attribute;
        this.sortDirection = "ASC";
        this.filters = new HashMap<>();
        this.valueFilter = new HashMap<>();

        listeners = new ArrayList<>();
    }

    public String getSortBy() {
        return sortBy;
    }

    public void setSortBy(String sortBy) {
        this.sortBy = sortBy;
        notifyListeners();
    }

    public String getSortDirection() {
        return sortDirection;
    }

    public void setSortDirection(String sortDirection) {
        if (sortDirection.equalsIgnoreCase("DESC")) {
            this.sortDirection = "DESC";
        } else {
            this.sortDirection = "ASC";
        }
        notifyListeners();
    }

    public List<Map<String, Object>> getFilters() {
        return new ArrayList<>(filters.values());
    }

    @Override
    public void clear() {
        this.sortBy = defaultSortBy;
        this.sortDirection = "ASC";
        this.filters.clear();
        this.valueFilter.clear();

        notifyListeners();
    }

    public void setValueFilter(String value) {
        if (value == null || value.isEmpty()) {
            filters.remove("valueFilter");
        } else {
            valueFilter.put(attribute, value);
            filters.put("valueFilter", valueFilter);
        }
        notifyListeners();
    }

    public String getValueFilter() {
        return (String) filters.getOrDefault("valueFilter", new HashMap<>()).getOrDefault(attribute, "");
    }

    @Override
    public void addListener(EventListenerInterface listener) {
        listeners.add(listener);
    }

    @Override
    public void removeListener(EventListenerInterface listener) {
        listeners.remove(listener);
    }

    private void notifyListeners() {
        listeners.forEach(listener -> listener.handle(new EventObject(this)));
    }
}
