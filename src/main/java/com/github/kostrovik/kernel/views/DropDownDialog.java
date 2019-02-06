package com.github.kostrovik.kernel.views;

import com.github.kostrovik.kernel.graphics.builders.ButtonBuilder;
import com.github.kostrovik.kernel.graphics.common.icons.SolidIcons;
import com.github.kostrovik.kernel.graphics.controls.common.columns.CommonColumn;
import com.github.kostrovik.kernel.graphics.controls.dropdown.SearchableDropDownField;
import com.github.kostrovik.kernel.graphics.controls.paged.InfinityTable;
import com.github.kostrovik.kernel.interfaces.FilterAttributeSetter;
import com.github.kostrovik.kernel.models.AbstractPopupWindow;
import com.github.kostrovik.kernel.models.ListFilterAndSorter;
import com.github.kostrovik.kernel.services.DropDownSelectedService;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Separator;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.EventObject;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * project: kernel
 * author:  kostrovik
 * date:    29/08/2018
 * github:  https://github.com/kostrovik/kernel
 */
public class DropDownDialog<T extends Comparable<T>> extends AbstractPopupWindow {
    private InfinityTable<T> items;
    private InfinityTable<T> selected;
    private ButtonBuilder buttonBuilder;
    private SearchableDropDownField<T> dropDownField;

    private ListFilterAndSorter defaultFilter;
    private ListFilterAndSorter defaultFilterSelected;

    private ObservableList<T> selectedItems;
    private List<T> selectedItemsCache;

    public DropDownDialog(Pane parent) {
        super(parent);
        this.buttonBuilder = new ButtonBuilder();
    }

    @Override
    public void setStage(Stage stage) {
        this.stage = stage;
        super.setWindowSize();
    }

    @Override
    public void initView(EventObject event) {
        dropDownField = (SearchableDropDownField<T>) event.getSource();
        if (dropDownField instanceof SearchableDropDownField) {
            selectedItems = dropDownField.getSelectionModel().getItems();
            selectedItemsCache = new ArrayList<>(dropDownField.getSelectionModel().getItems());

            defaultFilter = new ListFilterAndSorter(dropDownField.getLookupAttribute());
            defaultFilter.addFilterAttribute(new FilterAttributeSetter<String>() {
                @Override
                public String getAttributeName() {
                    return dropDownField.getLookupAttribute();
                }

                @Override
                public Object prepareValue(String value) {
                    if (Objects.isNull(value) || value.isBlank()) {
                        return null;
                    }
                    return value;
                }
            });

            defaultFilterSelected = new ListFilterAndSorter(dropDownField.getLookupAttribute());
            defaultFilterSelected.addFilterAttribute(new FilterAttributeSetter<String>() {
                @Override
                public String getAttributeName() {
                    return dropDownField.getLookupAttribute();
                }

                @Override
                public Object prepareValue(String value) {
                    if (Objects.isNull(value) || value.isBlank()) {
                        return null;
                    }
                    return value;
                }
            });
        }
        createView();
    }

    protected String getWindowTitle() {
        return "Диалог выбора.";
    }

    @Override
    protected Region getWindowContent() {
        createTable();
        createSelectedItemsTable();

        Button button = buttonBuilder.createButton(SolidIcons.ARROW_ALT_RIGHT_LIGHT, SolidIcons.ARROW_ALT_RIGHT_SOLID, "", true);
        button.setOnAction(event -> selectedItems.addAll(items.getSelectionModel().getSelectedItems().stream().filter(item -> !selectedItems.contains(item)).collect(Collectors.toList())));

        button.setFocusTraversable(false);
        button.prefWidthProperty().bind(button.heightProperty());
        button.setPrefHeight(44);
        button.setMinWidth(0);

        GridPane formLayout = new GridPane();
        formLayout.setHgap(10);
        formLayout.setVgap(10);
        formLayout.setPadding(new Insets(10, 0, 0, 0));

        items.prefWidthProperty().bind(view.widthProperty().divide(2).subtract(button.widthProperty()));
        selected.prefWidthProperty().bind(view.widthProperty().divide(2).subtract(button.widthProperty()));

        formLayout.addRow(0, items, button, selected);

        formLayout.prefHeightProperty().bind(view.heightProperty());
        formLayout.prefWidthProperty().bind(view.widthProperty());

        GridPane.setVgrow(items, Priority.ALWAYS);
        GridPane.setVgrow(selected, Priority.ALWAYS);

        GridPane.setHgrow(items, Priority.ALWAYS);
        GridPane.setHgrow(selected, Priority.ALWAYS);

        return formLayout;
    }

    @Override
    protected Collection<Node> getWindowButtons() {
        Button clearButton = buttonBuilder.createButton("Очистить");

        Separator separator = new Separator();
        separator.setOrientation(Orientation.VERTICAL);

        Button saveButton = buttonBuilder.createButton("Сохранить");
        Button cancelButton = buttonBuilder.createButton("Отмена");

        clearButton.setOnAction(event -> {
            if (dropDownField instanceof SearchableDropDownField) {
                selectedItems.clear();
            }
        });

        saveButton.setOnAction(event -> {
            notifyListeners(selectedItems);
            stage.close();
        });

        cancelButton.setOnAction(event -> {
            selectedItems.setAll(selectedItemsCache);
            stage.close();
        });

        return Arrays.asList(clearButton, separator, saveButton, cancelButton);
    }

    private void createTable() {
        items = new InfinityTable<>(dropDownField.getPaginationService(), defaultFilter, false);

        CommonColumn<T, String> name = new CommonColumn<>("Название");
        name.setCellValueFactory(dropDownField.getListLabelCallback());
        items.getColumns().addAll(name);
        items.getSelectionModel().setMultiSelect(dropDownField.getSelectionModel().isMultiSelect());
    }

    private void createSelectedItemsTable() {
        selected = new InfinityTable<>(new DropDownSelectedService<>(dropDownField), defaultFilterSelected, false);

        CommonColumn<T, String> name = new CommonColumn<>("Название");
        name.setCellValueFactory(dropDownField.getListLabelCallback());

        CommonColumn<T, Button> action = new CommonColumn<>("Действие");
        Callback<T, Button> actionValueCallback = param -> {
            Button actionButton = buttonBuilder.createButton("Удалить");
            actionButton.setOnAction(event -> removeItem(param));
            return actionButton;
        };
        action.setCellValueFactory(actionValueCallback);

        selected.getColumns().addAll(name, action);
        selected.setSelectable(false);

        selectedItems.addListener((ListChangeListener<T>) c -> defaultFilterSelected.clear());
    }

    private void removeItem(T item) {
        selectedItems.remove(item);
    }
}