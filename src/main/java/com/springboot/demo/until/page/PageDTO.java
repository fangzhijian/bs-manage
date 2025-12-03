package com.springboot.demo.until.page;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * {@code @description}
 * 分页返回参数
 *
 * @author fangzhijian
 * @since 2025-11-29 21:58
 */
@Data
public class PageDTO<T> implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 当前页
     */
    private long current;
    /**
     * 每页大小
     */
    private long size;
    /**
     * 总条数
     */
    private long total;
    /**
     * 数据列表
     */
    private List<T> records;

}
