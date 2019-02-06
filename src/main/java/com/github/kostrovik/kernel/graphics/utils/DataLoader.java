package com.github.kostrovik.kernel.graphics.utils;

import com.github.kostrovik.kernel.interfaces.ListFilterAndSorterInterface;
import com.github.kostrovik.kernel.interfaces.PaginationServiceInterface;
import com.github.kostrovik.kernel.models.PagedList;
import com.github.kostrovik.useful.interfaces.Listener;
import com.github.kostrovik.useful.models.AbstractObservable;
import com.github.kostrovik.useful.utils.InstanceLocatorUtil;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * project: kernel
 * author:  kostrovik
 * date:    2019-01-15
 * github:  https://github.com/kostrovik/kernel
 * <p>
 * Вспомогательный объект. Объект делает запросы на сервер каждый в отдельном потоке, и результаты записывает в список.
 * По итогам каждой загрузки выбирает из списка результатов последний актуальный и отправляет вызвавшему объекту.
 * Для определения актуальности результата загрузки используется метка timestamp.
 * Сделано как решение следующей проблемы.
 * Поступило два запроса на загрузку данных. Первый запрос загружает страницу номер 2, второй страницу номер 1.
 * По любым причинам (долго обрабатывал сервер, проблемы с сетью, маршрутизация) ответы пришли в другом порядке.
 * Первым пришел список страницы номер 1, вторым список страницы номер 2.
 * Имея сохраненную метку (в данном случае timestamp) мы можем определить какой результат считать последним актуальным.
 */
public class DataLoader<E> extends AbstractObservable {
    private static Logger logger = InstanceLocatorUtil.getLocator().getLogger(DataLoader.class);

    private PagedList<E> pageItems;
    /**
     * Сервис для постраничной загрузки элементов.
     */
    private PaginationServiceInterface<E> service;
    /**
     * Фильтр списка для загрузки.
     */
    private ListFilterAndSorterInterface filter;
    /**
     * Метка timestamp для идентификации последнего актуального результата.
     */
    private LocalDateTime currentStamp;
    /**
     * Список результатов.
     */
    private Map<LocalDateTime, DownloadResult<E>> downloadResults;

    public DataLoader(PaginationServiceInterface<E> service, ListFilterAndSorterInterface filter) {
        this.pageItems = new PagedList<>(Collections.emptyList());
        this.service = service;
        this.filter = filter;
        this.currentStamp = LocalDateTime.now();
        this.downloadResults = new ConcurrentHashMap<>();
    }

    /**
     * Загружает список элементов страницы.
     *
     * @param from смещение с какого элемента начинать
     * @param size количество элементов на странице
     */
    public void downloadPage(int from, int size) {
        PageInfo pageInfo = new PageInfo(from, size, filter);
        currentStamp = LocalDateTime.now();

        Listener<DownloadResult<E>> listener = new Listener<>() {
            @Override
            public void handle(DownloadResult<E> result) {
                LocalDateTime stamp = result.getStamp();

                if (stamp.compareTo(currentStamp) >= 0) {
                    downloadResults.put(stamp, result);
                    processingDownloadResults();
                }
            }

            @Override
            public void error(Throwable error) {
                logger.log(Level.WARNING, "Ошибка загрузки списка.", error);
                notifyListeners(new PagedList<>(Collections.emptyList()));
            }
        };

        ListPageLoader<E> loader = new ListPageLoader<>(listener, service, pageInfo, currentStamp);
        new Thread(loader).start();
    }

    /**
     * Ищет в списке результатов актуальный а так же удаляет все устаревшие записи.
     */
    private void processingDownloadResults() {
        downloadResults.forEach((key, value) -> {
            if (key.isBefore(currentStamp)) {
                downloadResults.remove(key);
            }
            if (key.isEqual(currentStamp)) {
                pageItems = value.getList();
                notifyListeners(pageItems);
            }
        });
    }
}