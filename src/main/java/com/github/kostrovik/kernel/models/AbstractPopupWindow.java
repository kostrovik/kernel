package com.github.kostrovik.kernel.models;

import com.github.kostrovik.kernel.interfaces.views.PopupWindowInterface;
import com.github.kostrovik.useful.models.AbstractObservable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.Collection;

/**
 * project: kernel
 * author:  kostrovik
 * date:    31/08/2018
 * github:  https://github.com/kostrovik/kernel
 * <p>
 * Абстракция всплывающего окна. Экспортируется наружу для других модулей. Реализует общие настройки всплывающих окон.
 */
public abstract class AbstractPopupWindow extends AbstractObservable implements PopupWindowInterface {
    /**
     * Константа указывает ширину по умолчанию для всплывающих окон.
     */
    private static final int DEFAULT_WIDTH = 980;
    /**
     * Константа указывает высоту по умолчанию для всплывающих окон.
     */
    private static final int DEFAULT_HEIGHT = 760;

    protected Stage stage;
    protected Pane parent;
    protected VBox view;

    protected AbstractPopupWindow(Pane parent) {
        this.parent = parent;
    }

    @Override
    public void setStage(Stage stage) {
        this.stage = stage;
        setWindowSize();
        createView();
    }

    @Override
    public Region getView() {
        return view;
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

        view.getChildren().setAll(getViewTitle(), getWindowContent(), getViewButtons());
    }

    protected abstract Region getWindowContent();

    protected abstract Collection<Node> getWindowButtons();

    protected Region getViewButtons() {
        HBox buttonsView = new HBox(10);
        buttonsView.setPadding(new Insets(10, 10, 10, 10));
        buttonsView.getChildren().setAll(getWindowButtons());
        buttonsView.setAlignment(Pos.CENTER_RIGHT);

        return buttonsView;
    }

    protected abstract String getWindowTitle();

    protected Region getViewTitle() {
        Text viewTitle = new Text(getWindowTitle());
        viewTitle.getStyleClass().add("view-title");

        HBox titleBox = new HBox(10);
        titleBox.setPadding(new Insets(10, 10, 10, 10));
        titleBox.getChildren().addAll(viewTitle);

        return titleBox;
    }

    protected void setWindowSize() {
        this.stage.setWidth(getWindowWidth());
        this.stage.setHeight(getWindowHeight());
    }
}
