package com.github.kostrovik.kernel.graphics.controls.table;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;
import javafx.scene.control.Skin;
import javafx.scene.control.TableView;

/**
 * Created by grom on 17/08/2018.
 * Project kernel
 * author <grom25174@gmail.com>
 */
public class ScrollableTableView<S> extends TableView<S> {
    private final ObjectProperty<Integer> firstVisibleIndex;
    private final ObjectProperty<Integer> lastVisibleIndex;


    public ScrollableTableView() {
        this.firstVisibleIndex = new SimpleObjectProperty<>();
        this.lastVisibleIndex = new SimpleObjectProperty<>();
    }

    public ScrollableTableView(ObservableList<S> items) {
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
        return new ScrollableTableViewSkin<>(this);
    }
}
