package com.github.kostrovik.kernel.graphics.controls.paged.table;

import com.github.kostrovik.kernel.graphics.controls.paged.flow.ScrollingBody;
import javafx.scene.control.SkinBase;

/**
 * project: kernel
 * author:  kostrovik
 * date:    2018-09-09
 * github:  https://github.com/kostrovik/kernel
 */
public class PagedTableSkin<E> extends SkinBase<PagedTable<E>> {
    protected ScrollingBody<E> body;

    public PagedTableSkin(PagedTable<E> control) {
        super(control);
        createSkin();
    }

    private void createSkin() {
        body = new ScrollingBody<>(getSkinnable());
        body.prefWidthProperty().bind(getSkinnable().widthProperty());
        body.prefHeightProperty().bind(getSkinnable().heightProperty());

        getChildren().addAll(body);
    }
}
