package com.github.kostrovik.kernel.graphics.builders;

import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.text.Text;
import com.github.kostrovik.kernel.graphics.common.ButtonIconPosition;
import com.github.kostrovik.kernel.graphics.common.icons.SolidIcons;

/**
 * Класс конструктор для создания кнопок приложения.
 * Используется для создания обычных кнопок и кнопок с иконками.
 * Для создания иконок использует словарь с иконками SolidIcons.
 * <p>
 * project: glcmtx
 * author:  kostrovik
 * date:    20/07/2018
 * github:  https://github.com/kostrovik/glcmtx
 */
public class ButtonBuilder {
    /**
     * Создает простую кнопку с надписью.
     *
     * @param buttonLabel the button label
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
     * @return the button
     */
    public Button createButton(SolidIcons buttonIcon) {
        Button button = new Button();
        setIcon(button, buttonIcon);
        return button;
    }

    /**
     * Создает комбинированную кнопку с иконкой и надписью.
     *
     * @param buttonIcon  the button icon
     * @param buttonLabel the button label
     * @return the button
     */
    public Button createButton(SolidIcons buttonIcon, String buttonLabel) {
        Button button = new Button();
        setIcon(button, buttonIcon);
        setLabel(button, buttonLabel);
        return button;
    }

    private void setLabel(Button button, String buttonLabel) {
        button.setText(buttonLabel);
    }

    private void setIcon(Button button, SolidIcons buttonIcon) {
        Text icon = new Text(buttonIcon.getSymbol());
        icon.setFont(buttonIcon.getFont());
        icon.getStyleClass().add("icon");
        button.setGraphic(icon);
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