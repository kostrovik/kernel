package com.github.kostrovik.kernel.models;

import com.github.kostrovik.kernel.interfaces.views.PopupWindowInterface;
import com.github.kostrovik.useful.interfaces.Listener;
import javafx.geometry.Insets;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.EventObject;
import java.util.List;

/**
 * project: kernel
 * author:  kostrovik
 * date:    31/08/2018
 * github:  https://github.com/kostrovik/kernel
 */
public abstract class AbstractPopupWindow implements PopupWindowInterface {

    /**
     * Константа указывает ширину по умолчанию для всплывающих окон.
     */
    private static final int DEFAULT_WIDTH = 980;
    /**
     * Константа указывает высоту по умолчанию для всплывающих окон.
     */
    private static final int DEFAULT_HEIGHT = 760;

    protected List<Listener<EventObject>> listeners;
    protected Stage stage;
    protected Pane parent;
    protected VBox view;

    protected AbstractPopupWindow(Pane parent) {
        this.listeners = new ArrayList<>();
        this.parent = parent;
    }

    @Override
    public void setStage(Stage stage) {
        this.stage = stage;
        setDefaultWindowSize();
        createView();
    }

    @Override
    public Region getView() {
        return view;
    }

    @Override
    public void addListener(Listener<EventObject> listener) {
        listeners.add(listener);
    }

    @Override
    public void removeListener(Listener<EventObject> listener) {
        listeners.remove(listener);
    }

    protected void notifyListeners(Object eventObject) {
        listeners.forEach(listener -> listener.handle(new EventObject(eventObject)));
    }

    protected int getWindowWidth() {
        return DEFAULT_WIDTH;
    }

    protected int getWindowHeight() {
        return DEFAULT_HEIGHT;
    }

    protected void createView() {
        view = new VBox(10);
        view.setPadding(new Insets(10, 10, 10, 10));

        view.prefWidthProperty().bind(parent.widthProperty());
        view.prefHeightProperty().bind(parent.heightProperty());

        view.getChildren().setAll(getViewTitle(), getWindowContent(), getWindowButtons());
    }

    protected abstract Region getWindowContent();

    protected abstract Region getWindowButtons();

    protected abstract String getWindowTitle();

    protected Region getViewTitle() {
        Text viewTitle = new Text(getWindowTitle());
        viewTitle.getStyleClass().add("view-title");

        HBox titleBox = new HBox(10);
        titleBox.setPadding(new Insets(10, 10, 10, 10));
        titleBox.getChildren().addAll(viewTitle);

        return titleBox;
    }

    protected void setDefaultWindowSize() {
        this.stage.setWidth(getWindowWidth());
        this.stage.setHeight(getWindowHeight());
    }
}
