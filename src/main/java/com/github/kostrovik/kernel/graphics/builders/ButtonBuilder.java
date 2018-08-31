package com.github.kostrovik.kernel.graphics.builders;

import com.github.kostrovik.kernel.graphics.common.ButtonIconPosition;
import com.github.kostrovik.kernel.graphics.common.icons.SolidIcons;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

/**
 * Класс конструктор для создания кнопок приложения.
 * Используется для создания обычных кнопок и кнопок с иконками.
 * Для создания иконок использует словарь с иконками SolidIcons.
 * <p>
 * project: kernel
 * author:  kostrovik
 * date:    20/07/2018
 * github:  https://github.com/kostrovik/kernel
 */
public class ButtonBuilder {
    /**
     * Создает простую кнопку с надписью.
     *
     * @param buttonLabel the button label
     *
     * @return the button
     */
    public Button createButton(String buttonLabel) {
        Button button = new Button();
        setLabel(button, buttonLabel);
        return button;
    }

    /**
     * Создает простую кнопку с иконкой.
     *
     * @param buttonIcon the button icon
     *
     * @return the button
     */
    public Button createButton(SolidIcons buttonIcon) {
        Button button = new Button();
        setIcon(button, buttonIcon, false);
        return button;
    }

    /**
     * Создает комбинированную кнопку с иконкой и надписью.
     *
     * @param buttonIcon  the button icon
     * @param buttonLabel the button label
     *
     * @return the button
     */
    public Button createButton(SolidIcons buttonIcon, String buttonLabel) {
        Button button = new Button();
        setIcon(button, buttonIcon, false);
        setLabel(button, buttonLabel);
        return button;
    }

    public Button createButton(SolidIcons buttonIcon, String buttonLabel, boolean bindFontSize) {
        Button button = new Button();
        setIcon(button, buttonIcon, bindFontSize);
        setLabel(button, buttonLabel);
        return button;
    }

    public Button createButton(SolidIcons buttonIcon, SolidIcons buttonHoverIcon, String buttonLabel, boolean bindFontSize) {
        Button button = new Button();
        setIcon(button, buttonIcon, buttonHoverIcon, bindFontSize);
        setLabel(button, buttonLabel);
        return button;
    }

    private void setLabel(Button button, String buttonLabel) {
        button.setText(buttonLabel);
    }

    private void setIcon(Button button, SolidIcons buttonIcon, boolean bindFontSize) {
        Text icon = new Text(buttonIcon.getSymbol());
        icon.setFont(buttonIcon.getFont());
        icon.getStyleClass().add("icon");
        button.setGraphic(icon);

        if (bindFontSize) {
            setIcontFont(button, buttonIcon, icon);

            button.heightProperty().addListener((observable, oldValue, newValue) -> setIcontFont(button, buttonIcon, icon));
        }
    }

    private void setIcon(Button button, SolidIcons buttonIcon, SolidIcons buttonHoverIcon, boolean bindFontSize) {
        setIcon(button, buttonIcon, bindFontSize);
        Text icon = (Text) button.getGraphic();

        button.hoverProperty().addListener((observable, oldValue, newValue) -> {
            SolidIcons selectedIcon;
            if (newValue) {
                selectedIcon = buttonHoverIcon;
            } else {
                selectedIcon = buttonIcon;
            }

            setIcontFont(button, selectedIcon, icon);
        });
    }

    private void setIcontFont(Button button, SolidIcons buttonIcon, Text icon) {
        Insets paddings = button.getPadding();
        int size = (int) (button.getPrefHeight() - paddings.getTop() - paddings.getBottom());
        Font font = Font.loadFont(buttonIcon.getFontPath(), size);
        icon.setFont(font);
    }

    public Button setIconPosition(Button button, ButtonIconPosition position) {
        switch (position) {
            case TOP:
                button.setContentDisplay(ContentDisplay.TOP);
                break;
            case LEFT:
                button.setContentDisplay(ContentDisplay.LEFT);
                break;
            case RIGHT:
                button.setContentDisplay(ContentDisplay.RIGHT);
                break;
            case BOTTOM:
                button.setContentDisplay(ContentDisplay.BOTTOM);
                break;
        }
        return button;
    }
}