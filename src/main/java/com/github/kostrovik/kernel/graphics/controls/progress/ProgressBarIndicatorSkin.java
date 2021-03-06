package com.github.kostrovik.kernel.graphics.controls.progress;

import javafx.scene.control.ProgressBar;
import javafx.scene.control.SkinBase;
import javafx.scene.text.Text;

/**
 * project: kernel
 * author:  kostrovik
 * date:    22/08/2018
 * github:  https://github.com/kostrovik/kernel
 */
public class ProgressBarIndicatorSkin extends SkinBase<ProgressBarIndicator> {
    private ProgressBar bar;
    private Text text;

    public ProgressBarIndicatorSkin(ProgressBarIndicator control) {
        super(control);
        createSkin();
        addListeners();
    }

    private void createSkin() {
        bar = new ProgressBar();
        text = new Text();

        setProgress(getSkinnable().getDone());

        getChildren().setAll(bar, text);
    }

    private void addListeners() {
        getSkinnable().doneProperty().addListener((observable, oldValue, newValue) -> setProgress(newValue.doubleValue()));
        getSkinnable().formatProperty().addListener((observable, oldValue, newValue) -> setProgress(getSkinnable().getDone()));
    }

    private void setProgress(double done) {
        double totalValue = getSkinnable().getTotal();
        if (totalValue == 0) {
            text.setText(String.valueOf(done));
            totalValue = 1;
        } else {
            text.setText(String.format(getSkinnable().getFormat(), getSkinnable().getFormatAttributes().toArray()));
        }

        bar.setProgress(done / totalValue);
        bar.setMinHeight(text.getBoundsInLocal().getHeight() + 10);
        bar.setMinWidth(text.getBoundsInLocal().getWidth() + 10);
        bar.prefWidthProperty().bind(getSkinnable().widthProperty());
    }
}