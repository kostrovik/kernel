package com.github.kostrovik.kernel.interfaces.views;

import javafx.stage.Stage;

/**
 * project: kernel
 * author:  kostrovik
 * date:    24/07/2018
 * github:  https://github.com/kostrovik/kernel
 */
public interface ViewEventListenerInterface {
    void handle(ViewEventInterface event);

    void setMainStage(Stage mainWindow);
}
