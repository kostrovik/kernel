package com.github.kostrovik.kernel.graphics.utils;

import com.github.kostrovik.kernel.models.PagedList;

import java.time.LocalDateTime;

/**
 * project: kernel
 * author:  kostrovik
 * date:    2019-01-11
 * github:  https://github.com/kostrovik/kernel
 */
public class DownloadResult<E> {
    private int offset;
    private int pageSize;
    private LocalDateTime stamp;
    private PagedList<E> list;

    public DownloadResult(int offset, int pageSize, LocalDateTime stamp, PagedList<E> list) {
        this.offset = offset;
        this.pageSize = pageSize;
        this.stamp = stamp;
        this.list = list;
    }

    public int getOffset() {
        return offset;
    }

    public int getPageSize() {
        return pageSize;
    }

    public LocalDateTime getStamp() {
        return stamp;
    }

    public PagedList<E> getList() {
        return list;
    }
}
