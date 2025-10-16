package com.bs.manage.controller.console;

import com.bs.manage.model.json.ResponseJson;
import com.bs.manage.model.param.console.BoardParam;
import com.bs.manage.service.console.BoardService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;



/**
 * 2020/3/16 10:55
 * fzj
 */
@RestController
@RequestMapping("admin/board")
@Validated
public class BoardController {

    private final BoardService boardService;

    public BoardController(BoardService boardService) {
        this.boardService = boardService;
    }


    /**
     * 仪表盘-金额
     *
     * @param param 入参
     * @return 各个金额
     */
    @GetMapping("amount")
    public ResponseJson amount(BoardParam param) {
        return boardService.amount(param);
    }

    /**
     * 业务过程加拜访率 成交率 客单价
     *
     * @param param 入参
     * @return 各结果
     */
    @GetMapping("process")
    public ResponseJson process(BoardParam param) {
        return boardService.process(param);
    }

    /**
     * 覆盖分表
     *
     * @param param 入参
     * @return 各结果
     */
    @GetMapping("cover")
    public ResponseJson cover(BoardParam param) {
        return boardService.cover(param);
    }

    /**
     * 拜访分表
     *
     * @param param 入参
     * @return 各结果
     */
    @GetMapping("visit")
    public ResponseJson visit(BoardParam param) {
        return boardService.visit(param);
    }

    /**
     * 成交分表
     *
     * @param param 入参
     * @return 各结果
     */
    @GetMapping("deal")
    public ResponseJson deal(BoardParam param) {
        return boardService.deal(param);
    }

    /**
     * 活跃分表
     *
     * @param param 入参
     * @return 各结果
     */
    @GetMapping("active")
    public ResponseJson active(BoardParam param) {
        return boardService.active(param);
    }

    /**
     * 释放分表
     *
     * @param param 入参
     * @return 各结果
     */
    @GetMapping("release")
    public ResponseJson release(BoardParam param) {
        return boardService.release(param);
    }

    /**
     * 日报汇总
     *
     * @param param 入参
     * @return 个结果
     */
    @GetMapping("report")
    public ResponseJson report(BoardParam param) {
        return boardService.report(param);
    }

    /**
     * 销售排行
     *
     * @param param 入参
     * @return 个结果
     */
    @GetMapping("top_sale")
    public ResponseJson topSale(BoardParam param) {
        return boardService.topSale(param);
    }

    /**
     * 成交数分类
     *
     * @param param 入参
     * @return 各种成交数
     */
    @GetMapping("deal_classify")
    public ResponseJson dealClassify(BoardParam param) {
        return boardService.dealClassify(param);
    }
}
