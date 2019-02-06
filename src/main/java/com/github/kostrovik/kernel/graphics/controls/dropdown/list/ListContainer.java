package com.github.kostrovik.kernel.graphics.controls.dropdown.list;

import com.github.kostrovik.kernel.graphics.controls.common.SelectionModel;
import com.github.kostrovik.kernel.graphics.controls.common.columns.CommonColumn;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.util.Objects;

/**
 * project: kernel
 * author:  kostrovik
 * date:    2019-02-03
 * github:  https://github.com/kostrovik/kernel
 */
public class ListContainer<E> extends Region {
    private Pane rowsList;
    private ObservableList<CommonColumn<E, ?>> columns;
    private ObservableList<E> items;
    private SelectionModel<E> selectionModel;
    private IntegerProperty focusedIndex;
    private static final String FOCUSED_STYLE_CLASS = "focused";

    public ListContainer(ObservableList<CommonColumn<E, ?>> columns, ObservableList<E> items, SelectionModel<E> selectionModel) {
        Objects.requireNonNull(columns);
        this.columns = columns;

        Objects.requireNonNull(items);
        this.items = items;

        Objects.requireNonNull(selectionModel);
        this.selectionModel = selectionModel;

        this.focusedIndex = new SimpleIntegerProperty(-1);

        createSkin();
    }

    public void selectFocused() {
        if (focusedIndex.get() >= 0) {
            ListRow<E> row = (ListRow<E>) rowsList.getChildren().get(focusedIndex.get());
            row.getStyleClass().removeAll(FOCUSED_STYLE_CLASS);
            selectionModel.addItem(row.getItem());
        }
    }

    public void setNextFocused() {
        if (focusedIndex.get() < items.size()) {
            focusedIndex.set(focusedIndex.get() + 1);
        }
    }

    public void setPreviousFocused() {
        if (focusedIndex.get() >= 0) {
            focusedIndex.set(focusedIndex.get() - 1);
        }
    }

    private void createSkin() {
        ScrollPane container = new ScrollPane();
        rowsList = createDataContainer();
        container.setContent(rowsList);

        container.minWidthProperty().bind(widthProperty());
        container.maxWidthProperty().bind(maxWidthProperty());
        container.maxHeightProperty().bind(maxHeightProperty());

        container.viewportBoundsProperty().addListener((arg0, arg1, arg2) -> {
            container.setFitToWidth(rowsList.prefWidth(-1) < arg2.getWidth());
            container.setFitToHeight(rowsList.prefHeight(-1) < arg2.getHeight());
        });

        container.getStyleClass().setAll("list-block");

        getChildren().setAll(container);
    }

    private Pane createDataContainer() {
        VBox content = new VBox();
        createRows(content);

        items.addListener((ListChangeListener<E>) c -> createRows(content));
        focusedIndex.addListener((observable, oldValue, newValue) -> {
            if (newValue.intValue() < 0 || newValue.intValue() >= items.size()) {
                removeFocus();
            } else {
                setFocused(newValue.intValue());
            }
        });

        return content;
    }

    private void createRows(Pane container) {
        container.getChildren().clear();
        items.forEach(e -> container.getChildren().add(createRow(e)));
    }

    private ListRow<E> createRow(E item) {
        return new ListRow<>(columns, item, selectionModel);
    }

    private void setFocused(int index) {
        removeFocus();
        rowsList.getChildren().get(index).getStyleClass().add(FOCUSED_STYLE_CLASS);
    }

    private void removeFocus() {
        rowsList.getChildren().forEach(node -> node.getStyleClass().removeAll(FOCUSED_STYLE_CLASS));
    }
}