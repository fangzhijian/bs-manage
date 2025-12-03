package com.springboot.demo;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.springboot.demo.config.ManageApplication;
import com.springboot.demo.model.bean.account.User;
import com.springboot.demo.service.user.UserService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * 2020/6/2 10:27
 * fzj
 */
@SpringBootTest(classes = ManageApplication.class)
@Slf4j
public class SpringTest {

    @Resource
    private UserService userService;

    @Test
    public void test() {

    }

}
