package com.github.kostrovik.kernel.graphics.controls.base.selection;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.*;
import java.util.stream.Collectors;

/**
 * project: kernel
 * author:  kostrovik
 * date:    2018-09-12
 * github:  https://github.com/kostrovik/kernel
 */
public class PagedTableMultiSelectionModel<E> {
    private ObservableList<E> selectedItems;

    private boolean isMultiselect;

    public PagedTableMultiSelectionModel() {
        this.selectedItems = FXCollections.observableArrayList();
        this.isMultiselect = false;
    }

    public ObservableList<E> getSelectedItems() {
        return selectedItems;
    }

    public void addSelectedItem(E item) {
        if (Objects.nonNull(item) && !selectedItems.contains(item)) {
            if (!isMultiselect) {
                selectedItems.clear();
            }
            selectedItems.add(item);
        }
    }

    public void addSelectedItem(E... item) {
        List<E> items = new ArrayList<>(Arrays.asList(item)).stream().filter(param -> Objects.nonNull(param) && !selectedItems.contains(param)).collect(Collectors.toList());

        addFilteredItems(items);
    }

    public void addSelectedItem(List<E> items) {
        if (Objects.nonNull(items)) {
            List<E> filteredItems = items.stream().filter(param -> Objects.nonNull(param) && !selectedItems.contains(param)).collect(Collectors.toList());

            addFilteredItems(filteredItems);
        }
    }

    private void addFilteredItems(List<E> items) {
        if (!items.isEmpty()) {
            if (!isMultiselect) {
                selectedItems.clear();
                selectedItems.add(items.get(items.size() - 1));
            } else {
                selectedItems.addAll(items);
            }
        }
    }

    public void removeSelectedItem(E item) {
        selectedItems.remove(item);
    }

    public boolean isMultiselect() {
        return isMultiselect;
    }

    public void setMultiselect(boolean multiselect) {
        isMultiselect = multiselect;
    }
}
