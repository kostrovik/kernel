package com.github.kostrovik.kernel.graphics.controls.dropdown;

import com.github.kostrovik.kernel.graphics.builders.ButtonBuilder;
import com.github.kostrovik.kernel.graphics.common.icons.SolidIcons;
import com.github.kostrovik.kernel.graphics.controls.common.SelectionModel;
import javafx.beans.property.ObjectProperty;
import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.SkinBase;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.util.Callback;

import java.util.stream.Collectors;

/**
 * project: kernel
 * author:  kostrovik
 * date:    2019-01-23
 * github:  https://github.com/kostrovik/kernel
 */
class InputControlSkin<E> extends SkinBase<InputControl<E>> {
    private ButtonBuilder buttonBuilder;
    private TextField textField;
    private ObjectProperty<Callback<E, String>> callback;
    private SelectionModel<E> selectionModel;

    InputControlSkin(InputControl<E> control) {
        super(control);
        this.buttonBuilder = new ButtonBuilder();
        this.callback = control.callbackProperty();
        this.selectionModel = control.getSelectionModel();
        createSkin();
    }

    private void createSkin() {
        HBox container = new HBox();

        container.setPadding(new Insets(1, 1, 1, 1));
        container.getStyleClass().add("field");

        textField = new TextField();
        selectionModel.getItems().addListener((ListChangeListener) c -> textField.setText(getSelectedString()));
        selectionModel.multiSelectProperty().addListener((observable, oldValue, newValue) -> textField.setText(getSelectedString()));
        textField.setEditable(false);

        HBox.setHgrow(textField, Priority.ALWAYS);

        Button openDictionary = createButton(SolidIcons.ELIPSIS, "dictionary-button", getSkinnable().getOpenDialog());
        Button clear = createButton(SolidIcons.CROSS, "clear-button", this::clear);
        Button openList = createButton(SolidIcons.CARET_DOWN, "open-list-button", getSkinnable().getOpenList());

        textField.setOnMouseClicked(event -> {
            if (event.getButton().equals(MouseButton.PRIMARY)) {
                openList.fire();
            }
        });

        openDictionary.visibleProperty().bind(getSkinnable().getSelectionModel().multiSelectProperty());
        openList.setVisible(!getSkinnable().getSelectionModel().isMultiSelect());

        getSkinnable().getSelectionModel().multiSelectProperty().addListener((observable, oldValue, newValue) -> {
            container.getStyleClass().removeAll("multiple");
            if (newValue) {
                container.getStyleClass().add("multiple");
            }
            openList.setVisible(!newValue);
        });

        container.getChildren().addAll(textField, openDictionary, clear, openList);
        container.addEventFilter(MouseEvent.ANY, event -> getSkinnable().fireEvent(event.copyFor(getSkinnable(), getSkinnable())));

        getChildren().addAll(container);
    }

    private String getSelectedString() {
        return selectionModel.getItems().stream().map(item -> callback.get().call(item)).collect(Collectors.joining(";"));
    }

    private Button createButton(SolidIcons icon, String style, EventHandler<ActionEvent> handler) {
        Button button = buttonBuilder.createButton(icon);
        button.setFocusTraversable(false);
        button.prefHeightProperty().bind(textField.heightProperty());
        button.getStyleClass().add(style);
        button.managedProperty().bind(button.visibleProperty());
        button.setOnAction(handler);

        return button;
    }

    private void clear(ActionEvent event) {
        textField.clear();
        getSkinnable().getClear().handle(event);
    }
}