package com.github.kostrovik.kernel.graphics.controls.notification;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.control.SkinBase;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

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
        setColor(getSkinnable().getType());

        getSkinnable().typeProperty().addListener((observable, oldValue, newValue) -> setColor(newValue));
    }

    private void setColor(NotificationType type) {
        Color color;
        Color border;
        switch (type) {
            case ERROR:
                border = color = Color.INDIANRED;
                break;
            case SUCCESS:
                border = color = Color.LIGHTGREEN;
                break;
            default:
                color = Color.LIGHTGREEN;
                border = Color.TRANSPARENT;
                break;
        }

        control.setBorder(new Border(new BorderStroke(border, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
        message.setFill(color);
    }

    private void createSkin() {
        control = new Pane();
        control.visibleProperty().bind(getSkinnable().visibleProperty());
        control.maxWidthProperty().bind(getSkinnable().widthProperty());

        message = new Text();
        message.textProperty().bind(getSkinnable().messageProperty());
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
