package com.github.kostrovik.kernel.builders;

import com.github.kostrovik.kernel.common.ApplicationSettings;
import com.github.kostrovik.kernel.exceptions.SceneBuilderException;
import com.github.kostrovik.kernel.interfaces.ModuleConfiguratorInterface;
import com.github.kostrovik.kernel.interfaces.views.ContentViewInterface;
import com.github.kostrovik.kernel.dictionaries.LayoutType;
import com.github.kostrovik.kernel.interfaces.views.MenuBuilderInterface;
import com.github.kostrovik.kernel.interfaces.views.PopupWindowInterface;
import com.github.kostrovik.kernel.interfaces.views.ViewEventInterface;
import com.github.kostrovik.kernel.interfaces.views.ViewEventListenerInterface;
import com.github.kostrovik.kernel.views.ErrorView;
import com.github.kostrovik.kernel.views.SystemTrayView;
import com.github.kostrovik.useful.utils.InstanceLocatorUtil;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.Background;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.EventObject;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * project: kernel
 * author:  kostrovik
 * date:    21/07/2018
 * github:  https://github.com/kostrovik/kernel
 */
public final class SceneBuilder implements ViewEventListenerInterface {
    private Logger logger = InstanceLocatorUtil.getLocator().getLogger(SceneBuilder.class);
    private static volatile SceneBuilder builder;

    private Stage mainWindow;
    private Map<String, ContentViewInterface> storage;
    private ModulesConfigBuilder config;
    private ApplicationSettings settings;

    private SceneBuilder() {
        storage = new ConcurrentHashMap<>();
        config = ModulesConfigBuilder.getInstance();
        settings = ApplicationSettings.getInstance();
    }

    public static synchronized SceneBuilder getInstance() {
        if (builder == null) {
            builder = new SceneBuilder();
        }
        return builder;
    }

    public static SceneBuilder provider() {
        return getInstance();
    }

    @Override
    public void setMainStage(Stage stage) {
        mainWindow = stage;
    }

    public ContentViewInterface initScene(String moduleName, String viewName, LayoutType layoutType, EventObject event) {
        Scene scene;
        Stage stage = null;

        if ((layoutType.equals(LayoutType.POPUP) || layoutType.equals(LayoutType.POPUP_MESSAGE)) && storage.containsKey(moduleName + "_" + viewName)) {
            return storage.get(moduleName + "_" + viewName);
        }

        switch (layoutType) {
            case TAB:
                scene = getDefaultSceneTemplate();

                break;
            case POPUP:
                stage = new Stage();
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.initOwner(mainWindow);
                scene = getPopupSceneTemplate();

                break;
            case POPUP_MESSAGE:
                stage = new Stage();
                stage.initModality(Modality.NONE);
                stage.initOwner(mainWindow);
                scene = getPopupSceneTemplate();

                break;
            default:
                scene = getDefaultSceneTemplate();

                break;
        }

        Pane content = (Pane) scene.lookup(".scene-content");
        ContentViewInterface contentView;

        try {
            contentView = createView(config.getConfigForModule(moduleName), viewName, content);
            if (contentView instanceof PopupWindowInterface) {
                ((PopupWindowInterface) contentView).setStage(stage);
            }
        } catch (SceneBuilderException e) {
            contentView = new ErrorView(content);
        }

        contentView.initView(event);
        content.getChildren().setAll(contentView.getView());

        if (Objects.nonNull(stage) && isPopupView(layoutType)) {
            stage.setScene(scene);
            stage.setOnCloseRequest(event1 -> storage.remove(moduleName + "_" + viewName));
            stage.setOnHidden(event12 -> storage.remove(moduleName + "_" + viewName));
            storage.putIfAbsent(moduleName + "_" + viewName, contentView);
            stage.show();
        } else {
            mainWindow.setScene(scene);
        }
        return contentView;
    }

    private boolean isPopupView(LayoutType layoutType) {
        return layoutType.equals(LayoutType.POPUP) || layoutType.equals(LayoutType.POPUP_MESSAGE);
    }

    private ContentViewInterface createView(ModuleConfiguratorInterface config, String viewName, Pane content) {
        try {
            Map<String, Class<?>> moduleViews = config.getModuleViews();
            Class<?> viewClass = moduleViews.get(viewName);
            Constructor<?> constructor = viewClass.getDeclaredConstructor(Pane.class);
            return (ContentViewInterface) constructor.newInstance(content);
        } catch (NoSuchMethodException e) {
            logger.log(Level.SEVERE, "Не задан конструктор с необходимымой сигнатурой.", e);
            throw new SceneBuilderException(e);
        } catch (IllegalAccessException e) {
            logger.log(Level.SEVERE, "Конструктор не доступен.", e);
            throw new SceneBuilderException(e);
        } catch (InstantiationException | InvocationTargetException e) {
            logger.log(Level.SEVERE, "Не возможно создать объект.", e);
            throw new SceneBuilderException(e);
        } catch (NullPointerException e) {
            logger.log(Level.SEVERE, "Не задан класс отображения.", e);
            throw new SceneBuilderException(e);
        }
    }

    private Scene getDefaultSceneTemplate() {
        VBox vbox = new VBox();
        Scene scene = new Scene(vbox);

        vbox.getChildren().add(getSceneMenu());
        prepareTemplate(vbox, scene);

        if (settings.showMemoryUsage()) {
            vbox.getChildren().add(getSystemTray());
        }

        return scene;
    }

    private Scene getPopupSceneTemplate() {
        VBox vbox = new VBox(10);
        Scene scene = new Scene(vbox);
        prepareTemplate(vbox, scene);
        return scene;
    }

    private void prepareTemplate(Pane container, Scene scene) {
        Pane content = new Pane();
        content.getStyleClass().add("scene-content");
        container.getChildren().addAll(content);

        setBackground(content);

        content.prefWidthProperty().bind(container.widthProperty());
        content.prefHeightProperty().bind(container.heightProperty());

        setStyle(scene);
    }

    private MenuBar getSceneMenu() {
        MenuBar menuBar = new MenuBar();
        menuBar.setPadding(new Insets(0, 0, 0, 0));

        for (String module : config.moduleKeys()) {
            MenuBuilderInterface menu = config.getConfigForModule(module).getMenuBuilder();
            List<MenuItem> menuItems = menu.getMenuList();

            if (!menuItems.isEmpty()) {
                Menu addDataMenu = new Menu(menu.getModuleMenuName());
                addDataMenu.getItems().addAll(menuItems);

                menuBar.getMenus().add(addDataMenu);
            }
        }

        return menuBar;
    }

    private Region getSystemTray() {
        return new SystemTrayView().getView();
    }

    private void setBackground(Region container) {
        container.setBackground(Background.EMPTY);
    }

    private void setStyle(Scene scene) {
        try {
            scene.getStylesheets().add(Class.forName(this.getClass().getName()).getResource("/com/github/kostrovik/styles/application-controls.css").toExternalForm());
            scene.getStylesheets().add(Class.forName(this.getClass().getName()).getResource(String.format("/com/github/kostrovik/styles/themes/%s", settings.getDefaultColorTheme())).toExternalForm());
        } catch (ClassNotFoundException error) {
            logger.log(Level.WARNING, "Ошибка загрузки стилей.", error);
        }
    }

    @Override
    public ContentViewInterface handle(ViewEventInterface event) {
        return initScene(event.getModuleName(), event.getViewName(), event.getLayoutType(), new EventObject(event.getEventData()));
    }
}