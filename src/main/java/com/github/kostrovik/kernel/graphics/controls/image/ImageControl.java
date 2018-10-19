package com.github.kostrovik.kernel.graphics.controls.image;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;

import java.io.InputStream;

/**
 * project: kernel
 * author:  kostrovik
 * date:    2018-10-02
 * github:  https://github.com/kostrovik/kernel
 */
public class ImageControl extends Control {
    private ObjectProperty<InputStream> imageStream;
    private IntegerProperty imageWidth;
    private IntegerProperty imageHeight;

    public ImageControl() {
        this.imageStream = new SimpleObjectProperty<>();
        this.imageWidth = new SimpleIntegerProperty(100);
        this.imageHeight = new SimpleIntegerProperty(100);
    }

    @Override
    protected Skin<?> createDefaultSkin() {
        return new ImageControlSkin(this);
    }

    public InputStream getImageStream() {
        return imageStream.get();
    }

    public ObjectProperty<InputStream> imageStreamProperty() {
        return imageStream;
    }

    public void setImageStream(InputStream imageStream) {
        this.imageStream.set(imageStream);
    }

    public int getImageWidth() {
        return imageWidth.get();
    }

    public IntegerProperty imageWidthProperty() {
        return imageWidth;
    }

    public void setImageWidth(int imageWidth) {
        this.imageWidth.set(imageWidth);
    }

    public int getImageHeight() {
        return imageHeight.get();
    }

    public IntegerProperty imageHeightProperty() {
        return imageHeight;
    }

    public void setImageHeight(int imageHeight) {
        this.imageHeight.set(imageHeight);
    }
}
