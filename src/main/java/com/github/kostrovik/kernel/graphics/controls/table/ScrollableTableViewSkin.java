package com.github.kostrovik.kernel.graphics.controls.table;

import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.TableRow;
import javafx.scene.control.skin.TableViewSkin;
import javafx.scene.control.skin.VirtualFlow;

import java.util.Set;

/**
 * Created by grom on 17/08/2018.
 * Project kernel
 * author <grom25174@gmail.com>
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
        ((ScrollableTableView<T>) getSkinnable()).setFirstVisibleIndex(flow.getFirstVisibleCell().getIndex());
        ((ScrollableTableView<T>) getSkinnable()).setLastVisibleIndex(flow.getLastVisibleCell().getIndex());
    }
}