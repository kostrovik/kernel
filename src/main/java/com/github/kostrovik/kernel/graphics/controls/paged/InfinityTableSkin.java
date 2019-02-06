package com.github.kostrovik.kernel.graphics.controls.paged;

import com.github.kostrovik.kernel.dictionaries.PanelButton;
import com.github.kostrovik.kernel.graphics.common.icons.SolidIcons;
import com.github.kostrovik.kernel.graphics.controls.paged.table.PagedTableSkin;
import com.github.kostrovik.kernel.graphics.controls.panel.ListControlPanel;
import com.github.kostrovik.kernel.models.controls.ControlPanelButton;
import com.github.kostrovik.kernel.models.controls.ControlPanelButtons;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;

/**
 * project: kernel
 * author:  kostrovik
 * date:    2018-09-11
 * github:  https://github.com/kostrovik/kernel
 */
public class InfinityTableSkin<E> extends PagedTableSkin<E> {
    private InfinityTable<E> control;

    protected InfinityTableSkin(InfinityTable<E> control) {
        super(control);
        this.control = control;
        prepareSkin();
    }

    private void prepareSkin() {
        VBox group = new VBox(0);
        group.getChildren().addAll(body, createListPanel());

        getChildren().addAll(group);
    }

    private HBox createListPanel() {
        HBox panel = new HBox(10);
        panel.getStyleClass().setAll("list-panel");
        panel.setPadding(new Insets(5, 2, 5, 2));
        panel.setAlignment(Pos.CENTER_LEFT);

        HBox pagination = getPaginationPanel();
        HBox.setHgrow(pagination, Priority.ALWAYS);

        panel.getChildren().addAll(getControlPanel(), pagination);
        panel.managedProperty().bind(panel.visibleProperty());

        control.showControlPanelProperty().addListener((observable, oldValue, newValue) -> setPanelVisible(panel));
        control.showPaginationProperty().addListener((observable, oldValue, newValue) -> setPanelVisible(panel));

        return panel;
    }

    private void setPanelVisible(Pane panel) {
        panel.setVisible(control.isShowControlPanel() || control.isShowPagination());
    }

    private HBox getPaginationPanel() {
        HBox pagination = new HBox(10);

        Text itemsCount = new Text(getPaginationText());
        itemsCount.getStyleClass().add("pagination-text");

        body.totalCountProperty().addListener((observable, oldValue, newValue) -> itemsCount.setText(getPaginationText()));
        body.listOffsetProperty().addListener((observable, oldValue, newValue) -> itemsCount.setText(getPaginationText()));

        pagination.getChildren().addAll(itemsCount);
        pagination.setAlignment(Pos.CENTER_RIGHT);

        pagination.visibleProperty().bind(control.showPaginationProperty());
        pagination.managedProperty().bind(pagination.visibleProperty());

        return pagination;
    }

    private String getPaginationText() {
        if (body.getTotalCount() == 0) {
            return "Список пуст";
        } else {
            int first = body.getListOffset();
            return String.format("Показаны записи с %d по %d. Всего записей %d", first + 1, first + body.getPageItemsCount(), body.getTotalCount());
        }
    }

    private ListControlPanel getControlPanel() {
        Map<String, Button> buttonsMap = control.getButtons();
        if (control.isUseDefaultButtons()) {
            ControlPanelButtons defaultPanelButtons = new ControlPanelButtons(Collections.emptyList());
            defaultPanelButtons.addPanelButton(new ControlPanelButton(PanelButton.ADD.name(), 0, "", SolidIcons.PLUS));
            defaultPanelButtons.addPanelButton(new ControlPanelButton(PanelButton.REMOVE.name(), 1, "", SolidIcons.MINUS));
            defaultPanelButtons.addPanelButton(new ControlPanelButton(PanelButton.EDIT.name(), 2, "", SolidIcons.PEN));

            defaultPanelButtons.getButtonsMap().forEach((key, value) -> buttonsMap.put(key, value.getButton()));
        }
        buttonsMap.forEach((key, button) -> {
            setControlButtonConfig(button);
            if (control.getButtonActions().containsKey(key)) {
                button.setOnAction(control.getButtonActions().get(key));
            }
        });

        ListControlPanel panel = new ListControlPanel(buttonsMap);

        panel.visibleProperty().bind(control.showControlPanelProperty());
        panel.managedProperty().bind(panel.visibleProperty());

        return panel;
    }

    private void setControlButtonConfig(Button button) {
        if (Objects.nonNull(button)) {
            button.setFocusTraversable(false);
            button.prefWidthProperty().bind(button.heightProperty());
            button.setPrefHeight(18);
            button.setMinWidth(0);
        }
    }
}