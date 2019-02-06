package com.github.kostrovik.kernel.graphics.controls.paged.flow;

import com.github.kostrovik.kernel.graphics.controls.paged.rows.PagedRow;
import com.github.kostrovik.kernel.graphics.controls.paged.table.PagedTable;
import javafx.beans.property.IntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.shape.Rectangle;

import java.util.Objects;

/**
 * project: kernel
 * author:  kostrovik
 * date:    2019-01-16
 * github:  https://github.com/kostrovik/kernel
 * <p>
 * Циклический список строк таблицы. Создает массив строк которые помещаются в видимой области таблицы. Реализует методы
 * позволяющие перемещать голову в конец списка и наоборот без необходимости добавлять, удалять элементы.
 */
public class CyclicContainer<E> extends Region {
    /**
     * Высота строки таблицы. Принято что высота всех строк в таблице будет одинакова. Это необходимое условие при
     * расчете высоты прокрутки в случае когда элементы заранее не известны, и известно только их общее количество.
     */
    private IntegerProperty rowHeight;
    /**
     * Список строк.
     */
    private ObservableList<? super PagedRow<E>> rowsList;
    /**
     * Контейнер со списком строк и маской для обрезки видимой области.
     */
    private Pane container;
    /**
     * Индекс головы списка.
     */
    private int headRowIndex = 0;
    /**
     * Индекс хвоста списка.
     */
    private int tailRowIndex = -1;

    /**
     * Текущее смещение головы списка по вертикали.
     */
    private double headLayoutY = 0;
    /**
     * Текущее значение является ли голова не четной строкой.
     */
    private boolean headIsOdd;

    private PagedTable<E> table;

    CyclicContainer(PagedTable<E> table) {
        Objects.requireNonNull(table);

        this.table = table;
        this.rowHeight = table.rowHeightProperty();
        this.rowsList = FXCollections.observableArrayList();
        createSkin();
    }

    /**
     * Возвращает список строк в контейнере.
     *
     * @return the rows list
     */
    ObservableList<PagedRow<E>> getRowsList() {
        return (ObservableList<PagedRow<E>>) rowsList;
    }

    /**
     * Возвращает объект строки таблицы которая является в данный момент головой списка.
     *
     * @return the head
     */
    PagedRow<E> getHead() {
        return rowsList.isEmpty() ? null : getRowsList().get(headRowIndex);
    }

    /**
     * Возвращает объект строки таблицы которая является в данный момент хвостом списка.
     *
     * @return the tail
     */
    PagedRow<E> getTail() {
        return rowsList.isEmpty() ? null : getRowsList().get(tailRowIndex);
    }

    /**
     * Возвращает индекс головы списка.
     *
     * @return the head index
     */
    int getHeadIndex() {
        return headRowIndex;
    }

    /**
     * Возвращает индекс хвоста списка.
     *
     * @return the tail index
     */
    int getTailIndex() {
        return tailRowIndex;
    }

    /**
     * Вычисляет количество видимых строк. Округляет до целого в большую сторону.
     * Возвращаемое количество всегда на одну больше. Эта дополнительная строка используется при пролистывании списка
     * чтобы не появлялись пустые места.
     *
     * @return the int
     */
    int getVisibleRowsCount() {
        return (int) Math.ceil(container.getHeight() / rowHeight.get()) + 1;
    }

    /**
     * Прокрутка списка вверх с перемещением головы в конец скиска.
     */
    void rollUp() {
        PagedRow<E> headRow = getHead();
        PagedRow<E> tailRow = getTail();
        headRow.setLayoutY(tailRow.getLayoutY() + rowHeight.get());
        toggleRowClass(headRow, tailRow);
        incrementRow();
    }

    /**
     * Прокрутка списка вниз с перемещением хвоста в начало списка.
     */
    void rollDown() {
        PagedRow<E> headRow = getHead();
        PagedRow<E> tailRow = getTail();
        tailRow.setLayoutY(headRow.getLayoutY() - rowHeight.get());
        toggleRowClass(tailRow, headRow);
        decrementRow();
    }

    private void createSkin() {
        container = createDataContainer();
        container.prefWidthProperty().bind(widthProperty());
        container.prefHeightProperty().bind(heightProperty());
        container.heightProperty().addListener((observable, oldValue, newValue) -> createRows());
        getChildren().setAll(container);
    }

    private Pane createDataContainer() {
        Pane content = new Pane();
        Pane rowsContainer = new Pane();
        Rectangle wrapperMask = new Rectangle();
        rowsContainer.setClip(wrapperMask);
        rowsList = rowsContainer.getChildren();

        wrapperMask.widthProperty().bind(content.widthProperty());
        wrapperMask.heightProperty().bind(content.heightProperty());

        content.getChildren().setAll(rowsContainer);

        return content;
    }

    /**
     * Создает объекты пустых строк таблицы.
     * В случае когда высота контейнера увеличивается, добавляет в него новые пустые строки.
     * Когда высота уменьшается, удаляет из контейнера объекты строк.
     */
    private void createRows() {
        int count = getVisibleRowsCount();
        int delta = count - getRowsList().size();
        if (delta > 0) {
            for (int i = 0; i < delta; i++) {
                PagedRow<E> row = createRow();
                getRowsList().add(row);

                if (getHeadIndex() == 0) {
                    changeTailIndex(getTailIndex() + 1);
                }
            }

            headLayoutY = getHead().getLayoutY();
            headIsOdd = isOddRow(getHead());
        } else {
            delta = Math.abs(delta);
            int size = getRowsList().size();
            int lastIndex = size - delta - 1;

            headLayoutY = getHead().getLayoutY();
            headIsOdd = isOddRow(getHead());

            if (getHeadIndex() == 0) {
                changeTailIndex(lastIndex);
            } else if (getHeadIndex() > lastIndex) {
                changeHeadIndex(lastIndex);
                changeTailIndex(lastIndex - 1);
            }
            getRowsList().remove(lastIndex + 1, size);
        }

        setRowsConfig();
    }

    /**
     * Создает отдельный объект пустой строки. Устанавливает ему привязку высоты.
     *
     * @return the paged row
     */
    private PagedRow<E> createRow() {
        PagedRow<E> row = new PagedRow<>(table, null);
        row.prefHeightProperty().bind(rowHeight);
        row.minHeightProperty().bind(rowHeight);
        row.prefWidthProperty().bind(container.widthProperty());
        row.addEventFilter(MouseEvent.ANY, event -> container.fireEvent(event.copyFor(container, container)));

        return row;
    }

    /**
     * Устанавливает смещение для строк. Ставит настройки класса для четных, не четных строк.
     */
    private void setRowsConfig() {
        double pos = headLayoutY;
        int head = getHeadIndex();
        Node previousRow = getRowsList().get(head);
        for (int i = 0; i < getRowsList().size(); i++, pos += rowHeight.get()) {
            Node rowItem = getRowsList().get(head);
            rowItem.setLayoutY(pos);

            if (rowItem.equals(previousRow)) {
                if (headIsOdd) {
                    setOddClass(rowItem);
                } else {
                    setEvenClass(rowItem);
                }
            } else {
                if (isOddRow(previousRow)) {
                    setEvenClass(rowItem);
                } else {
                    setOddClass(rowItem);
                }
            }

            previousRow = rowItem;
            head = checkIndexValue(head + 1);
        }
    }

    /**
     * Перемещение индексов головы и хвоста по списку вперед.
     */
    private void incrementRow() {
        changeHeadIndex(getHeadIndex() + 1);
        changeTailIndex(getTailIndex() + 1);
    }

    /**
     * Перемещение индексов головы и хвоста по списку назад.
     */
    private void decrementRow() {
        changeHeadIndex(getHeadIndex() - 1);
        changeTailIndex(getTailIndex() - 1);
    }

    private void changeHeadIndex(int value) {
        headRowIndex = checkIndexValue(value);
    }

    private void changeTailIndex(int value) {
        tailRowIndex = checkIndexValue(value);
    }

    private int checkIndexValue(int value) {
        if (value >= getRowsList().size()) {
            return 0;
        }
        if (value < 0) {
            return getRowsList().size() - 1;
        }

        return value;
    }

    /**
     * Переключение класса для четных, не четных строк при прокрутке списка.
     *
     * @param row         the row
     * @param previousRow the previous row
     */
    private void toggleRowClass(PagedRow<E> row, PagedRow<E> previousRow) {
        Objects.requireNonNull(row);
        Objects.requireNonNull(previousRow);

        if (isOddRow(previousRow)) {
            setEvenClass(row);
        } else {
            setOddClass(row);
        }
    }

    /**
     * Ставит класс четной строки.
     *
     * @param row the row
     */
    private void setEvenClass(Node row) {
        row.getStyleClass().add("even");
        row.getStyleClass().removeAll("odd");
    }

    /**
     * Ставит класс не четной строки.
     *
     * @param row the row
     */
    private void setOddClass(Node row) {
        row.getStyleClass().add("odd");
        row.getStyleClass().removeAll("even");
    }

    private boolean isOddRow(Node row) {
        return row.getStyleClass().contains("odd");
    }
}