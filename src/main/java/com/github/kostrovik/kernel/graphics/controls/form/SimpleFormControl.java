package com.github.kostrovik.kernel.graphics.controls.form;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.util.Callback;

import java.util.Objects;

/**
 * project: kernel
 * author:  kostrovik
 * date:    2018-11-29
 * github:  https://github.com/kostrovik/kernel
 */
public class SimpleFormControl<T extends Control> extends Control {
    private Label label;
    private T control;
    private Callback<T, Boolean> validationCallback;
    private BooleanProperty valid;
    private BooleanProperty showLabel;

    public SimpleFormControl(String label, T control) {
        this.label = new Label(label);
        Objects.requireNonNull(control);
        this.control = control;
        this.validationCallback = param -> true;
        this.valid = new SimpleBooleanProperty(true);
        this.showLabel = new SimpleBooleanProperty(true);
        addListeners();
    }

    public Label getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label.setText(label);
    }

    public T getControl() {
        return control;
    }

    public void setControl(T control) {
        Objects.requireNonNull(control);
        this.control = control;
    }

    public Callback<T, Boolean> getValidationCallback() {
        return validationCallback;
    }

    public void setValidationCallback(Callback<T, Boolean> validationCallback) {
        Objects.requireNonNull(validationCallback);
        this.validationCallback = validationCallback;
    }

    public boolean isValid() {
        setValid(true);
        validate();
        return valid.get();
    }

    public BooleanProperty validProperty() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid.set(valid);
    }

    public void validate() {
        setValid(validationCallback.call(control));
    }

    public boolean isShowLabel() {
        return showLabel.get();
    }

    public BooleanProperty showLabelProperty() {
        return showLabel;
    }

    public void setShowLabel(boolean showLabel) {
        this.showLabel.set(showLabel);
    }


    private void addListeners() {
        validProperty().addListener((observable, oldValue, newValue) -> setErrorClass(newValue));
        disableProperty().addListener((observable, oldValue, newValue) -> setControlDisable(newValue));
        visibleProperty().addListener((observable, oldValue, newValue) -> setControlVisibility(newValue));
        managedProperty().addListener((observable, oldValue, newValue) -> setControlManaged(newValue));
    }

    private void setErrorClass(boolean isValid) {
        if (isValid) {
            getControl().getStyleClass().removeAll("has-error");
        } else {
            getControl().getStyleClass().add("has-error");
        }
    }

    private void setControlDisable(boolean isDisabled) {
        getControl().setDisable(isDisabled);
    }

    private void setControlVisibility(boolean isVisible) {
        getLabel().setVisible(isVisible);
        getControl().setVisible(isVisible);
    }

    private void setControlManaged(boolean isManaged) {
        getLabel().setManaged(isManaged);
        getControl().setManaged(isManaged);
    }
}
