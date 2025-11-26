package com.bs.manage.task;

import com.bs.manage.service.account.UserService;
import com.bs.manage.service.customer.CustomerShopAuthService;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;


/**
 * 2020/4/13 12:00
 * fzj
 * 消息
 */
//@Component
public class NotifyTask {

    private final UserService userService;
    private final CustomerShopAuthService customerAuthService;


    public NotifyTask(UserService userService, CustomerShopAuthService customerAuthService) {
        this.userService = userService;
        this.customerAuthService = customerAuthService;
    }


    /**
     * 新增客户释放提醒,每天重新写入数据,需在客户释放定时任务之后
     *
     * @see com.bs.manage.task.ConsoleTask release()
     */
    @Scheduled(cron = "0 0 3 * * ?")
    @Async
    public void customerRelease() {
        userService.insertToNotifyCustomerRelease();
    }

    /**
     * 新增客户授权到期提醒
     */
    @Scheduled(cron = "0 0 1 * * ?")
    @Async
    public void customerAuth() {
        customerAuthService.insertToNotifyCustomerAuth();
    }
}
