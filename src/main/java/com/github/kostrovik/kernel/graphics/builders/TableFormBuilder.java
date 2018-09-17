package com.github.kostrovik.kernel.graphics.builders;

import com.github.kostrovik.kernel.graphics.controls.dropdown.SearchableDropDownField;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

/**
 * project: kernel
 * author:  kostrovik
 * date:    30/07/2018
 * github:  https://github.com/kostrovik/kernel
 */
public class TableFormBuilder {
    public GridPane createLayout() {
        GridPane formTable = new GridPane();
        formTable.setHgap(10);
        formTable.setVgap(10);
        formTable.setPadding(new Insets(10, 0, 0, 0));

        return formTable;
    }

    public TextField createFormTextField(GridPane layout, String label, boolean isPassword) {
        Label fieldLabel = new Label(label);
        TextField field = isPassword ? new PasswordField() : new TextField();

        layout.addRow(layout.getRowCount(), fieldLabel, field);

        GridPane.setHgrow(fieldLabel, Priority.NEVER);
        GridPane.setHgrow(field, Priority.ALWAYS);

        return field;
    }

    public TextArea createFormTextArea(GridPane layout, String label) {
        Label fieldLabel = new Label(label);
        TextArea field = new TextArea();

        layout.addRow(layout.getRowCount(), fieldLabel, field);

        GridPane.setHgrow(fieldLabel, Priority.NEVER);
        GridPane.setHgrow(field, Priority.ALWAYS);

        return field;
    }

    public CheckBox createFormCheckBoxField(GridPane layout, String label) {
        Label fieldLabel = new Label(label);
        CheckBox field = new CheckBox();

        layout.addRow(layout.getRowCount(), fieldLabel, field);

        GridPane.setHgrow(fieldLabel, Priority.NEVER);
        GridPane.setHgrow(field, Priority.ALWAYS);

        return field;
    }

    public CheckBox createFormCheckBoxField(GridPane layout, String label, int column) {
        CheckBox field = new CheckBox(label);

        layout.add(field, column, layout.getRowCount(), layout.getColumnCount(), 1);

        GridPane.setHgrow(field, Priority.ALWAYS);

        return field;
    }

    public void addSeparator(GridPane layout) {
        Separator separator = new Separator();

        layout.add(separator, 0, layout.getRowCount(), layout.getColumnCount(), 1);
    }

    public <E extends Comparable> SearchableDropDownField<E> createFormDropDownField(GridPane layout, String label, String attribute) {
        Label fieldLabel = new Label(label);
        SearchableDropDownField<E> field = new SearchableDropDownField<>(label, false, attribute);

        layout.addRow(layout.getRowCount(), fieldLabel, field);

        GridPane.setHgrow(fieldLabel, Priority.NEVER);
        GridPane.setHgrow(field, Priority.ALWAYS);

        return field;
    }
}
