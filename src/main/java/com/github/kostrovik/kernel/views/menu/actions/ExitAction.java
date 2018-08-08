package com.github.kostrovik.kernel.views.menu.actions;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;

/**
 * project: glcmtx
 * author:  kostrovik
 * date:    23/07/2018
 * github:  https://github.com/kostrovik/glcmtx
 */
public class ExitAction implements EventHandler<ActionEvent> {
    @Override
    public void handle(ActionEvent event) {
        System.exit(0);
    }
}