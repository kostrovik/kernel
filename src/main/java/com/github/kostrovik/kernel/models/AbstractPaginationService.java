package com.github.kostrovik.kernel.models;

import com.github.kostrovik.kernel.interfaces.ListFilterAndSorterInterface;
import com.github.kostrovik.kernel.interfaces.PaginationServiceInterface;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * project: kernel
 * author:  kostrovik
 * date:    2019-01-11
 * github:  https://github.com/kostrovik/kernel
 */
public abstract class AbstractPaginationService<E> implements PaginationServiceInterface<E> {
    protected Map<String, List<String>> prepareQueryParams(int offset, int pageSize, ListFilterAndSorterInterface conditions) {
        Map<String, List<String>> queryParams = new HashMap<>();

        queryParams.put("offset", Collections.singletonList(String.valueOf(offset)));
        queryParams.put("per-page", Collections.singletonList(String.valueOf(pageSize)));

        if (conditions != null) {
            queryParams.put(
                    "sort-by",
                    conditions.getSortBy()
                            .entrySet()
                            .parallelStream()
                            .map(entry -> String.format("%s(%s)", entry.getKey(), entry.getValue().name()))
                            .collect(Collectors.toList()));
        }

        return queryParams;
    }
}
