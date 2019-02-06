package com.github.kostrovik.kernel.views;

import com.github.kostrovik.kernel.interfaces.views.ContentViewInterface;
import com.github.kostrovik.useful.models.AbstractObservable;
import javafx.scene.Node;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import java.util.EventObject;

/**
 * project: kernel
 * author:  kostrovik
 * date:    2018-12-16
 * github:  https://github.com/kostrovik/kernel
 */
public class ErrorView extends AbstractObservable implements ContentViewInterface {
    private Region view;
    private Pane parent;

    public ErrorView(Pane parent) {
        this.parent = parent;
        this.view = createView();
    }

    @Override
    public void initView(EventObject event) {
        // Страница не имеет параметров для инициализации.
    }

    @Override
    public Region getView() {
        return view;
    }

    private Region createView() {
        ScrollPane contentPane = new ScrollPane();
        contentPane.prefWidthProperty().bind(parent.widthProperty());
        contentPane.prefHeightProperty().bind(parent.heightProperty());

        Text value = new Text();
        value.setFill(Color.ORANGE);
        value.setText("Произошла ошибка при создании страницы.");
        value.setFont(Font.font(18));

        StackPane textHolder = new StackPane(value);
        ScrollBar scrollBar = new ScrollBar();

        textHolder.prefWidthProperty().bind(parent.widthProperty().subtract(scrollBar.getWidth()));
        textHolder.prefHeightProperty().bind(parent.heightProperty().subtract(scrollBar.getWidth()));

        contentPane.setContent(textHolder);

        contentPane.viewportBoundsProperty().addListener((arg0, arg1, arg2) -> {
            Node content = contentPane.getContent();
            contentPane.setFitToWidth(content.prefWidth(-1) < arg2.getWidth());
            contentPane.setFitToHeight(content.prefHeight(-1) < arg2.getHeight());
        });

        return contentPane;
    }
}
