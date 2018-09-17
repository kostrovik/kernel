package com.github.kostrovik.kernel.graphics.common;

import com.github.kostrovik.kernel.graphics.builders.ButtonBuilder;
import com.github.kostrovik.kernel.graphics.builders.TableFormBuilder;
import com.github.kostrovik.kernel.graphics.common.icons.SolidIcons;
import com.github.kostrovik.kernel.graphics.controls.base.columns.PagedColumn;
import com.github.kostrovik.kernel.graphics.controls.dropdown.SearchableDropDownField;
import com.github.kostrovik.kernel.graphics.controls.field.LabeledTextField;
import com.github.kostrovik.kernel.graphics.controls.notification.Notification;
import com.github.kostrovik.kernel.interfaces.controls.ControlBuilderFacadeInterface;
import com.github.kostrovik.kernel.interfaces.controls.IconInterface;
import com.github.kostrovik.kernel.interfaces.controls.IconPositionInterface;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.util.Callback;

/**
 * Класс представляющий фасад к различным билдерам контролов.
 * <p>
 * project: kernel
 * author:  kostrovik
 * date:    20/07/2018
 * github:  https://github.com/kostrovik/kernel
 */
public class ControlBuilderFacade implements ControlBuilderFacadeInterface {
    @Override
    public Button createButton(String buttonTitle) {
        return createButton(buttonTitle, null);
    }

    @Override
    public Button createButton(String buttonTitle, IconInterface icon) {
        ButtonBuilder builder = new ButtonBuilder();
        if (icon == null) {
            return builder.createButton(buttonTitle);
        }

        return builder.createButton((SolidIcons) icon, buttonTitle);
    }

    @Override
    public Button createButton(String buttonTitle, IconInterface icon, IconPositionInterface iconPosition) {
        ButtonBuilder builder = new ButtonBuilder();
        Button button = builder.createButton((SolidIcons) icon, buttonTitle);
        builder.setIconPosition(button, (ButtonIconPosition) iconPosition);

        return button;
    }

    @Override
    public Button createButton(String buttonTitle, IconInterface icon, boolean bindFontSize) {
        ButtonBuilder builder = new ButtonBuilder();
        return builder.createButton((SolidIcons) icon, buttonTitle, bindFontSize);
    }

    @Override
    public Button createButton(String buttonTitle, IconInterface icon, IconInterface iconhover, boolean bindFontSize) {
        ButtonBuilder builder = new ButtonBuilder();
        return builder.createButton((SolidIcons) icon, (SolidIcons) iconhover, buttonTitle, bindFontSize);
    }

    @Override
    public Notification createFormNotification() {
        return new Notification();
    }

    @Override
    public LabeledTextField createTextField(String labelValue) {
        return new LabeledTextField(labelValue);
    }

    @Override
    public LabeledTextField createPasswordField(String labelValue) {
        return new LabeledTextField(labelValue, true);
    }


    @Override
    public <E, V> PagedColumn<E, V> createTableColumn(String columnName) {
        return new PagedColumn<>(columnName);
    }

    @Override
    public <E, V> PagedColumn<E, V> createTableColumn(String columnName, Callback<E, V> cellValueFactory) {
        PagedColumn<E, V> column = createTableColumn(columnName);
        column.setCellValueFactory(cellValueFactory);
        return column;
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
    public <E extends Comparable> SearchableDropDownField<E> addDropDownField(GridPane formLayout, String label, String attribute) {
        TableFormBuilder builder = new TableFormBuilder();
        return builder.createFormDropDownField(formLayout, label, attribute);
    }

    @Override
    public <E extends Comparable> SearchableDropDownField<E> createDropDownField(String labelValue, String attribute) {
        return new SearchableDropDownField<>(labelValue, attribute);
    }
}
