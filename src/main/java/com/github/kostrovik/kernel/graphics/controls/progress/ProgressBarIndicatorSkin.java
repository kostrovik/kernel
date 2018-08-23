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

        syncProgress();
        getSkinnable().doneProperty().addListener((observable, oldValue, newValue) -> syncProgress());
    }

    private void createSkin() {
        bar = new ProgressBar();
        text = new Text();
        getChildren().setAll(bar, text);
    }

    private void syncProgress() {
        if (getSkinnable().getTotal() == 0) {
            text.setText(String.valueOf(getSkinnable().getProgress()));
            bar.setProgress(getSkinnable().getProgress());
        } else {
            text.setText(String.format(getSkinnable().getFormat(), getSkinnable().getDone(), getSkinnable().getTotal()));
            bar.setProgress(getSkinnable().getDone() / getSkinnable().getTotal());
        }

        bar.setMinHeight(text.getBoundsInLocal().getHeight() + 10);
        bar.setMinWidth(text.getBoundsInLocal().getWidth() + 10);
    }
}