package com.github.kostrovik.kernel.models;

import com.github.kostrovik.kernel.interfaces.views.MenuBuilderInterface;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * project: kernel
 * author:  kostrovik
 * date:    2018-12-20
 * github:  https://github.com/kostrovik/kernel
 */
public abstract class AbstractMenuBuilder implements MenuBuilderInterface {
    private String listAttribute;
    private String actionAttribute;
    private String titleAttribute;

    private AbstractMenuBuilder() {
        this("items", "action", "title");
    }

    private AbstractMenuBuilder(String listAttribute, String actionAttribute, String titleAttribute) {
        Objects.requireNonNull(listAttribute);
        this.listAttribute = listAttribute;
        Objects.requireNonNull(actionAttribute);
        this.actionAttribute = actionAttribute;
        Objects.requireNonNull(titleAttribute);
        this.titleAttribute = titleAttribute;
    }

    protected List<MenuItem> parseItemsMap(Map<String, Object> menuList) {
        List<MenuItem> menuItems = new ArrayList<>();
        for (Map.Entry<String, Object> entry : menuList.entrySet()) {
            menuItems.add(parseMap((Map<String, Object>) entry.getValue()));
        }
        return menuItems;
    }

    protected MenuItem parseMap(Map<String, Object> itemMap) {
        String action = (String) itemMap.getOrDefault(actionAttribute, null);
        String title = (String) itemMap.getOrDefault(titleAttribute, null);
        if (Objects.nonNull(action) && !action.trim().equalsIgnoreCase("null")) {
            MenuItem item = new MenuItem(title);
            item.setOnAction(prepareAction(action));
            return item;
        }

        Menu submenu = new Menu(title);
        Map<String, Object> items = (Map<String, Object>) itemMap.getOrDefault(listAttribute, new ArrayList<>());
        submenu.getItems().setAll(parseItemsMap(items));

        return submenu;
    }

    protected abstract EventHandler<ActionEvent> prepareAction(String actionClass);
}