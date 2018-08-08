package com.github.kostrovik.kernel.views;

import com.github.kostrovik.kernel.settings.Configurator;
import com.github.kostrovik.kernel.dictionaries.ColorThemeDictionary;
import com.github.kostrovik.kernel.settings.ApplicationSettings;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import com.github.kostrovik.kernel.interfaces.controls.ControlBuilderFacadeInterface;
import com.github.kostrovik.kernel.interfaces.views.PopupWindowInterface;

import java.util.EventObject;
import java.util.Objects;
import java.util.logging.Logger;

/**
 * project: glcmtx
 * author:  kostrovik
 * date:    27/07/2018
 * github:  https://github.com/kostrovik/glcmtx
 */
public class ColorThemesListView implements PopupWindowInterface {
    private static Logger logger = Configurator.getConfig().getLogger(ColorThemesListView.class.getName());

    private Stage stage;
    private ApplicationSettings settings;
    private String selectedTheme;
    private Pane parent;
    private VBox view;
    private ControlBuilderFacadeInterface facade;
    private Configurator configurator;

    public ColorThemesListView(Pane parent, Stage stage) {
        this.stage = stage;
        this.settings = ApplicationSettings.getInstance();
        this.parent = parent;
        this.selectedTheme = settings.getDefaultColorTheme();
        this.configurator = Configurator.getConfig();
        this.facade = Objects.requireNonNull(configurator.getControlBuilder());
        this.view = createView();
    }

    @Override
    public void initView(EventObject event) {
        selectedTheme = settings.getDefaultColorTheme();
    }

    @Override
    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @Override
    public Region getView() {
        return view;
    }

    private VBox createView() {
        VBox view = new VBox(10);
        view.setPadding(new Insets(10, 10, 10, 10));

        view.prefWidthProperty().bind(parent.widthProperty());
        view.prefHeightProperty().bind(parent.heightProperty());

        VBox table = createTable();
        table.prefHeightProperty().bind(view.heightProperty());

        view.getChildren().setAll(viewTitle(), table, viewButtons());

        return view;
    }

    private Region viewTitle() {
        Text title = new Text("Выбор цветовой темы приложения.");
        title.getStyleClass().add("view-title");

        HBox titleView = new HBox(10);
        titleView.setPadding(new Insets(10, 10, 10, 10));
        titleView.getChildren().addAll(title);

        return titleView;
    }

    private Region viewButtons() {
        Button saveButton = facade.createButton("Сохранить");
        Button cancelButton = facade.createButton("Отмена");

        saveButton.setOnAction(event -> {
            settings.saveDefaultColorTheme(selectedTheme);
            stage.close();
        });

        cancelButton.setOnAction(event -> stage.close());

        HBox buttonView = new HBox(10);
        buttonView.setPadding(new Insets(10, 10, 10, 10));
        buttonView.getChildren().addAll(saveButton, cancelButton);
        buttonView.setAlignment(Pos.CENTER_RIGHT);

        return buttonView;
    }

    private VBox createTable() {
        VBox buttons = new VBox(10);
        ToggleGroup group = new ToggleGroup();

        RadioButton lightTheme = new RadioButton("Ligth");
        lightTheme.setToggleGroup(group);

        lightTheme.setSelected(selectedTheme.equals(ColorThemeDictionary.LIGHT.getThemeName()));

        lightTheme.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                selectedTheme = ColorThemeDictionary.LIGHT.getThemeName();
            }
        });

        RadioButton darkAdmin = new RadioButton("Dark-admin");
        darkAdmin.setToggleGroup(group);
        darkAdmin.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                selectedTheme = ColorThemeDictionary.DARK_ADMIN.getThemeName();
            }
        });
        darkAdmin.setSelected(selectedTheme.equals(ColorThemeDictionary.DARK_ADMIN.getThemeName()));

        buttons.getChildren().addAll(lightTheme, darkAdmin);

        return buttons;
    }
}
