package com.github.kostrovik.kernel.models;

import com.github.kostrovik.kernel.interfaces.EventListenerInterface;
import com.github.kostrovik.kernel.interfaces.controls.ListFilterAndSorterInterface;

import java.util.ArrayList;
import java.util.EventObject;
import java.util.List;

/**
 * project: kernel
 * author:  kostrovik
 * date:    2018-09-17
 * github:  https://github.com/kostrovik/kernel
 */
public abstract class AbstractListFilter implements ListFilterAndSorterInterface {
    private List<EventListenerInterface> listeners;

    protected AbstractListFilter() {
        listeners = new ArrayList<>();
    }

    @Override
    public void addListener(EventListenerInterface listener) {
        listeners.add(listener);
    }

    @Override
    public void removeListener(EventListenerInterface listener) {
        listeners.remove(listener);
    }

    protected void notifyListeners() {
        listeners.forEach(listener -> listener.handle(new EventObject(this)));
    }
}
