package com.bs.manage.controller.kpi;

import com.bs.manage.annotation.Role;
import com.bs.manage.code.CodeCaption;
import com.bs.manage.model.bean.kpi.KpiMain;
import com.bs.manage.model.json.ResponseJson;
import com.bs.manage.model.json.kpi.EmailContent;
import com.bs.manage.model.param.kpi.KpiListParam;
import com.bs.manage.service.kpi.KpiMainService;
import com.bs.manage.until.CodeUtil;
import org.hibernate.validator.constraints.Range;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.Map;


/**
 * 2020/6/15 16:43
 * fzj
 */
@RestController
@RequestMapping("admin/kpi")
@Validated
public class KpiController {

    private final KpiMainService kpiMainService;

    public KpiController(KpiMainService kpiMainService) {
        this.kpiMainService = kpiMainService;
    }

    /**
     * 保存kpi邮件通知模板
     *
     * @param emailContent 邮件内容
     * @return 是否成功
     */
    @PostMapping("email_content")
    @Role(roleId = 21)
    public ResponseJson emailContent(@Validated EmailContent emailContent) {
        kpiMainService.emailContent(emailContent);
        return ResponseJson.success();
    }

    /**
     * 查看kpi邮件通知模板
     *
     * @return 邮箱模板内容
     */
    @GetMapping("email_content")
    @Role(roleId = 21)
    public ResponseJson getEmailContent() {
        return ResponseJson.success(kpiMainService.getEmailContent());
    }

    @GetMapping()
    public ResponseJson getByPage(@Validated KpiListParam param) {
        return ResponseJson.success(kpiMainService.getByPage(param));
    }


    @GetMapping("all_status")
    public ResponseJson getAllStatus() {
        Map<Integer, String> captionMap = CodeUtil.getCaptionMap(CodeCaption.KPI_STATUS);
        Map<Integer, String> map = new HashMap<>(captionMap);
        map.remove(CodeCaption.KPI_STATUS_3);
        return ResponseJson.success(map);
    }

    /**
     * 修改考勤状态
     *
     * @param kpiId            考核主表id
     * @param status           考核状态
     * @param status_extra_msg 状态额外信息 , 改进时需要填改进原因
     * @return 成功与否
     */
    @PutMapping("update_status")
    public ResponseJson updateKpiStatus(Long kpiId, Integer status, String status_extra_msg) {
        return kpiMainService.updateKpiStatus(kpiId, status, status_extra_msg);
    }

    /**
     * 查看考核详情
     *
     * @param kpiMain 入参
     * @return 考核详情列表
     */
    @GetMapping("content")
    public ResponseJson getContent(@Validated KpiMain kpiMain) {
        return kpiMainService.getContent(kpiMain);
    }

    /**
     * 保存考核计划
     * 列表中的修改和删除只是前端ui
     *
     * @param kpiMain 入参
     * @return 保存考核详情
     */
    @PostMapping("content")
    public ResponseJson saveContent(@RequestBody @Validated KpiMain kpiMain) {
        return kpiMainService.saveContent(kpiMain);
    }

    /**
     * 查询流水状态
     *
     * @param kpi_id kpi主表id
     * @return 流水状态信息
     */
    @GetMapping("flowProgress")
    public ResponseJson flowProgress(@NotNull Long kpi_id) {
        return kpiMainService.flowProgress(kpi_id);
    }

    /**
     * 修改考核计划
     * 只用于评分
     *
     * @param kpiMain 入参
     * @return 修改考核详情
     */
    @PutMapping("content")
    public ResponseJson updateContent(@RequestBody @Validated KpiMain kpiMain) {
        return kpiMainService.updateContent(kpiMain);
    }

    /**
     * 获取下属成员
     *
     * @param type 1-可读成员 2- 可编写成员
     * @return 账号列表
     */
    @GetMapping("getSubordinate")
    public ResponseJson getSubordinate(@NotNull @Range(min = 1, max = 2) Integer type) {
        return ResponseJson.success(kpiMainService.getSubordinate(type));
    }

    /**
     * 导出列表
     *
     * @param param 入参
     */
    @RequestMapping("exportList")
    @Role(roleId = 23)
    public void exportList(KpiListParam param, HttpServletResponse response) {
        kpiMainService.exportList(param, response);
    }

    /**
     * 导出详细
     *
     * @param param 入参
     */
    @RequestMapping("exportDetail")
    @Role(roleId = 23)
    public void exportDetail(KpiListParam param, HttpServletResponse response) {
        kpiMainService.exportDetail(param, response);
    }


}
