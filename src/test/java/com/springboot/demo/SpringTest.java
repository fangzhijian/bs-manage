package com.springboot.demo;

import com.alibaba.fastjson2.JSON;
import com.springboot.demo.config.ManageApplication;
import com.springboot.demo.model.TestData;
import com.springboot.demo.service.TestDataService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;

/**
 * 2020/6/2 10:27
 * fzj
 */
@SpringBootTest(classes = ManageApplication.class)
@Slf4j
public class SpringTest {

    @Resource
    private TestDataService testDataService;
    @Test
    public void test() {
        TestData testData = new TestData();
        testData.setItems(List.of("a","e","f"));
        testDataService.save(testData);
        TestData byId = testDataService.getById(testData.getId());
        System.out.println(JSON.toJSONString(byId));
    }

}
