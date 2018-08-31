package com.github.kostrovik.kernel.graphics.controls.table;

import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.TableRow;
import javafx.scene.control.skin.TableViewSkin;
import javafx.scene.control.skin.VirtualFlow;

import java.util.Set;

/**
 * project: kernel
 * author:  kostrovik
 * date:    17/08/2018
 * github:  https://github.com/kostrovik/kernel
 */
public class ScrollableTableViewSkin<T> extends TableViewSkin<T> {
    private VirtualFlow<TableRow<T>> flow;

    public ScrollableTableViewSkin(ScrollableTableView<T> control) {
        super(control);

        flow = getVirtualFlow();

        Set<Node> scrollBars = flow.lookupAll(".scroll-bar");
        scrollBars.forEach(bar -> {
            Orientation orientation = ((ScrollBar) bar).getOrientation();
            if (orientation.equals(Orientation.VERTICAL)) {
                ((ScrollBar) bar).valueProperty().addListener((observable, oldValue, newValue) -> setVisibleIndex());
            }
        });
    }

    private void setVisibleIndex() {
        TableRow<T> firstVisible = flow.getFirstVisibleCell();
        TableRow<T> lastVisible = flow.getLastVisibleCell();

        ((ScrollableTableView<T>) getSkinnable()).setFirstVisibleIndex((firstVisible != null) ? firstVisible.getIndex() : 0);
        ((ScrollableTableView<T>) getSkinnable()).setLastVisibleIndex((lastVisible != null) ? lastVisible.getIndex() : 0);
    }
}