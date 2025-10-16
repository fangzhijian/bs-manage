package com.bs.manage.service.console;

import com.bs.manage.model.json.ResponseJson;
import com.bs.manage.model.param.console.BoardParam;


/**
 * 2020/3/16 11:24
 * fzj
 */
public interface BoardService {

    /**
     * 仪表盘-金额
     *
     * @param param 入参
     * @return 各个金额
     */
    ResponseJson amount(BoardParam param);

    /**
     * 业务过程加拜访率 成交率 客单价
     *
     * @param param 入参
     * @return 各结果
     */
    ResponseJson process(BoardParam param);

    /**
     * 覆盖分表
     *
     * @param param 入参
     * @return 各结果
     */
    ResponseJson cover(BoardParam param);

    /**
     * 拜访分表
     *
     * @param param 入参
     * @return 各结果
     */
    ResponseJson visit(BoardParam param);

    /**
     * 成交分表
     *
     * @param param 入参
     * @return 各结果
     */
    ResponseJson deal(BoardParam param);


    /**
     * 活跃分表
     *
     * @param param 入参
     * @return 各结果
     */
    ResponseJson active(BoardParam param);


    /**
     * 释放分表
     *
     * @param param 入参
     * @return 各结果
     */
    ResponseJson release(BoardParam param);

    /**
     * 日报汇总
     *
     * @param param 入参
     * @return 个结果
     */
    ResponseJson report(BoardParam param);

    /**
     * 销售排行
     *
     * @param param 入参
     * @return 个结果
     */
    ResponseJson topSale(BoardParam param);


    /**
     * 成交数分类
     *
     * @param param 入参
     * @return 各种成交数
     */
    ResponseJson dealClassify(BoardParam param);


}
