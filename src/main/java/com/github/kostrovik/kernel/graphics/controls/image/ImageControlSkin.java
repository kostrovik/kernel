package com.github.kostrovik.kernel.graphics.controls.image;

import com.github.kostrovik.kernel.settings.Configurator;
import javafx.application.Platform;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.SkinBase;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * project: kernel
 * author:  kostrovik
 * date:    2018-10-02
 * github:  https://github.com/kostrovik/kernel
 */
public class ImageControlSkin extends SkinBase<ImageControl> {
    private static Logger logger = Configurator.getConfig().getLogger(ImageControlSkin.class.getName());

    private StackPane control;
    private ImageView imageView;
    private double ratioDelta;
    private Image image;
    private boolean isEmptyImage;
    private byte[] imageRawData;

    public ImageControlSkin(ImageControl control) {
        super(control);

        createSkin();

        getSkinnable().minWidthProperty().bind(getSkinnable().imageWidthProperty());
        getSkinnable().minHeightProperty().bind(getSkinnable().imageHeightProperty());

        getSkinnable().imageStreamProperty().addListener((observable, oldValue, newValue) -> {
            InputStream stream;
            if (Objects.nonNull(newValue)) {
                stream = newValue;
                isEmptyImage = false;
            } else {
                stream = Objects.requireNonNull(getDefaultImageFile());
                isEmptyImage = true;
            }

            try {
                imageRawData = stream.readAllBytes();
            } catch (IOException e) {
                logger.log(Level.WARNING, "Не возможно прочитать файл", e);
            }

            setViewImage();
            setViewportPosition(getSkinnable().getImageWidth(), getSkinnable().getImageHeight());
        });

        getSkinnable().imageWidthProperty().addListener((observable, oldValue, newValue) -> {
            setViewImage();
            setViewportPosition(getSkinnable().getImageWidth(), getSkinnable().getImageHeight());
        });

        getSkinnable().imageHeightProperty().addListener((observable, oldValue, newValue) -> {
            setViewImage();
            setViewportPosition(getSkinnable().getImageWidth(), getSkinnable().getImageHeight());
        });
    }

    private void createSkin() {
        Pane container = new Pane();
        control = new StackPane();

        control.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
        imageView = new ImageView();

        InputStream stream;
        if (Objects.nonNull(getSkinnable().getImageStream())) {
            stream = getSkinnable().getImageStream();
            isEmptyImage = false;
        } else {
            stream = Objects.requireNonNull(getDefaultImageFile());
            isEmptyImage = true;
        }

        try {
            imageRawData = stream.readAllBytes();
        } catch (IOException e) {
            logger.log(Level.WARNING, "Не возможно прочитать файл", e);
        }

        setViewImage();

        imageView.setPreserveRatio(true);
        imageView.setSmooth(true);
        imageView.setCache(true);

        control.maxWidthProperty().bind(getSkinnable().imageWidthProperty());
        control.maxHeightProperty().bind(getSkinnable().imageHeightProperty());

        control.prefWidthProperty().bind(getSkinnable().imageWidthProperty());
        control.prefHeightProperty().bind(getSkinnable().imageHeightProperty());

        control.getChildren().addAll(imageView);

        container.getChildren().addAll(control);

        getChildren().addAll(container);

        Platform.runLater(() -> setViewportPosition(getSkinnable().getImageWidth(), getSkinnable().getImageHeight()));
    }

    private void setViewImage() {
        InputStream imageStream = new ByteArrayInputStream(imageRawData);
        image = new Image(imageStream, getSkinnable().getImageWidth(), getSkinnable().getImageHeight(), true, true);
        imageView.setImage(image);

        ratioDelta = image.getHeight() == 0 ? 0 : image.getWidth() / image.getHeight();

        imageView.fitWidthProperty().bind(image.widthProperty());
        imageView.fitHeightProperty().bind(image.heightProperty());
    }

    private InputStream getDefaultImageFile() {
        String path = "/com/github/kostrovik/images/no-img.png";
        try {
            return Class.forName(this.getClass().getName()).getResourceAsStream(path);
        } catch (ClassNotFoundException e) {
            logger.log(Level.WARNING, String.format("Не найден файл: %s", path), e);
        }

        return null;
    }

    private void setViewportPosition(double width, double height) {
        if (isEmptyImage) {
            width = 50;
            height = 50;
        }

        double deltaK;
        double imgW;
        double imgH;
        double posX;
        double posY;

        if (image.getWidth() > image.getHeight()) {
            deltaK = getSkinnable().getImageWidth() / width;
            imgW = image.getWidth() * deltaK;
            imgH = imgW / ratioDelta;
        } else {
            deltaK = getSkinnable().getImageHeight() / height;
            imgH = image.getHeight() * deltaK;
            imgW = imgH * ratioDelta;
        }

        posX = (image.getWidth() - imgW) / 2;
        posY = (image.getHeight() - imgH) / 2;

        Rectangle2D viewport = new Rectangle2D(posX, posY, imgW, imgH);
        imageView.setViewport(viewport);
    }

    @Override
    protected double computeMinHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
        return topInset + getSkinnable().getImageHeight() + bottomInset;
    }
}
