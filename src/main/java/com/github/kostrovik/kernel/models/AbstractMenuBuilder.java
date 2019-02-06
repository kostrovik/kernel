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
 *
 * Абстракция для конструирования меню модуля. Экспортируется наружу для других модулей. Позволяет создавать
 * древовидное вложенное меню с подкатегориями.
 * Стоит учитывать, что для создания вложенного дерева используется рекурсия. Поэтому вложенность дерева является
 * "условно безграничной". Вложенность ограничена размером стека вызовов.
 */
public abstract class AbstractMenuBuilder implements MenuBuilderInterface {
    /**
     * Название атрибута содержащего список пунктов меню.
     */
    private String listAttribute;
    /**
     * Название атрибута содержащего путь к классу Action для пункта меню.
     */
    private String actionAttribute;
    /**
     * Атрибут содержащий название пункта меню.
     */
    private String titleAttribute;

    /**
     * Constructor for Abstract menu builder.
     */
    protected AbstractMenuBuilder() {
        this("items", "action", "title");
    }

    /**
     * Constructor for Abstract menu builder.
     *
     * @param listAttribute   the list attribute
     * @param actionAttribute the action attribute
     * @param titleAttribute  the title attribute
     */
    protected AbstractMenuBuilder(String listAttribute, String actionAttribute, String titleAttribute) {
        Objects.requireNonNull(listAttribute);
        this.listAttribute = listAttribute;
        Objects.requireNonNull(actionAttribute);
        this.actionAttribute = actionAttribute;
        Objects.requireNonNull(titleAttribute);
        this.titleAttribute = titleAttribute;
    }

    /**
     * Парсер для списка пунктов меню. Используется рекурсивно.
     *
     * @param menuList the menu list
     *
     * @return the list
     */
    protected List<MenuItem> parseItemsMap(Map<String, Object> menuList) {
        List<MenuItem> menuItems = new ArrayList<>();
        for (Map.Entry<String, Object> entry : menuList.entrySet()) {
            menuItems.add(parseMap((Map<String, Object>) entry.getValue()));
        }
        return menuItems;
    }

    /**
     * Парсер пункта меню. Либо создает и возвращает объект MenuItem. Либо если action для пункта меню null то
     * вызывает парсер списка и передает ему объект items. По итогам возвращает дерево Menu.
     * Используется рекурсивно.
     *
     * @param itemMap the item map
     *
     * @return the menu item
     */
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

    /**
     * Реализация должна уметь рефлективно создать объект по полученному имени класса.
     *
     * @param actionClass the action class
     *
     * @return the event handler
     */
    protected abstract EventHandler<ActionEvent> prepareAction(String actionClass);
}