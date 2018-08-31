package com.github.kostrovik.kernel.interfaces.controls;

import com.github.kostrovik.kernel.graphics.controls.dropdown.SearchableDropDownField;
import javafx.scene.control.Button;
import javafx.scene.control.Skinnable;
import javafx.scene.control.TableColumn;
import javafx.scene.layout.GridPane;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

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

    <E, V> TableColumn<E, V> createTableColumn(String columnName);

    <E> TableColumn<E, String> createTableStringColumn(String columnName, String propertyName);

    <E> TableColumn<E, String> createTableMultilineColumn(String columnName, String propertyName);

    <E> TableColumn<E, Boolean> createTableBooleanColumn(String columnName, String propertyName);

    <E> TableColumn<E, Integer> createTableIntegerColumn(String columnName, String propertyName);

    <E> TableColumn<E, LocalDateTime> createTableLocalDateTimeColumn(String columnName, String propertyName, DateTimeFormatter formatter);

    GridPane createTableFormLayout();

    Skinnable addTextField(GridPane formLayout, String label);

    Skinnable addTextAreaField(GridPane formLayout, String label);

    Skinnable addPasswordField(GridPane formLayout, String label);

    Skinnable addCheckBoxField(GridPane formLayout, String label);

    Skinnable addCheckBoxField(GridPane formLayout, String label, int column);

    void addSeparator(GridPane formLayout);

    <E extends Comparable> SearchableDropDownField<E> addDropDownField(GridPane formLayout, String label);

    <E extends Comparable> SearchableDropDownField<E> createDropDownField(String labelValue, String attribute);
}
