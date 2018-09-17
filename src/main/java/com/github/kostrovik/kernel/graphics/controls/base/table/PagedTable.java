package com.github.kostrovik.kernel.graphics.controls.base.table;

import com.github.kostrovik.kernel.graphics.controls.base.columns.PagedColumn;
import com.github.kostrovik.kernel.graphics.controls.base.selection.PagedTableMultiSelectionModel;
import com.github.kostrovik.kernel.interfaces.controls.ListFilterAndSorterInterface;
import com.github.kostrovik.kernel.interfaces.controls.PaginationServiceInterface;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;

import java.util.List;
import java.util.Objects;

/**
 * project: kernel
 * author:  kostrovik
 * date:    2018-09-09
 * github:  https://github.com/kostrovik/kernel
 */
public class PagedTable<E> extends Control {
    private int defaultCellHeight = 24;
    private int defaultHeaderCellHeight = 24;

    private ObservableList<PagedColumn<E, ?>> columns;

    private PaginationServiceInterface<E> paginationService;
    private ListFilterAndSorterInterface filter;

    private ObservableList<E> items;

    private IntegerProperty totalCount;
    private IntegerProperty pageNumber;
    private IntegerProperty offset;
    private PagedTableMultiSelectionModel<E> selectionModel;

    private BooleanProperty selectable;

    public PagedTable(PaginationServiceInterface<E> paginationService, ListFilterAndSorterInterface filter) {
        this();

        Objects.requireNonNull(paginationService, "Сервис постраничного получения данных не может быть NULL.");
        this.paginationService = paginationService;

        Objects.requireNonNull(filter, "Фильтр для постраничного получения данных не может быть NULL.");
        this.filter = filter;

        items = FXCollections.observableArrayList();
    }

    public PagedTable(PaginationServiceInterface<E> paginationService, ListFilterAndSorterInterface filter, List<E> selectedItems) {
        this(paginationService, filter);
        selectionModel.addSelectedItem(selectedItems);
    }

    public PagedTable(ObservableList<E> items) {
        this();

        Objects.requireNonNull(items, "Объект со списком элементов не может быть NULL.");
        this.items = items;
    }

    public PagedTable(ObservableList<E> items, List<E> selectedItems) {
        this();

        Objects.requireNonNull(items, "Объект со списком элементов не может быть NULL.");
        this.items = items;

        this.selectionModel.addSelectedItem(selectedItems);
    }

    private PagedTable() {
        getStyleClass().setAll("paged-table");

        this.columns = FXCollections.observableArrayList();
        this.totalCount = new SimpleIntegerProperty(0);
        this.pageNumber = new SimpleIntegerProperty(0);
        this.offset = new SimpleIntegerProperty(0);
        this.selectionModel = new PagedTableMultiSelectionModel<>();
        this.selectable = new SimpleBooleanProperty(true);
    }

    public ObservableList<PagedColumn<E, ?>> getColumns() {
        return columns;
    }

    public Integer getTotalCount() {
        return totalCount.get();
    }

    public IntegerProperty totalCountProperty() {
        return totalCount;
    }

    public void setTotalCount(Integer totalCount) {
        this.totalCount.set(totalCount);
    }

    public Integer getPageNumber() {
        return pageNumber.get();
    }

    public IntegerProperty pageNumberProperty() {
        return pageNumber;
    }

    public void setPageNumber(Integer pageNumber) {
        this.pageNumber.set(pageNumber);
    }

    public Integer getOffset() {
        return offset.get();
    }

    public IntegerProperty offsetProperty() {
        return offset;
    }

    public void setOffset(Integer offset) {
        this.offset.set(offset);
    }

    public int getDefaultCellHeight() {
        return defaultCellHeight;
    }

    public void setDefaultCellHeight(int defaultCellHeight) {
        this.defaultCellHeight = defaultCellHeight;
    }

    public int getDefaultHeaderCellHeight() {
        return defaultHeaderCellHeight;
    }

    public void setDefaultHeaderCellHeight(int defaultHeaderCellHeight) {
        this.defaultHeaderCellHeight = defaultHeaderCellHeight;
    }

    public PagedTableMultiSelectionModel<E> getSelectionModel() {
        return selectionModel;
    }

    public ObservableList<E> getItems() {
        return items;
    }

    public boolean isSelectable() {
        return selectable.get();
    }

    public BooleanProperty selectableProperty() {
        return selectable;
    }

    public void setSelectable(boolean selectable) {
        this.selectable.set(selectable);
    }

    public void updateItems() {
        if (getSkin() != null) {
            ((PagedTableSkin) getSkin()).updateItems();
        }
    }

    @Override
    protected Skin<?> createDefaultSkin() {
        return new PagedTableSkin<>(this, paginationService, filter);
    }
}
