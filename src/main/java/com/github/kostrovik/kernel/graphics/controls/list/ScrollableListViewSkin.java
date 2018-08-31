package com.github.kostrovik.kernel.graphics.controls.list;

import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.ListCell;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.skin.ListViewSkin;
import javafx.scene.control.skin.VirtualFlow;

import java.util.Set;

/**
 * project: kernel
 * author:  kostrovik
 * date:    30/08/2018
 * github:  https://github.com/kostrovik/kernel
 */
public class ScrollableListViewSkin<T> extends ListViewSkin<T> {
    private VirtualFlow<ListCell<T>> flow;

    public ScrollableListViewSkin(ScrollableListView<T> control) {
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
        ListCell<T> firstVisible = flow.getFirstVisibleCell();
        ListCell<T> lastVisible = flow.getLastVisibleCell();

        ((ScrollableListView<T>) getSkinnable()).setFirstVisibleIndex((firstVisible != null) ? firstVisible.getIndex() : 0);
        ((ScrollableListView<T>) getSkinnable()).setLastVisibleIndex((lastVisible != null) ? lastVisible.getIndex() : 0);
    }
}