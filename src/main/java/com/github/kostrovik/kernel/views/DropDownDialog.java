package com.github.kostrovik.kernel.views;

import com.github.kostrovik.kernel.dictionaries.SortDirection;
import com.github.kostrovik.kernel.graphics.builders.ButtonBuilder;
import com.github.kostrovik.kernel.graphics.common.icons.SolidIcons;
import com.github.kostrovik.kernel.graphics.controls.base.InfinityTable;
import com.github.kostrovik.kernel.graphics.controls.base.columns.PagedColumn;
import com.github.kostrovik.kernel.interfaces.controls.ListFilterAndSorterInterface;
import com.github.kostrovik.kernel.interfaces.controls.PaginationServiceInterface;
import com.github.kostrovik.kernel.models.AbstractPopupWindow;
import com.github.kostrovik.kernel.models.EmptyListFilter;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Separator;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EventObject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * project: kernel
 * author:  kostrovik
 * date:    29/08/2018
 * github:  https://github.com/kostrovik/kernel
 */
public class DropDownDialog<T> extends AbstractPopupWindow {
    private PaginationServiceInterface<T> paginationService;
    private Callback<T, String> listLabelCallback;
    private InfinityTable<T> table;
    private InfinityTable<T> selectedItemsTable;

    private ObservableList<T> selectedItems;
    private ButtonBuilder buttonBuilder;

    public DropDownDialog(Pane parent) {
        super(parent);
        this.selectedItems = FXCollections.observableArrayList();
        this.buttonBuilder = new ButtonBuilder();
    }

    @Override
    public void setStage(Stage stage) {
        this.stage = stage;
        super.setDefaultWindowSize();
    }

    @Override
    public void initView(EventObject event) {
        Map<String, Object> data = (Map<String, Object>) event.getSource();
        paginationService = (PaginationServiceInterface<T>) data.get("service");
        listLabelCallback = (Callback<T, String>) data.get("callback");
        selectedItems.setAll((Collection<? extends T>) data.get("selectedItems"));

        createView();
    }

    protected String getWindowTitle() {
        return "Список множественного выбора.";
    }

    @Override
    protected Region getWindowContent() {
        createTable();
        createSelectedItemsTable();

        Button button = buttonBuilder.createButton(SolidIcons.ARROW_ALT_RIGHT_LIGHT, SolidIcons.ARROW_ALT_RIGHT_SOLID, "", true);

        button.setOnAction(event -> selectedItems.addAll(table.getSelectedItems().stream().filter(item -> !selectedItems.contains(item)).collect(Collectors.toList())));

        button.setFocusTraversable(false);
        button.prefWidthProperty().bind(button.heightProperty());
        button.setPrefHeight(44);
        button.setMinWidth(0);

        GridPane formLayout = new GridPane();
        formLayout.setHgap(10);
        formLayout.setVgap(10);
        formLayout.setPadding(new Insets(10, 0, 0, 0));

        table.prefWidthProperty().bind(view.widthProperty().divide(2).subtract(button.widthProperty()));
        selectedItemsTable.prefWidthProperty().bind(view.widthProperty().divide(2).subtract(button.widthProperty()));

        formLayout.addRow(0, table, button, selectedItemsTable);

        formLayout.prefHeightProperty().bind(view.heightProperty());
        formLayout.prefWidthProperty().bind(view.widthProperty());

        GridPane.setVgrow(table, Priority.ALWAYS);
        GridPane.setVgrow(selectedItemsTable, Priority.ALWAYS);

        GridPane.setHgrow(table, Priority.ALWAYS);
        GridPane.setHgrow(selectedItemsTable, Priority.ALWAYS);

        return formLayout;
    }

    @Override
    protected Region getWindowButtons() {
        Button clearButton = buttonBuilder.createButton("Очистить");
        Separator separator = new Separator();
        separator.setOrientation(Orientation.VERTICAL);

        Button saveButton = buttonBuilder.createButton("Сохранить");
        Button cancelButton = buttonBuilder.createButton("Отмена");

        clearButton.setOnAction(event -> selectedItems.clear());

        saveButton.setOnAction(event -> {
            notifyListeners(selectedItems);
            stage.close();
        });

        cancelButton.setOnAction(event -> stage.close());

        HBox buttonView = new HBox(10);
        buttonView.setPadding(new Insets(10, 10, 10, 10));
        buttonView.getChildren().addAll(clearButton, separator, saveButton, cancelButton);
        buttonView.setAlignment(Pos.CENTER_RIGHT);

        return buttonView;
    }

    private void createTable() {
        ListFilterAndSorterInterface defaultFilter = new EmptyListFilter() {
            @Override
            public Map<String, SortDirection> getSortBy() {
                return new HashMap<>();
            }

            @Override
            public void setSortBy(Map<String, SortDirection> sortBy) {
                // Обект фильтра является заглушкой. Поэтому реализация метода отсутсвует.
            }

            @Override
            public Map<String, Object> getFilter() {
                return new HashMap<>();
            }

            @Override
            public void clear() {
                // Обект фильтра является заглушкой. Поэтому реализация метода отсутсвует.
            }
        };

        table = new InfinityTable<>(paginationService, defaultFilter);

        PagedColumn<T, String> name = new PagedColumn<>("Название");
        name.setCellValueFactory(listLabelCallback);
        table.getColumns().addAll(name);
        table.setMultiselection(true);
    }

    private void createSelectedItemsTable() {
        selectedItemsTable = new InfinityTable<>(selectedItems);

        PagedColumn<T, String> name = new PagedColumn<>("Название");
        name.setCellValueFactory(listLabelCallback);

        PagedColumn<T, Button> action = new PagedColumn<>("Действие");
        Callback<T, Button> actionValueCallback = param -> {
            Button actionButton = buttonBuilder.createButton("Удалить");
            actionButton.setOnAction(event -> removeItem(param));

            actionButton.setMinWidth(73);
            return actionButton;
        };
        action.setCellValueFactory(actionValueCallback);
        action.setColumnMaxWidth(93);

        selectedItemsTable.getColumns().addAll(name, action);
        selectedItemsTable.setSelectable(false);
    }

    private void removeItem(T item) {
        selectedItems.remove(item);
    }
}