package com.bs.manage.task;

import com.bs.manage.code.CodeCaption;
import com.bs.manage.constant.Constants;
import com.bs.manage.model.bean.account.User;
import com.bs.manage.model.bean.kpi.KpiMain;
import com.bs.manage.model.bean.notify.NotifyKpi;
import com.bs.manage.service.account.UserService;
import com.bs.manage.service.common.DingDingService;
import com.bs.manage.service.kpi.KpiMainService;
import com.bs.manage.service.notify.NotifyKpiService;
import com.bs.manage.until.DateUtil;
import com.bs.manage.until.MailUtil;
import com.bs.manage.until.NumberUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 2020/6/15 10:26
 * fzj
 */
//@Component
public class KpiTask {

    @Value("${webUrl}")
    private String DEFAULT_DING_MSG_URL;
    @Value("${spring.profiles.active}")
    private String active;

    private final UserService userService;
    private final DingDingService dingDingService;
    private final NotifyKpiService notifyKpiService;
    private final KpiMainService kpiMainService;

    public KpiTask(UserService userService, DingDingService dingDingService, NotifyKpiService notifyKpiService, KpiMainService kpiMainService) {
        this.userService = userService;
        this.dingDingService = dingDingService;
        this.notifyKpiService = notifyKpiService;
        this.kpiMainService = kpiMainService;
    }


    /**
     * 提醒kpi还没制定的人员
     */
    @Scheduled(cron = "0 0 9 5 * ?")
    @Async
    public void unMakeKpi() {
        if (!"prod".equals(active)) {
            return;
        }
        int month = Integer.parseInt(LocalDate.now().format(DateUtil.yyyyMM_FORMATTER));
        List<KpiMain> kpiMains = kpiMainService.getAllBySelectKey(KpiMain.builder().month(month).build());
        List<User> users = userService.getAllBySelectKey(User.builder().status(CodeCaption.STATUS_OK).build());
        List<User> unMakeUsers = new ArrayList<>();
        List<String> ddUserIds = new ArrayList<>();
        List<String> emails = new ArrayList<>();
        for (User user : users) {
            if (NumberUtil.isNotBlank(user.getParent_id())) {
                boolean hasMake = false;
                for (KpiMain kpiMain : kpiMains) {
                    if (kpiMain.getUser_id().equals(user.getId())) {
                        hasMake = true;
                        break;
                    }
                }
                if (!hasMake) {
                    ddUserIds.add(user.getDd_user_id());
                    emails.add(user.getPosition());
                    unMakeUsers.add(user);
                }
            }
        }
        if (unMakeUsers.size() > 0) {
            //给成员发送钉钉工作通知
            dingDingService.notifyForWork(ddUserIds, Constants.DEFAULT_DING_TITLE,
                    String.format("请即时联系你的主管制定%s月的绩效方案", month), DEFAULT_DING_MSG_URL, Constants.DEFAULT_DING_PIC_URL);
            MailUtil.sendMail("绩效考核", String.format("请即时联系你的主管制定%s月的绩效方案", month), emails);

            //上级发送邮件和钉钉
            Map<Long, List<User>> collect = unMakeUsers.stream().collect(Collectors.groupingBy(User::getParent_id));
            for (Long key : collect.keySet()) {
                Optional<User> optionalUser = users.stream().filter(x -> x.getId().equals(key)).findFirst();
                if (optionalUser.isPresent()) {
                    User parentUser = optionalUser.get();
                    String names = collect.get(key).stream().map(User::getName).collect(Collectors.joining("、"));
                    String message = "你还未对成员:".concat(names).concat("制定绩效方案,请立刻处理");

                    MailUtil.sendMail("绩效考核", message, Collections.singletonList(parentUser.getPosition()));
                    dingDingService.notifyForWork(Collections.singletonList(parentUser.getDd_user_id()), Constants.DEFAULT_DING_TITLE, message, DEFAULT_DING_MSG_URL, Constants.DEFAULT_DING_PIC_URL);
                }
            }
        }
    }


    /**
     * 提醒完成自评
     */
    @Scheduled(cron = "0 0 9 7 * ?")
    @Async
    public void oneselfScore() {
        if (!"prod".equals(active)) {
            return;
        }
        int month = Integer.parseInt(LocalDate.now().minusMonths(1).format(DateUtil.yyyyMM_FORMATTER));
        List<KpiMain> kpiMains = kpiMainService.getAllBySelectKey(KpiMain.builder().month(month).build());

        List<Long> userIds = new ArrayList<>();
        List<NotifyKpi> notifyKpiList = new ArrayList<>();
        for (KpiMain kpiMain : kpiMains) {
            if (CodeCaption.KPI_STATUS_5 != kpiMain.getStatus() && CodeCaption.KPI_STATUS_6 != kpiMain.getStatus() &&
                    CodeCaption.KPI_STATUS_8 != kpiMain.getStatus() && CodeCaption.KPI_STATUS_9 != kpiMain.getStatus()) {
                userIds.add(kpiMain.getUser_id());
            }
            NotifyKpi notifyKpi = NotifyKpi.builder().kpi_id(kpiMain.getId()).notify_user(kpiMain.getUser_id()).has_read(CodeCaption.FALSE).text("请于今日完成上月绩效的自评输入").build();
            notifyKpiList.add(notifyKpi);
        }
        List<String> userIdList = userService.getByIds(userIds).stream().map(User::getDd_user_id).collect(Collectors.toList());

        if (userIdList.size() > 0) {
            //发送钉钉工作通知
            dingDingService.notifyForWork(userIdList, Constants.DEFAULT_DING_TITLE, "请于今日登录云系统完成上月绩效的自评输入", DEFAULT_DING_MSG_URL, Constants.DEFAULT_DING_PIC_URL);
            //系统消息通知
            notifyKpiService.insertBatch(notifyKpiList);
        }

    }


}
