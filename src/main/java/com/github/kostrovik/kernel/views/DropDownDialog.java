package com.github.kostrovik.kernel.views;

import com.github.kostrovik.kernel.graphics.common.icons.SolidIcons;
import com.github.kostrovik.kernel.graphics.controls.table.InfinityEntityTable;
import com.github.kostrovik.kernel.graphics.controls.table.ScrollableTableView;
import com.github.kostrovik.kernel.interfaces.controls.PaginationServiceInterface;
import com.github.kostrovik.kernel.models.AbstractPopupWindow;
import com.github.kostrovik.kernel.settings.Configurator;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.util.Callback;

import java.util.Collection;
import java.util.EventObject;
import java.util.Map;
import java.util.logging.Logger;

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
    private InfinityEntityTable<T> table;
    private ScrollableTableView<T> selectedItemsTable;

    private ObservableList<T> selectedItems;

    public DropDownDialog(Pane parent) {
        super(parent);
        this.selectedItems = FXCollections.observableArrayList();
    }

    @Override
    public void initView(EventObject event) {
        Map<String, Object> data = (Map<String, Object>) event.getSource();
        paginationService = (PaginationServiceInterface<T>) data.get("service");
        listLabelCallback = (Callback<T, String>) data.get("callback");
        selectedItems.setAll((Collection<? extends T>) data.get("selectedItems"));

        table.setPaginationService(paginationService);
    }

    protected String getWindowTitle() {
        return "Список множественного выбора.";
    }

    @Override
    protected Region getWindowContent() {
        createTable();
        createSelectedItemsTable();

        Button button = facade.createButton("", SolidIcons.ARROW_ALT_RIGHT_LIGHT, SolidIcons.ARROW_ALT_RIGHT_SOLID, true);
        button.setOnAction(event -> table.getSelectionModel().getSelectedItems().forEach(item -> {
            if (!selectedItems.contains(item)) {
                selectedItems.add(item);
            }
        }));

        button.setFocusTraversable(false);
        button.prefWidthProperty().bind(button.heightProperty());
        button.setPrefHeight(44);
        button.setMinWidth(0);

        GridPane formLayout = facade.createTableFormLayout();
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
        table = new InfinityEntityTable<>();

        TableColumn<T, String> name = facade.createTableColumn("Название");
        name.setCellValueFactory(param -> {
            T value = param.getValue();
            TableColumn column = param.getTableColumn();

            String data = listLabelCallback.call(value);

            Text text = new Text(data);
            text.applyCss();
            if (column.getWidth() < text.getBoundsInLocal().getWidth()) {
                column.setMinWidth(text.getBoundsInLocal().getWidth());
            }

            return new ReadOnlyObjectWrapper<>(data);
        });

        table.setTableColumns(name);
        table.setPaginationService(paginationService);
        table.selectionModelProperty().addListener((observable, oldValue, newValue) -> newValue.setSelectionMode(SelectionMode.MULTIPLE));
    }

    private void createSelectedItemsTable() {
        selectedItemsTable = new ScrollableTableView<>(selectedItems);

        TableColumn<T, String> name = facade.createTableColumn("Название");
        name.setCellValueFactory(param -> {
            T value = param.getValue();
            TableColumn column = param.getTableColumn();

            String data = listLabelCallback.call(value);

            Text text = new Text(data);
            text.applyCss();
            if (column.getWidth() < text.getBoundsInLocal().getWidth()) {
                column.setMinWidth(text.getBoundsInLocal().getWidth());
            }

            return new ReadOnlyObjectWrapper<>(data);
        });

        TableColumn<T, String> action = facade.createTableColumn("Действие");
        action.setCellFactory(new Callback<>() {
            @Override
            public TableCell<T, String> call(TableColumn<T, String> param) {
                TableCell<T, String> cell = new TableCell<>() {
                    Button actionButton = facade.createButton("Удалить");

                    @Override
                    public void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (isEmpty()) {
                            setGraphic(null);
                            setText(null);
                        } else {
                            actionButton.setOnAction(event -> removeItem(selectedItemsTable.getItems().get(getIndex())));
                            setGraphic(actionButton);
                            Platform.runLater(() -> {
                                if (action.getMinWidth() < actionButton.getBoundsInLocal().getWidth() + 10) {
                                    action.setMinWidth(actionButton.getBoundsInLocal().getWidth() + 10);
                                }
                            });
                            setText(null);
                        }
                    }
                };
                cell.setAlignment(Pos.CENTER_RIGHT);
                return cell;
            }
        });

        selectedItemsTable.getColumns().addAll(name, action);
        selectedItemsTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        selectedItemsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    private void removeItem(T item) {
        selectedItems.remove(item);
    }
}