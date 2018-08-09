package com.github.kostrovik.kernel.interfaces.views;

import javafx.stage.Stage;

/**
 * project: glcmtx
 * author:  kostrovik
 * date:    24/07/2018
 * github:  https://github.com/kostrovik/glcmtx
 */
public interface ViewEventListenerInterface {
    void handle(ViewEventInterface event);

    void setMainStage(Stage mainWindow);
}
