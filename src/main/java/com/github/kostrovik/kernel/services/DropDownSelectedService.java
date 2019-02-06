package com.github.kostrovik.kernel.services;

import com.github.kostrovik.kernel.graphics.controls.dropdown.SearchableDropDownField;
import com.github.kostrovik.kernel.interfaces.ListFilterAndSorterInterface;
import com.github.kostrovik.kernel.interfaces.PaginationServiceInterface;
import com.github.kostrovik.kernel.models.PagedList;

/**
 * project: kernel
 * author:  kostrovik
 * date:    2019-02-06
 * github:  https://github.com/kostrovik/kernel
 */
public class DropDownSelectedService<E extends Comparable<E>> implements PaginationServiceInterface<E> {
    private SearchableDropDownField<E> dropDownField;

    public DropDownSelectedService(SearchableDropDownField<E> dropDownField) {
        this.dropDownField = dropDownField;
    }

    @Override
    public PagedList<E> getFilteredList(int offset, int pageSize, ListFilterAndSorterInterface conditions) {
        return new PagedList<>(dropDownField.getSelectionModel().getItems());
    }
}
