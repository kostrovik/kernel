package com.github.kostrovik.kernel.graphics.controls.notification;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.control.SkinBase;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

import java.util.Objects;

/**
 * project: kernel
 * author:  kostrovik
 * date:    21/07/2018
 * github:  https://github.com/kostrovik/kernel
 */
public class NotificationSkin extends SkinBase<Notification> {
    private Pane control;
    private Text message;
    private static final double BORDER_PADDING_HORIZONTAL = 10;
    private static final double BORDER_PADDING_VERTICAL = 5;

    public NotificationSkin(Notification control) {
        super(control);
        createSkin();
        setText();
        setColor();
        setVisible();

        control.showProperty().addListener(observable -> setVisible());
        control.messageProperty().addListener(observable -> setText());
        control.typeProperty().addListener(observable -> setColor());
    }

    private void setVisible() {
        control.setVisible(getSkinnable().getIsVisible());
    }

    private void setText() {
        message.setText(getSkinnable().getMessage());
    }

    private void setColor() {
        if (Objects.nonNull(getSkinnable().getType())) {
            switch (getSkinnable().getType()) {
                case ERROR:
                    control.setBorder(new Border(new BorderStroke(Color.INDIANRED, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
                    message.setFill(Color.INDIANRED);
                    break;
                case SUCCESS:
                    control.setBorder(new Border(new BorderStroke(Color.LIGHTGREEN, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
                    message.setFill(Color.LIGHTGREEN);
                    break;
                default:
                    control.setBorder(new Border(new BorderStroke(Color.TRANSPARENT, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
                    message.setFill(Color.LIGHTGREEN);
                    break;
            }
        }
    }

    private void createSkin() {
        control = new Pane();
        control.setVisible(false);
        control.maxWidthProperty().bind(getSkinnable().widthProperty());

        message = new Text();
        message.setTextAlignment(TextAlignment.CENTER);
        message.wrappingWidthProperty().bind(control.widthProperty().subtract(BORDER_PADDING_HORIZONTAL * 2));

        HBox messageBlock = new HBox();
        messageBlock.setPadding(new Insets(BORDER_PADDING_VERTICAL, BORDER_PADDING_HORIZONTAL, BORDER_PADDING_VERTICAL, BORDER_PADDING_HORIZONTAL));
        messageBlock.prefWidthProperty().bind(control.widthProperty());

        messageBlock.getChildren().addAll(message);

        control.minHeightProperty().bind(messageBlock.heightProperty());

        Platform.runLater(() -> control.setPrefHeight(messageBlock.getHeight()));

        control.getChildren().addAll(messageBlock);

        getChildren().addAll(control);
    }
}
