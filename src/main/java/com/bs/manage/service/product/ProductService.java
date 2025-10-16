package com.bs.manage.service.product;

import com.bs.manage.model.bean.product.Product;
import com.bs.manage.model.json.ResponseJson;
import com.bs.manage.model.param.product.ProductSearchParam;
import com.bs.manage.service.common.CommonService;
import org.apache.ibatis.annotations.Param;

/**
 * 2020/3/2 10:59
 * fzj
 */
public interface ProductService extends CommonService<Product> {

    ResponseJson getByPage(ProductSearchParam param);

    /**
     * 修改项目下面的品牌id
     *
     * @param brandId   品牌id
     * @param projectId 项目id
     */
    void updateAsBrandId(@Param("brandId") Long brandId, @Param("projectId") Long projectId);

}
