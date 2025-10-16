package com.bs.manage.mapper.product;

import com.bs.manage.mapper.common.CommonMapper;
import com.bs.manage.model.bean.product.Product;
import com.bs.manage.model.param.product.ProductSearchParam;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 2020/3/2 10:54
 * fzj
 */
@Repository
public interface ProductMapper extends CommonMapper<Product> {

    List<Product> getByPage(ProductSearchParam param);

    Integer countByPage(ProductSearchParam param);

    /**
     * 修改项目下面的品牌id
     *
     * @param brandId   品牌id
     * @param projectId 项目id
     * @return 修改结果
     */
    Integer updateAsBrandId(@Param("brandId") Long brandId, @Param("projectId") Long projectId);

}
