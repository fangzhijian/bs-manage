package com.bs.manage.controller.notify;

import com.bs.manage.model.bean.notify.NotifyCustomerAuth;
import com.bs.manage.model.bean.notify.NotifyCustomerRelease;
import com.bs.manage.model.bean.notify.NotifyKpi;
import com.bs.manage.model.json.ResponseJson;
import com.bs.manage.model.param.notify.OutlineResult;
import com.bs.manage.service.notify.NotifyCustomerAuthService;
import com.bs.manage.service.notify.NotifyCustomerReleaseService;
import com.bs.manage.service.notify.NotifyKpiService;
import org.hibernate.validator.constraints.Range;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.constraints.NotNull;

/**
 * 2020/4/13 11:40
 * fzj
 */
@RestController
@RequestMapping("admin/notify")
@Validated
public class NotifyController {

    private final NotifyCustomerAuthService notifyCustomerAuthService;
    private final NotifyCustomerReleaseService notifyCustomerReleaseService;
    private final NotifyKpiService notifyKpiService;

    public NotifyController(NotifyCustomerAuthService notifyCustomerAuthService, NotifyCustomerReleaseService notifyCustomerReleaseService, NotifyKpiService notifyKpiService) {
        this.notifyCustomerAuthService = notifyCustomerAuthService;
        this.notifyCustomerReleaseService = notifyCustomerReleaseService;
        this.notifyKpiService = notifyKpiService;
    }


    /**
     * 通知栏概要
     *
     * @return 各个未读消息数
     */
    @GetMapping("outline")
    public ResponseJson outline() {
        OutlineResult result = new OutlineResult();
        Integer customerAuth = notifyCustomerAuthService.countByUnRead();
        Integer customerRelease = notifyCustomerReleaseService.countByUnRead();
        Integer kpi = notifyKpiService.countByUnRead();
        result.setCustomer_auth(customerAuth);
        result.setCustomer_release(customerRelease);
        result.setKpi(kpi);
        result.setTotal(customerAuth + customerRelease+kpi);
        return ResponseJson.success(result);
    }

    /**
     * 客户授权过期通知
     *
     * @param limit  每页条数
     * @param offset 第几条开始
     * @return 客户授权过期通知列表
     */
    @GetMapping("customer_auth")
    public ResponseJson customerAuth(@NotNull Integer limit, @NotNull Integer offset) {
        return ResponseJson.success(notifyCustomerAuthService.getByPage(limit, offset));
    }

    /**
     * 客户待释放通知
     *
     * @param limit  每页条数
     * @param offset 第几条开始
     * @return 客户待释放通知消息列表
     */
    @GetMapping("customer_release")
    public ResponseJson customerRelease(@NotNull Integer limit, @NotNull Integer offset) {
        return ResponseJson.success(notifyCustomerReleaseService.getByPage(limit, offset));
    }

    /**
     * 考核通知
     *
     * @param limit  每页条数
     * @param offset 第几条开始
     * @return 考核通知消息列表
     */
    @GetMapping("kpi")
    public ResponseJson kpi(@NotNull Integer limit, @NotNull Integer offset) {
        return ResponseJson.success(notifyKpiService.getByPage(limit, offset));
    }

    /**
     * 标记通知为已读
     *
     * @param id          消息通知id
     * @param has_read    0-未读 1-已读
     * @param notify_type 通知的类型 1-客户授权 2-客户释放 3-考勤通知
     * @return 标记失败与成功
     */
    @PutMapping("mark_read/{id}")
    public ResponseJson markRead(@PathVariable("id") Long id, @NotNull Integer has_read, @NotNull @Range(min = 1, max = 3) Integer notify_type) {
        if (notify_type == 1) {
            return notifyCustomerAuthService.update(NotifyCustomerAuth.builder().has_read(has_read).id(id).build());
        } else if (notify_type == 2) {
            return notifyCustomerReleaseService.update(NotifyCustomerRelease.builder().has_read(has_read).id(id).build());
        } else {
            return notifyKpiService.update(NotifyKpi.builder().has_read(has_read).id(id).build());
        }
    }
}
