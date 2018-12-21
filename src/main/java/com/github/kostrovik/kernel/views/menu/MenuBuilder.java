package com.github.kostrovik.kernel.views.menu;

import com.github.kostrovik.kernel.models.AbstractMenuBuilder;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Menu;

import java.util.ArrayList;
import java.util.List;

/**
 * project: kernel
 * author:  kostrovik
 * date:    23/07/2018
 * github:  https://github.com/kostrovik/kernel
 */
public class MenuBuilder extends AbstractMenuBuilder {
    @Override
    public List<Menu> getMenuList() {
        return new ArrayList<>();
    }

    @Override
    public String getModuleMenuName() {
        return "";
    }

    @Override
    protected EventHandler<ActionEvent> prepareAction(String actionClass) {
        return null;
    }
}