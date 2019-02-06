package com.github.kostrovik.kernel.interfaces.views;

import javafx.scene.control.Menu;

import java.util.List;

/**
 * project: kernel
 * author:  kostrovik
 * date:    24/07/2018
 * github:  https://github.com/kostrovik/kernel
 */
public interface MenuBuilderInterface {
    List<Menu> getMenuList();

    String getModuleMenuName();
}
