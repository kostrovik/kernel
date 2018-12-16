package com.github.kostrovik.kernel.graphics.controls.dropdown;

import com.github.kostrovik.kernel.interfaces.controls.PaginationServiceInterface;
import com.github.kostrovik.useful.utils.InstanceLocatorUtil;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;
import javafx.util.Callback;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * project: kernel
 * author:  kostrovik
 * date:    22/08/2018
 * github:  https://github.com/kostrovik/kernel
 */
public class SearchableDropDownField<T extends Comparable> extends Control {
    private static Logger logger = InstanceLocatorUtil.getLocator().getLogger(SearchableDropDownField.class.getName());

    private StringProperty label;
    private BooleanProperty showLabel;
    private Callback<T, String> listLabelCallback;
    private ObservableList<T> selectedItems;
    private BooleanProperty multiple;

    private ObjectProperty<PaginationServiceInterface<T>> paginationService;
    private String lookupAttribute;

    public SearchableDropDownField(String label, String lookupAttribute) {
        this(label, true, lookupAttribute);
    }

    public SearchableDropDownField(String label, boolean showLabel, String lookupAttribute) {
        this(label);
        this.showLabel = new SimpleBooleanProperty(showLabel);
        this.lookupAttribute = lookupAttribute;
    }

    private SearchableDropDownField(String label) {
        this.label = new SimpleStringProperty(label);
        this.listLabelCallback = param -> param.toString();
        this.selectedItems = FXCollections.observableArrayList();
        this.multiple = new SimpleBooleanProperty(true);
        this.paginationService = new SimpleObjectProperty<>();

        getStyleClass().add("drop-down");

        try {
            getStylesheets().add(Class.forName(this.getClass().getName()).getResource("/com/github/kostrovik/styles/controls/searchable-dropdown.css").toExternalForm());
        } catch (ClassNotFoundException error) {
            logger.log(Level.WARNING, "Ошибка загрузки стилей.", error);
        }
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

    public Callback<T, String> getListLabelCallback() {
        return listLabelCallback;
    }

    public void setListLabelCallback(Callback<T, String> listLabelCallback) {
        this.listLabelCallback = listLabelCallback;
    }

    public ObservableList<T> getSelectedItems() {
        return selectedItems;
    }

    public boolean isMultiple() {
        return multiple.get();
    }

    public BooleanProperty multipleProperty() {
        return multiple;
    }

    public void setMultiple(boolean multiple) {
        this.multiple.set(multiple);
    }

    public PaginationServiceInterface getPaginationService() {
        return paginationService.get();
    }

    public ObjectProperty<PaginationServiceInterface<T>> paginationServiceProperty() {
        return paginationService;
    }

    public void setPaginationService(PaginationServiceInterface paginationService) {
        this.paginationService.set(paginationService);
    }

    public String getLookupAttribute() {
        return lookupAttribute;
    }

    @Override
    protected Skin<?> createDefaultSkin() {
        return new SearchableDropDownFieldSkin<>(this);
    }
}