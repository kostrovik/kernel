package com.github.kostrovik.kernel.graphics.controls.paged.flow;

import com.github.kostrovik.kernel.exceptions.SceneControlException;
import com.github.kostrovik.kernel.graphics.controls.paged.rows.PagedHeaderRow;
import com.github.kostrovik.kernel.graphics.controls.paged.rows.PagedRow;
import com.github.kostrovik.kernel.graphics.controls.paged.table.PagedTable;
import com.github.kostrovik.kernel.graphics.utils.DataLoader;
import com.github.kostrovik.kernel.models.PagedList;
import com.github.kostrovik.useful.interfaces.Listener;
import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.ListChangeListener;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.ScrollBar;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;

import java.util.Collections;
import java.util.EventObject;
import java.util.List;

/**
 * project: kernel
 * author:  kostrovik
 * date:    2019-01-11
 * github:  https://github.com/kostrovik/kernel
 */
public class ScrollingBody<E> extends Region {
    /**
     * Контрол таблицы для получения колонок и вычисления общей ширины.
     */
    private PagedTable<E> table;
    /**
     * Загрузчик списка элементов.
     */
    private DataLoader<E> loader;
    /**
     * Циклический список строк таблицы.
     */
    private CyclicContainer<E> cyclicContainer;
    /**
     * Высота строки таблицы. Принято что высота всех строк в таблице будет одинакова. Это необходимое условие при
     * расчете высоты прокрутки в случае когда элементы заранее не известны, и известно только их общее количество.
     */
    private IntegerProperty rowHeight;
    /**
     * Общее количество всех элементов.
     */
    private IntegerProperty totalCount;
    private IntegerProperty listOffset;
    /**
     * Список элементов текущей отображаемой страницы.
     */
    private PagedList<E> pageItems;
    /**
     * Заголовок таблицы.
     */
    private PagedHeaderRow<E> headerRow;
    /**
     * Весь контрол включающий в себя заголовок таблицы, список строк и полосы прокрутки (вертикальную и горизонтальную).
     */
    private StackPane body;
    /**
     * Вертикальная прокрутка.
     */
    private ScrollBar vbar;
    /**
     * Горизонтальная прокрутка.
     */
    private ScrollBar hbar;
    /**
     * Блок с заголовком и строками.
     */
    private Pane content;

    private int firstItem = 0;
    private int lastItem = 0;
    private int pageSize = 0;

    public ScrollingBody(PagedTable<E> table) {
        this.table = table;
        this.rowHeight = table.rowHeightProperty();
        this.totalCount = new SimpleIntegerProperty(0);
        this.listOffset = new SimpleIntegerProperty(0);
        this.pageItems = new PagedList<>(Collections.emptyList());

        this.loader = new DataLoader<>(table.getPaginationService(), table.getFilter());
        this.cyclicContainer = new CyclicContainer<>(table);
        this.headerRow = new PagedHeaderRow<>(table);

        table.getFilter().addListener(new Listener<>() {
            @Override
            public void handle(EventObject result) {
                vbar.setValue(0);
                hbar.setValue(0);
                pageItems = new PagedList<>(Collections.emptyList());
                totalCount.set(pageItems.getTotal());
                listOffset.set(pageItems.getOffset());

                loader.downloadPage(0, pageSize * 3);
            }

            @Override
            public void error(Throwable error) {
                throw new SceneControlException(error);
            }
        });

        createSkin();
    }

    private void createSkin() {
        body = new StackPane();

        vbar = createVerticalBar();
        hbar = createHorizontalBar();
        hbar.maxWidthProperty().bind(body.widthProperty().subtract(vbar.widthProperty()));
        vbar.visibleProperty().addListener((observable, oldValue, newValue) -> {
            hbar.maxWidthProperty().unbind();
            if (newValue) {
                hbar.maxWidthProperty().bind(body.widthProperty().subtract(vbar.widthProperty()));
            } else {
                hbar.maxWidthProperty().bind(body.widthProperty());
            }
        });

        content = new Pane();
        content.getStyleClass().add("table-body");

        content.getChildren().setAll(headerRow, cyclicContainer);
        content.maxWidthProperty().bind(body.widthProperty().subtract(vbar.widthProperty()));
        content.maxHeightProperty().bind(body.heightProperty().subtract(hbar.heightProperty()));

        Rectangle contentMask = new Rectangle();
        content.setClip(contentMask);
        contentMask.widthProperty().bind(content.widthProperty());
        contentMask.heightProperty().bind(content.heightProperty());

        headerRow.prefWidthProperty().bind(content.widthProperty());

        cyclicContainer.layoutYProperty().bind(headerRow.heightProperty());
        cyclicContainer.prefWidthProperty().bind(content.widthProperty());
        cyclicContainer.maxWidthProperty().bind(content.widthProperty());
        cyclicContainer.prefHeightProperty().bind(content.heightProperty().subtract(headerRow.heightProperty()));
        cyclicContainer.addEventFilter(ScrollEvent.ANY, event -> vbar.fireEvent(event.copyFor(vbar, vbar)));

        body.getChildren().setAll(content, vbar, hbar);
        body.prefWidthProperty().bind(widthProperty());
        body.prefHeightProperty().bind(heightProperty());

        StackPane.setAlignment(content, Pos.TOP_LEFT);
        StackPane.setAlignment(vbar, Pos.TOP_RIGHT);
        StackPane.setAlignment(hbar, Pos.BOTTOM_LEFT);

        getChildren().addAll(body);

        setListeners();

        Platform.runLater(() -> loader.downloadPage(0, pageSize * 3));
    }

    private ScrollBar createVerticalBar() {
        ScrollBar scrollBar = new ScrollBar();
        scrollBar.setOrientation(Orientation.VERTICAL);
        scrollBar.setVisible(false);

        scrollBar.valueProperty().addListener((observable, oldValue, newValue) -> scrollItems(oldValue.doubleValue(), newValue.doubleValue(), cyclicContainer.getRowsList()));
        scrollBar.addEventHandler(ScrollEvent.SCROLL, event -> loadPage());
        scrollBar.skinProperty().addListener((observable, oldValue, newValue) -> setVerticalBarLoadEvent(".thumb", ".track", ".increment-button", ".decrement-button"));

        return scrollBar;
    }

    private void setVerticalBarLoadEvent(String... selectors) {
        for (String selector : selectors) {
            vbar.lookup(selector).addEventHandler(MouseEvent.MOUSE_RELEASED, event -> loadPage());
        }
    }

    private ScrollBar createHorizontalBar() {
        ScrollBar scrollBar = new ScrollBar();
        scrollBar.setVisible(false);

        scrollBar.valueProperty().addListener((observable, oldValue, newValue) -> {
            double value = newValue.doubleValue() < 0 ? 0 : newValue.doubleValue();
            headerRow.setLayoutX(0 - value);
            cyclicContainer.getRowsList().forEach(row -> row.setLayoutX(0 - value));
        });

        return scrollBar;
    }

    private void setListeners() {
        totalCount.addListener((observable, oldValue, newValue) -> {
            setVBarVisible(newValue.intValue(), cyclicContainer.getHeight());
            checkBarValue(vbar);
        });
        cyclicContainer.heightProperty().addListener((observable, oldValue, newValue) -> {
            setVBarVisible(totalCount.get(), cyclicContainer.getHeight());
            checkBarValue(vbar);
        });
        cyclicContainer.getRowsList().addListener((ListChangeListener<Node>) c -> {
            pageSize = cyclicContainer.getVisibleRowsCount();
            setVBarVisible(totalCount.get(), cyclicContainer.getHeight());
            checkBarValue(vbar);

            if (pageSize > 0) {
                loadPage();
                setRowsItems();
            }
        });

        headerRow.widthProperty().addListener((observable, oldValue, newValue) -> {
            setHBarVisible();
            checkBarValue(hbar);
        });
        cyclicContainer.widthProperty().addListener((observable, oldValue, newValue) -> {
            setHBarVisible();
            checkBarValue(hbar);
        });

        content.borderProperty().addListener((observable, oldValue, newValue) -> {
            BorderWidths border = newValue.getStrokes().get(0).getWidths();
            cyclicContainer.prefWidthProperty().unbind();
            cyclicContainer.prefWidthProperty().bind(content.widthProperty().subtract(border.getLeft()).subtract(border.getRight()));
            cyclicContainer.prefHeightProperty().unbind();
            cyclicContainer.prefHeightProperty().bind(content.heightProperty().subtract(headerRow.heightProperty()).subtract(border.getBottom()));
        });

        loader.addListener(new Listener<>() {
            @Override
            public void handle(EventObject result) {
                pageItems = (PagedList<E>) result.getSource();
                totalCount.set(pageItems.getTotal());
                listOffset.set(pageItems.getOffset());
                Platform.runLater(ScrollingBody.this::setRowsItems);
            }

            @Override
            public void error(Throwable error) {
                throw new SceneControlException(error);
            }
        });
    }

    private void checkBarValue(ScrollBar bar) {
        bar.setValue(Math.min(bar.getValue(), bar.getMax()));
    }

    /**
     * После загрузки списка элементов заполняет строки таблицы на основе положения прокрутки.
     */
    private void setRowsItems() {
        pageSize = cyclicContainer.getVisibleRowsCount();
        if (pageSize > 0) {
            firstItem = (int) Math.floor((vbar.getValue()) - pageItems.getOffset());
            if (firstItem < 0) {
                firstItem = 0;
            }
            lastItem = firstItem - 1;

            int index = cyclicContainer.getHeadIndex();
            for (int i = 0, itemIndex = firstItem; i < cyclicContainer.getRowsList().size(); i++, itemIndex++) {
                PagedRow<E> row = cyclicContainer.getRowsList().get(index);

                if (itemIndex < pageItems.getSize()) {
                    row.setItem(pageItems.getList().get(itemIndex));
                    lastItem++;
                } else {
                    row.setItem(null);
                }

                index++;
                if (index == cyclicContainer.getRowsList().size()) {
                    index = 0;
                }
            }
        }
    }

    /**
     * Устанавливает видимость полосы прокрутки если.
     *
     * @param count         the count
     * @param wrapperHeight the wrapper height
     */
    private void setVBarVisible(int count, double wrapperHeight) {
        vbar.setVisible(count > cyclicContainer.getVisibleRowsCount() - 1);
        resetBarValue(vbar);
        setScrollConfig(wrapperHeight, rowHeight.get(), count, vbar);

        content.maxWidthProperty().unbind();
        if (vbar.isVisible()) {
            content.maxWidthProperty().bind(body.widthProperty().subtract(vbar.widthProperty()));
        } else {
            content.maxWidthProperty().bind(body.widthProperty());
        }
    }

    private void setHBarVisible() {
        double rowWidth = table.getColumns().stream().mapToDouble(Region::getPrefWidth).sum();
        double wrapperWidth = cyclicContainer.getWidth();

        hbar.setVisible(rowWidth > wrapperWidth);
        resetBarValue(hbar);
        setHbarConfig(wrapperWidth, rowWidth);

        content.maxHeightProperty().unbind();
        if (hbar.isVisible()) {
            content.maxHeightProperty().bind(body.heightProperty().subtract(hbar.heightProperty()));
        } else {
            content.maxHeightProperty().bind(body.heightProperty());
        }
    }

    private void resetBarValue(ScrollBar bar) {
        if (!bar.isVisible()) {
            bar.setValue(0);
        }
    }

    private void setHbarConfig(double containerSize, double itemSize) {
        double max = itemSize - containerSize;
        double visibleAmount = max * containerSize / (itemSize + containerSize);
        hbar.setMax(max);
        hbar.setVisibleAmount(visibleAmount);
    }

    /**
     * Вычисляет высоту полосы прокрутки и размеры бегунка.
     *
     * @param containerSize the container size
     * @param itemSize      the item size
     * @param itemsCount    the items count
     * @param bar           the bar
     */
    private void setScrollConfig(double containerSize, double itemSize, int itemsCount, ScrollBar bar) {
        double visibleCellsSize = containerSize / itemSize;
        double max = itemsCount - visibleCellsSize;
        double visibleAmount = max * visibleCellsSize / (itemsCount + visibleCellsSize);

        bar.setMax(max < 0 ? 0 : max);
        bar.setVisibleAmount(visibleAmount < 0 ? 0 : visibleAmount);
    }

    /**
     * Сдвигает список строк вверх или вниз в зависимости от движения прокрутки.
     *
     * @param oldValue the old value
     * @param newValue the new value
     * @param list     the list
     */
    private void scrollItems(double oldValue, double newValue, List<? extends Node> list) {
        double scr = (oldValue - newValue) * rowHeight.get();
        list.forEach(node -> node.setLayoutY(node.getLayoutY() + scr));
        listBorderHandling();
    }

    /**
     * Прокрутка циклического списка на необходимое количество оборотов после смещения.
     */
    private void listBorderHandling() {
        PagedRow<E> headRow = cyclicContainer.getHead();
        while ((int) headRow.getLayoutY() < 0 && (int) headRow.getLayoutY() + rowHeight.get() <= 0) {
            rollUp();
            headRow = cyclicContainer.getHead();
        }
        while ((int) headRow.getLayoutY() > 0) {
            rollDown();
            headRow = cyclicContainer.getHead();
        }
    }

    private void rollUp() {
        PagedRow<E> headRow = cyclicContainer.getHead();
        cyclicContainer.rollUp();

        firstItem++;
        lastItem++;
        if (lastItem >= 0 && lastItem < pageItems.getSize()) {
            headRow.setItem(pageItems.getList().get(lastItem));
        } else {
            headRow.setItem(null);
        }
    }

    private void rollDown() {
        PagedRow<E> tailRow = cyclicContainer.getTail();
        cyclicContainer.rollDown();

        firstItem--;
        lastItem--;
        if (firstItem >= 0 && firstItem < pageItems.getSize()) {
            tailRow.setItem(pageItems.getList().get(firstItem));
        } else {
            tailRow.setItem(null);
        }
    }

    /**
     * Загружает необходимую страницу со списком элементов на основе положения прокрутки.
     */
    private void loadPage() {
        int position = (int) vbar.getValue();
        int from = (int) vbar.getValue() - pageSize;
        from = from < 0 ? 0 : from;

        if (position < pageItems.getOffset()
                || (position + pageSize > pageItems.getOffset() + pageItems.getSize()
                && pageItems.getOffset() + pageItems.getSize() < totalCount.get())
        ) {
            loader.downloadPage(from, pageSize * 3);
        }
    }

    public int getTotalCount() {
        return totalCount.get();
    }

    public IntegerProperty totalCountProperty() {
        return totalCount;
    }

    public int getListOffset() {
        return listOffset.get();
    }

    public IntegerProperty listOffsetProperty() {
        return listOffset;
    }

    public int getPageItemsCount() {
        return pageItems.getSize();
    }
}