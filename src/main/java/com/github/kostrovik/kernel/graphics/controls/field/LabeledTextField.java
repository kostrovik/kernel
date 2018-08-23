package com.github.kostrovik.kernel.graphics.controls.field;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ObjectPropertyBase;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;

/**
 * project: kernel
 * author:  kostrovik
 * date:    24/07/2018
 * github:  https://github.com/kostrovik/kernel
 */
public class LabeledTextField extends Control {
    private final ObjectProperty<String> label;
    private final ObjectProperty<String> text;
    private final ObjectProperty<Boolean> editable;
    private boolean isPassoword;

    private ObjectProperty<EventHandler<ActionEvent>> onAction = new ObjectPropertyBase<EventHandler<ActionEvent>>() {
        @Override
        protected void invalidated() {
            setEventHandler(ActionEvent.ACTION, get());
        }

        @Override
        public Object getBean() {
            return LabeledTextField.this;
        }

        @Override
        public String getName() {
            return "onAction";
        }
    };

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

    public ObjectProperty<EventHandler<ActionEvent>> onActionProperty() {
        return onAction;
    }

    public EventHandler<ActionEvent> getOnAction() {
        return onActionProperty().get();
    }

    public void setOnAction(EventHandler<ActionEvent> value) {
        onActionProperty().set(value);
    }

    public void setFocus(boolean value) {
        setFocused(value);
    }

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
