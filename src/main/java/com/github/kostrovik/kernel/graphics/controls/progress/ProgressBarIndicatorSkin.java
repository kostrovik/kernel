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
    private String format;
    private double total;

    public ProgressBarIndicatorSkin(ProgressBarIndicator control) {
        super(control);
        createSkin();

        getSkinnable().formatProperty().addListener((observable, oldValue, newValue) -> format = newValue);
        getSkinnable().totalProperty().addListener((observable, oldValue, newValue) -> total = newValue.doubleValue());

        syncProgress(getSkinnable().getDone());
        getSkinnable().doneProperty().addListener((observable, oldValue, newValue) -> syncProgress(newValue.doubleValue()));
    }

    private void createSkin() {
        bar = new ProgressBar();
        text = new Text();
        getChildren().setAll(bar, text);
        format = getSkinnable().getFormat();
        total = getSkinnable().getTotal();
    }

    private void syncProgress(double done) {
        if (total == 0) {
            text.setText(String.valueOf(getSkinnable().getProgress()));
            bar.setProgress(getSkinnable().getProgress());
        } else {
            text.setText(String.format(format, done, total));
            bar.setProgress((int) done / total);
        }

        bar.setMinHeight(text.getBoundsInLocal().getHeight() + 10);
        bar.setMinWidth(text.getBoundsInLocal().getWidth() + 10);

        bar.prefWidthProperty().bind(getSkinnable().widthProperty());
    }
}