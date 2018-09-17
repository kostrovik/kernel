package com.github.kostrovik.kernel.views;

import com.github.kostrovik.kernel.dictionaries.SortDirection;
import com.github.kostrovik.kernel.graphics.common.icons.SolidIcons;
import com.github.kostrovik.kernel.graphics.controls.base.InfinityTable;
import com.github.kostrovik.kernel.graphics.controls.base.columns.PagedColumn;
import com.github.kostrovik.kernel.interfaces.controls.ListFilterAndSorterInterface;
import com.github.kostrovik.kernel.interfaces.controls.PaginationServiceInterface;
import com.github.kostrovik.kernel.models.AbstractListFilter;
import com.github.kostrovik.kernel.models.AbstractPopupWindow;
import com.github.kostrovik.kernel.settings.Configurator;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Separator;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * project: kernel
 * author:  kostrovik
 * date:    29/08/2018
 * github:  https://github.com/kostrovik/kernel
 */
public class DropDownDialog<T> extends AbstractPopupWindow {
    private static Logger logger = Configurator.getConfig().getLogger(DropDownDialog.class.getName());

    private PaginationServiceInterface<T> paginationService;
    private Callback<T, String> listLabelCallback;
    private InfinityTable<T> table;
    private InfinityTable<T> selectedItemsTable;

    private ObservableList<T> selectedItems;

    public DropDownDialog(Pane parent) {
        super(parent);
        this.selectedItems = FXCollections.observableArrayList();
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

        Button button = facade.createButton("", SolidIcons.ARROW_ALT_RIGHT_LIGHT, SolidIcons.ARROW_ALT_RIGHT_SOLID, true);

        button.setOnAction(event -> selectedItems.addAll(table.getSelectedItems().stream().filter(item -> !selectedItems.contains(item)).collect(Collectors.toList())));

        button.setFocusTraversable(false);
        button.prefWidthProperty().bind(button.heightProperty());
        button.setPrefHeight(44);
        button.setMinWidth(0);

        GridPane formLayout = facade.createTableFormLayout();

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
        Button clearButton = facade.createButton("Очистить");
        Separator separator = new Separator();
        separator.setOrientation(Orientation.VERTICAL);

        Button saveButton = facade.createButton("Сохранить");
        Button cancelButton = facade.createButton("Отмена");

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
        ListFilterAndSorterInterface defaultFilter = new AbstractListFilter() {
            @Override
            public Map<String, SortDirection> getSortBy() {
                return new HashMap<>();
            }

            @Override
            public void setSortBy(Map<String, SortDirection> sortBy) {
                // Обект фильтра является заглушкой. Поэтому реализация метода отсутсвует.
            }

            @Override
            public List<Map<String, Object>> getFilters() {
                return new ArrayList<>();
            }

            @Override
            public void clear() {
                // Обект фильтра является заглушкой. Поэтому реализация метода отсутсвует.
            }
        };

        table = new InfinityTable<>(paginationService, defaultFilter);

        PagedColumn<T, String> name = facade.createTableColumn("Название", listLabelCallback);
        table.getColumns().addAll(name);
        table.setMultiselection(true);
    }

    private void createSelectedItemsTable() {
        selectedItemsTable = new InfinityTable<>(selectedItems);

        PagedColumn<T, String> name = facade.createTableColumn("Название", listLabelCallback);

        PagedColumn<T, Button> action = facade.createTableColumn("Действие");
        Callback<T, Button> actionValueCallback = param -> {
            Button actionButton = facade.createButton("Удалить");
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