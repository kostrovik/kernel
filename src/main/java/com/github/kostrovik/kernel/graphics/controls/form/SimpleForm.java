package com.github.kostrovik.kernel.graphics.controls.form;

import com.github.kostrovik.kernel.graphics.controls.dropdown.SearchableDropDownField;
import com.github.kostrovik.kernel.interfaces.PaginationServiceInterface;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Control;
import javafx.scene.control.DatePicker;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Separator;
import javafx.scene.control.Skin;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.util.Objects;

/**
 * project: kernel
 * author:  kostrovik
 * date:    2018-11-28
 * github:  https://github.com/kostrovik/kernel
 */
public class SimpleForm extends Control {
    private ObservableList<SimpleFormControl<?>> formControls;
    private BooleanProperty formValid;

    public SimpleForm() {
        this.formControls = FXCollections.observableArrayList();
        this.formValid = new SimpleBooleanProperty(true);
    }

    public SimpleFormControl<TextField> addTextField(String label) {
        return addTextField(label, false);
    }

    public SimpleFormControl<TextField> addTextField(String label, boolean isPassword) {
        TextField field = isPassword ? new PasswordField() : new TextField();
        return addField(label, field);
    }

    public SimpleFormControl<TextArea> addTextArea(String label) {
        TextArea field = new TextArea();
        return addField(label, field);
    }

    public SimpleFormControl<CheckBox> addCheckBoxField(String label) {
        CheckBox field = new CheckBox();
        return addField(label, field);
    }

    public SimpleFormControl<CheckBox> addCheckBoxField(String label, boolean showLabel) {
        CheckBox field = new CheckBox(label);
        SimpleFormControl<CheckBox> formControl = new SimpleFormControl<>(label, field);
        formControl.setShowLabel(showLabel);
        formControls.add(formControl);
        return formControl;
    }

    public void addSeparator() {
        Separator separator = new Separator();
        addField("", separator);
    }

    public <E extends Comparable<E>> SimpleFormControl<SearchableDropDownField<E>> addDropDownField(PaginationServiceInterface<E> service, String label, String attribute) {
        SearchableDropDownField<E> field = new SearchableDropDownField<>(service, label, attribute);
        return addField(label, field);
    }

    public SimpleFormControl<DatePicker> addDatePickerField(String label) {
        DatePicker field = new DatePicker();
        return addField(label, field);
    }

    private <T extends Control> SimpleFormControl<T> addField(String label, T control) {
        if (Objects.nonNull(control)) {
            SimpleFormControl<T> formControl = new SimpleFormControl<>(label, control);
            formControls.add(formControl);
            return formControl;
        }
        return null;
    }

    public boolean isFormValid() {
        validate();
        return formValid.get();
    }

    public BooleanProperty formValidProperty() {
        return formValid;
    }

    public void validate() {
        long result = formControls.parallelStream().filter(SimpleFormControl::isValid).count();
        formValid.set(result == formControls.size());
    }

    @Override
    protected Skin<?> createDefaultSkin() {
        return new SimpleFormSkin(this, formControls);
    }
}
