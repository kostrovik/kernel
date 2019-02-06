package com.github.kostrovik.kernel.graphics.controls.paged.selection;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * project: kernel
 * author:  kostrovik
 * date:    2018-09-12
 * github:  https://github.com/kostrovik/kernel
 */
public class TableSelectionModel<E> {
    private ObservableList<E> selectedItems;
    private E current;

    private boolean multiSelect;

    public TableSelectionModel() {
        this.selectedItems = FXCollections.observableArrayList();
        this.multiSelect = false;
    }

    public ObservableList<E> getSelectedItems() {
        return selectedItems;
    }

    public E getCurrent() {
        return current;
    }

    public void addItem(E item) {
        if (Objects.nonNull(item) && !selectedItems.contains(item)) {
            if (multiSelect) {
                selectedItems.add(item);
            } else {
                selectedItems.setAll(item);
            }
        }
        updateCurrent();
    }

    public void addItems(E... item) {
        List<E> items = Stream.of(item).filter(Objects::nonNull).collect(Collectors.toList());
        addList(items);
    }

    public void addItems(List<E> items) {
        if (Objects.nonNull(items)) {
            List<E> filteredItems = items.stream().filter(Objects::nonNull).collect(Collectors.toList());
            addList(filteredItems);
        }
    }

    private void addList(List<E> items) {
        Stream<E> elements = items.stream().filter(param -> !selectedItems.contains(param));
        if (multiSelect) {
            selectedItems.addAll(elements.collect(Collectors.toList()));
        } else {
            elements.findFirst().ifPresentOrElse(e -> selectedItems.setAll(e), () -> selectedItems.clear());
        }
        updateCurrent();
    }

    public void removeItem(E item) {
        selectedItems.remove(item);
        updateCurrent();
    }

    public boolean isMultiSelect() {
        return multiSelect;
    }

    public void setMultiSelect(boolean multiSelect) {
        this.multiSelect = multiSelect;
    }

    private void updateCurrent(){
        current = selectedItems.isEmpty() ? null : selectedItems.get(selectedItems.size() - 1);
    }
}
