package com.github.kostrovik.kernel.graphics.controls.field;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;

/**
 * project: glcmtx
 * author:  kostrovik
 * date:    24/07/2018
 * github:  https://github.com/kostrovik/glcmtx
 */
public class LabeledTextField extends Control {
    private final ObjectProperty<String> label;
    private final ObjectProperty<String> text;
    private final ObjectProperty<Boolean> editable;
    private boolean isPassoword;

    public LabeledTextField(String label) {
        this.label = new SimpleObjectProperty<>();
        this.text = new SimpleObjectProperty<>("");
        this.editable = new SimpleObjectProperty<>();
        setLabel(label);
    }

    public LabeledTextField(String label, boolean isPassoword) {
        this(label);
        this.isPassoword = isPassoword;
    }

    // свойсто название поля
    public ObjectProperty<String> labelProperty() {
        return label;
    }

    public String getLabel() {
        return label.get();
    }

    public void setLabel(String labelValue) {
        label.set(labelValue);
    }
    // -- свойсто название поля --

    // свойсто текст
    public ObjectProperty<String> textProperty() {
        return text;
    }

    public String getText() {
        return text.get();
    }

    public void setText(String textValue) {
        text.set(textValue);
    }
    // -- свойсто название поля --

    // свойсто редактирования
    public ObjectProperty<Boolean> editableProperty() {
        return editable;
    }

    public Boolean isEditable() {
        return editable.get();
    }

    public void setEditable(Boolean editableValue) {
        editable.set(editableValue);
    }
    // -- свойсто редактирования --

    public boolean isPassoword() {
        return isPassoword;
    }

    @Override
    protected Skin<?> createDefaultSkin() {
        return new LabeledTextFieldSkin(this);
    }

    public void clear() {
        setText("");
    }
}
