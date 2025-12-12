package com.springboot.demo.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.springboot.demo.model.TestData;
import com.springboot.demo.service.TestDataService;
import com.springboot.demo.mapper.TestDataMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
* {@code @description} 针对表【test_data】的数据库操作Service实现
* @author fangzhijian
* @since 2025-12-05 12:41:20
*/
@Service
@Slf4j
public class TestDataServiceImpl extends ServiceImpl<TestDataMapper, TestData> implements TestDataService{

}




