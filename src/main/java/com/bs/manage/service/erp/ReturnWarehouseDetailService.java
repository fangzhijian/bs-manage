package com.bs.manage.service.erp;

import com.bs.manage.model.bean.erp.ReturnWarehouseDetail;
import com.bs.manage.service.common.CommonService;

import java.util.List;

/**
 * 2023/4/18 16:01
 * fzj
 */
public interface ReturnWarehouseDetailService extends CommonService<ReturnWarehouseDetail> {

    //获取直播订单
    List<ReturnWarehouseDetail> getLiveOrder(Integer id);

}
