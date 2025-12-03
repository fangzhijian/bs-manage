package com.springboot.demo.until.page;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * {@code @description}
 * 分页入参基础类
 *
 * @author fangzhijian
 * @since 2025-11-29 22:06
 */
@Data
public class BaseQueryPage implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 当前页，默认1
     */
    private long current = 1;
    /**
     * 每页大小，默认10
     */
    private long size = 10;

    /**
     * 用于MybatisPlus.page入参
     *
     * @param tClass dao.class
     * @param <T>    dao类
     * @return 分页入参类
     */
    public <T> Page<T> getPage(Class<T> tClass) {
        return new Page<>(current, size);
    }
}
