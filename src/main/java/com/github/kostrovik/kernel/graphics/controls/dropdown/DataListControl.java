package com.github.kostrovik.kernel.graphics.controls.dropdown;

import com.github.kostrovik.kernel.graphics.controls.common.SelectionModel;
import com.github.kostrovik.kernel.interfaces.FilterAttributeSetter;
import com.github.kostrovik.kernel.interfaces.PaginationServiceInterface;
import com.github.kostrovik.kernel.models.ListFilterAndSorter;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;
import javafx.util.Callback;

import java.util.Objects;

/**
 * project: kernel
 * author:  kostrovik
 * date:    2019-01-23
 * github:  https://github.com/kostrovik/kernel
 */
public class DataListControl<E> extends Control {
    private ObjectProperty<Callback<E, String>> callback;
    private ListFilterAndSorter filter;
    private PaginationServiceInterface<E> service;
    private String filterAttribute;
    private SelectionModel<E> selectionModel;

    DataListControl(Callback<E, String> callback, PaginationServiceInterface<E> service, String filterAttribute, SelectionModel<E> selectionModel) {
        this.callback = new SimpleObjectProperty<>(callback);
        this.service = service;
        this.filter = new ListFilterAndSorter(filterAttribute);
        this.filter.addFilterAttribute(new FilterAttributeSetter<String>() {
            @Override
            public String getAttributeName() {
                return filterAttribute;
            }

            @Override
            public Object prepareValue(String value) {
                if (Objects.isNull(value) || value.isBlank()) {
                    return null;
                }
                return value;
            }
        });

        this.filterAttribute = filterAttribute;
        this.selectionModel = selectionModel;
        getStylesheets().add(this.getClass().getResource("/com/github/kostrovik/styles/controls/searchable-dropdown.css").toExternalForm());
    }

    public Callback<E, String> getCallback() {
        return callback.get();
    }

    public ObjectProperty<Callback<E, String>> callbackProperty() {
        return callback;
    }

    public void setCallback(Callback<E, String> callback) {
        this.callback.set(callback);
    }

    public ListFilterAndSorter getFilter() {
        return filter;
    }

    public PaginationServiceInterface<E> getService() {
        return service;
    }

    public String getFilterAttribute() {
        return filterAttribute;
    }

    public SelectionModel<E> getSelectionModel() {
        return selectionModel;
    }

    @Override
    protected Skin<?> createDefaultSkin() {
        return new DataListControlSkin<>(this);
    }

    void clear() {
        DataListControlSkin<E> skin = (DataListControlSkin<E>) getSkin();
        skin.clear();
    }
}