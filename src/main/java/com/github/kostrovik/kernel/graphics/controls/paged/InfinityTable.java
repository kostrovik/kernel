package com.github.kostrovik.kernel.graphics.controls.paged;

import com.github.kostrovik.kernel.graphics.controls.paged.table.PagedTable;
import com.github.kostrovik.kernel.interfaces.ListFilterAndSorterInterface;
import com.github.kostrovik.kernel.interfaces.PaginationServiceInterface;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.Skin;

/**
 * project: kernel
 * author:  kostrovik
 * date:    2018-09-11
 * github:  https://github.com/kostrovik/kernel
 */
public class InfinityTable<E> extends PagedTable<E> {
    private BooleanProperty showPagination;
    private BooleanProperty showControlPanel;

    private ObservableMap<String, EventHandler<ActionEvent>> buttonActions;
    private ObservableMap<String, Button> buttons;

    private boolean useDefaultButtons;

    public InfinityTable(PaginationServiceInterface<E> paginationService, ListFilterAndSorterInterface filter) {
        super(paginationService, filter);
        this.showPagination = new SimpleBooleanProperty(true);
        this.showControlPanel = new SimpleBooleanProperty(true);

        this.buttonActions = FXCollections.observableHashMap();
        this.buttons = FXCollections.observableHashMap();

        getStyleClass().add("infinity-table-control");
        getStylesheets().add(this.getClass().getResource("/com/github/kostrovik/styles/controls/infinity-table-control.css").toExternalForm());
    }

    public InfinityTable(PaginationServiceInterface<E> paginationService, ListFilterAndSorterInterface filter, boolean useDefaultButtons) {
        this(paginationService, filter);
        this.useDefaultButtons = useDefaultButtons;
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

    @Override
    protected Skin<?> createDefaultSkin() {
        return new InfinityTableSkin<>(this);
    }
}
