package com.github.kostrovik.kernel.graphics.controls.base.flow;

import com.github.kostrovik.kernel.graphics.controls.base.rows.PagedHeaderRow;
import com.github.kostrovik.kernel.graphics.controls.base.rows.PagedRow;
import com.github.kostrovik.kernel.graphics.controls.base.table.PagedTable;
import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.ListChangeListener;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.ScrollBar;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import javafx.stage.Screen;

import java.util.LinkedList;

/**
 * project: kernel
 * author:  kostrovik
 * date:    2018-09-13
 * github:  https://github.com/kostrovik/kernel
 */
public class ScrollingFlow<E, T extends PagedRow<E>> extends Region {
    protected final int defaultCellHeight;

    /**
     * Список строк используемый для отображения данных. При скролле используется для конвейерной замены данных.
     */
    protected LinkedList<T> rows;

    protected PagedTable<E> table;
    protected PagedHeaderRow<E> header;

    /**
     * Вертикальный и горизонтальный скролл для больших списков.
     */
    protected ScrollBar vbar;
    protected ScrollBar hbar;

    /**
     * Панель на которую выводятся скроллы и строки с данными.
     */
    protected StackPane pane;

    protected Pane cellsContainer;

    protected Pane dataContainer;

    protected Rectangle mask;

    protected IntegerProperty totalCount;
    protected DoubleProperty layoutYPos;
    protected DoubleProperty visibleCellsHeight;
    protected DoubleProperty scrolledCellsHeight;

    protected int listOffset = 0;
    protected int pageSize = 0;
    protected double finishPos = 0;

    protected IntegerProperty pageNumber;

    public ScrollingFlow(PagedTable<E> table, PagedHeaderRow<E> header) {
        this.table = table;
        this.header = header;
        defaultCellHeight = table.getDefaultCellHeight();

        rows = new LinkedList<>();

        hbar = new ScrollBar();
        vbar = new ScrollBar();
        vbar.setOrientation(Orientation.VERTICAL);

        pane = new StackPane();
        mask = new Rectangle();
        cellsContainer = new Pane();
        cellsContainer.setClip(mask);

        dataContainer = new Pane();

        totalCount = new SimpleIntegerProperty(0);
        layoutYPos = new SimpleDoubleProperty(0);
        visibleCellsHeight = new SimpleDoubleProperty(0);
        scrolledCellsHeight = new SimpleDoubleProperty(0);
        pageNumber = new SimpleIntegerProperty(0);

        setListeners();

        Platform.runLater(() -> {
            setItems();
            initListData();
        });
    }

    protected void initListData() {
        totalCount.set(table.getItems().size());

        Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();

        listOffset = 0;
        table.setOffset(listOffset);

        pageSize = pageSize <= 0 ? (int) Math.ceil(primaryScreenBounds.getHeight() / defaultCellHeight) : pageSize;
        pageNumber.set(0);

        updateItems();
    }

    public void reloadData() {
        updateItems();
    }

    protected void setListeners() {
        table.totalCountProperty().bind(totalCount);
        table.pageNumberProperty().bind(pageNumber);

        mask.widthProperty().bind(pane.widthProperty().subtract(vbar.widthProperty()));
        mask.heightProperty().bind(pane.heightProperty().subtract(hbar.heightProperty()));
        mask.heightProperty().addListener((observable, oldValue, newValue) -> {
            visibleCellsHeight.set(mask.getHeight() / defaultCellHeight);
            pageSize = (int) visibleCellsHeight.get();

            setItems();
            updateItems();
        });

        pane.prefWidthProperty().bind(widthProperty());
        pane.prefHeightProperty().bind(heightProperty());

        pane.heightProperty().addListener((observable, oldValue, newValue) -> setVBarVisible());
        pane.widthProperty().addListener((observable, oldValue, newValue) -> setHBarVisible());

        StackPane.setAlignment(vbar, Pos.TOP_RIGHT);
        StackPane.setAlignment(hbar, Pos.BOTTOM_LEFT);
        StackPane.setAlignment(cellsContainer, Pos.TOP_LEFT);

        cellsContainer.getChildren().addAll(dataContainer);

        pane.getChildren().addAll(cellsContainer, vbar, hbar);

        getChildren().add(pane);

        dataContainer.widthProperty().addListener((observable, oldValue, newValue) -> setHBarVisible());
        dataContainer.addEventFilter(ScrollEvent.ANY, event -> vbar.fireEvent(event.copyFor(vbar, vbar)));
        dataContainer.layoutXProperty().bind(hbar.valueProperty().multiply(-1));

        hbar.maxWidthProperty().bind(pane.widthProperty().subtract(vbar.widthProperty()));
        hbar.managedProperty().bind(hbar.visibleProperty());
        hbar.visibleProperty().addListener((observable, oldValue, newValue) -> {
            mask.heightProperty().unbind();
            if (newValue) {
                mask.heightProperty().bind(pane.heightProperty().subtract(hbar.heightProperty()));
            } else {
                mask.heightProperty().bind(pane.heightProperty());
            }
            setHbarVisibleAmount();
        });

        vbar.managedProperty().bind(vbar.visibleProperty());
        vbar.visibleProperty().addListener((observable, oldValue, newValue) -> {
            mask.widthProperty().unbind();
            if (newValue) {
                mask.widthProperty().bind(pane.widthProperty().subtract(vbar.widthProperty()));
            } else {
                mask.widthProperty().bind(pane.widthProperty());
            }
            setVbarVisibleAmount();
        });
        vbar.valueProperty().addListener((observable, oldValue, newValue) -> {
            double value = (newValue.doubleValue() - oldValue.doubleValue()) * (double) defaultCellHeight;
            layoutYPos.set(layoutYPos.get() - value);
        });
        /**
         * Этот обработчик нужен чтобы убрать инерционность скрола.
         * Смысл в том что в MacOS скролл имеет инерционность и после события SCROLL_FINISHED продолжает менять значение
         * scrollbar. Так как загрузка новой страницы данных происходит по событию SCROLL_FINISHED то возникают случаи
         * когда по инерции скролл уезжает на пустоую область.
         * Вешать загрузку данных на инерционную прокрутку не целесообразно, это породит множество запросов и заддосит сервер.
         * */
        vbar.addEventHandler(ScrollEvent.SCROLL, event -> {
            if (event.isInertia()) {
                vbar.setValue(finishPos);
            }
        });
        vbar.addEventHandler(ScrollEvent.SCROLL_FINISHED, event -> finishPos = vbar.getValue());

        totalCount.addListener((observable, oldValue, newValue) -> setVBarVisible());

        table.getItems().addListener((ListChangeListener<E>) c -> updateItems());

        scrolledCellsHeight.bind(layoutYPos.divide(defaultCellHeight));

        layoutYPos.addListener((observable, oldValue, newValue) -> swapBounds(newValue.doubleValue() < oldValue.doubleValue()));

        header.layoutXPosProperty().bind(hbar.valueProperty().multiply(-1));
        header.rowWidthProperty().bind(dataContainer.widthProperty());
    }

    /**
     * Создает объекты строк для конвейера. Количество создаваемых строк на 2 больше чем видимое количество.
     */
    protected void setItems() {
        for (int i = rows.size(); i <= Math.ceil(visibleCellsHeight.get()); i++) {
            PagedRow<E> row = new PagedRow<>(table, null);

            row.prefWidthProperty().bind(mask.widthProperty());
            row.setPrefHeight(defaultCellHeight);

            row.rowIndexProperty().addListener((observable, oldValue, newValue) -> row.setLayoutY(newValue * defaultCellHeight));
            layoutYPos.addListener((observable, oldValue, newValue) -> row.setLayoutY(newValue.doubleValue() + (row.getRowIndex() * defaultCellHeight)));
            row.setRowIndex(i);

            if (i % 2 == 0) {
                row.getStyleClass().addAll("even");
            } else {
                row.getStyleClass().addAll("odd");
            }

            rows.add((T) row);
            dataContainer.getChildren().add(row);
        }

        setHBarVisible();
        setVBarVisible();
    }

    protected void updateItems() {
        int startIndex = (int) (Math.floor(Math.abs(scrolledCellsHeight.get())) - (pageNumber.get() * pageSize));

        if (!table.getItems().isEmpty()) {
            for (int i = 0, index = startIndex; i < rows.size(); i++, index++) {
                PagedRow<E> row = rows.get(i);
                if (index >= 0 && index < table.getItems().size()) {
                    row.setItem(table.getItems().get(index));
                } else {
                    row.setItem(null);
                }
            }
        } else {
            rows.forEach(row -> row.setItem(null));
        }
    }

    protected void setHBarVisible() {
        hbar.setVisible(dataContainer.getWidth() > mask.getWidth());

        if (hbar.isVisible()) {
            setHbarVisibleAmount();
        }else{
            hbar.setValue(0);
        }
    }

    protected void setHbarVisibleAmount() {
        double max = dataContainer.getWidth() - mask.getWidth();
        double visibleAmount = max * mask.getWidth() / (dataContainer.getWidth() + mask.getWidth());

        hbar.setMax(max);
        hbar.setVisibleAmount(visibleAmount);
    }

    protected void setVBarVisible() {
        vbar.setVisible(totalCount.get() > pageSize || table.getItems().size() > visibleCellsHeight.get());

        if (vbar.isVisible()) {
            setVbarVisibleAmount();
        }
    }

    protected void setVbarVisibleAmount() {
        double max = totalCount.get() - visibleCellsHeight.get();
        double visibleAmount = max * visibleCellsHeight.get() / (totalCount.get() + visibleCellsHeight.get());

        vbar.setMax(max);
        vbar.setVisibleAmount(visibleAmount);
    }

    protected void swapBounds(boolean scrollDown) {
        int hiddenRowsCount = (int) Math.floor(Math.abs(scrolledCellsHeight.get()));
        int itemIndex = hiddenRowsCount - (pageNumber.get() * pageSize);

        for (int i = 0; i < rows.size(); i++, itemIndex++, hiddenRowsCount++) {
            PagedRow<E> item = rows.get(i);

            item.setRowIndex(hiddenRowsCount);
            if (itemIndex >= 0 && itemIndex < table.getItems().size()) {
                item.setItem(table.getItems().get(itemIndex));
            } else {
                item.setItem(null);
            }
        }
    }
}
