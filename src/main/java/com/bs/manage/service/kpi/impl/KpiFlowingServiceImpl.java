package com.bs.manage.service.kpi.impl;

import com.bs.manage.mapper.kpi.KpiFlowingMapper;
import com.bs.manage.model.bean.kpi.KpiFlowing;
import com.bs.manage.service.common.impl.CommonServiceImpl;
import com.bs.manage.service.kpi.KpiFlowingService;
import org.springframework.stereotype.Service;

/**
 * 2020/6/15 16:35
 * fzj
 */
@Service
public class KpiFlowingServiceImpl extends CommonServiceImpl<KpiFlowing> implements KpiFlowingService {

    private final KpiFlowingMapper kpiFlowingMapper;

    public KpiFlowingServiceImpl(KpiFlowingMapper kpiFlowingMapper) {
        this.kpiFlowingMapper = kpiFlowingMapper;
    }

    @Override
    public void afterPropertiesSet() {
        setCommonMapper(kpiFlowingMapper);
    }
}
