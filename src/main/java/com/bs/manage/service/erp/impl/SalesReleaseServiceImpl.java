package com.bs.manage.service.erp.impl;

import com.bs.manage.mapper.erp.SalesReleaseMapper;
import com.bs.manage.model.bean.erp.SalesRelease;
import com.bs.manage.service.common.impl.CommonServiceImpl;
import com.bs.manage.service.erp.SalesReleaseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 2023/4/6 14:32
 * fzj
 */
@Service
@Slf4j
public class SalesReleaseServiceImpl extends CommonServiceImpl<SalesRelease> implements SalesReleaseService{

    private final SalesReleaseMapper salesReleaseMapper;

    public SalesReleaseServiceImpl(SalesReleaseMapper salesReleaseMapper) {
        this.salesReleaseMapper = salesReleaseMapper;
    }

    @Override
    public void afterPropertiesSet() {
        setCommonMapper(salesReleaseMapper);
    }
}
