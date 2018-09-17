package com.github.kostrovik.kernel.interfaces.controls;

import com.github.kostrovik.kernel.graphics.controls.base.columns.PagedColumn;
import com.github.kostrovik.kernel.graphics.controls.dropdown.SearchableDropDownField;
import javafx.scene.control.Button;
import javafx.scene.control.Skinnable;
import javafx.scene.layout.GridPane;
import javafx.util.Callback;

/**
 * project: kernel
 * author:  kostrovik
 * date:    30/07/2018
 * github:  https://github.com/kostrovik/kernel
 */
public interface ControlBuilderFacadeInterface {
    Button createButton(String buttonTitle);

    Button createButton(String buttonTitle, IconInterface icon);

    Button createButton(String buttonTitle, IconInterface icon, IconPositionInterface iconPosition);

    Button createButton(String buttonTitle, IconInterface icon, boolean bindFontSize);

    Button createButton(String buttonTitle, IconInterface icon, IconInterface iconHover, boolean bindFontSize);

    Skinnable createFormNotification();

    Skinnable createTextField(String labelValue);

    Skinnable createPasswordField(String labelValue);

    <E, V> PagedColumn<E, V> createTableColumn(String columnName);

    <E, V> PagedColumn<E, V> createTableColumn(String columnName, Callback<E, V> cellValueFactory);

    GridPane createTableFormLayout();

    Skinnable addTextField(GridPane formLayout, String label);

    Skinnable addTextAreaField(GridPane formLayout, String label);

    Skinnable addPasswordField(GridPane formLayout, String label);

    Skinnable addCheckBoxField(GridPane formLayout, String label);

    Skinnable addCheckBoxField(GridPane formLayout, String label, int column);

    void addSeparator(GridPane formLayout);

    <E extends Comparable> SearchableDropDownField<E> addDropDownField(GridPane formLayout, String label, String attribute);

    <E extends Comparable> SearchableDropDownField<E> createDropDownField(String labelValue, String attribute);
}
