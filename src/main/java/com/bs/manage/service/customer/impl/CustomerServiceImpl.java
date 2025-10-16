package com.bs.manage.service.customer.impl;

import com.bs.manage.code.CodeCaption;
import com.bs.manage.code.PubCode;
import com.bs.manage.exception.MyRunException;
import com.bs.manage.intercepter.UserToken;
import com.bs.manage.mapper.customer.CustomerMapper;
import com.bs.manage.model.bean.account.User;
import com.bs.manage.model.bean.account.UserTags;
import com.bs.manage.model.bean.common.CommonModel;
import com.bs.manage.model.bean.common.LabelModel;
import com.bs.manage.model.bean.console.BussProcessSummary;
import com.bs.manage.model.bean.console.DateReport;
import com.bs.manage.model.bean.customer.Customer;
import com.bs.manage.model.bean.customer.CustomerShop;
import com.bs.manage.model.bean.customer.CustomerShopAuth;
import com.bs.manage.model.bean.customer.CustomerBindLog;
import com.bs.manage.model.bean.customer.CustomerCategory;
import com.bs.manage.model.bean.customer.CustomerLevel;
import com.bs.manage.model.bean.customer.CustomerRelease;
import com.bs.manage.model.bean.customer.CustomerShopContact;
import com.bs.manage.model.bean.customer.CustomerTagRelation;
import com.bs.manage.model.bean.customer.ProductCategory;
import com.bs.manage.model.bean.customer.ProductNation;
import com.bs.manage.model.json.ExcelData;
import com.bs.manage.model.json.MultiLabel;
import com.bs.manage.model.json.Page;
import com.bs.manage.model.json.ResponseJson;
import com.bs.manage.model.bean.customer.CustomerContact;
import com.bs.manage.model.param.console.DateReportSearchParam;
import com.bs.manage.model.param.customer.BatchAttachParam;
import com.bs.manage.model.param.customer.CustomerSearchParam;
import com.bs.manage.service.console.BussProcessSummaryService;
import com.bs.manage.service.console.DateReportService;
import com.bs.manage.service.customer.CustomerShopAuthService;
import com.bs.manage.service.customer.CustomerBindLogService;
import com.bs.manage.service.customer.CustomerCategoryService;
import com.bs.manage.service.customer.CustomerContactService;
import com.bs.manage.service.customer.CustomerLevelService;
import com.bs.manage.service.customer.CustomerReleaseService;
import com.bs.manage.service.customer.CustomerService;
import com.bs.manage.service.account.UserService;
import com.bs.manage.service.account.UserTagsService;
import com.bs.manage.service.common.impl.CommonServiceImpl;
import com.bs.manage.service.customer.CustomerShopContactService;
import com.bs.manage.service.customer.CustomerShopService;
import com.bs.manage.service.customer.CustomerTagRelationService;
import com.bs.manage.service.customer.ProductCategoryService;
import com.bs.manage.service.customer.ProductNationService;
import com.bs.manage.until.CodeUtil;
import com.bs.manage.until.DateUtil;
import com.bs.manage.until.ExcelUtils;
import com.bs.manage.until.NumberUtil;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.springframework.beans.BeanUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * 2020/2/27 10:44
 * fzj
 */
@Slf4j
@Service
public class CustomerServiceImpl extends CommonServiceImpl<Customer> implements CustomerService {

    private final CustomerMapper customerMapper;
    private final CustomerCategoryService customerCategoryService;
    private final CustomerContactService customerContactService;
    private final ProductCategoryService productCategoryService;
    private final ProductNationService productNationService;
    private final CustomerLevelService customerLevelService;
    private final CustomerTagRelationService customerTagRelationService;
    private final UserTagsService userTagsService;
    private final CustomerShopService customerShopService;
    private final CustomerShopContactService customerShopContactService;
    private final CustomerShopAuthService customerShopAuthService;
    private final UserService userService;
    private final BussProcessSummaryService bussProcessSummaryService;
    private final CustomerReleaseService customerReleaseService;
    private final DateReportService dateReportService;
    private final CustomerBindLogService customerBindLogService;

    public CustomerServiceImpl(CustomerMapper customerMapper, CustomerCategoryService customerCategoryService, CustomerContactService customerContactService, ProductCategoryService productCategoryService, ProductNationService productNationService,
                               CustomerLevelService customerLevelService, CustomerTagRelationService customerTagRelationService, UserTagsService userTagsService, CustomerShopService customerShopService, CustomerShopContactService customerShopContactService,
                               CustomerShopAuthService customerShopAuthService, UserService userService, BussProcessSummaryService bussProcessSummaryService, CustomerReleaseService customerReleaseService, @Lazy DateReportService dateReportService, CustomerBindLogService customerBindLogService) {
        this.customerMapper = customerMapper;
        this.customerCategoryService = customerCategoryService;
        this.customerContactService = customerContactService;
        this.productCategoryService = productCategoryService;
        this.productNationService = productNationService;
        this.customerLevelService = customerLevelService;
        this.customerTagRelationService = customerTagRelationService;
        this.userTagsService = userTagsService;
        this.customerShopService = customerShopService;
        this.customerShopContactService = customerShopContactService;
        this.customerShopAuthService = customerShopAuthService;
        this.userService = userService;
        this.bussProcessSummaryService = bussProcessSummaryService;
        this.customerReleaseService = customerReleaseService;
        this.dateReportService = dateReportService;
        this.customerBindLogService = customerBindLogService;
    }


    /**
     * 查询所有需要统计活跃的客户日报数量
     *
     * @return 客户数量
     */
    @Override
    public Integer countByActiveCustomerDateReport() {
        return customerMapper.countByActiveCustomerDateReport();
    }

    /**
     * 需要统计活跃的客户和日报
     *
     * @param offset 第几条开始
     * @param limit  每页条数
     * @return 客户和日报列表
     */
    @Override
    public List<Customer> getByActiveCustomerDateReport(Integer offset, Integer limit) {
        return customerMapper.getByActiveCustomerDateReport(offset, limit);
    }

    /**
     * 需要释放的客户
     *
     * @param release_type       1-未拜访释放 2-未成交释放 3-未复购释放
     * @param customer_gradation 客户分层
     * @param left_day           待释放天数
     * @return 释放的客户
     */
    @Override
    public List<CustomerRelease> getByReleaseCustomer(Integer release_type, String customer_gradation, Integer left_day) {
        return customerMapper.getByReleaseCustomer(release_type, customer_gradation, left_day);
    }

    /**
     * 客户释放的后续操作
     *
     * @param releaseList 待释放的客户列表
     */
    @Override
    @Transactional
    public void afterRelease(List<CustomerRelease> releaseList) {
        int month = Integer.parseInt(LocalDateTime.now().format(DateUtil.yyyyMM_FORMATTER));
        List<Customer> updateList = new ArrayList<>();
        List<BussProcessSummary> processSummaryList = new ArrayList<>();
        List<CustomerRelease> customerReleaseList = new ArrayList<>();
        for (CustomerRelease customerRelease : releaseList) {
            if (CodeCaption.RELEASE_TYPE_VISIT == customerRelease.getRelease_type() && customerRelease.getLeft_day() < 1) {
                //客户跟进人id=0即无跟进人,代表释放
                updateList.add(Customer.builder().id(customerRelease.getId()).process_user_id(0L).build());

                BussProcessSummary processSummary = BussProcessSummary.builder().user_id(customerRelease.getProcess_user_id()).summary_month(month)
                        .customer_category_id(customerRelease.getCustomer_category_id()).build();
                if ("S".equals(customerRelease.getCustomer_gradation())) {
                    processSummary.setRelease_s(1);
                    processSummary.setRelease_visit_s(1);
                } else if ("T".equals(customerRelease.getCustomer_gradation())) {
                    processSummary.setRelease_t(1);
                    processSummary.setRelease_visit_t(1);
                } else if ("Y".equals(customerRelease.getCustomer_gradation())) {
                    processSummary.setRelease_y(1);
                    processSummary.setRelease_visit_y(1);
                } else if ("C".equals(customerRelease.getCustomer_gradation())) {
                    processSummary.setRelease_c(1);
                    processSummary.setRelease_visit_c(1);
                }
                processSummaryList.add(processSummary);
            } else {
                customerReleaseList.add(customerRelease);
            }

        }
        //立即释放的释放客户
        if (updateList.size() > 0) {
            this.updateBatch(updateList);
        }
        //增加释放数
        if (processSummaryList.size() > 0) {
            bussProcessSummaryService.updateBatchByUnique(processSummaryList);
        }
        //先清空待释放的客户表
        customerReleaseService.truncate();
        //再新增待释放的客户
        if (customerReleaseList.size() > 0) {
            customerReleaseService.insertBatch(customerReleaseList);
        }

    }

    /**
     * 查询客户是否已存在重复
     *
     * @param keyword 客户名称或唯一ID
     * @return 客户列表
     */
    @Override
    public ResponseJson judgeRepeat(String keyword) {
        List<CustomerShop> customerShops = customerShopService.judgeRepeat(keyword);
        List<Customer> customerList = customerMapper.judgeRepeat(keyword,customerShops);
        Page<Customer> page = new Page<>();
        page.setTotal(customerList.size());
        page.setItems(customerList);
        return ResponseJson.success(page);
    }

    @Override
    public ResponseJson getAllShop(String parentUniqueId) {
        List<CustomerShop> customerShops = customerShopService.getAllShop(parentUniqueId);
        return ResponseJson.success(customerShops);
    }

    @Override
    public ResponseJson getShopDetail(Long customer_shop_id) {
        CustomerShop customerShop = customerShopService.getShopDetail(customer_shop_id);
        if (customerShop == null) {
            return ResponseJson.fail("店铺不存在");
        }
        customerShop.setTrade_type_label(CodeUtil.getCaption(CodeCaption.TRADE_TYPE, customerShop.getTrade_type()));
        //一级、二级、三级客户属性
        List<CustomerCategory> categoryMapperAll = customerCategoryService.getAll();
        int a = 0;
        for (CustomerCategory customerCategory : categoryMapperAll) {
            if (customerCategory.getId().equals(customerShop.getCustomer_category_id())) {
                customerShop.setCustomer_category_name(customerCategory.getName());
                a++;
            }
            if (customerCategory.getId().equals(customerShop.getCustomer_category_id2())) {
                customerShop.setCustomer_category_name2(customerCategory.getName());
                a++;
            }
            if (customerCategory.getId().equals(customerShop.getCustomer_category_id3())) {
                customerShop.setCustomer_category_name3(customerCategory.getName());
                a++;
            }
            if (a >= 3) {
                break;
            }
        }

        //联系人
        List<CustomerShopContact> customerContactList = customerShopContactService.getAllBySelectKey(CustomerShopContact.builder().customer_shop_id(customer_shop_id).delete_status(0).build());
        customerShop.setCustomer_shop_contact(customerContactList);

        //授权信息
        List<CustomerShopAuth> customerAuthList = customerShopAuthService.getAllBySelectKey(CustomerShopAuth.builder().customer_shop_id(customer_shop_id).build());
        for (CustomerShopAuth customerAuth : customerAuthList) {
            customerAuth.setAuth_type_label(CodeUtil.getCaption(CodeCaption.AUTH_TYPE, customerAuth.getAuth_type()));
        }
        customerShop.setCustomer_authList(customerAuthList);
        return ResponseJson.success(customerShop);
    }

    @Override
    @Transactional
    public ResponseJson updateShop(CustomerShop bean) {

        //设置客户分层
        Customer customer = new Customer();
        BeanUtils.copyProperties(bean, customer);
        setCustomerGradation(customer);
        bean.setCustomer_gradation(customer.getCustomer_gradation());

        CustomerShop customerShop = customerShopService.getById(bean.getId());
        if (customerShop == null) {
            throw new MyRunException("店铺不存在");
        }

        bean.setUnique_id(null);
        bean.setParent_unique_id(null);
        customerShopService.update(bean);

        List<CustomerShopContact> insertList = new ArrayList<>();
        List<CustomerShopContact> updateList = new ArrayList<>();
        if (bean.getCustomer_shop_contact() != null) {
            for (CustomerShopContact customerShopContact : bean.getCustomer_shop_contact()) {
                customerShopContact.setCustomer_shop_id(customerShop.getId());
                if (NumberUtil.isBlank(customerShopContact.getId())) {
                    insertList.add(customerShopContact);
                } else {
                    updateList.add(customerShopContact);
                }
            }
            if (insertList.size() > 0) {
                customerShopContactService.insertBatch(insertList);
            }
            if (updateList.size() > 0) {
                customerShopContactService.updateBatch(updateList);
            }
        }
        return ResponseJson.success();
    }

    @Override
    @Transactional
    public ResponseJson deleteShop(Long customer_shop_id) {
        CustomerShop customerShop = customerShopService.getById(customer_shop_id);
        if (customerShop == null) {
            return ResponseJson.fail("店铺已经被删除了");
        }
        customerShopService.delete(customer_shop_id);
        Customer customer = super.getOneBySelectKey(Customer.builder().unique_id(customerShop.getParent_unique_id()).build());
        //减少店铺数
        if (customer != null) {
            super.update(Customer.builder().shop_num(-1).id(customer.getId()).build());
        }
        return ResponseJson.success();
    }

    @Override
    @Transactional
    public ResponseJson upgradeShop(Long customer_shop_id) {
        CustomerShop customerShop = customerShopService.getById(customer_shop_id);
        if (customerShop == null) {
            return ResponseJson.fail("店铺不存在");
        }
        Customer customer = super.getOneBySelectKey(Customer.builder().unique_id(customerShop.getParent_unique_id()).build());
        if (customer != null && customer.getShop_num() <= 1) {
            return ResponseJson.fail("客户已经没有更多的店铺了");
        }
        if (customerShop.getUnique_id().equals(customerShop.getParent_unique_id())) {
            return ResponseJson.fail("店铺与原客户属性类似,无法升级到新客户");
        }

        //生成和店铺属性类似的客户
        Customer insertCustomer = new Customer();
        BeanUtils.copyProperties(customerShop, insertCustomer);
        insertCustomer.setCreated_at(null);
        insertCustomer.setImportType(2);
        insertCustomer.setIsUpgrade(true);
        if (customer != null) {
            insertCustomer.setTeam_id(customer.getTeam_id());
            insertCustomer.setProcess_user_id(customer.getProcess_user_id());
        }
        List<CustomerContact> customerContacts = customerShopContactService.getAllBySelectKey(CustomerShopContact.builder().customer_shop_id(customer_shop_id).delete_status(0).build())
                .stream().map(x -> {
                    CustomerContact customerContact = new CustomerContact();
                    BeanUtils.copyProperties(x, customerContact);
                    return customerContact;
                }).collect(Collectors.toList());
        insertCustomer.setCustomer_contact(customerContacts);
        ResponseJson responseJson = this.insert(insertCustomer);
        if (responseJson.getCode() != PubCode.SUCCESS.code()) {
            return responseJson;
        }

        //更换店铺的上级唯一识别为自己的唯一识别
        customerShopService.update(CustomerShop.builder().parent_unique_id(customerShop.getUnique_id()).id(customer_shop_id).build());

        //原客户店铺数减1
        if (customer != null) {
            super.update(Customer.builder().shop_num(-1).id(customer.getId()).build());
        }
        return ResponseJson.success();
    }

    @Override
    public void releaseAllByUserId(Long userId) {
        customerMapper.releaseAllByUserId(userId);
    }

    /**
     * 设置客户分层
     *
     * @param customer 客户信息
     */
    private void setCustomerGradation(Customer customer) {
        if (StringUtils.hasText(customer.getCustomer_gradation())) {
            return;
        }
        BigDecimal trade_month = customer.getTrade_month();
        Long level_id = customer.getCustomer_level_id();

        if (!(CustomerCategory.RETAILER.equals(customer.getCustomer_category_id()) && CustomerCategory.TAO_BAO.equals(customer.getCustomer_category_id2()))) {
            if (trade_month == null) {
                throw new MyRunException("必须填写客户规模");
            }
        }
        if (CustomerCategory.TAO_BAO.equals(customer.getCustomer_category_id2()) && NumberUtil.isBlank(level_id)) {
            throw new MyRunException("淘宝客户需填写客户等级");
        }
        if (CustomerCategory.RETAILER.equals(customer.getCustomer_category_id())) {
            if (CustomerCategory.TAO_BAO.equals(customer.getCustomer_category_id2())) {
                if (level_id == 20L || level_id == 21) {
                    customer.setCustomer_gradation("S");
                } else if (level_id >= 17 && level_id <= 19) {
                    customer.setCustomer_gradation("T");
                } else if (level_id >= 11 && level_id <= 15) {
                    customer.setCustomer_gradation("Y");
                } else {
                    customer.setCustomer_gradation("C");
                }
            } else {
                if (trade_month.compareTo(new BigDecimal(500)) >= 0) {
                    customer.setCustomer_gradation("S");
                } else if (trade_month.compareTo(new BigDecimal(150)) >= 0) {
                    customer.setCustomer_gradation("T");
                } else if (trade_month.compareTo(new BigDecimal(30)) >= 0) {
                    customer.setCustomer_gradation("Y");
                } else {
                    customer.setCustomer_gradation("C");
                }
            }
        } else if (CustomerCategory.DEALER.equals(customer.getCustomer_category_id())) {
            if (trade_month.compareTo(new BigDecimal(50)) >= 0) {
                customer.setCustomer_gradation("S");
            } else if (trade_month.compareTo(new BigDecimal(20)) >= 0) {
                customer.setCustomer_gradation("T");
            } else if (trade_month.compareTo(new BigDecimal(5)) >= 0) {
                customer.setCustomer_gradation("Y");
            } else {
                customer.setCustomer_gradation("C");
            }

        } else {
            throw new MyRunException("客户属性不存在");
        }
    }

    @Override
    @Transactional(propagation = Propagation.NESTED)
    public ResponseJson insert(Customer bean) {
        bean.setTrade_month(bean.getTrade_month() == null ? BigDecimal.ZERO : bean.getTrade_month());
        //设置客户分层
        setCustomerGradation(bean);

        //新增客户信息
        if (bean.getImportType() != 3) {
            if (super.getOneBySelectKey(Customer.builder().unique_id(bean.getUnique_id()).build()) != null) {
                return ResponseJson.fail("客户已存在");
            }
            bean.setTotal_amount(bean.getTotal_amount() == null ? BigDecimal.ZERO : bean.getTotal_amount());
            bean.setNear_deal_time(DateUtil.NULL);
            bean.setPrev_deal_time(DateUtil.NULL);
            bean.setActiveness(CodeCaption.ACTIVENESS_NEW);
            bean.setAllocate_again(0);
            if (bean.getImportType() == 1 || bean.getIsUpgrade()) {
                bean.setShop_num(1);
            } else {
                bean.setShop_num(0);
            }
            //跟进人
            bean.setProcess_user_id(bean.getProcess_user_id() == null ? 0L : bean.getProcess_user_id());
            if (NumberUtil.isNotBlank(bean.getProcess_user_id())) {
                User user = userService.getById(bean.getProcess_user_id());
                //检查账号是否满足绑定跟进人
                ResponseJson responseJson = checkUserWithProcess(user);
                if (PubCode.SUCCESS.code() != responseJson.getCode()) {
                    return responseJson;
                }
                bean.setBind_time(LocalDateTime.now());
                bean.setTeam_id(user.getTeam_id());
            }
            //客户编号
            String maxSn = customerMapper.getMaxSn(bean.getCustomer_category_id());
            String sn = getNextSn(maxSn, bean.getCustomer_category_id());
            bean.setSn(sn);
            //插入客户信息
            super.insert(bean);
            //插入客户联系人
            if (bean.getCustomer_contact() != null && bean.getCustomer_contact().size() > 0) {
                customerContactService.insertBatchAsCustomer(bean.getCustomer_contact(), bean.getId());
            }
            //插入绑定日志
            if (NumberUtil.isNotBlank(bean.getProcess_user_id())) {
                customerBindLogService.insert(CustomerBindLog.builder().customer_id(bean.getId()).user_id(bean.getProcess_user_id()).action(1).build());
            }
        }
        //新增店铺信息
        if (bean.getImportType() != 2) {
            CustomerShop customerShop = new CustomerShop();
            BeanUtils.copyProperties(bean, customerShop);
            if (bean.getImportType() == 1) {
                customerShop.setParent_unique_id(customerShop.getUnique_id());
            }
            if (!StringUtils.hasText(customerShop.getParent_unique_id())) {
                throw new MyRunException("店铺的上级唯一识别必填");
            }
            if (customerShopService.getOneBySelectKey(CustomerShop.builder().unique_id(customerShop.getUnique_id()).build()) != null) {
                throw new MyRunException("店铺已存在");
            }
            //增加店铺数
            if (bean.getImportType() == 3) {
                Customer customer = super.getOneBySelectKey(Customer.builder().unique_id(bean.getParent_unique_id()).build());
                if (customer == null) {
                    throw new MyRunException("店铺所绑定的客户不存在");
                }
                super.update(Customer.builder().id(customer.getId()).shop_num(1).build());
            }
            //插入客户店铺
            customerShopService.insert(customerShop);
            //插入客户店铺联系人
            if (bean.getCustomer_contact().size() > 0) {
                List<CustomerShopContact> customerShopContacts = bean.getCustomer_contact().stream().map(x -> {
                    CustomerShopContact customerShopContact = new CustomerShopContact();
                    BeanUtils.copyProperties(x, customerShopContact);
                    customerShopContact.setCustomer_shop_id(customerShop.getId());
                    return customerShopContact;
                }).collect(Collectors.toList());
                customerShop.setCustomer_shop_contact(customerShopContacts);
                customerShopContactService.insertBatch(customerShopContacts);
            }
        }

        return ResponseJson.success();
    }

    @Override
    @Transactional
    public ResponseJson delete(Long id) {
        Customer customer = customerMapper.getById(id);
        if (customer == null) {
            return ResponseJson.fail("客户已经被删除了");
        }
        if (NumberUtil.isNotBlank(customer.getProcess_user_id())) {
            return ResponseJson.fail("客户已有人跟进无法删除");
        }
        List<CustomerShop> customerShops = customerShopService.getAllBySelectKey(CustomerShop.builder().parent_unique_id(customer.getUnique_id()).build());
        if (customerShops.size() > 1) {
            return ResponseJson.fail("客户下面已有多家店铺,需要先解除店铺关联");
        }
        customerShopService.deleteByParentUniqueId(customer.getUnique_id());
        return super.delete(id);
    }

    /**
     * 导入客户
     *
     * @param file excel
     */
    @Override
    @Transactional
    public ResponseJson customerImport(MultipartFile file) {

        Sheet sheet = ExcelUtils.getSheet(file);
        int totalRow = sheet.getLastRowNum();

        List<Customer> customerList = new ArrayList<>(totalRow);
        List<Customer> failedList = new ArrayList<>(totalRow);

        Map<String, Long> categoryMap = customerCategoryService.getAll().stream().collect(Collectors.toMap(LabelModel::getName, CommonModel::getId));
        Map<String, Long> productCategoryMap = productCategoryService.getAll().stream().collect(Collectors.toMap(ProductCategory::getName, CommonModel::getId));
        Map<String, Long> productNationMap = productNationService.getAll().stream().collect(Collectors.toMap(ProductNation::getName, CommonModel::getId));
        Map<String, Long> customerLevelMap = customerLevelService.getAll().stream().collect(Collectors.toMap(CustomerLevel::getName, CommonModel::getId));

        for (int i = 8; i < totalRow + 1; i++) {
            Row row = sheet.getRow(i);
            if (row == null) {
                continue;
            }
            Customer customer = new Customer();
            customer.setInsertRowNum(i + 1);
            Integer importType = ExcelUtils.getInteger(row.getCell(0));
            if (importType == null) {
                importType = 0;
            }
            customer.setImportType(importType);
            customer.setName(ExcelUtils.getString(row.getCell(1)));
            customer.setUnique_id(ExcelUtils.getString(row.getCell(2)));
            customer.setParent_unique_id(ExcelUtils.getString(row.getCell(3)));
            customer.setCustomer_category_id(categoryMap.get(ExcelUtils.getString(row.getCell(4))));
            customer.setCustomer_category_id2(categoryMap.get(ExcelUtils.getString(row.getCell(5))));
            customer.setCustomer_category_id3(categoryMap.get(ExcelUtils.getString(row.getCell(6))));
            customer.setCustomer_level_id(customerLevelMap.get(ExcelUtils.getString(row.getCell(7))));
            customer.setShop_url(ExcelUtils.getString(row.getCell(8)));
            customer.setProvince(ExcelUtils.getString(row.getCell(9)));
            customer.setCity(ExcelUtils.getString(row.getCell(10)));
            customer.setBusiness_address(ExcelUtils.getString(row.getCell(11)));
            customer.setCustomer_product_category_id(productCategoryMap.get(ExcelUtils.getString(row.getCell(12))));
            customer.setCustomer_product_nation_id(productNationMap.get(ExcelUtils.getString(row.getCell(13))));
            customer.setTrade_month(ExcelUtils.getBigDecimal(row.getCell(14)));
            customer.setTrade_type(ExcelUtils.getInteger(row.getCell(15)));

            CustomerContact customerContact = new CustomerContact();
            customerContact.setPerson(ExcelUtils.getString(row.getCell(16)));
            customerContact.setPerson_title(ExcelUtils.getString(row.getCell(17)));
            customerContact.setMobile(ExcelUtils.getString(row.getCell(18)));
            customerContact.setWangwang(ExcelUtils.getString(row.getCell(19)));
            customerContact.setWx(ExcelUtils.getString(row.getCell(20)));
            customer.setProcess_user_id(ExcelUtils.getLong(row.getCell(21)));

            List<CustomerContact> contactList = new ArrayList<>();
            contactList.add(customerContact);
            customer.setCustomer_contact(contactList);
            customerList.add(customer);
        }
        //店铺类型3排在最后,防止店铺找不到上级客户
        customerList.sort(Comparator.comparing(Customer::getImportType));
        for (Customer customer : customerList) {
            if (customer.getImportType() == 0) {
                customer.setInsertError("请检查第".concat(customer.getInsertRowNum().toString()).concat("行是否为无效空数据,若是则不影响导入结果"));
                failedList.add(customer);
                continue;
            }
            try {
                ResponseJson responseJson = this.insert(customer);
                if (PubCode.SUCCESS.code() != responseJson.getCode()) {
                    log.error(responseJson.getMsg());
                    customer.setInsertError(responseJson.getMsg());
                    failedList.add(customer);
                }
            } catch (MyRunException e) {
                customer.setInsertError(e.getMessage());
                failedList.add(customer);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                customer.setInsertError("网络开了个小差");
                failedList.add(customer);
            }
        }
        if (failedList.size() > 0) {
            return ResponseJson.fail(PubCode.BUSINESS_ERROR.code(), String.format("存在%s条数据导入失败", failedList.size()), failedList);
        } else {
            return ResponseJson.success();
        }
    }

    @Override
    public void customerDownload(CustomerSearchParam param, HttpServletResponse response) {
        User user = UserToken.getContext();
        //销售员只能看到自己及下级的客户
        if (CodeCaption.ROLE_SALE == user.getRole()) {
            param.setTeam_id(user.getTeam_id());
            if (param.getProcess_user_id() == null || param.getProcess_user_id() != 0) {
                List<Long> process_user_ids = userService.subordinateForSale(user).stream().map(User::getId).collect(Collectors.toList());
                param.setProcess_user_ids(process_user_ids);
            }
        }
        Integer count = customerMapper.countByPage(param);
        List<CustomerCategory> categories = customerCategoryService.getAll();
        List<ProductCategory> productCategories = productCategoryService.getAll();
        List<ProductNation> productNations = productNationService.getAll();
        List<Customer> customerList = new ArrayList<>();
        int limit = 1000;
        int order = count / limit + 1;
        param.setMobile("mobile"); //用于返回手机号
        for (int i = 0; i < order; i++) {
            param.setLimit(limit);
            param.setOffset(limit * i);
            customerList.addAll(customerMapper.getByPage(param));
        }

        ExcelData excelData = new ExcelData();
        String[] columnNames = {"序号", "客户编号", "客户名称", "唯一识别", "手机号", "一级属性", "二级属性", "三级属性", "淘宝客户等级", "店铺网址", "所在省份", "所在城市", "主营产品类目", "主营产品国籍",
                "客户规模", "贸易类型", "业务负责人", "客户分层", "活跃度", "年度销售额", "录入时间"};// 列名
        excelData.setColumnNames(columnNames);
        List<List<Object>> rows = new ArrayList<>(customerList.size());
        for (int i = 0; i < customerList.size(); i++) {
            List<Object> row = new ArrayList<>();
            Customer customer = customerList.get(i);
            row.add(i + 1);
            row.add(customer.getSn());
            row.add(customer.getName());
            row.add(customer.getUnique_id());
            row.add(customer.getMobile());
            row.add(customer.getCustomer_category_name());

            String categoryName2 = null;
            String categoryName3 = null;
            for (CustomerCategory category : categories) {
                if (category.getId().equals(customer.getCustomer_category_id2())) {
                    categoryName2 = category.getName();
                } else if (category.getId().equals(customer.getCustomer_category_id3())) {
                    categoryName3 = category.getName();
                }
            }
            row.add(categoryName2);
            row.add(categoryName3);


            row.add(customer.getCustomer_level_name());
            row.add(customer.getShop_url());
            row.add(customer.getProvince());
            row.add(customer.getCity());

            String productCategoryName = null;
            for (ProductCategory productCategory : productCategories) {
                if (productCategory.getId().equals(customer.getCustomer_product_category_id())) {
                    productCategoryName = productCategory.getName();
                }
            }
            row.add(productCategoryName);

            String productNationName = null;
            for (ProductNation productNation : productNations) {
                if (productNation.getId().equals(customer.getCustomer_product_nation_id())) {
                    productNationName = productNation.getName();
                }
            }
            row.add(productNationName);

            row.add(customer.getTrade_month());
            row.add(CodeUtil.getCaption(CodeCaption.TRADE_TYPE, customer.getTrade_type()));
            row.add(customer.getProcess_user_name());
            row.add(customer.getCustomer_gradation());
            row.add(CodeUtil.getCaption(CodeCaption.ACTIVENESS, customer.getActiveness()));
            row.add(customer.getTotal_amount());
            row.add(DateUtil.DATE_TIME_FORMATTER.format(customer.getCreated_at()));
            rows.add(row);
        }
        excelData.setRows(rows);
        ExcelUtils.exportExcel(response, "客户", excelData);

    }

    /**
     * 客户列表
     *
     * @param param 查询参数
     */
    @Override
    public ResponseJson getByPage(CustomerSearchParam param) {
        User user = UserToken.getContext();
        //销售员只能看到自己及下级的客户
        if (CodeCaption.ROLE_SALE == user.getRole()) {
            param.setTeam_id(user.getTeam_id());
            if (param.getProcess_user_id() == null || param.getProcess_user_id() != 0) {
                List<Long> process_user_ids = userService.subordinateForSale(user).stream().map(User::getId).collect(Collectors.toList());
                param.setProcess_user_ids(process_user_ids);
            }
        }

        Map<String, Object> result = new HashMap<>();
        Integer count = customerMapper.countByPage(param);
        result.put("total", count);
        if (count > 0) {
            List<Customer> customers = customerMapper.getByPage(param);
            List<Long> customerIds = customers.stream().map(Customer::getId).collect(Collectors.toList());
            if (customerIds.size() > 0) {
                List<CustomerTagRelation> customerTags = customerTagRelationService.getCustomerTags(customerIds);
                for (Customer customer : customers) {
                    customer.setCustomer_tags(customerTags.stream().filter(x -> x.getCustomer_id().equals(customer.getId())).collect(Collectors.toList()));
                }
            }
            result.put("items", customers);
        }
        result.put("tags", userTagsService.getAllBySelectKey(UserTags.builder().user_id(user.getId()).build()));
        return ResponseJson.success(result);
    }

    /**
     * @param id     id
     * @param limit  日报分页最大条数
     * @param offset 日报分页第几条开始
     * @return 客户详细详细
     */
    @Override
    public ResponseJson processAll(Long id, Integer limit, Integer offset) {
        Customer customer = customerMapper.processAll(id);
        if (customer == null) {
            return ResponseJson.fail("客户不存在");
        }
        customer.setTrade_type_label(CodeUtil.getCaption(CodeCaption.TRADE_TYPE, customer.getTrade_type()));
        customer.setActiveness_label(CodeUtil.getCaption(CodeCaption.ACTIVENESS, customer.getActiveness()));
        //一级、二级、三级客户属性
        List<CustomerCategory> categoryMapperAll = customerCategoryService.getAll();
        int a = 0;
        for (CustomerCategory customerCategory : categoryMapperAll) {
            if (customerCategory.getId().equals(customer.getCustomer_category_id())) {
                customer.setCustomer_category_name(customerCategory.getName());
                a++;
            }
            if (customerCategory.getId().equals(customer.getCustomer_category_id2())) {
                customer.setCustomer_category_name2(customerCategory.getName());
                a++;
            }
            if (customerCategory.getId().equals(customer.getCustomer_category_id3())) {
                customer.setCustomer_category_name3(customerCategory.getName());
                a++;
            }
            if (a >= 3) {
                break;
            }
        }

        //联系人
        List<CustomerContact> customerContactList = customerContactService.getAllBySelectKey(CustomerContact.builder().customer_id(customer.getId()).delete_status(0).build());
        customer.setCustomer_contact(customerContactList);


        //日报列表
        DateReportSearchParam param = new DateReportSearchParam();
        param.setLimit(limit);
        param.setOffset(offset);
        param.setQueryByCustomer(true);
        param.setCustomer_id(customer.getId());
        Page<DateReport> dateReportPage = dateReportService.getByPage(param);
        customer.setDateReportPage(dateReportPage);
        return ResponseJson.success(customer);
    }

    /**
     * 获取配置
     */
    @Override
    public ResponseJson getCustomerConfigure() {
        Map<String, Object> map = new HashMap<>();
        //只获取客户一级和二级属性
        List<MultiLabel> labelList = new ArrayList<>();
        List<CustomerCategory> categories = customerCategoryService.getAllBySelectKey(CustomerCategory.builder().parent_id(0L).build());
        for (CustomerCategory category : categories) {
            MultiLabel multiLabel = new MultiLabel().setId(category.getId()).setValue(category.getId()).setLabel(category.getName());
            labelList.add(multiLabel);
            List<CustomerCategory> childCategories = customerCategoryService.getAllBySelectKey(CustomerCategory.builder().parent_id(category.getId()).build());
            if (childCategories.size() > 0) {
                List<MultiLabel> children = new ArrayList<>();
                for (CustomerCategory childCategory : childCategories) {
                    children.add(new MultiLabel().setId(childCategory.getId()).setValue(childCategory.getId()).setLabel(childCategory.getName()));
                }
                multiLabel.setChildren(children);
            }
        }
        map.put("categories_two", labelList);
        map.put("categories", MultiLabel.getInstance(customerCategoryService.getAll()));
        map.put("levels", customerLevelService.getAll());
        map.put("productCategories", productCategoryService.getAll());
        map.put("productNations", productNationService.getAll());
        return ResponseJson.success(map);
    }

    /**
     * 账号关联客户
     */
    @Override
    @Transactional
    public ResponseJson batchAttachUser(BatchAttachParam param) {

        if (param.getIds() == null || param.getIds().size() == 0) {
            return ResponseJson.fail("还未选择一个有效的客户");
        }

        User user = userService.getById(param.getUser_id());
        ResponseJson responseJson = checkUserWithProcess(user);
        if (PubCode.SUCCESS.code() != responseJson.getCode()) {
            return responseJson;
        }

        List<Customer> updateList = param.getIds().stream()
                .map(x -> Customer.builder().id(x).process_user_id(user.getId()).team_id(user.getTeam_id()).bind_time(LocalDateTime.now()).build()).collect(Collectors.toList());
        User loginUser = UserToken.getContext();
        int action;
        if (loginUser.getId().equals(param.getUser_id())) {
            action = 1;     //自己捡起
            for (Customer customer : updateList) {
                customer.setAllocate_again(0); //自己捡起时重复分配次数清零
            }
        } else {
            action = 2;     //上级分配
            List<CustomerBindLog> nearBindForCustomerIds = customerBindLogService.getNearBindForCustomerIds(param.getIds()); //查询最近的绑定者
            for (Customer customer : updateList) {
                boolean oneself = false;
                for (CustomerBindLog bindLog : nearBindForCustomerIds) {
                    if (bindLog.getCustomer_id().equals(customer.getId())) {
                        if (bindLog.getUser_id().equals(param.getUser_id()) && bindLog.getAction() == 2) {
                            oneself = true;
                            customer.setAllocate_again(999); //上次分配的账号是这个账号时,分配次数加1
                        }
                        break;
                    }
                }
                if (!oneself) {
                    customer.setAllocate_again(1); // 上次分配的账号不是这个账号时,分配次数从1开始
                }
            }
        }

        //更新客户跟进信息
        customerMapper.updateBatch(updateList);

        //插入绑定日志
        List<CustomerBindLog> bindLogs = param.getIds().stream().map(x -> CustomerBindLog.builder().customer_id(x).user_id(param.getUser_id()).action(action).build()).collect(Collectors.toList());
        customerBindLogService.insertBatch(bindLogs);

        return ResponseJson.success();
    }


    /**
     * 检查账号是否满足绑定跟进人
     *
     * @param user 账号信息
     * @return 是否满足
     */
    private ResponseJson checkUserWithProcess(User user) {
        if (user == null || CodeCaption.ROLE_SALE != user.getRole()) {
            return ResponseJson.fail("只有销售专员才可以绑定客户");
        }
        if (CodeCaption.STATUS_OK != user.getStatus()) {
            return ResponseJson.fail("账号状态不可用");
        }
        if (NumberUtil.isBlank(user.getTeam_id())) {
            return ResponseJson.fail("账号还未属于一个团队");
        }
        Integer bind = customerMapper.countBindCustomer(user.getId());
        Integer customerLimit = user.getCustomer_limit();
        if (customerLimit == null) {
            customerLimit = 250;
        }
        if (bind > customerLimit) {
            return ResponseJson.fail("绑定的客户数已超过上限,上限数".concat(customerLimit.toString()));
        }
        return ResponseJson.success();
    }

    /**
     * @param customer_id 客户id
     * @param is_release  是否是释放
     * @return 解除关联
     */
    @Override
    @Transactional
    public ResponseJson detachUser(Long customer_id, Integer is_release) {
        List<User> subordinate = userService.subordinateForSale(UserToken.getContext());
        List<Long> ids = subordinate.stream().map(CommonModel::getId).collect(Collectors.toList());

        Customer customer = super.getById(customer_id);
        if (customer == null || !ids.contains(customer.getProcess_user_id())) {
            return ResponseJson.fail("解绑失败,你或你的下级未绑定过该用户");
        }
        if (is_release != null && CodeCaption.TRUE == is_release) {
            CustomerRelease customerRelease = customerReleaseService.getById(customer_id);
            if (customerRelease == null) {
                return ResponseJson.fail("已经被释放了!");
            }
            if (CodeCaption.RELEASE_TYPE_VISIT == customerRelease.getRelease_type()) {
                return ResponseJson.fail("未拜访的客户无法手动释放");
            }
            int month = Integer.parseInt(LocalDateTime.now().format(DateUtil.yyyyMM_FORMATTER));
            BussProcessSummary processSummary = BussProcessSummary.builder().user_id(customerRelease.getProcess_user_id()).summary_month(month)
                    .customer_category_id(customerRelease.getCustomer_category_id()).build();
            if ("S".equals(customerRelease.getCustomer_gradation())) {
                processSummary.setRelease_s(1);
                if (CodeCaption.RELEASE_TYPE_DEAL == customerRelease.getRelease_type()) {
                    processSummary.setRelease_deal_s(1);
                } else if (CodeCaption.RELEASE_TYPE_REPEAT == customerRelease.getRelease_type()) {
                    processSummary.setRelease_repeat_s(1);
                }
            } else if ("T".equals(customerRelease.getCustomer_gradation())) {
                processSummary.setRelease_t(1);
                if (CodeCaption.RELEASE_TYPE_DEAL == customerRelease.getRelease_type()) {
                    processSummary.setRelease_deal_t(1);
                } else if (CodeCaption.RELEASE_TYPE_REPEAT == customerRelease.getRelease_type()) {
                    processSummary.setRelease_repeat_t(1);
                }
            } else if ("Y".equals(customerRelease.getCustomer_gradation())) {
                processSummary.setRelease_y(1);
                if (CodeCaption.RELEASE_TYPE_DEAL == customerRelease.getRelease_type()) {
                    processSummary.setRelease_deal_y(1);
                } else if (CodeCaption.RELEASE_TYPE_REPEAT == customerRelease.getRelease_type()) {
                    processSummary.setRelease_repeat_y(1);
                }
            } else if ("C".equals(customerRelease.getCustomer_gradation())) {
                processSummary.setRelease_c(1);
                if (CodeCaption.RELEASE_TYPE_DEAL == customerRelease.getRelease_type()) {
                    processSummary.setRelease_deal_c(1);
                } else if (CodeCaption.RELEASE_TYPE_REPEAT == customerRelease.getRelease_type()) {
                    processSummary.setRelease_repeat_c(1);
                }
            }

            List<BussProcessSummary> processSummaryList = Collections.singletonList(processSummary);
            //增加释放客户数
            bussProcessSummaryService.updateBatchByUnique(processSummaryList);
            //删除待释放的客户
            customerReleaseService.delete(customer_id);
        }

        //解绑客户
        super.update(Customer.builder().id(customer_id).process_user_id(0L).build());
        return ResponseJson.success();
    }

    /**
     * 仅仅修改客户信息
     *
     * @param customer 客户信息
     */
    @Override
    public void updateOnly(Customer customer) {
        super.update(customer);
    }

    @Override
    @Transactional
    public ResponseJson update(Customer bean) {

        //设置客户分层
        setCustomerGradation(bean);

        bean.setSn(null);
        bean.setProcess_user_id(null);
        Customer customer = super.getById(bean.getId());
        if (customer == null) {
            return ResponseJson.fail("客户不存在");
        }
        //客户一级属性修改时改编号级活跃度等逻辑
        if (NumberUtil.isNotBlank(bean.getCustomer_category_id()) && !customer.getCustomer_category_id().equals(bean.getCustomer_category_id())) {
            String maxSn = customerMapper.getMaxSn(bean.getCustomer_category_id());
            String sn = getNextSn(maxSn, bean.getCustomer_category_id());
            bean.setSn(sn);
        }

        bean.setUnique_id(null);
        bean.setTotal_amount(null);
        bean.setShop_num(null);
        super.update(bean);

        CustomerShop customerShop = customerShopService.getOneBySelectKey(CustomerShop.builder().unique_id(customer.getUnique_id()).parent_unique_id(customer.getUnique_id()).build());
        List<CustomerContact> insertList = new ArrayList<>();               //新增客户联系人
        List<CustomerContact> updateList = new ArrayList<>();               //修改客户联系人
        List<CustomerShopContact> customerShopContacts = new ArrayList<>(); //新增或修改的店铺联系人
        if (bean.getCustomer_contact() != null) {
            for (CustomerContact customerContact : bean.getCustomer_contact()) {
                CustomerShopContact customerShopContact = CustomerShopContact.builder().customer_shop_id(customerShop.getId()).person(customerContact.getPerson()).person_title(customerContact.getPerson_title())
                        .mobile(customerContact.getMobile()).wangwang(customerContact.getWangwang()).wx(customerContact.getWx()).build();
                if (NumberUtil.isBlank(customerContact.getId())) {
                    insertList.add(customerContact);
                    customerShopContacts.add(customerShopContact);
                } else {
                    updateList.add(customerContact);
                    CustomerContact contact = customerContactService.getById(customerContact.getId());
                    if (contact != null) {
                        //店铺中查不到相同的联系人就不更新 ,老数据联系人和手机号相同就代表相同
                        if (StringUtils.hasText(contact.getPerson()) && StringUtils.hasText(contact.getMobile())) {
                            CustomerShopContact shopContact = customerShopContactService.getOneBySelectKey(CustomerShopContact.builder().customer_shop_id(customerShop.getId()).person(contact.getPerson()).mobile(contact.getMobile()).build());
                            if (shopContact != null) {
                                customerShopContact.setId(shopContact.getId());
                                customerShopContacts.add(customerShopContact);
                            }
                        }
                    }
                }
            }
            if (insertList.size() > 0) {
                customerContactService.insertBatchAsCustomer(insertList, bean.getId());
            }
            if (updateList.size() > 0) {
                customerContactService.updateBatch(updateList);
            }
        }

        //当客户下存在克隆自己的店铺时,同步更新店铺
        if (customerShop != null) {
            BeanUtils.copyProperties(customer, customerShop, "id");
            customerShop.setCustomer_shop_contact(customerShopContacts);
            updateShop(customerShop);
        }
        return ResponseJson.success();
    }

    @Override
    public void afterPropertiesSet() {
        setCommonMapper(customerMapper);
    }

    /**
     * 获取下个客户编号
     *
     * @param maxSn              最大客户编号
     * @param customerCategoryId 客户一级属性id
     * @return 下个客户编号
     */
    private String getNextSn(String maxSn, Long customerCategoryId) {
        if (!StringUtils.hasText(maxSn)) {
            if (CodeCaption.CUSTOMER_CATEGORY_RETAILER == customerCategoryId) {
                return "A1000000";
            } else if (CodeCaption.CUSTOMER_CATEGORY_DEALER == customerCategoryId) {
                return "B1000000";
            } else {
                throw new MyRunException("客户一级属性不存在");
            }
        } else {
            int order = Integer.parseInt(maxSn.substring(1));
            return String.format("%s%s", maxSn.substring(0, 1), order + 1);
        }
    }

}
