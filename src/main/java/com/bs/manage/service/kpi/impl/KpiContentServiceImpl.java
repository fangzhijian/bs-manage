package com.bs.manage.service.kpi.impl;

import com.bs.manage.mapper.kpi.KpiContentMapper;
import com.bs.manage.model.bean.kpi.KpiContent;
import com.bs.manage.service.common.impl.CommonServiceImpl;
import com.bs.manage.service.kpi.KpiContentService;
import org.springframework.stereotype.Service;

/**
 * 2020/6/15 16:33
 * fzj
 */
@Service
public class KpiContentServiceImpl extends CommonServiceImpl<KpiContent> implements KpiContentService {

    private final KpiContentMapper kpiContentMapper;

    public KpiContentServiceImpl(KpiContentMapper kpiContentMapper) {
        this.kpiContentMapper = kpiContentMapper;
    }

    @Override
    public void afterPropertiesSet() {
        setCommonMapper(kpiContentMapper);
    }

    @Override
    public void deleteByKpiId(Long kpiId) {
        kpiContentMapper.deleteByKpiId(kpiId);
    }
}
