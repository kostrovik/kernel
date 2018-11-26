package com.github.kostrovik.kernel.graphics.controls.progress;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;

/**
 * project: kernel
 * author:  kostrovik
 * date:    22/08/2018
 * github:  https://github.com/kostrovik/kernel
 */
public class ProgressBarIndicator extends Control {
    private DoubleProperty progress;
    private DoubleProperty done;
    private DoubleProperty total;
    private ObjectProperty<String> format;
    private String defaultFormat = "%.2f / %.2f";

    public ProgressBarIndicator(double progress) {
        this();
        setProgress(progress);
    }

    public ProgressBarIndicator(double done, double total) {
        this();
        setDone(done);
        setTotal(total);
    }

    private ProgressBarIndicator() {
        this.progress = new SimpleDoubleProperty(-1.0);
        this.done = new SimpleDoubleProperty(-1.0);
        this.total = new SimpleDoubleProperty(-1.0);
        this.format = new SimpleObjectProperty<>(defaultFormat);
    }

    // свойсто прогресс
    public void setProgress(double value) {
        progressProperty().set(value);
    }

    public double getProgress() {
        return progress.get();
    }

    public DoubleProperty progressProperty() {
        return progress;
    }
    // -- свойсто прогресс --

    // свойсто прогресс
    public void setDone(double value) {
        doneProperty().set(value);
    }

    public double getDone() {
        return done.get();
    }

    public DoubleProperty doneProperty() {
        return done;
    }
    // -- свойсто прогресс --

    // свойсто прогресс
    public void setTotal(double value) {
        totalProperty().set(value);
    }

    public double getTotal() {
        return total.get();
    }

    public DoubleProperty totalProperty() {
        return total;
    }
    // -- свойсто прогресс --

    // свойсто форматирования текста
    public void setFormat(String format) {
        this.format.set(format);
    }

    public String getFormat() {
        return format.get();
    }

    public ObjectProperty<String> formatProperty() {
        return format;
    }
    // -- свойсто форматирования текста --

    @Override
    protected Skin<?> createDefaultSkin() {
        return new ProgressBarIndicatorSkin(this);
    }
}
