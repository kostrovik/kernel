package com.github.kostrovik.kernel.graphics.controls.field;

import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.SkinBase;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

/**
 * project: kernel
 * author:  kostrovik
 * date:    24/07/2018
 * github:  https://github.com/kostrovik/kernel
 */
public class LabeledTextFieldSkin extends SkinBase<LabeledTextField> {
    private TextField textField;

    public LabeledTextFieldSkin(LabeledTextField control) {
        super(control);
        createSkin();
        getSkinnable().editableProperty().addListener(observable -> lockTextField());
        getSkinnable().textProperty().addListener((observable, oldValue, newValue) -> {
            if (!oldValue.equals(newValue)) {
                setText();
            }
        });

        getSkinnable().onActionProperty().addListener((observable, oldValue, newValue) -> textField.setOnAction(newValue));

        textField.focusedProperty().addListener((observable, oldValue, newValue) -> getSkinnable().setFocus(newValue));
    }

    private void lockTextField() {
        textField.setEditable(getSkinnable().isEditable());
    }

    private void setText() {
        textField.setText(getSkinnable().getText());
    }

    private void createSkin() {
        HBox group = new HBox(10);
        group.setAlignment(Pos.CENTER_LEFT);

        Label label = new Label(getSkinnable().getLabel());
        label.setFocusTraversable(false);

        Platform.runLater(() -> label.setMinWidth(label.getBoundsInLocal().getWidth()));

        textField = getSkinnable().isPassoword() ? new PasswordField() : new TextField();
        textField.textProperty().addListener((observable, oldValue, newValue) -> getSkinnable().setText(newValue));

        HBox.setHgrow(textField, Priority.ALWAYS);

        group.getChildren().addAll(label, textField);

        getChildren().addAll(group);
    }
}
