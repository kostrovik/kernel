package com.github.kostrovik.kernel.graphics.controls.dropdown;

import com.github.kostrovik.kernel.exceptions.SceneControlException;
import com.github.kostrovik.kernel.graphics.controls.common.columns.CommonColumn;
import com.github.kostrovik.kernel.graphics.controls.dropdown.list.ListContainer;
import com.github.kostrovik.kernel.graphics.utils.DataLoader;
import com.github.kostrovik.kernel.models.PagedList;
import com.github.kostrovik.useful.interfaces.Listener;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.SkinBase;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.util.EventObject;

/**
 * project: kernel
 * author:  kostrovik
 * date:    2019-01-23
 * github:  https://github.com/kostrovik/kernel
 */
public class DataListControlSkin<E> extends SkinBase<DataListControl<E>> {
    private DataLoader<E> loader;
    private ListContainer<E> dataList;
    private TextField searchField;
    private ObservableList<E> items;
    private ObservableList<CommonColumn<E, ?>> columns;

    DataListControlSkin(DataListControl<E> control) {
        super(control);
        this.loader = new DataLoader<>(getSkinnable().getService(), getSkinnable().getFilter());
        this.items = FXCollections.observableArrayList();
        this.columns = FXCollections.observableArrayList();
        this.searchField = createSearchField();
        createSkin();
        downloadData();

        loader.addListener(new Listener<>() {
            @Override
            public void handle(EventObject result) {
                PagedList<E> pageItems = (PagedList<E>) result.getSource();
                Platform.runLater(() -> items.setAll(pageItems.getList()));
            }

            @Override
            public void error(Throwable error) {
                throw new SceneControlException(error);
            }
        });
        getSkinnable().getFilter().addListener(new Listener<>() {
            @Override
            public void handle(EventObject result) {
                downloadData();
            }

            @Override
            public void error(Throwable error) {
                throw new SceneControlException(error);
            }
        });
    }

    private void createSkin() {
        VBox container = new VBox();
        container.setPadding(new Insets(5));
        container.setAlignment(Pos.CENTER_LEFT);
        container.getStyleClass().add("data-block");

        CommonColumn<E, String> col = new CommonColumn<>(getSkinnable().getFilterAttribute());
        col.cellValueFactoryProperty().bind(getSkinnable().callbackProperty());
        col.setAlignment(Pos.CENTER_LEFT);
        columns.add(col);

        dataList = new ListContainer<>(columns, items, getSkinnable().getSelectionModel());
        dataList.maxHeightProperty().bind(getSkinnable().maxHeightProperty());
        dataList.maxWidthProperty().bind(getSkinnable().maxWidthProperty());
        dataList.setFocusTraversable(false);

        container.getChildren().addAll(searchField, dataList, createListInfo());
        getChildren().addAll(container);
    }

    private TextField createSearchField() {
        TextField textField = new TextField();
        textField.requestFocus();

        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.isBlank()) {
                getSkinnable().getFilter().clear();
            } else {
                getSkinnable().getFilter().setFilterAttributeValue(getSkinnable().getFilterAttribute(), newValue);
            }
        });

        textField.addEventFilter(KeyEvent.KEY_RELEASED, event -> {
            if (event.getCode().isArrowKey() && (event.getCode() == KeyCode.UP || event.getCode() == KeyCode.DOWN)) {
                arrowsEvent(event);
            }
            if (event.getCode() == KeyCode.ENTER) {
                addSelectedEvent();
            }
        });
        return textField;
    }

    private HBox createListInfo() {
        HBox container = new HBox();
        container.setPadding(new Insets(2));
        container.setAlignment(Pos.CENTER_RIGHT);

        String format = "Быстрый поиск %d";
        Text count = new Text(String.format(format, items.size()));
        count.getStyleClass().add("items-count");
        items.addListener((ListChangeListener<E>) c -> count.setText(String.format(format, items.size())));

        container.getChildren().add(count);
        return container;
    }

    private void arrowsEvent(KeyEvent event) {
        boolean upButton = event.getCode().equals(KeyCode.UP);

        if (upButton) {
            dataList.setPreviousFocused();
        } else {
            dataList.setNextFocused();
        }
    }

    private void addSelectedEvent() {
        dataList.selectFocused();
    }

    private void downloadData() {
        loader.downloadPage(0, (int) (getSkinnable().getMaxHeight() / 24));
    }

    public void clear() {
        searchField.clear();
        getSkinnable().getFilter().clear();
    }
}