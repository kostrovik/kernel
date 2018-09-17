package com.github.kostrovik.kernel.graphics.controls.base;

import com.github.kostrovik.kernel.graphics.controls.base.columns.PagedColumn;
import com.github.kostrovik.kernel.interfaces.controls.ListFilterAndSorterInterface;
import com.github.kostrovik.kernel.interfaces.controls.PaginationServiceInterface;
import com.github.kostrovik.kernel.settings.Configurator;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * project: kernel
 * author:  kostrovik
 * date:    2018-09-11
 * github:  https://github.com/kostrovik/kernel
 */
public class InfinityTable<E> extends Control {
    private static Logger logger = Configurator.getConfig().getLogger(InfinityTable.class.getName());

    private ObservableList<PagedColumn<E, ?>> columns;

    private PaginationServiceInterface<E> paginationService;
    private ListFilterAndSorterInterface filter;

    private ObservableList<E> items;

    private BooleanProperty showPagination;
    private BooleanProperty showControlPanel;

    private ObservableMap<String, EventHandler<ActionEvent>> buttonActions;
    private ObservableMap<String, Button> buttons;

    private boolean useDefaultButtons;

    private ObjectProperty<List<E>> selectedItems;
    private BooleanProperty multiselection;
    private BooleanProperty selectable;

    public InfinityTable(PaginationServiceInterface<E> paginationService, ListFilterAndSorterInterface filter) {
        this();

        Objects.requireNonNull(paginationService, "Сервис постраничного получения данных не может быть NULL.");
        this.paginationService = paginationService;

        Objects.requireNonNull(filter, "Фильтр для постраничного получения данных не может быть NULL.");
        this.filter = filter;
    }

    public InfinityTable(PaginationServiceInterface<E> paginationService, ListFilterAndSorterInterface filter, boolean useDefaultButtons) {
        this(paginationService, filter);
        this.useDefaultButtons = useDefaultButtons;
    }

    public InfinityTable(ObservableList<E> items) {
        this();

        Objects.requireNonNull(items, "Объект со списком элементов не может быть NULL.");
        this.items = items;
    }

    public InfinityTable(ObservableList<E> items, boolean useDefaultButtons) {
        this(items);
        this.useDefaultButtons = useDefaultButtons;
    }

    private InfinityTable() {
        this.columns = FXCollections.observableArrayList();
        this.showPagination = new SimpleBooleanProperty(false);
        this.showControlPanel = new SimpleBooleanProperty(false);

        this.buttons = FXCollections.observableHashMap();
        this.buttonActions = FXCollections.observableHashMap();

        this.selectedItems = new SimpleObjectProperty<>(new ArrayList<>());
        this.multiselection = new SimpleBooleanProperty(false);
        this.selectable = new SimpleBooleanProperty(true);

        getStyleClass().add("infinity-table-control");

        try {
            getStylesheets().add(Class.forName(this.getClass().getName()).getResource("/com/github/kostrovik/styles/controls/infinity-table-control.css").toExternalForm());
        } catch (ClassNotFoundException error) {
            logger.log(Level.WARNING, "Ошибка загрузки стилей.", error);
        }
    }

    public ObservableList<PagedColumn<E, ?>> getColumns() {
        return columns;
    }

    public ObservableList<E> getItems() {
        return items;
    }

    public boolean isShowPagination() {
        return showPagination.get();
    }

    public BooleanProperty showPaginationProperty() {
        return showPagination;
    }

    public void setShowPagination(boolean showPagination) {
        this.showPagination.set(showPagination);
    }

    public boolean isShowControlPanel() {
        return showControlPanel.get();
    }

    public BooleanProperty showControlPanelProperty() {
        return showControlPanel;
    }

    public void setShowControlPanel(boolean showControlPanel) {
        this.showControlPanel.set(showControlPanel);
    }

    public ObservableMap<String, EventHandler<ActionEvent>> getButtonActions() {
        return buttonActions;
    }

    public void setButtonActions(ObservableMap<String, EventHandler<ActionEvent>> buttonActions) {
        this.buttonActions = buttonActions;
    }

    public ObservableMap<String, Button> getButtons() {
        return buttons;
    }

    public void setButtons(ObservableMap<String, Button> buttons) {
        this.buttons = buttons;
    }

    public boolean isUseDefaultButtons() {
        return useDefaultButtons;
    }

    public void setUseDefaultButtons(boolean useDefaultButtons) {
        this.useDefaultButtons = useDefaultButtons;
    }

    public List<E> getSelectedItems() {
        return selectedItems.get();
    }

    public ObjectProperty<List<E>> selectedItemsProperty() {
        return selectedItems;
    }

    public void setSelectedItems(List selectedItems) {
        this.selectedItems.set(selectedItems);
    }

    public boolean isMultiselection() {
        return multiselection.get();
    }

    public BooleanProperty multiselectionProperty() {
        return multiselection;
    }

    public void setMultiselection(boolean multiselection) {
        this.multiselection.set(multiselection);
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
            ((InfinityTableSkin) getSkin()).updateItems();
        }
    }

    @Override
    protected Skin<?> createDefaultSkin() {
        return new InfinityTableSkin<>(this, paginationService, filter);
    }
}
