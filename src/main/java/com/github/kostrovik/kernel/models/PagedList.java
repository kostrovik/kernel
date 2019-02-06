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
    private int offset;

    public PagedList(List<T> list) {
        this(list, list.size(), 0);
    }

    public PagedList(List<T> list, int total, int offset) {
        this.list = list;
        this.total = total;
        this.offset = offset;
    }

    public List<T> getList() {
        return list;
    }

    public int getTotal() {
        return total;
    }

    public int getOffset() {
        return offset;
    }

    public int getSize() {
        return list.size();
    }
}