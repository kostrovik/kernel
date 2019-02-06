package com.github.kostrovik.kernel.graphics.utils;

import com.github.kostrovik.kernel.interfaces.ListFilterAndSorterInterface;
import com.github.kostrovik.kernel.interfaces.PaginationServiceInterface;
import com.github.kostrovik.kernel.models.PagedList;
import com.github.kostrovik.useful.interfaces.Listener;

import java.time.LocalDateTime;

/**
 * project: kernel
 * author:  kostrovik
 * date:    28/08/2018
 * github:  https://github.com/kostrovik/kernel
 */
public class ListPageLoader<E> implements Runnable {
    private Listener<DownloadResult<E>> listener;
    private PaginationServiceInterface<E> service;
    private LocalDateTime threadStamp;

    private int offset;
    private int pageSize;
    private ListFilterAndSorterInterface filter;

    public ListPageLoader(Listener<DownloadResult<E>> listener, PaginationServiceInterface<E> service, PageInfo pageInfo, LocalDateTime threadOrder) {
        this.listener = listener;
        this.service = service;
        this.offset = pageInfo.getOffset();
        this.pageSize = pageInfo.getPageSize();
        this.filter = pageInfo.getFilter();
        this.threadStamp = threadOrder;
    }

    @Override
    public void run() {
        PagedList<E> dataList = service.getFilteredList(offset, pageSize, filter);
        DownloadResult result = new DownloadResult(offset, pageSize, threadStamp, dataList);
        listener.handle(result);
    }
}
