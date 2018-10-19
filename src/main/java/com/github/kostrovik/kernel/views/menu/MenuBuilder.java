package com.github.kostrovik.kernel.views.menu;

import com.github.kostrovik.kernel.interfaces.views.MenuBuilderInterface;
import javafx.scene.control.MenuItem;

import java.util.ArrayList;
import java.util.List;

/**
 * project: kernel
 * author:  kostrovik
 * date:    23/07/2018
 * github:  https://github.com/kostrovik/kernel
 */
public class MenuBuilder implements MenuBuilderInterface {
    @Override
    public List<MenuItem> getMenuList() {
        return new ArrayList<>();
    }

    @Override
    public String getModuleMenuName() {
        return "";
    }
}
