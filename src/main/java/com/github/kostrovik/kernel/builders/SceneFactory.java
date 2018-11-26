package com.github.kostrovik.kernel.builders;

import com.github.kostrovik.kernel.common.ApplicationSettings;
import com.github.kostrovik.kernel.graphics.controls.progress.ProgressBarIndicator;
import com.github.kostrovik.kernel.interfaces.ModuleConfiguratorInterface;
import com.github.kostrovik.kernel.interfaces.views.ContentViewInterface;
import com.github.kostrovik.kernel.interfaces.views.LayoutType;
import com.github.kostrovik.kernel.interfaces.views.MenuBuilderInterface;
import com.github.kostrovik.kernel.interfaces.views.PopupWindowInterface;
import com.github.kostrovik.kernel.interfaces.views.ViewEventInterface;
import com.github.kostrovik.kernel.interfaces.views.ViewEventListenerInterface;
import com.github.kostrovik.useful.utils.InstanceLocatorUtil;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.EventObject;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * project: kernel
 * author:  kostrovik
 * date:    21/07/2018
 * github:  https://github.com/kostrovik/kernel
 */
public final class SceneFactory implements ViewEventListenerInterface {
    private static Logger logger = InstanceLocatorUtil.getLocator().getLogger(SceneFactory.class.getName());

    private Stage mainWindow;

    private static volatile SceneFactory factory;
    private static Map<String, ContentViewInterface> storage = new ConcurrentHashMap<>();
    private static ModulesConfigBuilder config = ModulesConfigBuilder.getInstance();
    private static ApplicationSettings settings = ApplicationSettings.getInstance();

    private SceneFactory() {
    }

    public static SceneFactory getInstance() {
        if (factory == null) {
            synchronized (SceneFactory.class) {
                if (factory == null) {
                    factory = new SceneFactory();
                }
            }
        }
        return factory;
    }

    public static SceneFactory provider() {
        return getInstance();
    }

    public void setMainStage(Stage stage) {
        mainWindow = stage;
    }

    public ContentViewInterface initScene(String moduleName, String viewName, LayoutType layoutType, EventObject event) {
        Scene scene;
        Stage stage = null;

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
            default:
                scene = getDefaultSceneTemplate();

                break;
        }

        Pane content = (Pane) scene.lookup(".scene-content");

        ContentViewInterface contentView = storage.getOrDefault(moduleName + "_" + viewName, createView(moduleName, viewName, content));

        if (contentView == null) {
            content.getChildren().setAll(errorCreateScene(content));
        } else {
            if (contentView instanceof PopupWindowInterface) {
                ((PopupWindowInterface) contentView).setStage(stage);
            }

            storage.putIfAbsent(moduleName + "_" + viewName, contentView);

            contentView.initView(event);

            content.getChildren().setAll(contentView.getView());
        }

        if (layoutType.equals(LayoutType.POPUP) && stage != null) {
            stage.setScene(scene);
            stage.show();
        } else {
            mainWindow.setScene(scene);
        }

        return contentView;
    }

    private ContentViewInterface createView(String moduleName, String viewName, Pane content) {
        ModuleConfiguratorInterface moduleConfiguration = config.getConfigForModule(moduleName);
        Map<String, Class<?>> moduleViews = moduleConfiguration.getModuleViews();

        ContentViewInterface view = null;
        Class<?> viewClass = moduleViews.get(viewName);
        if (viewClass != null) {
            Constructor<?> constructor;
            try {
                constructor = viewClass.getDeclaredConstructor(Pane.class);
                view = (ContentViewInterface) constructor.newInstance(content);
            } catch (NoSuchMethodException e) {
                logger.log(Level.SEVERE, "Не задан конструктор с необходимымой сигнатурой.", e);
            } catch (IllegalAccessException e) {
                logger.log(Level.SEVERE, "Конструктор не доступен.", e);
            } catch (InstantiationException | InvocationTargetException e) {
                logger.log(Level.SEVERE, String.format("Не возможно создать объект %s.", viewClass.getName()), e);
            }
        }

        return view;
    }

    private Scene getDefaultSceneTemplate() {
        VBox vbox = new VBox();
        Scene scene = new Scene(vbox);

        Pane content = new Pane();
        content.getStyleClass().add("scene-content");
        vbox.getChildren().addAll(getSceneMenu(), content);

        if (settings.showMemoryUsage()) {
            vbox.getChildren().add(getSystemTray());
        }

        setBackground(content);

        content.prefWidthProperty().bind(vbox.widthProperty());
        content.prefHeightProperty().bind(vbox.heightProperty());

        setStyle(scene);

        return scene;
    }

    private Scene getPopupSceneTemplate() {
        VBox vbox = new VBox(10);

        Scene scene = new Scene(vbox);

        Pane content = new Pane();
        content.getStyleClass().add("scene-content");
        vbox.getChildren().addAll(content);

        setBackground(content);

        content.prefWidthProperty().bind(vbox.widthProperty());
        content.prefHeightProperty().bind(vbox.heightProperty());

        setStyle(scene);

        return scene;
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

    private Region errorCreateScene(Pane parent) {
        ScrollPane view = new ScrollPane();
        setBackground(view);

        view.prefWidthProperty().bind(parent.widthProperty());
        view.prefHeightProperty().bind(parent.heightProperty());

        Text value = new Text();
        value.setFill(Color.ORANGE);
        value.setText("Не возможно создать страницу.");
        value.setFont(Font.font(18));

        StackPane textHolder = new StackPane(value);
        ScrollBar scrollBar = new ScrollBar();

        textHolder.prefWidthProperty().bind(parent.widthProperty().subtract(scrollBar.getWidth()));
        textHolder.prefHeightProperty().bind(parent.heightProperty().subtract(scrollBar.getWidth()));

        view.setContent(textHolder);

        view.viewportBoundsProperty().addListener((arg0, arg1, arg2) -> {
            Node content = view.getContent();
            view.setFitToWidth(content.prefWidth(-1) < arg2.getWidth());
            view.setFitToHeight(content.prefHeight(-1) < arg2.getHeight());
        });

        return view;
    }

    private HBox getSystemTray() {
        HBox tray = new HBox(5);
        tray.setPadding(new Insets(5, 5, 5, 5));
        tray.setAlignment(Pos.BOTTOM_RIGHT);

        Timer timer = new Timer();
        ProgressBarIndicator bar = new ProgressBarIndicator(0, 1);
        MemoryStateBuilder timerTask = new MemoryStateBuilder(bar);
        timer.schedule(timerTask, 1000, 5000);

        tray.getChildren().setAll(bar);

        return tray;
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