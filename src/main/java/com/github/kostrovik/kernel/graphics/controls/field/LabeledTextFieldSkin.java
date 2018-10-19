package com.github.kostrovik.kernel.graphics.controls.field;

import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

import java.text.NumberFormat;
import java.text.ParsePosition;

/**
 * project: kernel
 * author:  kostrovik
 * date:    24/07/2018
 * github:  https://github.com/kostrovik/kernel
 */
public class LabeledTextFieldSkin extends SkinBase<LabeledTextField> {
    private TextField textField;
    private TextFormatter<?> textFormatter;
    private TextFormatter<?> defaultTextFormatter;

    public LabeledTextFieldSkin(LabeledTextField control) {
        super(control);
        this.textFormatter = getTextFormatter();

        createSkin();
        getSkinnable().editableProperty().addListener(observable -> lockTextField());
        getSkinnable().textProperty().addListener((observable, oldValue, newValue) -> {
            if (!oldValue.equals(newValue)) {
                setText();
            }
        });

        getSkinnable().onActionProperty().addListener((observable, oldValue, newValue) -> textField.setOnAction(newValue));

        textField.focusedProperty().addListener((observable, oldValue, newValue) -> getSkinnable().setFocus(newValue));

        getSkinnable().onlyIntegerProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                textField.setTextFormatter(textFormatter);
            } else {
                textField.setTextFormatter(defaultTextFormatter);
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
        HBox group = new HBox(10);
        group.setAlignment(Pos.CENTER_LEFT);

        Label label = new Label(getSkinnable().getLabel());
        label.setFocusTraversable(false);

        Platform.runLater(() -> label.setMinWidth(label.getBoundsInLocal().getWidth()));

        textField = getSkinnable().isPassoword() ? new PasswordField() : new TextField();
        textField.textProperty().addListener((observable, oldValue, newValue) -> getSkinnable().setText(newValue));
        setText();

        defaultTextFormatter = textField.getTextFormatter();
        if (getSkinnable().isOnlyInteger()) {
            textField.setTextFormatter(textFormatter);
        }

        HBox.setHgrow(textField, Priority.ALWAYS);

        group.getChildren().addAll(label, textField);

        getChildren().addAll(group);
    }

    private TextFormatter<?> getTextFormatter() {
        NumberFormat format = NumberFormat.getIntegerInstance();

        return new TextFormatter<>(c -> {
            if (c.getControlNewText().isEmpty()) {
                return c;
            }

            if (c.getControlText().length() < c.getControlNewText().length() && c.getControlNewText().charAt(c.getRangeStart()) == ',') {
                c.setText(".");
            }

            ParsePosition parsePosition = new ParsePosition(0);
            Object object = format.parse(c.getControlNewText(), parsePosition);

            if (object == null || parsePosition.getIndex() < c.getControlNewText().length()) {
                return null;
            } else {
                return c;
            }
        });
    }
}
