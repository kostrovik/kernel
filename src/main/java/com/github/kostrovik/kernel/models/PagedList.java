package com.github.kostrovik.kernel.models;

import java.util.List;

/**
 * project: kernel
 * author:  kostrovik
 * date:    23/08/2018
 * github:  https://github.com/kostrovik/kernel
 */
public class PagedList<T> {
    private List<T> list;
    private int total;

    public PagedList(List<T> list, int total) {
        this.list = list;
        this.total = total;
    }

    public List<T> getList() {
        return list;
    }

    public int getTotal() {
        return total;
    }
}