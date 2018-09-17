package com.github.kostrovik.kernel.graphics.controls.dropdown;

import com.github.kostrovik.kernel.dictionaries.ViewTypeDictionary;
import com.github.kostrovik.kernel.graphics.common.icons.SolidIcons;
import com.github.kostrovik.kernel.graphics.controls.list.ScrollableListView;
import com.github.kostrovik.kernel.graphics.helper.ListPageDataLoader;
import com.github.kostrovik.kernel.graphics.helper.PageInfo;
import com.github.kostrovik.kernel.interfaces.EventListenerInterface;
import com.github.kostrovik.kernel.interfaces.controls.ControlBuilderFacadeInterface;
import com.github.kostrovik.kernel.interfaces.views.ContentViewInterface;
import com.github.kostrovik.kernel.interfaces.views.LayoutType;
import com.github.kostrovik.kernel.interfaces.views.ViewEventInterface;
import com.github.kostrovik.kernel.interfaces.views.ViewEventListenerInterface;
import com.github.kostrovik.kernel.models.PagedList;
import com.github.kostrovik.kernel.settings.Configurator;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.css.Styleable;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.skin.VirtualFlow;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Border;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.Callback;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * project: kernel
 * author:  kostrovik
 * date:    22/08/2018
 * github:  https://github.com/kostrovik/kernel
 */
public class SearchableDropDownFieldSkin<T extends Comparable> extends SkinBase<SearchableDropDownField<T>> {
    private HBox inputGroup;
    private TextField textField;
    private HBox fieldBlock;
    private Button openDictionary;
    private Button clear;
    private Button openList;

    private ScrollableListView<T> itemsList;
    private TextField searchPattern;
    private VBox popupGroup;

    private PopupControl popupControl;

    private ObjectProperty<Boolean> isListVisible;
    private ControlBuilderFacadeInterface facade;
    private Callback<T, String> callback;

    private PageInfo pageInfo;
    private DropDownListFilter listFilter;

    public SearchableDropDownFieldSkin(SearchableDropDownField<T> control) {
        super(control);
        this.isListVisible = new SimpleObjectProperty<>(false);
        this.facade = Configurator.getConfig().getControlBuilder();
        callback = getSkinnable().getListLabelCallback();
        pageInfo = new PageInfo();

        listFilter = new DropDownListFilter(getSkinnable().getLookupAttribute());
        listFilter.addListener(event -> downloadData());


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

            if (filter == null || filter.length() == 0) {
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

        itemsList = new ScrollableListView<>();
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


        itemsList.skinProperty().addListener((observable, oldValue, newValue) -> {
            VirtualFlow<ListCell<T>> flow = (VirtualFlow<ListCell<T>>) itemsList.lookup(".virtual-flow");

            ScrollBar hBar = null;
            ScrollBar vBar = null;
            Set<Node> scrollBars = flow.lookupAll(".scroll-bar");
            for (Node bar : scrollBars) {
                Orientation orientation = ((ScrollBar) bar).getOrientation();
                if (orientation.equals(Orientation.HORIZONTAL)) {
                    hBar = (ScrollBar) bar;
                }
                if (orientation.equals(Orientation.VERTICAL)) {
                    vBar = (ScrollBar) bar;
                }
            }

            ListCell<T> cell = flow.getCell(0);
            Border borders = cell.getBorder();

            double height = cell.getHeight();
            if (borders != null) {
                height += borders.getStrokes().get(0).getWidths().getBottom();
                height += borders.getStrokes().get(0).getWidths().getTop();
            }

            itemsList.setPrefHeight(1 + itemsList.getItems().size() * height);

            double maxWidth = 0;
            for (T item : itemsList.getItems()) {
                Text text = new Text(callback.call(item));
                text.applyCss();

                if (maxWidth < text.getBoundsInLocal().getWidth()) {
                    maxWidth = text.getBoundsInLocal().getWidth();
                }
            }

            double wDelta = vBar.getWidth() + cell.getPadding().getLeft() + cell.getPadding().getRight();
            itemsList.setMinWidth(maxWidth + wDelta + 1);
        });

        itemsList.setMinHeight(30);
        itemsList.setMaxSize(400, 400);

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

        Label label = new Label(getSkinnable().getLabel());
        label.setFocusTraversable(false);
        label.visibleProperty().bind(getSkinnable().showLabelProperty());
        label.managedProperty().bind(getSkinnable().showLabelProperty());

        Platform.runLater(() -> label.setMinWidth(label.getBoundsInLocal().getWidth()));

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

        openDictionary.setOnAction(event -> openDialog());

        clear = facade.createButton("", SolidIcons.CROSS);
        clear.setOnAction(event -> clearSelectedValues());
        clear.setFocusTraversable(false);
        clear.prefHeightProperty().bind(textField.heightProperty());
        clear.getStyleClass().add("clear-button");

        openList = facade.createButton("", SolidIcons.CARET_DOWN);
        openList.setFocusTraversable(false);
        openList.prefHeightProperty().bind(textField.heightProperty());
        openList.getStyleClass().add("open-list-button");
        openList.setOnAction(event -> {
            if (popupControl.isShowing()) {
                isListVisible.set(false);
            } else {
                isListVisible.set(true);
            }
        });

        fieldBlock.getChildren().addAll(textField);
        checkIsMultiple();

        getSkinnable().isMultipleProperty().addListener((observable, oldValue, newValue) -> {
            fieldBlock.getChildren().setAll(textField);
            checkIsMultiple();
        });

        inputGroup.getChildren().addAll(label, fieldBlock);
    }

    private void checkIsMultiple() {
        String styleClass = "multiple";
        if (getSkinnable().isMultiple()) {
            fieldBlock.getChildren().addAll(openDictionary, clear);
            fieldBlock.getStyleClass().add(styleClass);
        } else {
            fieldBlock.getChildren().addAll(clear, openList);
            fieldBlock.getStyleClass().remove(styleClass);
        }
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

        view.addListener(event -> getSkinnable().getSelectedItems().setAll((List<T>) event.getSource()));
    }

    private void downloadData() {
        updatePageInfo();

        EventListenerInterface task = event -> {
            Map<String, Object> result = (Map<String, Object>) event.getSource();
            PagedList<T> dataList = (PagedList<T>) result.get("dataList");
            itemsList.getItems().setAll(dataList.getList());
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