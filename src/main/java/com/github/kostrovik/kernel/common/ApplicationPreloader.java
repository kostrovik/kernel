package com.github.kostrovik.kernel.common;

import com.github.kostrovik.kernel.dictionaries.ViewTypeDictionary;
import com.github.kostrovik.kernel.interfaces.EventListenerInterface;
import com.github.kostrovik.kernel.settings.ApplicationSettings;
import com.github.kostrovik.kernel.settings.Configurator;
import javafx.application.Platform;
import javafx.application.Preloader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import com.github.kostrovik.kernel.graphics.common.icons.SolidIcons;
import com.github.kostrovik.kernel.graphics.controls.notification.Notification;
import com.github.kostrovik.kernel.graphics.controls.notification.NotificationType;
import com.github.kostrovik.kernel.interfaces.controls.ControlBuilderFacadeInterface;
import com.github.kostrovik.kernel.interfaces.views.LayoutType;
import com.github.kostrovik.kernel.interfaces.views.ViewEventInterface;
import com.github.kostrovik.kernel.interfaces.views.ViewEventListenerInterface;

import java.util.EventObject;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * project: glcmtx
 * author:  kostrovik
 * date:    18/07/2018
 * github:  https://github.com/kostrovik/glcmtx
 */
public class ApplicationPreloader extends Preloader {
    private static Logger logger = Configurator.getConfig().getLogger(ApplicationPreloader.class.getName());

    private ApplicationSettings settings;
    private Stage stage;
    private EventListenerInterface consumer;
    private TextField userLogin;
    private TextField userPassword;
    private Button enterButton;
    private Notification formNotification;
    private ControlBuilderFacadeInterface facade;
    private Configurator configurator;

    public ApplicationPreloader(EventListenerInterface consumer) {
        this.consumer = consumer;
        this.settings = ApplicationSettings.getInstance();
        this.configurator = Configurator.getConfig();
        this.facade = Objects.requireNonNull(configurator.getControlBuilder());
    }

    private Scene createLoginScene() {
        AnchorPane pane = new AnchorPane();

        VBox loginForm = new VBox(10);
        loginForm.setPadding(new Insets(10, 10, 10, 10));
        loginForm.setPrefWidth(300);

        GridPane formLayout = facade.createTableFormLayout();
        userLogin = (TextField) facade.addTextField(formLayout, "Логин");

        userLogin.addEventFilter(KeyEvent.KEY_RELEASED, event -> formNotification.setIsVisible(false));

        userPassword = (TextField) facade.addPasswordField(formLayout, "Пароль");

        userPassword.addEventFilter(KeyEvent.KEY_RELEASED, event -> formNotification.setIsVisible(false));

        loginForm.getChildren().add(formLayout);

        enterButton = facade.createButton("Войти");
        enterButton.setOnAction(t -> {
            disableForm(true);
            checkForm();
        });

        Button exitButton = facade.createButton("Отмена");
        exitButton.setOnAction(t -> System.exit(0));

        HBox buttons = new HBox(10);
        buttons.getChildren().addAll(enterButton, exitButton);
        buttons.setAlignment(Pos.CENTER_RIGHT);

        loginForm.getChildren().add(buttons);

        formNotification = (Notification) facade.createFormNotification();

        loginForm.getChildren().add(formNotification);

        loginForm.addEventHandler(KeyEvent.KEY_RELEASED, ev -> {
            if (ev.getCode() == KeyCode.ENTER) {
                enterButton.fire();
                ev.consume();
            }
        });

        HBox settingsBlock = createSettingsBlock();

        pane.getChildren().addAll(loginForm, settingsBlock);
        AnchorPane.setRightAnchor(loginForm, 20.0);
        AnchorPane.setTopAnchor(loginForm, 20.0);

        AnchorPane.setRightAnchor(settingsBlock, 0.0);
        AnchorPane.setLeftAnchor(settingsBlock, 0.0);
        AnchorPane.setBottomAnchor(settingsBlock, 0.0);

        Scene preloader = new Scene(pane, 600, 400);

        try {
            preloader.getStylesheets().add(Class.forName(this.getClass().getName()).getResource("/styles/preloader.css").toExternalForm());
            preloader.getStylesheets().add(Class.forName(this.getClass().getName()).getResource(String.format("/styles/themes/%s", settings.getDefaultColorTheme())).toExternalForm());
        } catch (ClassNotFoundException error) {
            logger.log(Level.WARNING, "Ошибка загрузки изображения для preloader.", error);
        }

        return preloader;
    }

    private HBox createSettingsBlock() {
        HBox settingsBlock = new HBox(10);
        settingsBlock.getStyleClass().add("settings-block");
        settingsBlock.setAlignment(Pos.CENTER_RIGHT);

        Button serverListButton = facade.createButton("Серверы", SolidIcons.DATA_BASE);
        serverListButton.setOnAction(t -> createServersListScene());

        Button colorThemesButton = facade.createButton("Цветовая тема", SolidIcons.PALETTE);
        colorThemesButton.setOnAction(t -> createColorThemesListScene());

        settingsBlock.getChildren().addAll(serverListButton, colorThemesButton);

        return settingsBlock;
    }

    private void createServersListScene() {
        ViewEventListenerInterface listener = configurator.getEventListener();
        listener.handle(new ViewEventInterface() {
            @Override
            public String getModuleName() {
                return ApplicationPreloader.class.getModule().getName();
            }

            @Override
            public String getViewName() {
                return ViewTypeDictionary.DATA_BASE_SERVER_LIST.name();
            }

            @Override
            public Object getEventData() {
                return settings.getHosts();
            }

            @Override
            public LayoutType getLayoutType() {
                return LayoutType.POPUP;
            }
        });
    }

    private void createColorThemesListScene() {
        ViewEventListenerInterface listener = configurator.getEventListener();
        listener.handle(new ViewEventInterface() {
            @Override
            public String getModuleName() {
                return ApplicationPreloader.class.getModule().getName();
            }

            @Override
            public String getViewName() {
                return ViewTypeDictionary.COLOR_THEME_LIST.name();
            }

            @Override
            public Object getEventData() {
                return settings.getDefaultColorTheme();
            }

            @Override
            public LayoutType getLayoutType() {
                return LayoutType.POPUP;
            }
        });
    }

    private void disableForm(boolean isDisabled) {
        if (isDisabled) {
            userLogin.setEditable(false);
            userPassword.setEditable(false);
        } else {
            userLogin.setEditable(true);
            userPassword.setEditable(true);
        }
        enterButton.setDisable(isDisabled);
    }

    @Override
    public void start(Stage stage) {
        this.stage = stage;
        stage.setScene(createLoginScene());

        stage.setResizable(false);
        stage.initStyle(StageStyle.UNDECORATED);

        stage.show();
    }

    private void checkForm() {
        if (userLogin.getText().trim().isEmpty()) {
            String location = String.format("class: %s, method: %s", this.getClass().getName(), "checkForm()");
            handleErrorNotification(new Preloader.ErrorNotification(location, "Необходимо заполнить логин.", new Exception()));
        } else if (consumer != null) {
            Map<String, String> userAuthForm = new HashMap<>();
            userAuthForm.put("login", userLogin.getText());
            userAuthForm.put("password", userPassword.getText());
            consumer.handle(new EventObject(userAuthForm));
        }
    }

    @Override
    public boolean handleErrorNotification(Preloader.ErrorNotification errorNotification) {
        formNotification.setMessage(errorNotification.getDetails());
        formNotification.setType(NotificationType.ERROR);

        disableForm(false);
        return true;
    }

    @Override
    public void handleStateChangeNotification(Preloader.StateChangeNotification state) {
        if (state.getType() == Preloader.StateChangeNotification.Type.BEFORE_START) {
            Platform.runLater(() -> stage.hide());
        }
    }
}
