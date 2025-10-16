package com.bs.manage.service.kpi;

import com.bs.manage.model.bean.account.User;
import com.bs.manage.model.bean.kpi.KpiMain;
import com.bs.manage.model.json.Page;
import com.bs.manage.model.json.ResponseJson;
import com.bs.manage.model.json.kpi.EmailContent;
import com.bs.manage.model.param.kpi.KpiListParam;
import com.bs.manage.model.param.kpi.KpiListResult;
import com.bs.manage.service.common.CommonService;

import jakarta.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 2020/6/15 16:29
 * fzj
 */
public interface KpiMainService extends CommonService<KpiMain> {

    void emailContent(EmailContent emailContent);

    EmailContent getEmailContent();

    Page<KpiListResult> getByPage(KpiListParam param);

    ResponseJson updateKpiStatus(Long kpiId, Integer status, String status_extra_msg);

    ResponseJson getContent(KpiMain kpiMain);

    ResponseJson saveContent(KpiMain kpiMain);

    /**
     * 查询流水状态
     *
     * @param kpi_id kpi主表id
     * @return 流水状态信息
     */
    ResponseJson flowProgress(Long kpi_id);

    ResponseJson updateContent(KpiMain kpiMain);

    /**
     * 获取下属成员
     *
     * @param type 1-可读成员 2- 可编写成员
     * @return 账号列表
     */
    List<User> getSubordinate(Integer type);


    void exportList(KpiListParam param, HttpServletResponse response);


    void exportDetail(KpiListParam param, HttpServletResponse response);

}
