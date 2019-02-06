package com.github.kostrovik.kernel.views;

import com.github.kostrovik.kernel.graphics.controls.progress.ProgressBarIndicator;
import com.github.kostrovik.kernel.interfaces.views.ContentViewInterface;
import com.github.kostrovik.useful.models.AbstractObservable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;

import java.util.ArrayList;
import java.util.EventObject;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * project: kernel
 * author:  kostrovik
 * date:    2018-12-16
 * github:  https://github.com/kostrovik/kernel
 */
public class SystemTrayView extends AbstractObservable implements ContentViewInterface {
    private Region view;
    private ProgressBarIndicator bar;

    public SystemTrayView() {
        view = createView();
    }

    @Override
    public void initView(EventObject event) {
        bar.setDone(0);
        bar.setTotal(1);
        initMemoryListener();
    }

    @Override
    public Region getView() {
        return view;
    }

    private Region createView() {
        HBox tray = new HBox(5);
        tray.setPadding(new Insets(5, 5, 5, 5));
        tray.setAlignment(Pos.BOTTOM_RIGHT);

        bar = new ProgressBarIndicator(0);
        bar.setFormat("%s / %s");

        tray.getChildren().setAll(bar);

        return tray;
    }

    private void initMemoryListener() {
        Timer timer = new Timer();

        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                long total = Runtime.getRuntime().totalMemory();
                long free = Runtime.getRuntime().freeMemory();
                setMemoryBarValues(total, free);
            }
        };

        timer.schedule(task, 0, 5000);
    }

    private void setMemoryBarValues(long total, long free) {
        List<Object> attributes = new ArrayList<>();
        attributes.add(formatValue(total - free));
        attributes.add(formatValue(total));

        bar.setFormatAttributes(attributes);

        bar.setTotal(total);
        bar.setDone((double) total - free);
    }

    private String formatValue(long v) {
        if (v < 1024) return String.format("%d B", v);
        int z = (63 - Long.numberOfLeadingZeros(v)) / 10;
        return String.format("%.2f %sB", (double) v / (1L << (z * 10)), " KMGTPE".charAt(z));
    }
}
