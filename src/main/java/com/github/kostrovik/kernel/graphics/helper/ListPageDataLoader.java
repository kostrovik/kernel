package com.github.kostrovik.kernel.graphics.helper;

import com.github.kostrovik.kernel.interfaces.EventListenerInterface;
import com.github.kostrovik.kernel.interfaces.controls.PaginationServiceInterface;
import com.github.kostrovik.kernel.models.PagedList;
import javafx.application.Platform;

import java.time.LocalDateTime;
import java.util.EventObject;
import java.util.HashMap;
import java.util.Map;

/**
 * project: kernel
 * author:  kostrovik
 * date:    28/08/2018
 * github:  https://github.com/kostrovik/kernel
 */
public class ListPageDataLoader<T> implements Runnable {
    private EventListenerInterface task;
    private PaginationServiceInterface<T> service;
    private PageInfo pageInfo;
    private LocalDateTime threadStamp;

    public ListPageDataLoader(EventListenerInterface task, PaginationServiceInterface<T> service, PageInfo pageInfo, LocalDateTime threadOrder) {
        this.task = task;
        this.service = service;
        this.pageInfo = pageInfo;
        this.threadStamp = threadOrder;
    }

    @Override
    public void run() {
        Map<String, Object> result = new HashMap<>();
        result.put("offset", pageInfo.getOffset());
        result.put("pageSize", pageInfo.getPageSize());
        result.put("pageNumber", pageInfo.getPageNumber());

        PagedList<T> dataList = service.getFilteredList(pageInfo.getOffset(), pageInfo.getPageSize(), pageInfo.getFilter());

        result.put("dataList", dataList);
        result.put("threadStamp", threadStamp);

        Platform.runLater(() -> task.handle(new EventObject(result)));
    }

    public LocalDateTime getThreadStamp() {
        return threadStamp;
    }
}
