package com.github.kostrovik.kernel.graphics.controls.notification;

import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.control.SkinBase;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.TextAlignment;

/**
 * project: kernel
 * author:  kostrovik
 * date:    21/07/2018
 * github:  https://github.com/kostrovik/kernel
 */
public class NotificationSkin extends SkinBase<Notification> {
    private Group notificationGroup;
    private Label notificationMessage;
    private Rectangle notificationBorder;
    private final double BORDER_PADDING_HORIZONTAL = 10;
    private final double BORDER_PADDING_VERTICAL = 5;

    public NotificationSkin(Notification control) {
        super(control);
        createSkin();
        control.showProperty().addListener(observable -> setVisible());
        control.messageProperty().addListener(observable -> setText());
        control.typeProperty().addListener(observable -> setColor());
    }

    private void setVisible() {
        notificationGroup.setVisible(getSkinnable().getIsVisible());
    }

    private void setText() {
        notificationMessage.setText(getSkinnable().getMessage());
    }

    private void setColor() {
        switch (getSkinnable().getType()) {
            case ERROR:
                notificationMessage.setTextFill(Color.INDIANRED);
                notificationBorder.setStroke(Color.INDIANRED);
                break;
            case SUCCESS:
                notificationMessage.setTextFill(Color.LIGHTGREEN);
                notificationBorder.setStroke(Color.LIGHTGREEN);
                break;
        }
    }

    private void createSkin() {
        notificationGroup = new Group();
        notificationGroup.setVisible(false);

        notificationMessage = new Label();
        notificationMessage.setTextAlignment(TextAlignment.CENTER);
        notificationMessage.setAlignment(Pos.CENTER);
        notificationMessage.setWrapText(true);

        notificationBorder = new Rectangle(0, 0, Color.TRANSPARENT);
        notificationBorder.setArcWidth(2);
        notificationBorder.setArcHeight(2);

        notificationMessage.widthProperty().addListener((observable, oldValue, newValue) -> {
            // отступ рамки по горизонтали от границ текста
            notificationBorder.setLayoutX(notificationMessage.getBoundsInParent().getMinX() - BORDER_PADDING_HORIZONTAL);

            // ширина прямоугольника рамки по горизонтали с учетом отступа
            notificationBorder.setWidth(notificationMessage.getBoundsInParent().getWidth() + BORDER_PADDING_HORIZONTAL * 2);
        });
        notificationMessage.heightProperty().addListener((observable, oldValue, newValue) -> {
            // отступ рамки по вертикали от границ текста
            notificationBorder.setLayoutY(-BORDER_PADDING_VERTICAL);

            // высота прямоугольника рамки по вертикали с учетом отступа
            notificationBorder.setHeight(notificationMessage.getHeight() + BORDER_PADDING_VERTICAL * 2);
        });

        notificationGroup.getChildren().addAll(notificationBorder, notificationMessage);

        getChildren().addAll(notificationGroup);
    }

    private void updateSkin(double contentWidth, double contentHeight) {
        notificationMessage.setPrefWidth(contentWidth - notificationBorder.getStrokeWidth() * 2 - BORDER_PADDING_HORIZONTAL * 2);
        notificationMessage.setMaxWidth(contentWidth - notificationBorder.getStrokeWidth() * 2 - BORDER_PADDING_HORIZONTAL * 2);
    }

    protected void layoutChildren(double contentX, double contentY, double contentWidth, double contentHeight) {
        updateSkin(contentWidth, contentHeight);
        super.layoutChildren(contentX, contentY, contentWidth, contentHeight);
    }
}
