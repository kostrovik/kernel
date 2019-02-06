package com.github.kostrovik.kernel.models.controls;

import com.github.kostrovik.useful.models.AbstractObservable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

/**
 * project: kernel
 * author:  kostrovik
 * date:    2019-01-21
 * github:  https://github.com/kostrovik/kernel
 */
public class ControlPanelButtons extends AbstractObservable {
    private Map<Integer, ControlPanelButton> orderedButtons;

    public ControlPanelButtons(List<ControlPanelButton> buttons) {
        this.orderedButtons = new TreeMap<>();
        buttons.forEach(controlPanelButton -> orderedButtons.put(controlPanelButton.getOrder(), controlPanelButton));
    }

    public List<ControlPanelButton> getButtonsList() {
        return new ArrayList<>(orderedButtons.values());
    }

    public Map<String, ControlPanelButton> getButtonsMap() {
        Map<String, ControlPanelButton> buttonMap = new HashMap<>();
        orderedButtons.forEach((key, value) -> buttonMap.put(value.getButtonKey(), value));
        return buttonMap;
    }

    public void addPanelButton(ControlPanelButton button) {
        Objects.requireNonNull(button);
        orderedButtons.put(button.getOrder(), button);
        notifyListeners(this);
    }

    public void removePanelButton(ControlPanelButton button) {
        Objects.requireNonNull(button);
        ControlPanelButton removedButton = orderedButtons.get(button.getOrder());
        if (removedButton.equals(button)) {
            orderedButtons.remove(button.getOrder());
        } else {
            orderedButtons.entrySet().stream()
                    .filter(entry -> entry.getValue().equals(button))
                    .findFirst()
                    .ifPresent(entry -> orderedButtons.remove(entry.getKey()));
        }
        notifyListeners(this);
    }
}