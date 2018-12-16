package com.github.kostrovik.kernel.graphics.controls.form;

import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.Separator;
import javafx.scene.control.SkinBase;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

/**
 * project: kernel
 * author:  kostrovik
 * date:    2018-11-28
 * github:  https://github.com/kostrovik/kernel
 */
public class SimpleFormSkin extends SkinBase<SimpleForm> {
    private GridPane formLayout;
    private ObservableList<SimpleFormControl<?>> formControls;

    public SimpleFormSkin(SimpleForm control, ObservableList<SimpleFormControl<?>> formControls) {
        super(control);
        this.formControls = formControls;

        createSkin();
        getSkinnable().disabledProperty().addListener((observable, oldValue, newValue) -> formControls.forEach(formControl -> setCongrolDisabled(formControl, newValue)));
        this.formControls.addListener((ListChangeListener<SimpleFormControl>) c -> recreateForm());
    }

    private void setCongrolDisabled(SimpleFormControl formControl, boolean isDisabled) {
        if (!(formControl.getControl() instanceof Separator)) {
            formControl.getControl().setDisable(isDisabled);
        }
    }

    private void addField(SimpleFormControl formControl) {
        if (formControl.getControl() instanceof Separator) {
            formLayout.add(formControl.getControl(), 0, formLayout.getRowCount(), 2, 1);
        } else {
            if (formControl.isShowLabel()) {
                formLayout.addRow(formLayout.getRowCount(), formControl.getLabel(), formControl.getControl());
                GridPane.setHgrow(formControl.getLabel(), Priority.NEVER);
            } else {
                formLayout.add(formControl.getControl(), 1, formLayout.getRowCount(), 1, 1);
            }
            GridPane.setHgrow(formControl.getControl(), Priority.ALWAYS);
        }
    }

    private void recreateForm() {
        formLayout.getChildren().removeAll();
        this.formControls.forEach(this::addField);
    }

    private void createSkin() {
        formLayout = new GridPane();
        formLayout.setHgap(10);
        formLayout.setVgap(10);
        formLayout.setPadding(new Insets(10, 10, 10, 10));

        this.formControls.forEach(this::addField);

        getChildren().addAll(formLayout);
    }
}
