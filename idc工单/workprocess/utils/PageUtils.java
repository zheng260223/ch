/*
 * Copyright 2018 Unitslink technology Ltd., Co.
 * Licensed under the Apache License 2.0.
 */
package com.workprocess.utils;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

/**
 * SpringData分页校验工具
 *
 */
public class PageUtils {

    private static final int MAX_SIZE = 1000; //避免客户端一次获取过多数据造成性能问题

    public static PageRequest verify(Integer pageSize, Integer pageNumber) {
        int size = 10, page = 0;
        if (null != pageSize) {
            if (pageSize > MAX_SIZE) {
                size = MAX_SIZE;
            } else if (pageSize < 1) {
                size = 1;
            } else {
                size = pageSize;
            }
        }
        if (null != pageNumber) {
            if (pageNumber < 0) {
                page = 0;
            } else {
                page = pageNumber;
            }
        }
        return new PageRequest(page, size);
    }

    public static PageRequest verify(Integer pageSize, Integer pageNumber, Sort sort) {
        int size = 10, page = 0;
        if (null != pageSize) {
            if (pageSize > MAX_SIZE) {
                size = MAX_SIZE;
            } else if (pageSize < 1) {
                size = 1;
            } else {
                size = pageSize;
            }
        }
        if (null != pageNumber) {
            if (pageNumber < 0) {
                page = 0;
            } else {
                page = pageNumber;
            }
        }
        return new PageRequest(page, size, sort);
    }
}
