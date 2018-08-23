package com.github.kostrovik.kernel.graphics.controls.dropdown;

import com.github.kostrovik.kernel.graphics.common.icons.SolidIcons;
import com.github.kostrovik.kernel.interfaces.controls.ControlBuilderFacadeInterface;
import com.github.kostrovik.kernel.settings.Configurator;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.css.Styleable;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.Callback;

import java.util.Comparator;
import java.util.stream.Collectors;

/**
 * project: kernel
 * author:  kostrovik
 * date:    22/08/2018
 * github:  https://github.com/kostrovik/kernel
 */
public class SearchableDropDownFieldSkin<T extends Comparable> extends SkinBase<SearchableDropDownField<T>> {
    private HBox inputGroup;
    private Label label;
    private TextField textField;
    private Button openDictionary;
    private Button clear;
    private Button openList;

    private ListView<T> itemsList;
    private FilteredList<T> filteredList;
    private SortedList<T> sortedList;
    private TextField searchPattern;
    private VBox popupGroup;

    private PopupControl popupControl;

    private ObjectProperty<Boolean> isListVisible;
    private ControlBuilderFacadeInterface facade;
    private Callback<T, String> callback;

    public SearchableDropDownFieldSkin(SearchableDropDownField<T> control) {
        super(control);
        this.isListVisible = new SimpleObjectProperty<>(false);
        this.facade = Configurator.getConfig().getControlBuilder();
        callback = getSkinnable().getListLabelCallback();

        this.isListVisible.addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                Bounds bounds = textField.localToScreen(textField.getBoundsInLocal());
                popupControl.show(textField.getScene().getWindow(), bounds.getMinX(), bounds.getMinY() + bounds.getHeight());
                searchPattern.setDisable(false);
            } else {
                popupControl.hide();
                searchPattern.clear();
                searchPattern.setDisable(true);
                itemsList.getSelectionModel().clearSelection();
            }
        });

        getSkinnable().getSelectedItems().addListener((ListChangeListener<T>) c -> {
            if (c.getList().isEmpty()) {
                clearText();
            } else {
                prepareSelectedText();
            }
        });

        createSkin();
    }

    private void createSkin() {
        createInputGroup();
        createPopupGroup();

        popupControl = new PopupControl() {
            @Override
            public Styleable getStyleableParent() {
                return getSkinnable();
            }

            {
                setSkin(new Skin<>() {
                    @Override
                    public Skinnable getSkinnable() {
                        return getSkinnable();
                    }

                    @Override
                    public Node getNode() {
                        return popupGroup;
                    }

                    @Override
                    public void dispose() {
                    }
                });
            }
        };
        popupControl.showingProperty().addListener((observable, oldValue, newValue) -> isListVisible.set(newValue));
        popupControl.setConsumeAutoHidingEvents(false);
        popupControl.setAutoHide(false);
        popupControl.setAutoFix(true);
        popupControl.hide();

        getChildren().addAll(inputGroup);
    }

    private void createPopupGroup() {
        popupGroup = new VBox(0);
        popupGroup.setPadding(new Insets(5, 5, 5, 5));
        popupGroup.setAlignment(Pos.CENTER_LEFT);
        popupGroup.getStyleClass().add("popup-block");

        callback = getSkinnable().getListLabelCallback();

        searchPattern = new TextField();
        searchPattern.setFocusTraversable(false);

        filteredList = new FilteredList<>(FXCollections.observableArrayList(getSkinnable().getItems()), item -> true);
        sortedList = new SortedList<>(filteredList, Comparator.comparing(callback::call));

        searchPattern.textProperty().addListener(obs -> {
            String filter = searchPattern.getText();
            if (filter == null || filter.length() == 0) {
                filteredList.setPredicate(s -> true);
            } else {
                filteredList.setPredicate(s -> callback.call(s).contains(filter));
            }
        });

        searchPattern.addEventFilter(KeyEvent.KEY_RELEASED, event -> {
            if (event.getCode().isArrowKey() && (event.getCode() == KeyCode.UP || event.getCode() == KeyCode.DOWN)) {
                boolean upButton = event.getCode() == KeyCode.UP;
                int selectedIndex = itemsList.getSelectionModel().getSelectedIndex();

                if (upButton && selectedIndex != 0) {
                    if (selectedIndex < 0) {
                        itemsList.getSelectionModel().selectFirst();
                    } else {
                        itemsList.getSelectionModel().selectPrevious();
                    }
                }
                if (!upButton && selectedIndex < itemsList.getItems().size() - 1) {
                    if (selectedIndex < 0) {
                        itemsList.getSelectionModel().selectFirst();
                    } else {
                        itemsList.getSelectionModel().selectNext();
                    }
                }
            }
        });

        itemsList = new ListView<>(sortedList);
        itemsList.setCellFactory(new Callback<>() {
            @Override
            public ListCell<T> call(ListView<T> param) {
                return new ListCell<T>() {
                    @Override
                    public void updateItem(T item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item != null) {
                            setText(callback.call(item));

                            Text text = new Text(getText());
                            text.applyCss();

                            if (itemsList.getPrefWidth() < text.getBoundsInLocal().getWidth() + 10) {
                                itemsList.setPrefWidth(text.getBoundsInLocal().getWidth() + 10);
                            }

                        } else {
                            setText("");
                        }
                    }
                };
            }
        });
        itemsList.setFocusTraversable(false);
        itemsList.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
                addSelectedItem();
            }
        });

        itemsList.setPrefHeight(200);
        itemsList.setMaxSize(400, 400);

        HBox statistics = new HBox();
        statistics.setPadding(new Insets(2, 2, 2, 2));
        statistics.setAlignment(Pos.CENTER_RIGHT);

        Text count = new Text(String.format("Быстрый поиск %d", sortedList.size()));
        count.getStyleClass().add("items-count");
        sortedList.addListener((ListChangeListener<T>) c -> count.setText(String.format("Быстрый поиск %d", sortedList.size())));

        statistics.getChildren().add(count);

        popupGroup.getChildren().addAll(searchPattern, itemsList, statistics);
    }

    private void createInputGroup() {
        inputGroup = new HBox(10);
        inputGroup.setAlignment(Pos.CENTER_LEFT);

        HBox fieldBlock = new HBox(0);
        fieldBlock.setPadding(new Insets(1, 1, 1, 1));
        fieldBlock.getStyleClass().add("field-block");

        label = new Label(getSkinnable().getLabel());
        label.setFocusTraversable(false);

        Platform.runLater(() -> {
            label.setMinWidth(label.getBoundsInLocal().getWidth());
        });

        textField = new TextField();
        textField.setEditable(false);

        textField.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                if (popupControl.isShowing()) {
                    isListVisible.set(false);
                } else {
                    isListVisible.set(true);
                }
            }
        });
        textField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                isListVisible.set(newValue);
            }
        });
        textField.addEventFilter(KeyEvent.ANY, event -> {
            if (event.getEventType() == KeyEvent.KEY_RELEASED && event.getCode() != KeyCode.ESCAPE) {
                if (!popupControl.isShowing()) {
                    isListVisible.set(false);
                }
                isListVisible.set(true);
            }
            searchPattern.fireEvent(event);
        });

        HBox.setHgrow(textField, Priority.ALWAYS);

        openDictionary = facade.createButton("", SolidIcons.ELIPSIS);
        openDictionary.setFocusTraversable(false);
        openDictionary.prefHeightProperty().bind(textField.heightProperty());

        getSkinnable().onOpenDictionaryActionProperty().addListener((observable, oldValue, newValue) -> openDictionary.setOnAction(newValue));

        clear = facade.createButton("", SolidIcons.CROSS);
        clear.setOnAction(event -> clearSelectedValues());
        clear.setFocusTraversable(false);
        clear.prefHeightProperty().bind(textField.heightProperty());
        clear.getStyleClass().add("clear-button");

        openList = facade.createButton("", SolidIcons.CARET_DOWN);
        openList.setFocusTraversable(false);
        openList.prefHeightProperty().bind(textField.heightProperty());
        openList.setOnAction(event -> {
            if (popupControl.isShowing()) {
                isListVisible.set(false);
            } else {
                isListVisible.set(true);
                textField.requestFocus();
            }
        });

        fieldBlock.getChildren().addAll(textField);
        if (getSkinnable().isMultiple()) {
            fieldBlock.getChildren().addAll(openDictionary, clear);
        } else {
            fieldBlock.getChildren().addAll(openList);
        }

        getSkinnable().isMultipleProperty().addListener((observable, oldValue, newValue) -> {
            fieldBlock.getChildren().setAll(textField);
            if (getSkinnable().isMultiple()) {
                fieldBlock.getChildren().addAll(openDictionary, clear);
            } else {
                fieldBlock.getChildren().addAll(openList);
            }
        });

        inputGroup.getChildren().addAll(label, fieldBlock);
    }

    private void clearSelectedValues() {
        getSkinnable().getSelectedItems().clear();
    }

    private void clearText() {
        textField.clear();
    }

    private void addSelectedItem() {
        ObservableList<T> selectedItems = getSkinnable().getSelectedItems();

        if (!selectedItems.contains(itemsList.getSelectionModel().getSelectedItem())) {
            if (getSkinnable().isMultiple()) {
                selectedItems.add(itemsList.getSelectionModel().getSelectedItem());
            } else {
                selectedItems.setAll(itemsList.getSelectionModel().getSelectedItem());
            }
        }
    }

    private void prepareSelectedText() {
        ObservableList<T> selectedValues = getSkinnable().getSelectedItems();
        String str = selectedValues.stream().map(item -> callback.call(item)).collect(Collectors.joining(";"));

        textField.setText(str);
    }
}