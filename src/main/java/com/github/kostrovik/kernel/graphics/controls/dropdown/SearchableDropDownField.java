package com.github.kostrovik.kernel.graphics.controls.dropdown;

import com.github.kostrovik.kernel.graphics.controls.common.SelectionModel;
import com.github.kostrovik.kernel.interfaces.PaginationServiceInterface;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;
import javafx.util.Callback;

import java.util.Objects;

/**
 * project: kernel
 * author:  kostrovik
 * date:    22/08/2018
 * github:  https://github.com/kostrovik/kernel
 */
public class SearchableDropDownField<E extends Comparable> extends Control {
    private StringProperty label;
    private BooleanProperty showLabel;
    private ObjectProperty<Callback<E, String>> listLabelCallback;
    private PaginationServiceInterface<E> paginationService;
    private String lookupAttribute;
    private SelectionModel<E> selectionModel;

    public SearchableDropDownField(PaginationServiceInterface<E> paginationService, String label, String lookupAttribute) {
        this(label, lookupAttribute);
        Objects.requireNonNull(paginationService);
        this.paginationService = paginationService;
    }

    private SearchableDropDownField(String label, String lookupAttribute) {
        this.label = new SimpleStringProperty(label);
        this.showLabel = new SimpleBooleanProperty(true);
        this.listLabelCallback = new SimpleObjectProperty<>(Object::toString);
        this.selectionModel = new SelectionModel<>();
        this.lookupAttribute = lookupAttribute;

        getStyleClass().add("drop-down");
        getStylesheets().add(this.getClass().getResource("/com/github/kostrovik/styles/controls/searchable-dropdown.css").toExternalForm());
    }

    public String getLabel() {
        return label.get();
    }

    public StringProperty labelProperty() {
        return label;
    }

    public void setLabel(String label) {
        this.label.set(label);
    }

    public boolean isShowLabel() {
        return showLabel.get();
    }

    public BooleanProperty showLabelProperty() {
        return showLabel;
    }

    public void setShowLabel(boolean showLabel) {
        this.showLabel.set(showLabel);
    }

    public Callback<E, String> getListLabelCallback() {
        return listLabelCallback.get();
    }

    public ObjectProperty<Callback<E, String>> listLabelCallbackProperty() {
        return listLabelCallback;
    }

    public void setListLabelCallback(Callback<E, String> listLabelCallback) {
        this.listLabelCallback.set(listLabelCallback);
    }

    public PaginationServiceInterface<E> getPaginationService() {
        return paginationService;
    }

    public String getLookupAttribute() {
        return lookupAttribute;
    }

    public SelectionModel<E> getSelectionModel() {
        return selectionModel;
    }

    @Override
    protected Skin<?> createDefaultSkin() {
        return new SearchableDropDownFieldSkin<>(this);
    }
}