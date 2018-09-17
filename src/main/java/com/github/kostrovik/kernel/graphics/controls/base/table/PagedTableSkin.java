package com.github.kostrovik.kernel.graphics.controls.base.table;

import com.github.kostrovik.kernel.graphics.controls.base.flow.InfinityScrollingFlow;
import com.github.kostrovik.kernel.graphics.controls.base.flow.ScrollingFlow;
import com.github.kostrovik.kernel.graphics.controls.base.rows.PagedHeaderRow;
import com.github.kostrovik.kernel.graphics.controls.base.rows.PagedRow;
import com.github.kostrovik.kernel.interfaces.controls.ListFilterAndSorterInterface;
import com.github.kostrovik.kernel.interfaces.controls.PaginationServiceInterface;
import javafx.scene.control.SkinBase;
import javafx.scene.layout.VBox;

import java.util.Objects;

/**
 * project: kernel
 * author:  kostrovik
 * date:    2018-09-09
 * github:  https://github.com/kostrovik/kernel
 */
public class PagedTableSkin<E> extends SkinBase<PagedTable<E>> {
    private ScrollingFlow<E, PagedRow<E>> flow;

    public PagedTableSkin(PagedTable<E> control, PaginationServiceInterface<E> paginationService, ListFilterAndSorterInterface filter) {
        super(control);

        VBox table = new VBox();
        PagedHeaderRow<E> header = new PagedHeaderRow<>(control);
        table.getChildren().add(header);

        if (Objects.nonNull(paginationService) && Objects.nonNull(filter)) {
            /*InfinityScrollingFlow<E, PagedRow<E>>*/ flow = new InfinityScrollingFlow<>(control, header, paginationService, filter);

            flow.prefWidthProperty().bind(table.widthProperty());
            flow.prefHeightProperty().bind(table.heightProperty());

            table.getChildren().add(flow);
        } else {
            /*ScrollingFlow<E, PagedRow<E>>*/ flow = new ScrollingFlow(control, header);

            flow.prefWidthProperty().bind(table.widthProperty());
            flow.prefHeightProperty().bind(table.heightProperty());

            table.getChildren().add(flow);
        }

        getChildren().addAll(table);
    }

    public void updateItems() {
        flow.reloadData();
    }
}
