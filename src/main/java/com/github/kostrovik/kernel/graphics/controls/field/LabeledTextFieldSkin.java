package com.github.kostrovik.kernel.graphics.controls.field;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.SkinBase;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

/**
 * project: glcmtx
 * author:  kostrovik
 * date:    24/07/2018
 * github:  https://github.com/kostrovik/glcmtx
 */
public class LabeledTextFieldSkin extends SkinBase<LabeledTextField> {
    private HBox group;
    private Label label;
    private TextField textField;

    public LabeledTextFieldSkin(LabeledTextField control) {
        super(control);
        createSkin();
        control.editableProperty().addListener(observable -> lockTextField());
        control.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!oldValue.equals(newValue)) {
                setText();
            }
        });
    }

    private void lockTextField() {
        textField.setEditable(getSkinnable().isEditable());
    }

    private void setText() {
        textField.setText(getSkinnable().getText());
    }

    private void createSkin() {
        group = new HBox(10);
        group.setAlignment(Pos.CENTER_LEFT);

        label = new Label(getSkinnable().getLabel());

        Platform.runLater(() -> {
            label.setMinWidth(label.getBoundsInLocal().getWidth());
        });

        textField = getSkinnable().isPassoword() ? new PasswordField() : new TextField();
        textField.textProperty().addListener((observable, oldValue, newValue) -> getSkinnable().setText(newValue));

        HBox.setHgrow(textField, Priority.ALWAYS);

        group.getChildren().addAll(label, textField);

        getChildren().addAll(group);
    }
}
