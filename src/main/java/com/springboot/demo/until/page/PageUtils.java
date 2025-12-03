package com.springboot.demo.until.page;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;

/**
 * {@code @description}
 * 分页工具
 *
 * @author fangzhijian
 * @since 2025-11-29 21:57
 */
public class PageUtils {

    /**
     * 分页转换
     *
     * @param page 分页数据
     * @param <T>  dao类
     * @return 分页数据
     */
    public static <T> PageDTO<T> convert(IPage<T> page) {
        PageDTO<T> pageDTO = new PageDTO<T>();
        pageDTO.setCurrent(page.getCurrent());
        pageDTO.setSize(page.getSize());
        pageDTO.setTotal(page.getTotal());
        pageDTO.setRecords(page.getRecords());
        return pageDTO;
    }

    /**
     * 分页转换dao转dto
     *
     * @param page 分页数据
     * @param fun  lambda表达式 T转R
     * @param <T>  dao类
     * @param <R>  dto类
     * @return 分页数据
     */
    public static <T, R> PageDTO<R> convert(IPage<T> page, Function<T, R> fun) {
        PageDTO<R> pageDTO = new PageDTO<>();
        pageDTO.setCurrent(page.getCurrent());
        pageDTO.setSize(page.getSize());
        pageDTO.setTotal(page.getTotal());
        if (CollectionUtils.isNotEmpty(page.getRecords())) {
            List<R> list = page.getRecords().stream().map(fun).toList();
            pageDTO.setRecords(list);
        } else {
            pageDTO.setRecords(Collections.emptyList());
        }
        return pageDTO;
    }
}
