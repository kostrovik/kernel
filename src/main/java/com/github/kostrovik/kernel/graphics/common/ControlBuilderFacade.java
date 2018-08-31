package com.github.kostrovik.kernel.graphics.common;

import com.github.kostrovik.kernel.graphics.builders.CellPropertyValueFactory;
import com.github.kostrovik.kernel.graphics.controls.dropdown.SearchableDropDownField;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import com.github.kostrovik.kernel.graphics.builders.ButtonBuilder;
import com.github.kostrovik.kernel.graphics.builders.TableColumnBuilder;
import com.github.kostrovik.kernel.graphics.builders.TableFormBuilder;
import com.github.kostrovik.kernel.graphics.common.icons.SolidIcons;
import com.github.kostrovik.kernel.graphics.controls.field.LabeledTextField;
import com.github.kostrovik.kernel.graphics.controls.notification.Notification;
import com.github.kostrovik.kernel.interfaces.controls.ControlBuilderFacadeInterface;
import com.github.kostrovik.kernel.interfaces.controls.IconInterface;
import com.github.kostrovik.kernel.interfaces.controls.IconPositionInterface;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Класс представляющий фасад к различным билдерам контролов.
 * <p>
 * project: kernel
 * author:  kostrovik
 * date:    20/07/2018
 * github:  https://github.com/kostrovik/kernel
 */
public class ControlBuilderFacade implements ControlBuilderFacadeInterface {
    public Button createButton(String buttonTitle) {
        return createButton(buttonTitle, null);
    }

    public Button createButton(String buttonTitle, IconInterface icon) {
        ButtonBuilder builder = new ButtonBuilder();
        if (icon == null) {
            return builder.createButton(buttonTitle);
        }

        return builder.createButton((SolidIcons) icon, buttonTitle);
    }

    public Button createButton(String buttonTitle, IconInterface icon, IconPositionInterface iconPosition) {
        ButtonBuilder builder = new ButtonBuilder();
        Button button = builder.createButton((SolidIcons) icon, buttonTitle);
        builder.setIconPosition(button, (ButtonIconPosition) iconPosition);

        return button;
    }

    public Button createButton(String buttonTitle, IconInterface icon, boolean bindFontSize) {
        ButtonBuilder builder = new ButtonBuilder();
        Button button = builder.createButton((SolidIcons) icon, buttonTitle, bindFontSize);
        return button;
    }

    public Button createButton(String buttonTitle, IconInterface icon, IconInterface iconhover, boolean bindFontSize) {
        ButtonBuilder builder = new ButtonBuilder();
        Button button = builder.createButton((SolidIcons) icon, (SolidIcons) iconhover, buttonTitle, bindFontSize);
        return button;
    }

    public Notification createFormNotification() {
        return new Notification();
    }

    public LabeledTextField createTextField(String labelValue) {
        return new LabeledTextField(labelValue);
    }

    public LabeledTextField createPasswordField(String labelValue) {
        return new LabeledTextField(labelValue, true);
    }

    public <E, V> TableColumn<E, V> createTableColumn(String columnName) {
        TableColumnBuilder<E, V> builder = new TableColumnBuilder<>();
        return builder.createColumn(columnName);
    }

    public <E> TableColumn<E, String> createTableStringColumn(String columnName, String propertyName) {
        TableColumnBuilder<E, String> builder = new TableColumnBuilder<>();
        return builder.createStringValueColumn(columnName, propertyName);
    }

    public <E> TableColumn<E, String> createTableMultilineColumn(String columnName, String propertyName) {
        TableColumnBuilder<E, String> builder = new TableColumnBuilder<>();
        return builder.createMultilineStringValueColumn(columnName, propertyName);
    }

    public <E> TableColumn<E, Boolean> createTableBooleanColumn(String columnName, String propertyName) {
        TableColumnBuilder<E, Boolean> builder = new TableColumnBuilder<>();
        return builder.createBooleanValueColumn(columnName, propertyName);
    }

    @Override
    public <E> TableColumn<E, Integer> createTableIntegerColumn(String columnName, String propertyName) {
        TableColumnBuilder<E, Integer> builder = new TableColumnBuilder<>();
        TableColumn<E, Integer> column = builder.createColumn(columnName);
        column.setCellValueFactory(new CellPropertyValueFactory<>(propertyName));
        return column;
    }

    public <E> TableColumn<E, LocalDateTime> createTableLocalDateTimeColumn(String columnName, String propertyName, DateTimeFormatter formatter) {
        TableColumnBuilder<E, LocalDateTime> builder = new TableColumnBuilder<>();
        return builder.createLocalDateTimeValueColumn(columnName, propertyName, formatter);
    }

    @Override
    public GridPane createTableFormLayout() {
        TableFormBuilder builder = new TableFormBuilder();
        return builder.createLayout();
    }

    @Override
    public TextField addTextField(GridPane formLayout, String label) {
        TableFormBuilder builder = new TableFormBuilder();
        return builder.createFormTextField(formLayout, label, false);
    }

    @Override
    public TextArea addTextAreaField(GridPane formLayout, String label) {
        TableFormBuilder builder = new TableFormBuilder();
        return builder.createFormTextArea(formLayout, label);
    }

    @Override
    public TextField addPasswordField(GridPane formLayout, String label) {
        TableFormBuilder builder = new TableFormBuilder();
        return builder.createFormTextField(formLayout, label, true);
    }

    @Override
    public CheckBox addCheckBoxField(GridPane formLayout, String label) {
        TableFormBuilder builder = new TableFormBuilder();
        return builder.createFormCheckBoxField(formLayout, label);
    }

    @Override
    public CheckBox addCheckBoxField(GridPane formLayout, String label, int column) {
        TableFormBuilder builder = new TableFormBuilder();
        return builder.createFormCheckBoxField(formLayout, label, column);
    }

    @Override
    public void addSeparator(GridPane formLayout) {
        TableFormBuilder builder = new TableFormBuilder();
        builder.addSeparator(formLayout);
    }

    @Override
    public <E extends Comparable> SearchableDropDownField<E> addDropDownField(GridPane formLayout, String label) {
        TableFormBuilder builder = new TableFormBuilder();
        return builder.createFormDropDownField(formLayout, label);
    }

    @Override
    public <E extends Comparable> SearchableDropDownField<E> createDropDownField(String labelValue, String attribute) {
        return new SearchableDropDownField<E>(labelValue, attribute);
    }
}
