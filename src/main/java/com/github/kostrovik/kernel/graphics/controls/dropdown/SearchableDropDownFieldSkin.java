package com.github.kostrovik.kernel.graphics.controls.dropdown;

import com.github.kostrovik.kernel.dictionaries.LayoutType;
import com.github.kostrovik.kernel.dictionaries.ViewTypeDictionary;
import com.github.kostrovik.kernel.graphics.builders.ButtonBuilder;
import com.github.kostrovik.kernel.graphics.common.icons.SolidIcons;
import com.github.kostrovik.kernel.graphics.helper.ListPageDataLoader;
import com.github.kostrovik.kernel.graphics.helper.PageInfo;
import com.github.kostrovik.kernel.interfaces.views.ContentViewInterface;
import com.github.kostrovik.kernel.interfaces.views.ViewEventInterface;
import com.github.kostrovik.kernel.interfaces.views.ViewEventListenerInterface;
import com.github.kostrovik.kernel.models.PagedList;
import com.github.kostrovik.kernel.settings.Configurator;
import com.github.kostrovik.useful.interfaces.Listener;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.css.Styleable;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.PopupControl;
import javafx.scene.control.Skin;
import javafx.scene.control.SkinBase;
import javafx.scene.control.Skinnable;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.Callback;

import java.time.LocalDateTime;
import java.util.EventObject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * project: kernel
 * author:  kostrovik
 * date:    22/08/2018
 * github:  https://github.com/kostrovik/kernel
 */
public class SearchableDropDownFieldSkin<T extends Comparable> extends SkinBase<SearchableDropDownField<T>> {
    private HBox inputGroup;
    private VBox popupGroup;

    private TextField textField;
    private HBox fieldBlock;

    private ListView<T> itemsList;
    private TextField searchPattern;

    private PopupControl popupControl;

    private ObjectProperty<Boolean> isListVisible;
    private ButtonBuilder buttonBuilder;
    private Callback<T, String> callback;

    private PageInfo pageInfo;
    private DropDownListFilter listFilter;
    private ObservableList<T> selectedItems;

    public SearchableDropDownFieldSkin(SearchableDropDownField<T> control) {
        super(control);
        buttonBuilder = new ButtonBuilder();
        selectedItems = getSkinnable().getSelectedItems();
        this.isListVisible = new SimpleObjectProperty<>(false);
        callback = getSkinnable().getListLabelCallback();
        pageInfo = new PageInfo();

        listFilter = new DropDownListFilter(getSkinnable().getLookupAttribute());
        listFilter.addListener(new Listener<>() {
            @Override
            public void handle(EventObject result) {
                downloadData();
            }

            @Override
            public void error(Throwable error) {

            }
        });
//        listFilter.addListener(event -> downloadData());


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

        if (getSkinnable().getSelectedItems().isEmpty()) {
            clearText();
        } else {
            prepareSelectedText();
        }

        if (getSkinnable().getPaginationService() != null) {
            downloadData();
        }
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
                        // нечего не делает при обновлени skin
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

        searchPattern.textProperty().addListener(obs -> {
            String filter = searchPattern.getText();

            if (Objects.isNull(filter) || filter.length() == 0) {
                listFilter.clear();
            } else {
                listFilter.setValueFilter(filter);
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
            if (event.getCode() == KeyCode.ENTER && itemsList.getSelectionModel().getSelectedItem() != null) {
                if (getSkinnable().isMultiple()) {
                    addSelectedItem();
                } else {
                    addSelectedItem();
                    isListVisible.set(false);
                }
            }
        });

        itemsList = new ListView<>();
        itemsList.setCellFactory(new Callback<>() {
            @Override
            public ListCell<T> call(ListView<T> param) {
                return new ListCell<>() {
                    @Override
                    public void updateItem(T item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item != null) {
                            setText(callback.call(item));
                        } else {
                            setText("");
                        }
                    }
                };
            }
        });
        itemsList.setFocusTraversable(false);
        itemsList.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                if (getSkinnable().isMultiple()) {
                    if (event.getClickCount() == 2) {
                        addSelectedItem();
                    }
                } else {
                    addSelectedItem();
                    isListVisible.set(false);
                }
            }
        });

        itemsList.setMinHeight(30);
        itemsList.setMaxHeight(400);

        itemsList.prefWidthProperty().bind(textField.widthProperty().subtract(inputGroup.getSpacing()));
        itemsList.setMinWidth(300);

        HBox statistics = new HBox();
        statistics.setPadding(new Insets(2, 2, 2, 2));
        statistics.setAlignment(Pos.CENTER_RIGHT);

        Text count = new Text(String.format("Быстрый поиск %d", itemsList.getItems().size()));
        count.getStyleClass().add("items-count");
        itemsList.getItems().addListener((ListChangeListener<T>) c -> count.setText(String.format("Быстрый поиск %d", itemsList.getItems().size())));

        statistics.getChildren().add(count);

        popupGroup.getChildren().addAll(searchPattern, itemsList, statistics);
    }

    private void createInputGroup() {
        inputGroup = new HBox(10);
        inputGroup.setAlignment(Pos.CENTER_LEFT);

        fieldBlock = new HBox(0);
        fieldBlock.setPadding(new Insets(1, 1, 1, 1));
        fieldBlock.getStyleClass().add("field-block");

        Text label = new Text(getSkinnable().getLabel());
        label.getStyleClass().add("label");
        label.setFocusTraversable(false);
        label.visibleProperty().bind(getSkinnable().showLabelProperty());
        label.managedProperty().bind(getSkinnable().showLabelProperty());

        textField = new TextField();
        textField.setEditable(false);

        textField.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                isListVisible.set(!popupControl.isShowing());
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

        Button openDictionary = buttonBuilder.createButton(SolidIcons.ELIPSIS);
        openDictionary.setFocusTraversable(false);
        openDictionary.prefHeightProperty().bind(textField.heightProperty());

        openDictionary.setOnAction(event -> openDialog());

        Button clear = buttonBuilder.createButton(SolidIcons.CROSS);
        clear.setOnAction(event -> clearSelectedValues());
        clear.setFocusTraversable(false);
        clear.prefHeightProperty().bind(textField.heightProperty());
        clear.getStyleClass().add("clear-button");

        Button openList = buttonBuilder.createButton(SolidIcons.CARET_DOWN);
        openList.setFocusTraversable(false);
        openList.prefHeightProperty().bind(textField.heightProperty());
        openList.getStyleClass().add("open-list-button");
        openList.setOnAction(event -> isListVisible.set(!popupControl.isShowing()));

        if (getSkinnable().isMultiple()) {
            fieldBlock.getStyleClass().add("multiple");
        }
        fieldBlock.getChildren().addAll(textField, openDictionary, clear, openList);

        openDictionary.setManaged(getSkinnable().isMultiple());
        openDictionary.setVisible(getSkinnable().isMultiple());

        openList.setManaged(!getSkinnable().isMultiple());
        openList.setVisible(!getSkinnable().isMultiple());

        getSkinnable().multipleProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue && !fieldBlock.getStyleClass().contains("multiple")) {
                fieldBlock.getStyleClass().add("multiple");
            } else {
                fieldBlock.getStyleClass().removeAll("multiple");
            }
            openDictionary.setManaged(newValue);
            openDictionary.setVisible(newValue);

            openList.setManaged(!newValue);
            openList.setVisible(!newValue);
        });

        inputGroup.getChildren().addAll(label, fieldBlock);
        HBox.setHgrow(label, Priority.NEVER);
        HBox.setHgrow(fieldBlock, Priority.ALWAYS);
    }

    private void clearSelectedValues() {
        getSkinnable().getSelectedItems().clear();
    }

    private void clearText() {
        textField.clear();
    }

    private void addSelectedItem() {
        if (!selectedItems.contains(itemsList.getSelectionModel().getSelectedItem())) {
            if (getSkinnable().isMultiple()) {
                selectedItems.add(itemsList.getSelectionModel().getSelectedItem());
            } else {
                selectedItems.setAll(itemsList.getSelectionModel().getSelectedItem());
            }
        }
    }

    private void prepareSelectedText() {
        String str = selectedItems.stream().map(item -> callback.call(item)).collect(Collectors.joining(";"));
        textField.setText(str);
    }

    private void openDialog() {
        Configurator configurator = Configurator.getConfig();
        ViewEventListenerInterface listener = configurator.getEventListener();
        ContentViewInterface view = listener.handle(new ViewEventInterface() {
            @Override
            public String getModuleName() {
                return SearchableDropDownFieldSkin.class.getModule().getName();
            }

            @Override
            public String getViewName() {
                return ViewTypeDictionary.DROPDOWN_DIALOG.name();
            }

            @Override
            public Object getEventData() {
                Map<String, Object> data = new HashMap<>();
                data.put("service", getSkinnable().getPaginationService());
                data.put("callback", getSkinnable().getListLabelCallback());
                data.put("attribute", getSkinnable().getLookupAttribute());
                data.put("selectedItems", getSkinnable().getSelectedItems());

                return data;
            }

            @Override
            public LayoutType getLayoutType() {
                return LayoutType.POPUP;
            }
        });

//        view.addListener(event -> getSkinnable().getSelectedItems().setAll((List<T>) event.getSource()));
        view.addListener(new Listener<EventObject>() {
            @Override
            public void handle(EventObject result) {
                getSkinnable().getSelectedItems().setAll((List<T>) result.getSource());
            }

            @Override
            public void error(Throwable error) {

            }
        });
    }

    private void downloadData() {
        updatePageInfo();

        Listener<Map> task = new Listener<Map>() {
            @Override
            public void handle(Map result) {
                PagedList<T> dataList = (PagedList<T>) result.get("dataList");
                itemsList.getItems().setAll(dataList.getList());
            }

            @Override
            public void error(Throwable error) {

            }
        };

        new Thread(new ListPageDataLoader<>(task, getSkinnable().getPaginationService(), pageInfo, LocalDateTime.now())).start();
    }

    private void updatePageInfo() {
        pageInfo.setOffset(0);
        pageInfo.setPageSize((int) (itemsList.getMaxHeight() / 24));
        pageInfo.setFilter(listFilter);
        pageInfo.setHasNextPage(true);
    }
}