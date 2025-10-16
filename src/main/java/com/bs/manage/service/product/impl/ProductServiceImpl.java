package com.bs.manage.service.product.impl;

import com.bs.manage.constant.RedisConstants;
import com.bs.manage.exception.MyRunException;
import com.bs.manage.mapper.product.ProductMapper;
import com.bs.manage.model.bean.product.Product;
import com.bs.manage.model.bean.product.ProductProject;
import com.bs.manage.model.json.Page;
import com.bs.manage.model.json.ResponseJson;
import com.bs.manage.model.param.product.ProductSearchParam;
import com.bs.manage.service.common.impl.CommonServiceImpl;
import com.bs.manage.service.console.DateReportProductService;
import com.bs.manage.service.product.ProductProjectService;
import com.bs.manage.service.product.ProductService;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 2020/3/2 11:01
 * fzj
 */
@Service
public class ProductServiceImpl extends CommonServiceImpl<Product> implements ProductService {

    private final ProductMapper productMapper;
    private final ProductProjectService productProjectService;
    private final DateReportProductService dateReportProductService;

    public ProductServiceImpl(ProductMapper productMapper, ProductProjectService productProjectService, DateReportProductService dateReportProductService) {
        this.productMapper = productMapper;
        this.productProjectService = productProjectService;
        this.dateReportProductService = dateReportProductService;
    }


    @Override
    public void afterPropertiesSet() {
        setCommonMapper(productMapper);
    }

    @Override
    @Transactional
    public ResponseJson insert(Product bean) {
        intiProduct(bean);
        return super.insert(bean);
    }

    @Override
    @Transactional
    public ResponseJson delete(Long id) {
        if (dateReportProductService.existProduct(id)) {
            return ResponseJson.fail("产品已产生业绩,无法删除");
        }
        return super.delete(id);
    }

    @Override
    @Transactional
    public ResponseJson update(Product bean) {
        intiProduct(bean);
        return super.update(bean);
    }

    @Override
    @Cacheable(cacheNames = RedisConstants.PRODUCT)
    public List<Product> getAll() {
        return super.getAll();
    }

    private void intiProduct(Product bean) {
        ProductProject productProject = productProjectService.getById(bean.getProduct_project_id());
        if (productProject == null || productProject.getProduct_brand_id() == null) {
            throw new MyRunException("项目不存在");
        }
        bean.setProduct_brand_id(productProject.getProduct_brand_id());
    }

    @Override
    public ResponseJson getByPage(ProductSearchParam param) {
        Page<Product> page = new Page<>();
        int count = productMapper.countByPage(param);
        page.setTotal(count);
        if (count > 0) {
            page.setItems(productMapper.getByPage(param));
        }
        return ResponseJson.success(page);
    }

    /**
     * 修改项目下面的品牌id
     *
     * @param brandId   品牌id
     * @param projectId 项目id
     */
    @Override
    public void updateAsBrandId(Long brandId, Long projectId) {
        productMapper.updateAsBrandId(brandId, projectId);
    }
}
