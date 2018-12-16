package com.github.kostrovik.kernel.graphics.controls.progress;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;

import java.util.ArrayList;
import java.util.List;

/**
 * project: kernel
 * author:  kostrovik
 * date:    22/08/2018
 * github:  https://github.com/kostrovik/kernel
 */
public class ProgressBarIndicator extends Control {
    private DoubleProperty done;
    private DoubleProperty total;
    private StringProperty format;
    private final String defaultFormat = "%.2f / %.2f";
    private List<Object> formatAttributes;

    public ProgressBarIndicator(double done) {
        this(done, 0);
    }

    public ProgressBarIndicator(double done, double total) {
        this.done = new SimpleDoubleProperty(done);
        this.total = new SimpleDoubleProperty(total);
        this.format = new SimpleStringProperty(defaultFormat);
        this.formatAttributes = new ArrayList<>();
        this.formatAttributes.add(done);
        this.formatAttributes.add(total);
    }

    public void setDone(double value) {
        doneProperty().set(value);
    }

    public double getDone() {
        return done.get();
    }

    public DoubleProperty doneProperty() {
        return done;
    }

    public void setTotal(double value) {
        totalProperty().set(value);
    }

    public double getTotal() {
        return total.get();
    }

    public DoubleProperty totalProperty() {
        return total;
    }

    public String getFormat() {
        return format.get();
    }

    public StringProperty formatProperty() {
        return format;
    }

    public void setFormat(String format) {
        this.format.set(format);
    }

    public List<Object> getFormatAttributes() {
        return formatAttributes;
    }

    public void setFormatAttributes(List<Object> formatAttributes) {
        this.formatAttributes = formatAttributes;
    }

    @Override
    protected Skin<?> createDefaultSkin() {
        return new ProgressBarIndicatorSkin(this);
    }
}
