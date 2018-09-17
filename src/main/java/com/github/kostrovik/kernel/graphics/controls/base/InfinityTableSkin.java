package com.github.kostrovik.kernel.graphics.controls.base;

import com.github.kostrovik.kernel.graphics.controls.base.table.PagedTable;
import com.github.kostrovik.kernel.graphics.controls.panel.ListControlPanel;
import com.github.kostrovik.kernel.interfaces.controls.ListFilterAndSorterInterface;
import com.github.kostrovik.kernel.interfaces.controls.PaginationServiceInterface;
import javafx.collections.ListChangeListener;
import javafx.collections.MapChangeListener;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.SkinBase;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.util.ArrayList;
import java.util.Objects;

/**
 * project: kernel
 * author:  kostrovik
 * date:    2018-09-11
 * github:  https://github.com/kostrovik/kernel
 */
public class InfinityTableSkin<E> extends SkinBase<InfinityTable<E>> {
    private PagedTable<E> table;
    private PaginationServiceInterface<E> paginationService;
    private ListFilterAndSorterInterface filter;

    protected InfinityTableSkin(InfinityTable<E> control, PaginationServiceInterface<E> paginationService, ListFilterAndSorterInterface filter) {
        super(control);
        this.paginationService = paginationService;
        this.filter = filter;

        createSkin();
    }

    private void createSkin() {
        VBox group = new VBox(10);

        if (Objects.nonNull(paginationService) && Objects.nonNull(filter)) {
            table = new PagedTable<>(paginationService, filter, getSkinnable().getSelectedItems());
        } else {
            table = new PagedTable<>(getSkinnable().getItems(), getSkinnable().getSelectedItems());
        }


        table.addEventHandler(MouseEvent.ANY, event -> getSkinnable().fireEvent(event.copyFor(getSkinnable(), getSkinnable())));

        table.selectableProperty().bind(getSkinnable().selectableProperty());

        table.getColumns().addAll(getSkinnable().getColumns());

        table.getSelectionModel().getSelectedItems().addListener((ListChangeListener<E>) c -> getSkinnable().setSelectedItems(c.getList()));

        table.prefHeightProperty().bind(group.heightProperty());

        table.getSelectionModel().setMultiselect(getSkinnable().isMultiselection());
        getSkinnable().multiselectionProperty().addListener((observable, oldValue, newValue) -> table.getSelectionModel().setMultiselect(newValue));

        HBox tableGroup = new HBox(0);
        tableGroup.getChildren().addAll(table);
        HBox.setHgrow(table, Priority.ALWAYS);

        group.getChildren().addAll(tableGroup, createListPanel());

        getChildren().addAll(group);
    }

    public void updateItems() {
        table.updateItems();
    }

    private HBox createListPanel() {
        HBox panel = new HBox(10);

        HBox pagination = getListPagination();
        HBox.setHgrow(pagination, Priority.ALWAYS);

        panel.getChildren().addAll(getControlPanel(), pagination);

        setListPanelVisible(panel);

        getSkinnable().showControlPanelProperty().addListener((observable, oldValue, newValue) -> setListPanelVisible(panel));

        getSkinnable().showPaginationProperty().addListener((observable, oldValue, newValue) -> setListPanelVisible(panel));

        return panel;
    }

    private void setListPanelVisible(Pane panel) {
        if (!getSkinnable().isShowControlPanel() && !getSkinnable().isShowPagination()) {
            panel.setVisible(false);
            panel.setManaged(false);
        } else {
            panel.setVisible(true);
            panel.setManaged(true);
        }
    }

    private HBox getListPagination() {
        HBox pagination = new HBox(10);

        Text itemsCount = new Text();
        setPaginationText(itemsCount);
        itemsCount.getStyleClass().add("pagination-text");

        table.pageNumberProperty().addListener((observable, oldValue, newValue) -> setPaginationText(itemsCount));

        table.getItems().addListener((ListChangeListener<E>) c -> setPaginationText(itemsCount));

        pagination.getChildren().addAll(itemsCount);
        pagination.setAlignment(Pos.CENTER_RIGHT);

        setPaginationVisible(pagination);

        getSkinnable().showPaginationProperty().addListener((observable, oldValue, newValue) -> setPaginationVisible(pagination));

        return pagination;
    }

    private void setPaginationVisible(Pane panel) {
        if (!getSkinnable().isShowPagination()) {
            panel.setVisible(false);
            panel.setManaged(false);
        } else {
            panel.setVisible(true);
            panel.setManaged(true);
        }
    }

    private void setPaginationText(Text text) {
        if (table.getTotalCount() == 0) {
            text.setText("Список пуст");
        } else {
            int first = table.getOffset();
            text.setText(String.format("Показаны записи с %d по %d. Всего записей %d", first + 1, first + table.getItems().size(), table.getTotalCount()));
        }
    }

    private ListControlPanel getControlPanel() {
        ListControlPanel panel;
        if (getSkinnable().isUseDefaultButtons()) {
            panel = new ListControlPanel();
        } else {
            panel = new ListControlPanel(new ArrayList<>());
            panel.setButtons(getSkinnable().getButtons());
        }

        panel.getButtons().addListener((MapChangeListener<String, Button>) change -> {
            for (String buttonKey : change.getMap().keySet()) {
                if (getSkinnable().getButtonActions().containsKey(buttonKey)) {
                    change.getMap().get(buttonKey).setOnAction(getSkinnable().getButtonActions().get(buttonKey));
                }
            }
        });

        for (String buttonKey : panel.getButtons().keySet()) {
            if (getSkinnable().getButtonActions().containsKey(buttonKey)) {
                panel.getButtons().get(buttonKey).setOnAction(getSkinnable().getButtonActions().get(buttonKey));
            }
        }

        setControlPanelVisible(panel);

        getSkinnable().showControlPanelProperty().addListener((observable, oldValue, newValue) -> setControlPanelVisible(panel));

        return panel;
    }

    private void setControlPanelVisible(Control panel) {
        if (!getSkinnable().isShowControlPanel()) {
            panel.setVisible(false);
            panel.setManaged(false);
        } else {
            panel.setVisible(true);
            panel.setManaged(true);
        }
    }
}