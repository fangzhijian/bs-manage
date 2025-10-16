package com.bs.manage;

import com.bs.manage.config.ManageApplication;
import com.bs.manage.constant.RedisConstants;
import com.bs.manage.intercepter.UserToken;
import com.bs.manage.mapper.customer.CustomerBindLogMapper;
import com.bs.manage.mapper.customer.CustomerMapper;
import com.bs.manage.model.bean.account.User;
import com.bs.manage.model.bean.customer.Customer;
import com.bs.manage.model.bean.customer.CustomerBindLog;
import com.bs.manage.service.account.UserService;
import com.bs.manage.service.customer.CustomerBindLogService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@SpringBootTest(classes = ManageApplication.class)
public class ManageApplicationTests {

    @Autowired
    private CustomerMapper customerMapper;
    @Autowired
    private CustomerBindLogMapper customerBindLogMapper;
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    @Autowired
    private UserService userService;
    @Autowired
    private UserToken userToken;
    @Autowired
    private CustomerBindLogService customerBindLogService;



    @Test
    public void contextLoads() {
        List<CustomerBindLog> nearBindForCustomerIds = customerBindLogService.getNearBindForCustomerIds(Arrays.asList(3194L,3195L)); //查询最近的绑定者
        System.out.println(nearBindForCustomerIds);
    }

}
