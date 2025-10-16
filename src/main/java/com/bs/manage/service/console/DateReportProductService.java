package com.bs.manage.service.console;

import com.bs.manage.model.bean.console.DateReportProduct;
import com.bs.manage.service.common.CommonService;

/**
 * 2020/3/13 16:43
 * fzj
 */
public interface DateReportProductService extends CommonService<DateReportProduct> {

    /**
     * 根据日报id删除
     *
     * @param dateReportId 日报id
     * @return 删除结果
     */
    Integer deleteByDateReportId(Long dateReportId);

    /**
     * 日报中是否含有产品
     *
     * @param product_id 产品id
     * @return 含有结果
     */
    Boolean existProduct(Long product_id);

}
