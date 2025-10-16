package com.bs.manage.mapper.console;

import com.bs.manage.mapper.common.CommonMapper;
import com.bs.manage.model.bean.console.DateReportProduct;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 2020/3/4 12:01
 * fzj
 */
@Repository
public interface DateReportProductMapper extends CommonMapper<DateReportProduct> {

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
