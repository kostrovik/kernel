package com.github.kostrovik.kernel.graphics.builders;

import com.github.kostrovik.kernel.settings.Configurator;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;
import com.github.kostrovik.kernel.graphics.helper.PropertyReference;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Служебный класс для получения значений из объектов разных типов.
 * Используется при создании ячеек таблицы.
 *
 * project: kernel
 * author:  kostrovik
 * date:    25/07/2018
 * github:  https://github.com/kostrovik/kernel
 */
public class CellPropertyValueFactory<S, T> implements Callback<TableColumn.CellDataFeatures<S, T>, ObservableValue<T>> {
    private static Logger logger = Configurator.getConfig().getLogger(CellPropertyValueFactory.class.getName());

    /**
     * Название атрибута объекта значение которого необходимо получить.
     */
    private final String property;

    private Class<?> columnClass;
    private String previousProperty;
    private PropertyReference<T> propertyRef;

    public CellPropertyValueFactory(String property) {
        this.property = property;
    }

    @Override
    public ObservableValue<T> call(TableColumn.CellDataFeatures<S, T> param) {
        return getCellDataReflectively(param, param.getValue());
    }

    public final String getProperty() {
        return property;
    }

    /**
     * Использует рефлесию для получения getter и setter методов для доступа к атрибуту объекта.
     *
     * @param param   the param
     * @param rowData the row data
     * @return the cell data reflectively
     */
    private ObservableValue<T> getCellDataReflectively(TableColumn.CellDataFeatures<S, T> param, S rowData) {
        if (getProperty() == null || getProperty().isEmpty() || rowData == null) return null;

        try {
            // we attempt to cache the property reference here, as otherwise
            // performance suffers when working in large data models. For
            // a bit of reference, refer to RT-13937.
            if (columnClass == null || previousProperty == null ||
                    !columnClass.equals(rowData.getClass()) ||
                    !previousProperty.equals(getProperty())) {

                // create a new PropertyReference
                this.columnClass = rowData.getClass();
                this.previousProperty = getProperty();
                this.propertyRef = new PropertyReference<T>(rowData.getClass(), getProperty());
            }

            if (propertyRef != null) {
                if (propertyRef.hasProperty()) {
                    return propertyRef.getProperty(rowData);
                } else {
                    T value = propertyRef.get(rowData);

                    if (value instanceof Boolean) {
                        ReadOnlyBooleanWrapper booleanProp = new ReadOnlyBooleanWrapper((Boolean) value);
                        return (ObservableValue<T>) booleanProp;
                    }

                    return new ReadOnlyObjectWrapper<>(value);
                }
            }
        } catch (RuntimeException e) {
            logger.log(Level.WARNING, String.format("Не возможно получить свойство %s, в CellPropertyValueFactory: %s из переданного объекта %s", getProperty(), this, rowData.getClass()), e);
            propertyRef = null;
        }

        return null;
    }
}
