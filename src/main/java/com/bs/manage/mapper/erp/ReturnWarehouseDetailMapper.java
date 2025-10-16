package com.bs.manage.mapper.erp;

import com.bs.manage.mapper.common.CommonMapper;
import com.bs.manage.model.bean.erp.ReturnWarehouseDetail;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 2023/4/18 16:00
 * fzj
 */
@Repository
public interface ReturnWarehouseDetailMapper extends CommonMapper<ReturnWarehouseDetail> {

    //获取直播订单
    List<ReturnWarehouseDetail> getLiveOrder(Integer id);

}
