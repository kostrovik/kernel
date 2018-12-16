package com.github.kostrovik.kernel.graphics.controls.base.flow;

import com.github.kostrovik.kernel.graphics.controls.base.rows.PagedHeaderRow;
import com.github.kostrovik.kernel.graphics.controls.base.rows.PagedRow;
import com.github.kostrovik.kernel.graphics.controls.base.table.PagedTable;
import com.github.kostrovik.kernel.graphics.helper.ListPageDataLoader;
import com.github.kostrovik.kernel.graphics.helper.PageInfo;
import com.github.kostrovik.kernel.interfaces.EventListenerInterface;
import com.github.kostrovik.kernel.interfaces.controls.ListFilterAndSorterInterface;
import com.github.kostrovik.kernel.interfaces.controls.PaginationServiceInterface;
import com.github.kostrovik.kernel.models.PagedList;
import com.github.kostrovik.useful.interfaces.Listener;
import javafx.collections.ListChangeListener;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.StackPane;
import javafx.stage.Screen;

import java.time.LocalDateTime;
import java.util.EventObject;
import java.util.LinkedList;
import java.util.Map;

/**
 * project: kernel
 * author:  kostrovik
 * date:    2018-09-14
 * github:  https://github.com/kostrovik/kernel
 */
public class InfinityScrollingFlow<E, T extends PagedRow<E>> extends ScrollingFlow<E, T> {
    private boolean hasNextPage = true;
    private LinkedList<ListPageDataLoader> downloadTasks;

    private LocalDateTime lastRunningTask;

    private PaginationServiceInterface<E> paginationService;
    private ListFilterAndSorterInterface filter;

    public InfinityScrollingFlow(PagedTable<E> table, PagedHeaderRow<E> header, PaginationServiceInterface<E> paginationService, ListFilterAndSorterInterface filter) {
        super(table, header);
        this.paginationService = paginationService;
        this.filter = filter;
        this.filter.addListener(new Listener<EventObject>() {
            @Override
            public void handle(EventObject result) {
                clearItems();
            }

            @Override
            public void error(Throwable error) {

            }
        });
//        this.filter.addListener(event -> clearItems());

        downloadTasks = new LinkedList<>();
    }

    @Override
    protected void initListData() {
        totalCount.set(0);

        Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();

        listOffset = 0;
        table.setOffset(listOffset);

        pageSize = pageSize <= 0 ? (int) Math.ceil(primaryScreenBounds.getHeight() / defaultCellHeight) : pageSize;
        hasNextPage = true;
        pageNumber.set(0);

        downloadTask(0);
        startDownloading(LocalDateTime.now());
    }

    @Override
    public void reloadData() {
        clearItems();
    }

    private void downloadTask(int pgNumber) {
        PageInfo pageInfo = updatePageInfo(pgNumber);

        hasNextPage = true;

        EventListenerInterface task = event -> {
            Map<String, Object> result = (Map<String, Object>) event.getSource();

            LocalDateTime stamp = (LocalDateTime) result.get("threadStamp");

            if (stamp.equals(lastRunningTask)) {
                PagedList<E> dataList = (PagedList<E>) result.get("dataList");
                totalCount.set(dataList.getTotal());

                if (listOffset == 0 && dataList.getTotal() == dataList.getList().size()) {
                    hasNextPage = false;
                }

                if (listOffset + dataList.getList().size() == dataList.getTotal()) {
                    hasNextPage = false;
                }

                if (hasNextPage) {
                    listOffset = dataList.getList().size();
                }
                pageNumber.set((int) result.get("pageNumber"));

                table.getItems().setAll(dataList.getList());
            }
        };

        downloadTasks.add(new ListPageDataLoader<>(task, paginationService, pageInfo, LocalDateTime.now()));
    }

    private PageInfo updatePageInfo(int pgNumber) {
        PageInfo pageInfo = new PageInfo();
        pageInfo.setOffset(listOffset);
        pageInfo.setPageSize(pageSize * 3);
        pageInfo.setFilter(filter);
        pageInfo.setHasNextPage(hasNextPage);
        pageInfo.setPageNumber(pgNumber);

        return pageInfo;
    }

    /**
     * Запускает новый поток выполняющий загрузку списка данных. Проверяет очередь заданий на загрузку и берет из нее
     * последнее задание подходящее по времени запуска на загрузку. При запуске устанавливает метку lastRunningTask
     * которая содержит последнее запущенное задание. По этой метке в дальнейшем обрабатывается результат загрузки.
     * Кейс который решает данная конструкция.
     * 1 отправлены два запроса, один загрузит страницу 1 второй загрузит страницу 2.
     * 2 первым пришел запрос номер 2 и отобразились данные страницы 2.
     * 3 вторым пришел запрос номер 1 и его данные уже не должны быть показаны потому что он устаревший.
     *
     * @param stamp the stamp
     */
    private void startDownloading(LocalDateTime stamp) {
        ListPageDataLoader dataLoaderTask;

        do {
            dataLoaderTask = downloadTasks.poll();
        }
        while (!downloadTasks.isEmpty() && dataLoaderTask != null && dataLoaderTask.getThreadStamp().isBefore(stamp));

        if (dataLoaderTask != null) {
            lastRunningTask = dataLoaderTask.getThreadStamp();
            new Thread(dataLoaderTask).start();
        }
    }

    @Override
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
            double value = (newValue.doubleValue() - oldValue.doubleValue()) * defaultCellHeight;
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
        vbar.addEventHandler(ScrollEvent.SCROLL_FINISHED, event -> {
            finishPos = vbar.getValue();
            startDownloading(LocalDateTime.now());
        });
        vbar.skinProperty().addListener((observable, oldValue, newValue) -> {
            Node thumb = vbar.lookup(".thumb");
            Node track = vbar.lookup(".track");
            Node incButton = vbar.lookup(".increment-button");
            Node decButton = vbar.lookup(".decrement-button");

            track.addEventHandler(MouseEvent.MOUSE_RELEASED, event -> startDownloading(LocalDateTime.now()));
            thumb.addEventHandler(MouseEvent.MOUSE_RELEASED, event -> startDownloading(LocalDateTime.now()));
            incButton.addEventHandler(MouseEvent.MOUSE_RELEASED, event -> startDownloading(LocalDateTime.now()));
            decButton.addEventHandler(MouseEvent.MOUSE_RELEASED, event -> startDownloading(LocalDateTime.now()));
        });

        totalCount.addListener((observable, oldValue, newValue) -> setVBarVisible());

        table.getItems().addListener((ListChangeListener<E>) c -> updateItems());

        scrolledCellsHeight.bind(layoutYPos.divide(defaultCellHeight));

        layoutYPos.addListener((observable, oldValue, newValue) -> swapBounds(newValue.doubleValue() < oldValue.doubleValue()));

        header.layoutXPosProperty().bind(hbar.valueProperty().multiply(-1));
        header.rowWidthProperty().bind(dataContainer.widthProperty());
    }

    @Override
    protected void updateItems() {
        int startIndex = (int) (Math.floor(Math.abs(scrolledCellsHeight.get())) - (pageNumber.get() * pageSize));

        if (!table.getItems().isEmpty()) {
            for (int i = 0, index = startIndex; i < rows.size(); i++, index++) {
                PagedRow<E> row = rows.get(i);
                if (index >= 0 && index < table.getItems().size()) {
                    row.setItem(table.getItems().get(index));
                }
            }
        }
    }

    protected void clearItems() {
        for (int i = 0; i < rows.size(); i++) {
            PagedRow<E> row = rows.get(i);
            row.setItem(null);
        }
        vbar.setValue(0);
        initListData();
    }

    @Override
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
                downloadPage(scrollDown);
            }
        }
    }

    private void downloadPage(boolean scrollDown) {
        double pgNum = Math.abs(scrolledCellsHeight.get()) / visibleCellsHeight.get();
        int pgNumber = (int) Math.floor(pgNum);

        if (scrollDown) {
            if (hasNextPage) {
                listOffset = pgNumber * pageSize;
                table.setOffset(listOffset);
                downloadTask(pgNumber);
            }
        } else {
            pgNumber--;
            pgNumber = pgNumber < 0 ? 0 : pgNumber;
            listOffset = pgNumber * pageSize;
            table.setOffset(listOffset);

            downloadTask(pgNumber);
        }
    }
}