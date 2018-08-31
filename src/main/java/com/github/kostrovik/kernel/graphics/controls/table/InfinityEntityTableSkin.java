package com.github.kostrovik.kernel.graphics.controls.table;

import com.github.kostrovik.kernel.graphics.controls.panel.ListControlPanel;
import com.github.kostrovik.kernel.graphics.helper.ListPageDataLoader;
import com.github.kostrovik.kernel.graphics.helper.PageInfo;
import com.github.kostrovik.kernel.models.PagedList;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.MapChangeListener;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Screen;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

/**
 * project: kernel
 * author:  kostrovik
 * date:    27/08/2018
 * github:  https://github.com/kostrovik/kernel
 */
public class InfinityEntityTableSkin<T> extends SkinBase<InfinityEntityTable<T>> {
    private VBox group;
    private ScrollableTableView<T> table;

    private int offset = 0;
    private int pageSize = 0;
    private boolean hasNextPage = true;
    private ObjectProperty<PagedList<T>> entities;

    private Queue<ListPageDataLoader<T>> threadQueue;
    private PageInfo pageInfo;

    protected InfinityEntityTableSkin(InfinityEntityTable<T> control) {
        super(control);
        entities = new SimpleObjectProperty<>(new PagedList<>(new ArrayList<>(), 0));

        pageInfo = new PageInfo();
        threadQueue = new LinkedList<>();

        createSkin();

        if (getSkinnable().getPaginationService() != null) {
            initData();
        }

        getSkinnable().tableColumntsProperty().addListener((observable, oldValue, newValue) -> table.getColumns().setAll(newValue));
        getSkinnable().paginationServiceProperty().addListener((observable, oldValue, newValue) -> initData());

        getSkinnable().getFilter().addListener(event -> {
            initTable();
        });
    }

    public void initTable() {
        offset = 0;
        hasNextPage = true;
        table.getItems().clear();
        downloadData();
    }

    private void createSkin() {
        group = new VBox(10);

        table = new ScrollableTableView<>(FXCollections.observableArrayList());

        getSkinnable().setSelectionModel(table.getSelectionModel());

        table.selectionModelProperty().addListener((observable, oldValue, newValue) -> getSkinnable().setSelectionModel(newValue));

        table.setEditable(false);

        table.getColumns().addAll(getSkinnable().getTableColumns());

        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        table.lastVisibleProperty().addListener((observable, oldValue, newValue) -> downloadNextPage());

        table.prefHeightProperty().bind(group.heightProperty());

        group.getChildren().addAll(table, createListPanel());

        table.setOnMouseClicked(event -> getSkinnable().fireEvent(event));

        getChildren().addAll(group);
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

        table.getItems().addListener((ListChangeListener<T>) c -> setPaginationText(itemsCount));

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
        if (table.getItems().isEmpty()) {
            text.setText("Список пуст");
        } else {
            text.setText(String.format("Показаны записи с %d по %d. Всего записей %d", 1, table.getItems().size(), entities.get().getTotal()));
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

    private void downloadNextPage() {
        pageSize = table.getLastVisibleIndex() - table.getFirstVisibleIndex();
        if (pageSize == 0) {
            pageSize = (int) (table.getHeight() / 24);
        }
        if (table.getItems().size() - table.getLastVisibleIndex() < pageSize / 2 && hasNextPage) {
            pageSize += 2;
            downloadData();
        }
    }

    private void downloadData() {
        updatePageInfo();

        boolean runThread = false;
        if (threadQueue.isEmpty()) {
            runThread = true;
        }

        ListPageDataLoader<T> downloadDataThread = new ListPageDataLoader<>(getSkinnable().getPaginationService(), pageInfo, event -> {
            Platform.runLater(() -> {
                entities.set((PagedList<T>) event.getSource());
                table.getItems().addAll(entities.get().getList());

                if (entities.get().getList().isEmpty()) {
                    hasNextPage = false;
                } else {
                    offset = table.getItems().size();
                }
                updatePageInfo();

                threadQueue.poll();

                ListPageDataLoader<T> thread = threadQueue.peek();
                if (thread != null) {
                    thread.start();
                }
            });
        });
        threadQueue.add(downloadDataThread);

        if (runThread) {
            downloadDataThread.start();
        }
    }

    private void updatePageInfo() {
        pageInfo.setOffset(offset);
        pageInfo.setPageSize(pageSize);
        pageInfo.setFilter(getSkinnable().getFilter());
        pageInfo.setHasNextPage(hasNextPage);
    }

    private void initData() {
        Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();
        offset = 0;
        pageSize = (int) Math.floor(primaryScreenBounds.getHeight() / 24);
        table.getItems().clear();

        downloadData();
    }
}