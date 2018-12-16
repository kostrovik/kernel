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

    public SystemTrayView() {
        view = createView();
    }

    @Override
    public void initView(EventObject event) {
        // при инициализации не требуется делать каких либо установок
    }

    @Override
    public Region getView() {
        return view;
    }

    private Region createView() {
        HBox tray = new HBox(5);
        tray.setPadding(new Insets(5, 5, 5, 5));
        tray.setAlignment(Pos.BOTTOM_RIGHT);

        ProgressBarIndicator bar = new ProgressBarIndicator(0);
        bar.setFormat("%.2f %s / %.2f %s");
        initMemoryListener(bar);

        tray.getChildren().setAll(bar);

        return tray;
    }

    private void initMemoryListener(ProgressBarIndicator bar) {
        Timer timer = new Timer();

        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                long total = Runtime.getRuntime().totalMemory();
                long free = Runtime.getRuntime().freeMemory();

                List<Object> attributes = new ArrayList<>();
                attributes.add(bar.getDone());
                attributes.add(getDimension(total - free));
                attributes.add(bar.getTotal());
                attributes.add(getDimension(total));

                bar.setFormatAttributes(attributes);

                bar.setDone(formatValue(total - free));
                bar.setTotal(formatValue(total));
            }

            private double formatValue(long v) {
                if (v < 1024) return v;
                int z = (63 - Long.numberOfLeadingZeros(v)) / 10;
                return (double) v / (1L << (z * 10));
            }

            private String getDimension(long v) {
                if (v < 1024) return "B";
                int z = (63 - Long.numberOfLeadingZeros(v)) / 10;
                return String.format("%sB", " KMGTPE".charAt(z));
            }
        };

        timer.schedule(task, 0, 5000);
    }
}
