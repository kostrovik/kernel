package com.github.kostrovik.kernel.graphics.controls.common.cells;

import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.Node;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.OverrunStyle;
import javafx.scene.control.SkinBase;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

import java.util.Objects;

/**
 * project: kernel
 * author:  kostrovik
 * date:    2019-01-23
 * github:  https://github.com/kostrovik/kernel
 */
public class CellBaseSkin<E, C extends CellBase<E>> extends SkinBase<C> {
    protected StackPane cell;
    protected Pane container;
    protected DoubleProperty paddingH;
    protected DoubleProperty paddingV;

    public CellBaseSkin(C control) {
        super(control);
        this.paddingH = new SimpleDoubleProperty(0);
        this.paddingV = new SimpleDoubleProperty(0);
        createSkin();
    }

    private void createSkin() {
        cell = new StackPane();
        getSkinnable().getStyleClass().setAll("base-cell");

        container = new Pane();
        container.getStyleClass().setAll("cell-container");

        Region node = prepareNode();

        Rectangle cellMask = new Rectangle();
        cellMask.widthProperty().bind(container.widthProperty());
        cellMask.heightProperty().bind(container.heightProperty());

        container.setClip(cellMask);

        setCellAlignment();

        container.getChildren().setAll(node);
        cell.getChildren().setAll(container);

        getSkinnable().paddingProperty().addListener((observable, oldValue, newValue) -> {
            paddingH.set(newValue.getLeft() + newValue.getRight());
            paddingV.set(newValue.getTop() + newValue.getBottom());
        });

        cell.prefWidthProperty().bind(getSkinnable().widthProperty());
        cell.prefHeightProperty().bind(getSkinnable().heightProperty());

        getSkinnable().itemProperty().addListener((observable, oldValue, newValue) -> container.getChildren().setAll(prepareNode()));

        getChildren().addAll(cell);
    }

    protected void setCellAlignment() {
        cell.alignmentProperty().bind(getSkinnable().alignmentProperty());
    }

    protected Region prepareNode() {
        Object content = getCellContent();
        if (content instanceof Region) {
            configureCell((Region) content);
            return (Region) content;
        }

        Label node = new Label(content.toString().replaceAll("\r\n", "\n"));
        node.setWrapText(true);
        node.setTextOverrun(OverrunStyle.ELLIPSIS);
        node.maxHeightProperty().bind(cell.heightProperty());

        node.layoutXProperty().bind(container.widthProperty().subtract(node.widthProperty()).divide(2));
        node.layoutYProperty().bind(container.heightProperty().subtract(node.heightProperty()).divide(2));

        configureCell(node);

        return node;
    }

    protected Object getCellContent() {
        E item = getSkinnable().getItem();
        Object content = "";
        if (Objects.nonNull(item)) {
            content = Objects.requireNonNullElse(getSkinnable().getCellValueFactory().call(item), "");
        }

        return content;
    }

    protected void configureCell(Region node) {
        Platform.runLater(() -> config(node));
    }

    protected void config(Region node) {
        if (!getSkinnable().minWidthProperty().isBound()) {
            getSkinnable().setMinWidth(node.minWidth(1) + paddingH.get());
        }
        if (!getSkinnable().prefWidthProperty().isBound()) {
            getSkinnable().setPrefWidth(node.prefWidth(1) + paddingH.get());
        }

        if (!getSkinnable().minHeightProperty().isBound()) {
            getSkinnable().setMinHeight(node.minHeight(1) + paddingV.get());
        }
        if (!getSkinnable().prefHeightProperty().isBound()) {
            getSkinnable().setPrefHeight(node.prefHeight(1) + paddingV.get());
        }

        if (node instanceof Label) {
            Label label = (Label) node;
            if (Objects.isNull(label.getSkin())) {
                label.skinProperty().addListener((observable, oldValue, newValue) -> {
                    Text text = (Text) newValue.getNode().lookup(".text");
                    text.textProperty().addListener((observable1, oldValue1, newValue1) -> setContainerSize(text));
                    setContainerSize(text);
                });
            } else {
                setContainerSize(label.lookup(".text"));
            }
        } else {
            if (node instanceof Control) {
                Control control = (Control) node;
                if (Objects.isNull(control.getSkin())) {
                    control.skinProperty().addListener((observable, oldValue, newValue) -> setContainerSize(control));
                } else {
                    setContainerSize(control);
                }
            }
        }
    }

    protected void setContainerSize(Node node) {
        if (Objects.nonNull(node)) {
            container.setMaxWidth(node.prefWidth(1));
            container.setMaxHeight(node.prefHeight(1));
            container.layout();
        }
    }
}