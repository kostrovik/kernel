package com.github.kostrovik.kernel.graphics.controls.common;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * project: kernel
 * author:  kostrovik
 * date:    2019-02-03
 * github:  https://github.com/kostrovik/kernel
 */
public class SelectionModel<E> {
    private ObservableList<E> items;
    private E current;
    private BooleanProperty multiSelect;

    public SelectionModel() {
        this.items = FXCollections.observableArrayList();
        this.multiSelect = new SimpleBooleanProperty(false);
        this.multiSelect.addListener((observable, oldValue, newValue) -> items.remove(0, items.size() - 1));
    }

    public ObservableList<E> getItems() {
        return items;
    }

    public E getCurrent() {
        return current;
    }

    public void addItem(E item) {
        addItems(item);
    }

    public void addItems(E... item) {
        addList(Stream.of(item).filter(Objects::nonNull).collect(Collectors.toList()));
    }

    public void addItems(List<E> items) {
        if (Objects.nonNull(items)) {
            addList(items.stream().filter(Objects::nonNull).collect(Collectors.toList()));
        }
    }

    private void addList(List<E> items) {
        Stream<E> elements = items.stream().filter(param -> !this.items.contains(param));
        if (multiSelect.get()) {
            this.items.addAll(elements.collect(Collectors.toList()));
        } else {
            elements.findFirst().ifPresentOrElse(e -> this.items.setAll(e), () -> this.items.clear());
        }
        updateCurrent();
    }

    public void removeItem(E item) {
        items.remove(item);
        updateCurrent();
    }

    public boolean isMultiSelect() {
        return multiSelect.get();
    }

    public BooleanProperty multiSelectProperty() {
        return multiSelect;
    }

    public void setMultiSelect(boolean multiSelect) {
        this.multiSelect.set(multiSelect);
    }

    private void updateCurrent() {
        current = items.isEmpty() ? null : items.get(items.size() - 1);
    }
}