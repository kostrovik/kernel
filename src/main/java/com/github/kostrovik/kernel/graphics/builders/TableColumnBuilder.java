package com.github.kostrovik.kernel.graphics.builders;

import javafx.geometry.Pos;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.text.Text;
import javafx.util.Callback;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Класс конструктор для создания колонок таблицы.
 * <p>
 * project: kernel
 * author:  kostrovik
 * date:    24/07/2018
 * github:  https://github.com/kostrovik/kernel
 */
public class TableColumnBuilder<T, R> {

    /**
     * Создает колонку для любого типа значения и ставит ей минимальную ширину по названию колонки.
     *
     * @param <T>        the type parameter
     * @param <R>        the type parameter
     * @param columnName the column name
     *
     * @return the table column
     */
    public <T, R> TableColumn<T, R> createColumn(String columnName) {
        TableColumn<T, R> column = new TableColumn<>(columnName);

        Text columnNameText = new Text(columnName);
        columnNameText.applyCss();
        column.setMinWidth(columnNameText.getBoundsInLocal().getWidth() + 10);

        return column;
    }

    /**
     * Создает колонку для строковых значений и устанавливает ей минимальную ширину по содержимому.
     *
     * @param columnName the column name
     * @param property   the property
     *
     * @return the table column
     */
    public TableColumn<T, String> createStringValueColumn(String columnName, String property) {
        TableColumn<T, String> column = createColumn(columnName);
        column.setCellValueFactory(new CellPropertyValueFactory<>(property));

        column.setCellFactory(new Callback<>() {
            @Override
            public TableCell<T, String> call(TableColumn<T, String> param) {
                return new TableCell<>() {
                    @Override
                    public void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setText(null);
                        } else {
                            if (item != null) {
                                setText(item);

                                Text text = new Text(getText());
                                text.applyCss();

                                if (column.getMinWidth() < text.getBoundsInLocal().getWidth() + 10) {
                                    column.setMinWidth(text.getBoundsInLocal().getWidth() + 10);
                                }
                            } else {
                                setText(null);
                            }
                        }
                    }
                };
            }
        });

        return column;
    }

    /**
     * Создает колонку для многострочного значения которое переносится построчно при уменьшении ширины колонки.
     *
     * @param columnName the column name
     * @param property   the property
     *
     * @return the table column
     */
    public TableColumn<T, String> createMultilineStringValueColumn(String columnName, String property) {
        TableColumn<T, String> column = createColumn(columnName);
        column.setCellValueFactory(new CellPropertyValueFactory<>(property));
        column.setCellFactory(new Callback<>() {
            @Override
            public TableCell<T, String> call(TableColumn<T, String> param) {
                return new TableCell<>() {
                    private Text cellText;

                    @Override
                    public void cancelEdit() {
                        super.cancelEdit();
                        setGraphic(cellText);
                    }

                    @Override
                    public void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        setText(null);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            if (item != null) {
                                cellText = createText(item);
                                setGraphic(cellText);
                            } else {
                                setGraphic(null);
                            }
                        }
                    }

                    private Text createText(String value) {
                        Text text = new Text(value);
                        text.getStyleClass().add("text");
                        text.wrappingWidthProperty().bind(getTableColumn().widthProperty());
                        return text;
                    }
                };
            }
        });

        return column;
    }

    /**
     * Создает колонку для значений типа boolean которые отображаются в таблице как checkbox.
     *
     * @param columnName the column name
     * @param property   the property
     *
     * @return the table column
     */
    public TableColumn<T, Boolean> createBooleanValueColumn(String columnName, String property) {
        TableColumn<T, Boolean> column = createColumn(columnName);
        column.setCellValueFactory(new CellPropertyValueFactory<>(property));

        column.setCellFactory(param -> {
            CheckBoxTableCell<T, Boolean> cell = new CheckBoxTableCell<>();
            cell.setAlignment(Pos.CENTER);
            return cell;
        });

        return column;
    }

    /**
     * Создает колонку для значений типа LocalDateTime с возможностью задать формат выводимого значения.
     *
     * @param columnName the column name
     * @param property   the property
     * @param formatter  the formatter
     *
     * @return the table column
     */
    public TableColumn<T, LocalDateTime> createLocalDateTimeValueColumn(String columnName, String property, DateTimeFormatter formatter) {
        TableColumn<T, LocalDateTime> column = createColumn(columnName);
        column.setCellValueFactory(new CellPropertyValueFactory<>(property));

        column.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(LocalDateTime item, boolean empty) {
                super.updateItem(item, empty);
                if (empty)
                    setText(null);
                else {
                    if (item != null) {
                        setText(item.format(formatter));
                    } else {
                        setText(null);
                    }
                }
            }
        });

        return column;
    }
}
