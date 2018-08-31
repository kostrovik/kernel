package com.github.kostrovik.kernel.graphics.helper;

import com.github.kostrovik.kernel.interfaces.EventListenerInterface;
import com.github.kostrovik.kernel.interfaces.controls.PaginationServiceInterface;
import com.github.kostrovik.kernel.models.PagedList;
import com.github.kostrovik.kernel.settings.Configurator;

import java.util.ArrayList;
import java.util.EventObject;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * project: kernel
 * author:  kostrovik
 * date:    28/08/2018
 * github:  https://github.com/kostrovik/kernel
 */
public class ListPageDataLoader<T> extends Thread {
    private static Logger logger = Configurator.getConfig().getLogger(ListPageDataLoader.class.getName());
    private PageInfo pageInfo;
    private PaginationServiceInterface<T> service;
    private EventListenerInterface listener;

    public ListPageDataLoader(PaginationServiceInterface<T> service, PageInfo pageInfo, EventListenerInterface listener) {
        super("Download data thread");

        this.service = service;
        this.pageInfo = pageInfo;
        this.listener = listener;
    }

    @Override
    public void run() {
        PagedList<T> res = new PagedList<>(new ArrayList<>(), 0);

        if (pageInfo.isHasNextPage()) {
            try {
                Thread.sleep(50);
                res = service.getFilteredList(pageInfo.getOffset(), pageInfo.getPageSize(), pageInfo.getFilter());
            } catch (InterruptedException e) {
                logger.log(Level.WARNING, "Поток загрузки данных прерван", e);
            }
        }

        listener.handle(new EventObject(res));
    }
}
