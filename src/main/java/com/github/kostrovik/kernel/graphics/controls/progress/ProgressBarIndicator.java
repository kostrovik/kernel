package com.github.kostrovik.kernel.graphics.controls.progress;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.DoublePropertyBase;
import javafx.beans.property.ObjectProperty;
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
        this.format = new SimpleObjectProperty<>(defaultFormat);
        setProgress(progress);
    }

    public ProgressBarIndicator(double done, double total) {
        this.format = new SimpleObjectProperty<>(defaultFormat);
        setDone(done);
        setTotal(total);
    }

    // свойсто прогресс
    public void setProgress(double value) {
        progressProperty().set(value);
    }

    public double getProgress() {
        return progress == null ? -1 : progress.get();
    }

    public DoubleProperty progressProperty() {
        if (progress == null) {
            progress = new DoublePropertyBase(-1.0) {
                @Override
                public Object getBean() {
                    return ProgressBarIndicator.this;
                }

                @Override
                public String getName() {
                    return "progress";
                }
            };
        }
        return progress;
    }
    // -- свойсто прогресс --

    // свойсто прогресс
    public void setDone(double value) {
        doneProperty().set(value);
    }

    public double getDone() {
        return done == null ? -1 : done.get();
    }

    public DoubleProperty doneProperty() {
        if (done == null) {
            done = new DoublePropertyBase(-1.0) {
                @Override
                public Object getBean() {
                    return ProgressBarIndicator.this;
                }

                @Override
                public String getName() {
                    return "done";
                }
            };
        }
        return done;
    }
    // -- свойсто прогресс --

    // свойсто прогресс
    public void setTotal(double value) {
        totalProperty().set(value);
    }

    public double getTotal() {
        return total == null ? -1 : total.get();
    }

    public DoubleProperty totalProperty() {
        if (total == null) {
            total = new DoublePropertyBase(-1.0) {
                @Override
                public Object getBean() {
                    return ProgressBarIndicator.this;
                }

                @Override
                public String getName() {
                    return "total";
                }
            };
        }
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
