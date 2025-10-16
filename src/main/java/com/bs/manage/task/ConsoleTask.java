package com.bs.manage.task;

import com.bs.manage.code.CodeCaption;
import com.bs.manage.constant.Constants;
import com.bs.manage.model.bean.account.User;
import com.bs.manage.model.bean.configures.Configures;
import com.bs.manage.model.bean.console.BussProcessSummary;
import com.bs.manage.model.bean.console.IdAndCount;
import com.bs.manage.model.bean.customer.Customer;
import com.bs.manage.model.bean.customer.CustomerCategory;
import com.bs.manage.model.bean.customer.CustomerRelease;
import com.bs.manage.service.account.UserService;
import com.bs.manage.service.configures.ConfiguresService;
import com.bs.manage.service.console.BussProcessSummaryService;
import com.bs.manage.service.console.DateReportService;
import com.bs.manage.service.customer.CustomerCategoryService;
import com.bs.manage.service.customer.CustomerService;
import com.bs.manage.until.DateUtil;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 2020/3/13 17:59
 * fzj
 * 控制台仪表盘的统计任务
 */
@Component
public class ConsoleTask {

    private final UserService userService;
    private final CustomerService customerService;
    private final ConfiguresService configuresService;
    private final DateReportService dateReportService;
    private final CustomerCategoryService customerCategoryService;
    private final BussProcessSummaryService bussProcessSummaryService;

    public ConsoleTask(UserService userService, CustomerService customerService, ConfiguresService configuresService, DateReportService dateReportService,
                       CustomerCategoryService customerCategoryService, BussProcessSummaryService bussProcessSummaryService) {
        this.userService = userService;
        this.customerService = customerService;
        this.configuresService = configuresService;
        this.dateReportService = dateReportService;
        this.customerCategoryService = customerCategoryService;
        this.bussProcessSummaryService = bussProcessSummaryService;
    }


    /**
     * 客户活跃度
     */
    @Scheduled(cron = "0 30 0 * * ?")
    @Async
    public void active() {
        List<Customer> updateCustomerList = new ArrayList<>();

        //修改客户的活跃度
        LocalDateTime now = LocalDateTime.now();
        int count = customerService.countByActiveCustomerDateReport();
        int limit = 1000;
        int order = count / limit + 1;
        for (int i = 0; i < order; i++) {
            //条数多时提前批量更新客户活跃度
            if (updateCustomerList.size() >= 500) {
                customerService.updateBatch(updateCustomerList);
                updateCustomerList.clear();
            }

            List<Customer> customerList = customerService.getByActiveCustomerDateReport(i * limit, limit);
            for (Customer customer : customerList) {
                int activeness;
                long days;
                if (DateUtil.NULL.equals(customer.getNear_deal_time()) || DateUtil.NULL.equals(customer.getPrev_deal_time())) {
                    days = Duration.between(customer.getCreated_at(), now).toDays();
                    if (days <= 90) {
                        activeness = CodeCaption.ACTIVENESS_NEW;
                    } else if (days <= 180) {
                        activeness = CodeCaption.ACTIVENESS_INACTIVE;
                    } else {
                        activeness = CodeCaption.ACTIVENESS_SLEEP;
                    }
                } else {
                    days = Duration.between(customer.getCreated_at(), customer.getNear_deal_time()).toDays();
                    if (days <= 90) {
                        days = Duration.between(customer.getPrev_deal_time(), customer.getNear_deal_time()).toDays();
                        if (days <= 90) {
                            activeness = CodeCaption.ACTIVENESS_ACTIVE;
                        } else if (days <= 180) {
                            activeness = CodeCaption.ACTIVENESS_INACTIVE;
                        } else {
                            activeness = CodeCaption.ACTIVENESS_SLEEP;
                        }
                    } else if (days <= 180) {
                        activeness = CodeCaption.ACTIVENESS_INACTIVE;
                    } else {
                        activeness = CodeCaption.ACTIVENESS_SLEEP;
                    }
                }
                if (!customer.getActiveness().equals(activeness)) {
                    updateCustomerList.add(Customer.builder().id(customer.getId()).activeness(activeness).build());
                }
            }

        }

        //批量更新客户活跃度
        if (updateCustomerList.size() > 0) {
            customerService.updateBatch(updateCustomerList);
        }
    }

    /**
     * 统计覆盖数、拜访数、成交数、活跃数
     * 运行时间在active()方法前 ,以统计最新的活跃数
     */
    @Scheduled(cron = "0 0 1 * * ?")
    @Async
    public void coverToActive() {
        LocalDateTime now = LocalDateTime.now();
        int month;
        if (now.getDayOfMonth() == 1){
            month = Integer.parseInt(now.minusMonths(1).format(DateUtil.yyyyMM_FORMATTER));
        }else {
            month = Integer.parseInt(now.format(DateUtil.yyyyMM_FORMATTER));
        }
        List<User> userList = userService.getAllBySelectKey(User.builder().role(CodeCaption.ROLE_SALE).build());
        List<CustomerCategory> categories = customerCategoryService.getAllBySelectKey(CustomerCategory.builder().parent_id(0L).build());
        //获取覆盖数、拜访数、成交数、活跃数
        List<IdAndCount> count_cover = dateReportService.summaryByType(1, month);
        List<IdAndCount> count_visit = dateReportService.summaryByType(2, month);
        List<IdAndCount> count_deal = dateReportService.summaryByType(3, month);
        List<IdAndCount> count_active = dateReportService.summaryByType(4, month);
        List<BussProcessSummary> summaryList = new ArrayList<>();
        for (CustomerCategory category : categories) {
            for (User user : userList) {
                if (user.getTeam_id() == null) {
                    //销售专员需要有团队,没团队的可能是刚刚建的账号
                    continue;
                }
                //别更新释放的数量,释放数量在其他定时统计中
                BussProcessSummary summary = BussProcessSummary.builder().customer_category_id(category.getId()).user_id(user.getId()).team_id(user.getTeam_id()).summary_month(month)
                        .cover_s(0).cover_t(0).cover_y(0).cover_c(0)
                        .visit_s(0).visit_t(0).visit_y(0).visit_c(0)
                        .deal_s(0).deal_t(0).deal_y(0).deal_c(0)
                        .active_s(0).active_t(0).active_y(0).active_c(0).build();
                summaryList.add(summary);
                for (IdAndCount count : count_cover) {
                    if ("S".equals(count.getCustomer_gradation()) && user.getId().equals(count.getUser_id()) && category.getId().equals(count.getCustomer_category_id())) {
                        summary.setCover_s(count.getTotal());
                    } else if ("T".equals(count.getCustomer_gradation()) && user.getId().equals(count.getUser_id()) && category.getId().equals(count.getCustomer_category_id())) {
                        summary.setCover_t(count.getTotal());
                    } else if ("Y".equals(count.getCustomer_gradation()) && user.getId().equals(count.getUser_id()) && category.getId().equals(count.getCustomer_category_id())) {
                        summary.setCover_y(count.getTotal());
                    } else if ("C".equals(count.getCustomer_gradation()) && user.getId().equals(count.getUser_id()) && category.getId().equals(count.getCustomer_category_id())) {
                        summary.setCover_c(count.getTotal());
                    }
                }
                for (IdAndCount count : count_visit) {
                    if ("S".equals(count.getCustomer_gradation()) && user.getId().equals(count.getUser_id()) && category.getId().equals(count.getCustomer_category_id())) {
                        summary.setVisit_s(count.getTotal());
                    } else if ("T".equals(count.getCustomer_gradation()) && user.getId().equals(count.getUser_id()) && category.getId().equals(count.getCustomer_category_id())) {
                        summary.setVisit_t(count.getTotal());
                    } else if ("Y".equals(count.getCustomer_gradation()) && user.getId().equals(count.getUser_id()) && category.getId().equals(count.getCustomer_category_id())) {
                        summary.setVisit_y(count.getTotal());
                    } else if ("C".equals(count.getCustomer_gradation()) && user.getId().equals(count.getUser_id()) && category.getId().equals(count.getCustomer_category_id())) {
                        summary.setVisit_c(count.getTotal());
                    }
                }
                for (IdAndCount count : count_deal) {
                    if ("S".equals(count.getCustomer_gradation()) && user.getId().equals(count.getUser_id()) && category.getId().equals(count.getCustomer_category_id())) {
                        summary.setDeal_s(count.getTotal());
                    } else if ("T".equals(count.getCustomer_gradation()) && user.getId().equals(count.getUser_id()) && category.getId().equals(count.getCustomer_category_id())) {
                        summary.setDeal_t(count.getTotal());
                    } else if ("Y".equals(count.getCustomer_gradation()) && user.getId().equals(count.getUser_id()) && category.getId().equals(count.getCustomer_category_id())) {
                        summary.setDeal_y(count.getTotal());
                    } else if ("C".equals(count.getCustomer_gradation()) && user.getId().equals(count.getUser_id()) && category.getId().equals(count.getCustomer_category_id())) {
                        summary.setDeal_c(count.getTotal());
                    }
                }
                for (IdAndCount count : count_active) {
                    if ("S".equals(count.getCustomer_gradation()) && user.getId().equals(count.getUser_id()) && category.getId().equals(count.getCustomer_category_id())) {
                        summary.setActive_s(count.getTotal());
                    } else if ("T".equals(count.getCustomer_gradation()) && user.getId().equals(count.getUser_id()) && category.getId().equals(count.getCustomer_category_id())) {
                        summary.setActive_t(count.getTotal());
                    } else if ("Y".equals(count.getCustomer_gradation()) && user.getId().equals(count.getUser_id()) && category.getId().equals(count.getCustomer_category_id())) {
                        summary.setActive_y(count.getTotal());
                    } else if ("C".equals(count.getCustomer_gradation()) && user.getId().equals(count.getUser_id()) && category.getId().equals(count.getCustomer_category_id())) {
                        summary.setActive_c(count.getTotal());
                    }
                }
            }
        }

        //统计覆盖数、拜访数、成交数、活跃数的后续操作
        bussProcessSummaryService.afterSummary(summaryList);


    }

    /**
     * 客户释放,并统计释放数
     * 运行时间需跑在 coverToActive()方法后面,以不用判断存在,直接更新
     */
    @Scheduled(cron = "0 0 2 * * ?")
    @Async
    public void release() {
        List<Configures> configuresList = configuresService.getAll();
        List<CustomerRelease> releaseList = new ArrayList<>();
        for (Configures configures : configuresList) {
            if (configures.getCode().equals(Constants.CONFIG_RELEASE_VISIT)) {
                String[] values = configures.getCode_value().split(",");
                releaseList.addAll(customerService.getByReleaseCustomer(1, "S", Integer.parseInt(values[0].trim())));
                releaseList.addAll(customerService.getByReleaseCustomer(1, "T", Integer.parseInt(values[1].trim())));
                releaseList.addAll(customerService.getByReleaseCustomer(1, "Y", Integer.parseInt(values[2].trim())));
                releaseList.addAll(customerService.getByReleaseCustomer(1, "C", Integer.parseInt(values[3].trim())));
            } else if (configures.getCode().equals(Constants.CONFIG_RELEASE_DEAL)) {
                String[] values = configures.getCode_value().split(",");
                releaseList.addAll(customerService.getByReleaseCustomer(2, "S", Integer.parseInt(values[0].trim())));
                releaseList.addAll(customerService.getByReleaseCustomer(2, "T", Integer.parseInt(values[1].trim())));
                releaseList.addAll(customerService.getByReleaseCustomer(2, "Y", Integer.parseInt(values[2].trim())));
                releaseList.addAll(customerService.getByReleaseCustomer(2, "C", Integer.parseInt(values[3].trim())));
            } else if (configures.getCode().equals(Constants.CONFIG_RELEASE_REPEAT)) {
                String[] values = configures.getCode_value().split(",");
                releaseList.addAll(customerService.getByReleaseCustomer(3, "S", Integer.parseInt(values[0].trim())));
                releaseList.addAll(customerService.getByReleaseCustomer(3, "T", Integer.parseInt(values[1].trim())));
                releaseList.addAll(customerService.getByReleaseCustomer(3, "Y", Integer.parseInt(values[2].trim())));
                releaseList.addAll(customerService.getByReleaseCustomer(3, "C", Integer.parseInt(values[3].trim())));
            }
        }

        //客户释放的后续操作
        customerService.afterRelease(releaseList);

    }


}
