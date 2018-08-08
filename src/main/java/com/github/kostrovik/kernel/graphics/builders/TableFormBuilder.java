package com.github.kostrovik.kernel.graphics.builders;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

/**
 * project: glcmtx
 * author:  kostrovik
 * date:    30/07/2018
 * github:  https://github.com/kostrovik/glcmtx
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
}
