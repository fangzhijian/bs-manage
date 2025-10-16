package com.bs.manage.service.console;

import com.bs.manage.model.bean.console.BussProcessSummary;
import com.bs.manage.service.common.CommonService;

import java.util.List;

/**
 * 2020/3/12 19:12
 * fzj
 */
public interface BussProcessSummaryService extends CommonService<BussProcessSummary> {

    /**
     * 根据唯一所有批量更新
     *
     * @param list 业务过程统计集合
     */
    void updateBatchByUnique(List<BussProcessSummary> list);

    /**
     * 统计覆盖数、拜访数、成交数、活跃数的后续操
     *
     * @param list 业务过程统计集合
     */
    void afterSummary(List<BussProcessSummary> list);

}
