package com.github.kostrovik.kernel.models;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.kostrovik.kernel.exceptions.SaveEntityException;
import com.github.kostrovik.kernel.interfaces.JsonConverterInterface;
import com.github.kostrovik.kernel.interfaces.ListFilterAndSorterInterface;
import com.github.kostrovik.kernel.interfaces.ServerConnectionServiceInterface;
import com.github.kostrovik.kernel.interfaces.models.ModelInterface;
import com.github.kostrovik.useful.utils.InstanceLocatorUtil;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * project: kernel
 * author:  kostrovik
 * date:    2019-01-11
 * github:  https://github.com/kostrovik/kernel
 */
public class PaginationService<E extends ModelInterface> extends AbstractPaginationService<E> {
    private static Logger logger = InstanceLocatorUtil.getLocator().getLogger(PaginationService.class.getName());
    protected ServerConnectionServiceInterface connectionService;
    protected JsonConverterInterface<E> converter;
    protected ObjectMapper mapper;

    private String apiUrl;
    private String listUrl;
    private String createUrl;
    private String readUrl;
    private String updateUrl;
    private String removeUrl;
    private String answerListAttribute;

    public PaginationService(ServerConnectionServiceInterface connectionService, JsonConverterInterface<E> converter) {
        this.mapper = new ObjectMapper();
        this.connectionService = connectionService;
        this.converter = converter;
        this.apiUrl = "/";
        this.listUrl = "get-list";
        this.createUrl = "create";
        this.readUrl = "get-by-id";
        this.updateUrl = "update";
        this.removeUrl = "remove";
        this.answerListAttribute = "items";
    }

    public String getApiUrl() {
        return apiUrl;
    }

    public void setApiUrl(String apiUrl) {
        this.apiUrl = Objects.requireNonNull(apiUrl, "/");
    }

    public String getListUrl() {
        return listUrl;
    }

    public void setListUrl(String listUrl) {
        this.listUrl = Objects.requireNonNull(listUrl, "get-list");
    }

    public String getCreateUrl() {
        return createUrl;
    }

    public void setCreateUrl(String createUrl) {
        this.createUrl = Objects.requireNonNull(createUrl, "create");
    }

    public String getReadUrl() {
        return readUrl;
    }

    public void setReadUrl(String readUrl) {
        this.readUrl = Objects.requireNonNull(readUrl, "get-by-id");
    }

    public String getUpdateUrl() {
        return updateUrl;
    }

    public void setUpdateUrl(String updateUrl) {
        this.updateUrl = Objects.requireNonNull(updateUrl, "update");
    }

    public String getRemoveUrl() {
        return removeUrl;
    }

    public void setRemoveUrl(String removeUrl) {
        this.removeUrl = Objects.requireNonNull(removeUrl, "remove");
    }

    public String getAnswerListAttribute() {
        return answerListAttribute;
    }

    public void setAnswerListAttribute(String answerListAttribute) {
        this.answerListAttribute = Objects.requireNonNull(answerListAttribute, "items");
    }

    @Override
    public PagedList<E> getFilteredList(int offset, int pageSize, ListFilterAndSorterInterface conditions) {
        Map<String, List<String>> queryParams = prepareQueryParams(offset, pageSize, conditions);
        String data = "{}";
        try {
            data = new ObjectMapper().writeValueAsString(conditions.getFilter());
        } catch (JsonProcessingException error) {
            logger.log(Level.WARNING, "Не возможно создать JSON.", error);
        }

        ServerAnswer answer = connectionService.sendPost(apiUrl + listUrl, data, queryParams);

        List<E> entities = new ArrayList<>();
        PagedList<E> result = new PagedList<>(Collections.emptyList());

        if (answer.getStatus() == HttpURLConnection.HTTP_OK && answer.getBody() instanceof Map) {
            List<Map> entityList = (List<Map>) ((Map) answer.getBody()).get(answerListAttribute);

            entityList.forEach(entity -> {
                try {
                    E preparedEntity = converter.fromMap(entity);
                    entities.add(preparedEntity);
                } catch (Exception e) {
                    logger.log(Level.WARNING, "Ошибка конвертации данных.", e);
                }
            });

            Map<String, List<String>> responseHeaders = answer.getHeaders();
            List<String> count = responseHeaders.get("Pagination-Count");

            result = new PagedList<>(entities, Integer.parseInt(String.join("", count)), offset);
        }

        return result;
    }

    public E getById(Object id) {
        Objects.requireNonNull(id);
        Map<String, List<String>> queryParams = new HashMap<>();
        queryParams.put("id", Collections.singletonList(id.toString()));

        ServerAnswer answer = connectionService.sendPost(apiUrl + readUrl, "", queryParams);

        if (answer.getStatus() == HttpURLConnection.HTTP_OK && answer.getBody() instanceof Map) {
            return converter.fromMap((Map) answer.getBody());
        }

        return null;
    }

    public E save(E entity) throws SaveEntityException {
        if (Objects.nonNull(entity.getId())) {
            return update(entity);
        }
        return addNew(entity);
    }

    public E addNew(E entity) throws SaveEntityException {
        String data = converter.toJSON(entity);
        ServerAnswer answer = connectionService.sendPost(apiUrl + createUrl, data);

        if (answer.getStatus() == HttpURLConnection.HTTP_OK && answer.getBody() instanceof Map) {
            return converter.fromMap((Map) answer.getBody());
        }

        throw new SaveEntityException();
    }

    public E update(E entity) throws SaveEntityException {
        String data = converter.toJSON(entity);
        Map<String, List<String>> queryParams = new HashMap<>();
        queryParams.put("id", Collections.singletonList(entity.getId().toString()));

        ServerAnswer answer = connectionService.sendPost(apiUrl + updateUrl, data, queryParams);

        if (answer.getStatus() == HttpURLConnection.HTTP_OK && answer.getBody() instanceof Map) {
            return converter.fromMap((Map) answer.getBody());
        }

        throw new SaveEntityException();
    }

    public boolean delete(Object id) {
        Map<String, List<String>> queryParams = new HashMap<>();
        queryParams.put("id", Collections.singletonList(id.toString()));

        ServerAnswer answer = connectionService.sendPost(apiUrl + removeUrl, "", queryParams);

        return answer.getStatus() == HttpURLConnection.HTTP_OK;
    }
}