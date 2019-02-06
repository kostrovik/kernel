package com.github.kostrovik.kernel.graphics.controls.paged.table;

import com.github.kostrovik.kernel.graphics.controls.common.columns.CommonColumn;
import com.github.kostrovik.kernel.graphics.controls.paged.selection.TableSelectionModel;
import com.github.kostrovik.kernel.interfaces.ListFilterAndSorterInterface;
import com.github.kostrovik.kernel.interfaces.PaginationServiceInterface;
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
    private IntegerProperty rowHeight;
    private IntegerProperty headerRowHeight;
    private ObservableList<CommonColumn<E, ?>> columns;

    private PaginationServiceInterface<E> paginationService;
    private ListFilterAndSorterInterface filter;

    private TableSelectionModel<E> selectionModel;

    private BooleanProperty selectable;

    public PagedTable(PaginationServiceInterface<E> paginationService, ListFilterAndSorterInterface filter) {
        this();

        Objects.requireNonNull(paginationService, "Сервис постраничного получения данных не может быть NULL.");
        this.paginationService = paginationService;

        Objects.requireNonNull(filter, "Фильтр для постраничного получения данных не может быть NULL.");
        this.filter = filter;
    }

    public PagedTable(PaginationServiceInterface<E> paginationService, ListFilterAndSorterInterface filter, List<E> selectedItems) {
        this(paginationService, filter);
        selectionModel.addItems(selectedItems);
    }

    private PagedTable() {
        this.rowHeight = new SimpleIntegerProperty(24);
        this.headerRowHeight = new SimpleIntegerProperty(24);
        this.columns = FXCollections.observableArrayList();
        this.selectionModel = new TableSelectionModel<>();
        this.selectable = new SimpleBooleanProperty(true);
        getStyleClass().setAll("paged-table");
    }

    public ObservableList<CommonColumn<E, ?>> getColumns() {
        return columns;
    }

    public TableSelectionModel<E> getSelectionModel() {
        return selectionModel;
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

    public int getRowHeight() {
        return rowHeight.get();
    }

    public IntegerProperty rowHeightProperty() {
        return rowHeight;
    }

    public void setRowHeight(int rowHeight) {
        this.rowHeight.set(rowHeight);
    }

    public int getHeaderRowHeight() {
        return headerRowHeight.get();
    }

    public IntegerProperty headerRowHeightProperty() {
        return headerRowHeight;
    }

    public void setHeaderRowHeight(int headerRowHeight) {
        this.headerRowHeight.set(headerRowHeight);
    }

    public PaginationServiceInterface<E> getPaginationService() {
        return paginationService;
    }

    public ListFilterAndSorterInterface getFilter() {
        return filter;
    }

    @Override
    protected Skin<?> createDefaultSkin() {
        return new PagedTableSkin<>(this);
    }
}
