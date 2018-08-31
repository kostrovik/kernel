package com.github.kostrovik.kernel.graphics.controls.list;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;
import javafx.scene.control.ListView;
import javafx.scene.control.Skin;

/**
 * project: kernel
 * author:  kostrovik
 * date:    30/08/2018
 * github:  https://github.com/kostrovik/kernel
 */
public class ScrollableListView<S> extends ListView<S> {
    private final ObjectProperty<Integer> firstVisibleIndex;
    private final ObjectProperty<Integer> lastVisibleIndex;


    public ScrollableListView() {
        this.firstVisibleIndex = new SimpleObjectProperty<>();
        this.lastVisibleIndex = new SimpleObjectProperty<>();
    }

    public ScrollableListView(ObservableList<S> items) {
        super(items);
        this.firstVisibleIndex = new SimpleObjectProperty<>();
        this.lastVisibleIndex = new SimpleObjectProperty<>();
    }

    public ObjectProperty<Integer> firstVisibleProperty() {
        return firstVisibleIndex;
    }

    public Integer getFirstVisibleIndex() {
        return firstVisibleIndex.get();
    }

    public void setFirstVisibleIndex(Integer index) {
        firstVisibleIndex.set(index);
    }

    public ObjectProperty<Integer> lastVisibleProperty() {
        return lastVisibleIndex;
    }

    public Integer getLastVisibleIndex() {
        return lastVisibleIndex.get();
    }

    public void setLastVisibleIndex(Integer index) {
        lastVisibleIndex.set(index);
    }

    @Override
    protected Skin<?> createDefaultSkin() {
        return new ScrollableListViewSkin<>(this);
    }
}
