package com.github.kostrovik.kernel.graphics.controls.table;

import com.github.kostrovik.kernel.interfaces.EventListenerInterface;
import com.github.kostrovik.kernel.interfaces.controls.ListFilterAndSorter;
import com.github.kostrovik.kernel.interfaces.controls.PaginationServiceInterface;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.*;

import java.util.*;

/**
 * project: kernel
 * author:  kostrovik
 * date:    27/08/2018
 * github:  https://github.com/kostrovik/kernel
 */
public class InfinityEntityTable<T> extends Control {
    private final ObjectProperty<List<TableColumn<T, ?>>> tableColumns;
    private final ObjectProperty<PaginationServiceInterface<T>> paginationService;
    private final ObjectProperty<ListFilterAndSorter> filter;
    private final ObjectProperty<Boolean> showPagination;
    private final ObjectProperty<Boolean> showControlPanel;

    private final ObjectProperty<Map<String, EventHandler<ActionEvent>>> buttonActions;
    private final ObjectProperty<ObservableMap<String, Button>> buttons;

    private ObjectProperty<TableView.TableViewSelectionModel<T>> selectionModel;

    private boolean useDefaultButtons;

    private ListFilterAndSorter defaultFilter = new ListFilterAndSorter() {
        private List<EventListenerInterface> listeners = new ArrayList<>();

        @Override
        public String getSortBy() {
            return "id";
        }

        @Override
        public void setSortBy(String sortBy) {

        }

        @Override
        public String getSortDirection() {
            return "ASC";
        }

        @Override
        public void setSortDirection(String sortDirection) {

        }

        @Override
        public List<Map<String, Object>> getFilters() {
            return new ArrayList<>();
        }

        @Override
        public void clear() {

        }

        @Override
        public void addListener(EventListenerInterface listener) {
            listeners.add(listener);
        }

        @Override
        public void removeListener(EventListenerInterface listener) {
            listeners.remove(listener);
        }
    };

    public InfinityEntityTable() {
        this.tableColumns = new SimpleObjectProperty<>(new ArrayList<>());
        this.paginationService = new SimpleObjectProperty<>();
        this.showPagination = new SimpleObjectProperty<>(false);
        this.showControlPanel = new SimpleObjectProperty<>(false);
        this.filter = new SimpleObjectProperty<>(defaultFilter);
        this.buttons = new SimpleObjectProperty<>(FXCollections.observableHashMap());
        this.buttonActions = new SimpleObjectProperty<>(new HashMap<>());
        this.selectionModel = new SimpleObjectProperty<>();

        getStyleClass().add("entity-table-control");
    }

    public InfinityEntityTable(boolean useDefaultButtons) {
        this();
        this.useDefaultButtons = useDefaultButtons;
    }

    public ObjectProperty<List<TableColumn<T, ?>>> tableColumntsProperty() {
        return tableColumns;
    }

    public List<TableColumn<T, ?>> getTableColumns() {
        return tableColumns.get();
    }

    public void setTableColumns(List<TableColumn<T, ?>> columns) {
        tableColumns.set(columns);
    }

    public void setTableColumns(TableColumn<T, ?>... columns) {
        tableColumns.set(Arrays.asList(columns));
    }

    public ObjectProperty<PaginationServiceInterface<T>> paginationServiceProperty() {
        return paginationService;
    }

    public PaginationServiceInterface<T> getPaginationService() {
        return paginationService.get();
    }

    public void setPaginationService(PaginationServiceInterface<T> service) {
        paginationService.set(service);
    }

    public ObjectProperty<Boolean> showPaginationProperty() {
        return showPagination;
    }

    public Boolean isShowPagination() {
        return showPagination.get();
    }

    public void setShowPagination(Boolean show) {
        showPagination.set(show);
    }

    public ObjectProperty<Boolean> showControlPanelProperty() {
        return showControlPanel;
    }

    public Boolean isShowControlPanel() {
        return showControlPanel.get();
    }

    public void setShowControlPanel(Boolean show) {
        showControlPanel.set(show);
    }

    public ObjectProperty<ListFilterAndSorter> filterProperty() {
        return filter;
    }

    public ListFilterAndSorter getFilter() {
        return filter.get();
    }

    public void setFilter(ListFilterAndSorter filter) {
        this.filter.set(filter);
    }

    public ObjectProperty<ObservableMap<String, Button>> buttonsProperty() {
        return buttons;
    }

    public ObservableMap<String, Button> getButtons() {
        return buttonsProperty().get();
    }

    public void setButtons(ObservableMap<String, Button> buttons) {
        buttonsProperty().set(buttons);
    }

    public void addButton(String buttonKey, Button button) {
        getButtons().put(buttonKey, button);
    }

    public void clearButtons() {
        getButtons().clear();
    }

    public ObjectProperty<Map<String, EventHandler<ActionEvent>>> buttonActionsProperty() {
        return buttonActions;
    }

    public Map<String, EventHandler<ActionEvent>> getButtonActions() {
        return buttonActions.get();
    }

    public void setButtonActions(Map<String, EventHandler<ActionEvent>> actions) {
        buttonActions.set(actions);
    }

    public final ObjectProperty<TableView.TableViewSelectionModel<T>> selectionModelProperty() {
        return selectionModel;
    }

    public final void setSelectionModel(TableView.TableViewSelectionModel<T> value) {
        selectionModelProperty().set(value);
    }

    public final TableView.TableViewSelectionModel<T> getSelectionModel() {
        return selectionModel.get();
    }

    public boolean isUseDefaultButtons() {
        return useDefaultButtons;
    }

    public void updateTable() {
        InfinityEntityTableSkin skin = (InfinityEntityTableSkin) getSkin();
        if (skin != null) {
            skin.initTable();
        }
    }

    @Override
    protected Skin<?> createDefaultSkin() {
        return new InfinityEntityTableSkin(this);
    }
}
