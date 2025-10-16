package com.bs.manage.service.common;


import com.bs.manage.model.bean.common.CommonModel;
import com.bs.manage.model.json.ResponseJson;

import java.util.List;

/**
 * 2020/1/20 16:44
 * fzj
 * 通用接口
 */
public interface CommonService<T extends CommonModel> {

    /**
     * 插入
     *
     * @param bean 表对应的实体类
     * @return 是否插入成功
     */
    ResponseJson insert(T bean);

    /**
     * 批量插入
     *
     * @param list 表对应的实体类集合
     * @return 是否批量插入成功
     */
    Integer insertBatch(List<T> list);

    /**
     * 删除(假删除)
     *
     * @param id 主键id
     * @return 是否删除成功
     */
    ResponseJson delete(Long id);

    /**
     * 根据id批量删除(假删除)
     *
     * @param list id集合
     * @return 是否根据id批量删除(假删除)成功
     */
    Integer deleteByIds(List<Long> list);

    /**
     * 更新
     *
     * @param bean 表对应的实体类
     * @return 是否更新成功
     */
    ResponseJson update(T bean);

    /**
     * 批量更新
     *
     * @param list 表对应的实体类集合
     * @return 是否批量更新成功
     */
    Integer updateBatch(List<T> list);

    /**
     * 根据id查询
     *
     * @param id id
     * @return id所在行信息
     */
    T getById(Long id);

    /**
     * 查询所有信息,只用于小表,大表需要分页
     *
     * @return 整张表信息
     */
    List<T> getAll();

    /**
     * 根据字段条件查询
     *
     * @param bean 表对应的实体类
     * @return 一条符合条件的信息
     */
    T getOneBySelectKey(T bean);

    /**
     * 根据字段条件查询
     *
     * @param bean 表对应的实体类
     * @return 所有符合条件的信息
     */
    List<T> getAllBySelectKey(T bean);

}
