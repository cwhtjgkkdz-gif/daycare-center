package com.soft231.daycarecenterbackend.common;

import lombok.Data;

import java.util.List;

/**
 * 分页返回结果
 */
@Data
public class PageResult<T> {

    private long total;
    private long current;
    private long size;
    private List<T> records;

    public static <T> PageResult<T> of(long total, long current, long size, List<T> records) {
        PageResult<T> r = new PageResult<>();
        r.total = total;
        r.current = current;
        r.size = size;
        r.records = records;
        return r;
    }
}
