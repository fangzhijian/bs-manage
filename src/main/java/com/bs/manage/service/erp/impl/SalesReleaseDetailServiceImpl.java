package com.bs.manage.service.erp.impl;

import com.bs.manage.mapper.erp.SalesReleaseDetailMapper;
import com.bs.manage.model.bean.erp.SalesReleaseDetail;
import com.bs.manage.service.common.impl.CommonServiceImpl;
import com.bs.manage.service.erp.SalesReleaseDetailService;
import org.springframework.stereotype.Service;

/**
 * 2023/4/17 16:51
 * fzj
 */
@Service
public class SalesReleaseDetailServiceImpl extends CommonServiceImpl<SalesReleaseDetail> implements SalesReleaseDetailService {

    private final SalesReleaseDetailMapper salesReleaseDetailMapper;

    public SalesReleaseDetailServiceImpl(SalesReleaseDetailMapper salesReleaseDetailMapper) {
        this.salesReleaseDetailMapper = salesReleaseDetailMapper;
    }

    @Override
    public void afterPropertiesSet() {
        setCommonMapper(salesReleaseDetailMapper);
    }
}
