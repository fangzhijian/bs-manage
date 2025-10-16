package com.bs.manage.mapper.kpi;

import com.bs.manage.mapper.common.CommonMapper;
import com.bs.manage.model.bean.kpi.KpiContent;
import org.springframework.stereotype.Repository;

/**
 * 2020/6/15 16:31
 * fzj
 */
@Repository
public interface KpiContentMapper extends CommonMapper<KpiContent> {

    void deleteByKpiId(Long kpiId);

}
