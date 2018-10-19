package com.github.kostrovik.kernel.builders;

import com.github.kostrovik.kernel.graphics.controls.progress.ProgressBarIndicator;

import java.util.TimerTask;

/**
 * project: kernel
 * author:  kostrovik
 * date:    22/08/2018
 * github:  https://github.com/kostrovik/kernel
 */
public class MemoryStateBuilder extends TimerTask {
    private ProgressBarIndicator bar;

    public MemoryStateBuilder(ProgressBarIndicator bar) {
        this.bar = bar;
    }

    @Override
    public void run() {
        long total = Runtime.getRuntime().totalMemory();
        long free = Runtime.getRuntime().freeMemory();

        bar.setFormat("%.2f " + getDimension(total - free) + " / %.2f " + getDimension(total));
        bar.setTotal(formatValue(total));
        bar.setDone(formatValue(total - free));
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
}
