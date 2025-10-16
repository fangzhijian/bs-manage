package com.bs.manage.service.kpi;

import com.bs.manage.model.bean.kpi.KpiContent;
import com.bs.manage.service.common.CommonService;

/**
 * 2020/6/15 16:32
 * fzj
 */
public interface KpiContentService extends CommonService<KpiContent> {

    void deleteByKpiId(Long kpiId);

}
