package com.github.kostrovik.kernel.graphics.controls.dropdown;

import com.github.kostrovik.kernel.dictionaries.LayoutType;
import com.github.kostrovik.kernel.dictionaries.ViewTypeDictionary;
import com.github.kostrovik.kernel.interfaces.FilterAttributeSetter;
import com.github.kostrovik.kernel.interfaces.views.ViewEventInterface;
import com.github.kostrovik.kernel.interfaces.views.ViewEventListenerInterface;
import com.github.kostrovik.kernel.models.ListFilterAndSorter;
import com.github.kostrovik.kernel.settings.Configurator;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Bounds;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.PopupControl;
import javafx.scene.control.SkinBase;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.text.Text;

import java.util.Objects;

/**
 * project: kernel
 * author:  kostrovik
 * date:    22/08/2018
 * github:  https://github.com/kostrovik/kernel
 */
public class SearchableDropDownFieldSkin<T extends Comparable> extends SkinBase<SearchableDropDownField<T>> {
    private InputControl<T> inputControl;
    private PopupControl popupControl;

    private BooleanProperty listVisible;
    private ListFilterAndSorter listFilter;

    public SearchableDropDownFieldSkin(SearchableDropDownField<T> control) {
        super(control);
        this.listVisible = new SimpleBooleanProperty(false);
        this.listFilter = new ListFilterAndSorter(getSkinnable().getLookupAttribute());
        this.listFilter.addFilterAttribute(new FilterAttributeSetter<String>() {
            @Override
            public String getAttributeName() {
                return getSkinnable().getLookupAttribute();
            }

            @Override
            public Object prepareValue(String value) {
                if (Objects.isNull(value) || value.isBlank()) {
                    return null;
                }
                return value;
            }
        });

        createSkin();

        Platform.runLater(() -> getSkinnable().getScene().addEventFilter(MouseEvent.MOUSE_CLICKED, event -> {
            if (!isDropDown((Node) event.getTarget())) {
                popupControl.hide();
            }
        }));
    }

    private boolean isDropDown(Node node) {
        if (Objects.isNull(node)) {
            return false;
        }
        if (node.equals(getSkinnable())) {
            return true;
        }
        return isDropDown(node.getParent());
    }

    private void createSkin() {
        HBox input = createInputGroup();
        popupControl = createPopupGroup();
        getChildren().addAll(input);
    }

    private PopupControl createPopupGroup() {
        DataListControl<T> dataListControl = new DataListControl<>(
                getSkinnable().getListLabelCallback(),
                getSkinnable().getPaginationService(),
                getSkinnable().getLookupAttribute(),
                getSkinnable().getSelectionModel());
        dataListControl.callbackProperty().bind(getSkinnable().listLabelCallbackProperty());
        dataListControl.setMaxWidth(500);
        dataListControl.setMaxHeight(300);

        PopupControl popup = new PopupControl();
        popup.getScene().setRoot(dataListControl);
        popup.setConsumeAutoHidingEvents(true);
        popup.setAutoHide(false);
        popup.setAutoFix(true);
        popup.hide();

        popup.showingProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                dataListControl.clear();
            }
        });

        listVisible.bind(popup.showingProperty());

        return popup;
    }

    private HBox createInputGroup() {
        HBox container = new HBox(10);
        container.setAlignment(Pos.CENTER_LEFT);

        inputControl = new InputControl<>(getSkinnable().getSelectionModel(), event -> openDictionary(), event -> openList(), event -> clearSelected());
        inputControl.callbackProperty().bind(getSkinnable().listLabelCallbackProperty());

        getSkinnable().focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                popupControl.hide();
            }
        });

        Text label = createLabel();

        container.getChildren().addAll(label, inputControl);
        HBox.setHgrow(label, Priority.NEVER);
        HBox.setHgrow(inputControl, Priority.ALWAYS);

        return container;
    }

    private void openDictionary() {
        ViewEventListenerInterface listener = Configurator.getConfig().getEventListener();
        listener.handle(new ViewEventInterface() {
            @Override
            public String getModuleName() {
                return this.getClass().getModule().getName();
            }

            @Override
            public String getViewName() {
                return ViewTypeDictionary.DROPDOWN_DIALOG.name();
            }

            @Override
            public Object getEventData() {
                return getSkinnable();
            }

            @Override
            public LayoutType getLayoutType() {
                return LayoutType.POPUP;
            }
        });
    }

    private void clearSelected() {
        getSkinnable().getSelectionModel().getItems().clear();
    }

    private void openList() {
        if (popupControl.isShowing()) {
            popupControl.hide();
        } else {
            Bounds bounds = inputControl.localToScreen(inputControl.getBoundsInLocal());
            popupControl.show(inputControl.getScene().getWindow(), bounds.getMinX(), bounds.getMinY() + bounds.getHeight());
            listFilter.clear();
        }
    }

    private Text createLabel() {
        Text label = new Text(getSkinnable().getLabel());
        label.textProperty().bind(getSkinnable().labelProperty());
        label.getStyleClass().add("label");
        label.setFocusTraversable(false);
        label.visibleProperty().bind(getSkinnable().showLabelProperty());
        label.managedProperty().bind(label.visibleProperty());

        return label;
    }
}