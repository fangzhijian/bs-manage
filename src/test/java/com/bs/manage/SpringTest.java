package com.bs.manage;

import com.bs.manage.code.CodeCaption;
import com.bs.manage.config.ManageApplication;
import com.bs.manage.constant.Constants;
import com.bs.manage.mapper.account.UserMapper;
import com.bs.manage.mapper.console.DateReportMapper;
import com.bs.manage.mapper.customer.CustomerMapper;
import com.bs.manage.mapper.customer.CustomerShopMapper;
import com.bs.manage.model.bean.account.Team;
import com.bs.manage.model.bean.account.User;
import com.bs.manage.model.bean.console.DateReport;
import com.bs.manage.model.bean.customer.Customer;
import com.bs.manage.model.bean.customer.CustomerShop;
import com.bs.manage.model.bean.erp.ReturnWarehouseDetail;
import com.bs.manage.model.bean.erp.ReturnWarehouseEntry;
import com.bs.manage.model.bean.erp.SalesRelease;
import com.bs.manage.model.bean.erp.SalesReleaseDetail;
import com.bs.manage.model.bean.kpi.KpiMain;
import com.bs.manage.model.json.ExcelData;
import com.bs.manage.model.json.ResponseJson;
import com.bs.manage.model.json.XBaseAiJson;
import com.bs.manage.model.param.console.BoardParam;
import com.bs.manage.service.account.TeamService;
import com.bs.manage.service.account.UserService;
import com.bs.manage.service.common.DingDingService;
import com.bs.manage.service.erp.ReturnWarehouseDetailService;
import com.bs.manage.service.erp.ReturnWarehouseEntryService;
import com.bs.manage.service.erp.SalesReleaseDetailService;
import com.bs.manage.service.erp.SalesReleaseService;
import com.bs.manage.service.kpi.KpiMainService;
import com.bs.manage.service.notify.NotifyKpiService;
import com.bs.manage.until.DateUtil;
import com.bs.manage.until.ExcelUtils;
import com.bs.manage.until.MailUtil;
import com.bs.manage.until.NumberUtil;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 2020/6/2 10:27
 * fzj
 */
@SpringBootTest(classes = ManageApplication.class)
@Slf4j
public class SpringTest {

    @Autowired
    private Gson gson;
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Value("${spring.profiles.active}")
    private String active;
    @Autowired
    private DingDingService dingDingService;
    @Autowired
    private CustomerMapper customerMapper;
    @Autowired
    private CustomerShopMapper customerShopMapper;
    //    @Autowired
//    private CustomerProjectMapper customerProjectMapper;
    @Autowired
    private DateReportMapper dateReportMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private TeamService teamService;
    @Autowired
    private SalesReleaseService salesReleaseService;
    @Autowired
    private SalesReleaseDetailService salesReleaseDetailService;
    @Autowired
    private ReturnWarehouseEntryService returnWarehouseEntryService;
    @Autowired
    private ReturnWarehouseDetailService returnWarehouseDetailService;
    @Autowired
    UserService userService;
    @Autowired
    KpiMainService kpiMainService;
    @Value("${webUrl}")
    private String DEFAULT_DING_MSG_URL;
    @Autowired
    private RestTemplate restTemplate;

    @Test
    public void test() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("message", "怎么进件开户");
        jsonObject.addProperty("stream", "false");//是否开启流式传输
        jsonObject.addProperty("re_chat", "true");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth("application-af30aa0d9b80aa893ef348ca6c2c87dd");
        HttpEntity<String> httpEntity = new HttpEntity<>(jsonObject.toString(), headers);
        ResponseEntity<String> response = restTemplate.postForEntity(
                "http://120.26.23.172:18080/chat/api/chat_message/0199e6aa-2184-7670-bcd9-4736c5a74194",
                httpEntity, String.class);
        System.out.println(response.getStatusCode());
        System.out.println(response.getBody());

    }

    @Test
    public void testRedis() {
        Team team = new Team();
        team.setId(8L);
        team.setCreated_at(LocalDateTime.now());
        redisTemplate.opsForValue().set("test", team);
        Team team1 = (Team) redisTemplate.opsForValue().get("test");
        System.out.println(team1);
    }

    @Test
    public void jdbc() {
        List<ReturnWarehouseDetail> returnWarehouseDetailList = returnWarehouseDetailService.getLiveOrder(0);
        returnWarehouseDetailService.updateBatch(returnWarehouseDetailList);
        List<ReturnWarehouseEntry> returnWarehouseEntryList = returnWarehouseEntryService.getLiveOrder(0);
        returnWarehouseEntryService.updateBatch(returnWarehouseEntryList);

    }

    //合并客户
    private void mergeCustomer(String nameTo, String name, String snTo, String sn) {
        LocalDateTime now = LocalDateTime.now();
        String dateTime = now.format(DateUtil.DATE_TIME_FORMATTER);
        Customer customerTo = customerMapper.getOneBySelectKey(Customer.builder().name(nameTo).sn(snTo).build());
        if (customerTo == null) {
            log.error("被合并客户不存在,名称{},编号{}", nameTo, snTo);
            return;
        }
        if (customerTo.getShop_num() != 1) {
            log.error("被合并的客户店铺数太多了,{}", nameTo);
            return;
        }
        Customer customer = customerMapper.getOneBySelectKey(Customer.builder().name(name).sn(sn).build());
        if (customer == null) {
            log.error("客户不存在,名称{},编号{}", name, sn);
            return;
        }
        CustomerShop customerShop = customerShopMapper.getOneBySelectKey(CustomerShop.builder().parent_unique_id(customerTo.getUnique_id()).build());
        if (customerShop == null) {
            log.error("被合并客户的店铺不存在,名称{},编号{}", nameTo, sn);
            return;
        }
        //删除被合并的客户
        customerMapper.delete(customerTo.getId(), now);
        //合并后的店铺数加1
        customerMapper.update(Customer.builder().id(customer.getId()).shop_num(1).updated_at(now).build());
        //更新被合并店铺的项目客户
        customerShopMapper.update(CustomerShop.builder().id(customerShop.getId()).parent_unique_id(customer.getUnique_id()).updated_at(now).build());
        boolean prod = "prod".equals(active);
        if (prod) {
            jdbcTemplate.execute(String.format("UPDATE date_report SET customer_id = %s WHERE customer_id = %s", customer.getId(), customerTo.getId()));
        } else {
            jdbcTemplate.execute(String.format("update customer_project set delete_status = 1,deleted_at = '%s' where customer_id = %s", dateTime, customerTo.getId()));
        }
    }

    @Test
    public void data() {
        List<User> users = userService.getAllBySelectKey(User.builder().status(CodeCaption.STATUS_OK).build());
        for (User user : users) {
            System.out.println(user.getPosition());
            boolean matches = user.getPosition().matches("^\\s*\\w+(?:\\.{0,1}[\\w-]+)*@[a-zA-Z0-9]+(?:[-.][a-zA-Z0-9]+)*\\.[a-zA-Z]+\\s*$");
            if (!matches) {
                System.out.println(user);
            }
        }
    }

    @Test
    public void data1() {
        String uri = "/chat/api/chat_message/0199dce2-f617-7c12-a875-6309facd4457";
        String token = "application-af30aa0d9b80aa893ef348ca6c2c87dd";
        WebClient webClient = WebClient.builder().baseUrl("http://120.26.23.172:18080")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader("Authorization", "Bearer " + token).build();

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("message", "怎么进件开户");
        map.add("stream", "false");
        map.add("re_chat", "true");

        XBaseAiJson flux = webClient.post().uri(uri).body(BodyInserters.fromFormData(map))
                .retrieve().bodyToMono(XBaseAiJson.class).block();
        System.out.println(flux);
    }

    @Test
    public void redis() {
//        ResponseJson userId = dingDingService.getUserId("");
//        System.out.println(userId);
        List<String> userIdList = new ArrayList<>();
//        16303819742721258
        userIdList.add("15758594110958943");
        String title = "我秦始皇打钱";
        String text = "你好，我是秦始皇，我吃了长生不老药没死，现在有亿万兵力被封印在了皇陵，几万吨黄金埋在地下无法挖掘，现在需要你帮助我，援助5000块，等我成功赐你黄金万两、兵马大元帅，下面是我支付宝";
        String messageUrl = "http://yun.rili.cn/wnl/index.html";
        String picUrl = "http://data.hzleadspeed.com/upload/auth/20200415/962bd40735fae6cd3a4e05e739c0de2143a70f32.jpg";
        dingDingService.notifyForWork(userIdList, title, text, messageUrl, picUrl);

    }


    @Test
    public void doExcel() throws Exception {
//        String fileName = "C:\\Users\\fzj\\Desktop\\销售出库单\\20230531_销售出库单.xlsx";
//        String fileName = "C:\\Users\\fzj\\Desktop\\销售出库明细\\20230531_销售出库单明细.xlsx";
//        String fileName = "C:\\Users\\fzj\\Desktop\\退货入库单\\20230531_退货入库单.xlsx";
        String fileName = "C:\\Users\\fzj\\Desktop\\引入失败_2023122514361400.xlsx";
        FileInputStream inputStream = new FileInputStream(new File(fileName));
        Workbook workbook;
        boolean isExcel2003 = !fileName.matches("^.+\\.(?i)(xlsx)$");
        if (isExcel2003) {
            workbook = new HSSFWorkbook(inputStream);
        } else {
            workbook = new XSSFWorkbook(inputStream);
        }
        Sheet sheet = workbook.getSheetAt(0);
        int totalRow = sheet.getLastRowNum();
//        int totalRow = 5;
        log.info("总共读取{}行", totalRow + 1);
        if (totalRow <= 1) {
            return;
        }


        //导入销售出库单
//        uploadSaleRelease(sheet, totalRow);
        //导入销售出库明细
//        uploadSaleReleaseDetail(sheet, totalRow);
        //导入退货入库单
//        uploadReturnWarehouseEntry(sheet, totalRow);
        //导入退货入库明细
//        uploadReturnWarehouseDetail(sheet, totalRow);
    }

    public void uploadReturnWarehouseEntry(Sheet sheet, int totalRow) {
        List<ReturnWarehouseEntry> returnWarehouseEntryList = new ArrayList<>(totalRow - 1);
        for (int i = 1; i < totalRow + 1; i++) {
            Row row = sheet.getRow(i);
            ReturnWarehouseEntry returnWarehouseEntry = new ReturnWarehouseEntry();
            returnWarehouseEntry.setWarehouse_entry_no(ExcelUtils.getString(row.getCell(0)));
            returnWarehouseEntry.setWarehouse_entry_state(ExcelUtils.getString(row.getCell(1)));
            returnWarehouseEntry.setType(ExcelUtils.getString(row.getCell(2)));
            returnWarehouseEntry.setReturn_state(ExcelUtils.getString(row.getCell(3)));
            returnWarehouseEntry.setReturn_reason(ExcelUtils.getString(row.getCell(4)));
            returnWarehouseEntry.setReturn_remark(ExcelUtils.getString(row.getCell(5)));
            returnWarehouseEntry.setReturn_creator(ExcelUtils.getString(row.getCell(6)));
            returnWarehouseEntry.setShop_name(ExcelUtils.getString(row.getCell(7)));
            returnWarehouseEntry.setOriginal_order_num(ExcelUtils.getString(row.getCell(8)));
            returnWarehouseEntry.setOrder_num(ExcelUtils.getString(row.getCell(9)));
            returnWarehouseEntry.setOrder_logistics_company(ExcelUtils.getString(row.getCell(10)));
            returnWarehouseEntry.setOrder_logistics_num(ExcelUtils.getString(row.getCell(11)));
            returnWarehouseEntry.setCustomer_net_name(ExcelUtils.getString(row.getCell(12)));
            returnWarehouseEntry.setReturn_no(ExcelUtils.getString(row.getCell(13)));
            returnWarehouseEntry.setWarehouse_entry_user(ExcelUtils.getString(row.getCell(14)));
            returnWarehouseEntry.setActual_warehouse(ExcelUtils.getString(row.getCell(15)));
            returnWarehouseEntry.setLogistics_company(ExcelUtils.getString(row.getCell(16)));
            returnWarehouseEntry.setLogistics_num(ExcelUtils.getString(row.getCell(17)));
            returnWarehouseEntry.setReturn_total_income(ExcelUtils.getBigDecimal(row.getCell(18)));
            returnWarehouseEntry.setReturn_cost(ExcelUtils.getBigDecimal(row.getCell(19)));
            returnWarehouseEntry.setDiscounts(ExcelUtils.getBigDecimal(row.getCell(20)));
            returnWarehouseEntry.setPostage(ExcelUtils.getBigDecimal(row.getCell(21)));
            returnWarehouseEntry.setOther_balance(ExcelUtils.getBigDecimal(row.getCell(22)));
            returnWarehouseEntry.setAdjust_amount(ExcelUtils.getBigDecimal(row.getCell(23)));
            returnWarehouseEntry.setExpect_num(ExcelUtils.getInteger(row.getCell(24)));
            returnWarehouseEntry.setGoods_num(ExcelUtils.getInteger(row.getCell(25)));
            returnWarehouseEntry.setGoods_type_num(ExcelUtils.getInteger(row.getCell(26)));
            returnWarehouseEntry.setAdjust_num(ExcelUtils.getInteger(row.getCell(27)));
            returnWarehouseEntry.setRemark(ExcelUtils.getString(row.getCell(28)));
            returnWarehouseEntry.setMake_time(ExcelUtils.getString(row.getCell(29)) == null ?
                    null : LocalDateTime.parse(ExcelUtils.getString(row.getCell(29)), DateUtil.DATE_TIME_FORMATTER));
            returnWarehouseEntry.setUpdate_time(ExcelUtils.getString(row.getCell(30)) == null ?
                    null : LocalDateTime.parse(ExcelUtils.getString(row.getCell(30)), DateUtil.DATE_TIME_FORMATTER));
            returnWarehouseEntry.setAudit_time(ExcelUtils.getString(row.getCell(31)) == null ?
                    null : LocalDateTime.parse(ExcelUtils.getString(row.getCell(31)), DateUtil.DATE_TIME_FORMATTER));
            returnWarehouseEntry.setAudit_user(ExcelUtils.getString(row.getCell(32)));
            returnWarehouseEntry.setPush_state(ExcelUtils.getString(row.getCell(33)));
            returnWarehouseEntry.setPush_state(ExcelUtils.getString(row.getCell(34)));
            returnWarehouseEntry.setDistributor(ExcelUtils.getString(row.getCell(35)));
            returnWarehouseEntry.setSource_type(1);
            returnWarehouseEntryList.add(returnWarehouseEntry);

        }
        returnWarehouseEntryService.insertBatch(returnWarehouseEntryList);
        //更新订单来源类型为直播
        Integer id = returnWarehouseEntryList.get(0).getId().intValue();
        System.out.println("新增的第一个id为:" + id);
        List<ReturnWarehouseEntry> liveOrder = returnWarehouseEntryService.getLiveOrder(id);
        returnWarehouseEntryService.updateBatch(liveOrder);
    }


    public void uploadSaleRelease(Sheet sheet, int totalRow) {
        List<SalesRelease> salesReleaseList = new ArrayList<>(totalRow - 1);
        //最后一行没合计的话需改成 i < totalRow + 1
        for (int i = 1; i < totalRow; i++) {
            Row row = sheet.getRow(i);
            SalesRelease salesRelease = new SalesRelease();
            String orderNum = ExcelUtils.getString(row.getCell(0));
            salesRelease.setOrder_num(orderNum);
            salesRelease.setStock_out_num(ExcelUtils.getString(row.getCell(1)));
            salesRelease.setWarehouse(ExcelUtils.getString(row.getCell(2)));
            salesRelease.setWarehouse_type(ExcelUtils.getString(row.getCell(3)));
            salesRelease.setShop_name(ExcelUtils.getString(row.getCell(4)));
            salesRelease.setOrder_type(ExcelUtils.getString(row.getCell(5)));
            salesRelease.setOrder_time(ExcelUtils.getString(row.getCell(6)) == null ?
                    null : LocalDateTime.parse(ExcelUtils.getString(row.getCell(6)), DateUtil.DATE_TIME_FORMATTER));
            salesRelease.setPay_time(ExcelUtils.getString(row.getCell(7)) == null ?
                    null : LocalDateTime.parse(ExcelUtils.getString(row.getCell(7)), DateUtil.DATE_TIME_FORMATTER));
            salesRelease.setDeliver_count_down(ExcelUtils.getString(row.getCell(8)));
            salesRelease.setStatus(ExcelUtils.getString(row.getCell(9)));
            salesRelease.setOriginal_order_num(ExcelUtils.getString(row.getCell(10)));
            salesRelease.setDeliver_goods_status(ExcelUtils.getString(row.getCell(11)));
            salesRelease.setDeliver_goods_condition(ExcelUtils.getString(row.getCell(12)));
            salesRelease.setPush_information(ExcelUtils.getString(row.getCell(13)));
            salesRelease.setFreezing_cause(ExcelUtils.getString(row.getCell(14)));
            salesRelease.setSorting_number(ExcelUtils.getString(row.getCell(15)));
            salesRelease.setMake_order_user(ExcelUtils.getString(row.getCell(16)));
            salesRelease.setGoods_nums(ExcelUtils.getInteger(row.getCell(17)));
            salesRelease.setGoods_category(ExcelUtils.getInteger(row.getCell(18)));
            salesRelease.setCustomer_net_name(ExcelUtils.getString(row.getCell(19)));
            salesRelease.setConsignee(ExcelUtils.getString(row.getCell(20)));
            salesRelease.setReceiving_area(ExcelUtils.getString(row.getCell(21)));
            salesRelease.setReceiver_address(ExcelUtils.getString(row.getCell(22)));
            salesRelease.setReceiver_mobile_phone(ExcelUtils.getString(row.getCell(23)));
            salesRelease.setReceiver_phone(ExcelUtils.getString(row.getCell(24)));
            salesRelease.setEmail(ExcelUtils.getString(row.getCell(25)));
            salesRelease.setLogistics_company(ExcelUtils.getString(row.getCell(26)));
            salesRelease.setTotal_cost(ExcelUtils.getBigDecimal(row.getCell(27)));
            salesRelease.setEstimate_postage(ExcelUtils.getBigDecimal(row.getCell(28)));
            salesRelease.setPostage_cost(ExcelUtils.getBigDecimal(row.getCell(29)));
            salesRelease.setEstimate_weight(ExcelUtils.getBigDecimal(row.getCell(30)));
            salesRelease.setActual_weight(ExcelUtils.getBigDecimal(row.getCell(31)));
            salesRelease.setAmount_receivable(ExcelUtils.getBigDecimal(row.getCell(32)));
            salesRelease.setAmount_paid(ExcelUtils.getBigDecimal(row.getCell(33)));
            salesRelease.setAmount_COD(ExcelUtils.getBigDecimal(row.getCell(34)));
            salesRelease.setContain_invoice(ExcelUtils.getString(row.getCell(35)));
            salesRelease.setSalesman(ExcelUtils.getString(row.getCell(36)));
            salesRelease.setAudit_user(ExcelUtils.getString(row.getCell(37)));
            salesRelease.setFinance_audit_user(ExcelUtils.getString(row.getCell(38)));
            salesRelease.setPrint_user(ExcelUtils.getString(row.getCell(39)));
            salesRelease.setOrder_picking_user(ExcelUtils.getString(row.getCell(40)));
            salesRelease.setPack_user(ExcelUtils.getString(row.getCell(41)));
            salesRelease.setExamine_goods_user(ExcelUtils.getString(row.getCell(42)));
            salesRelease.setInspector_user(ExcelUtils.getString(row.getCell(43)));
            salesRelease.setDeliver_goods_user(ExcelUtils.getString(row.getCell(44)));
            salesRelease.setPrint_num(ExcelUtils.getString(row.getCell(45)));
            salesRelease.setLogistics_print_status(ExcelUtils.getString(row.getCell(46)));
            salesRelease.setDeliver_print_status(ExcelUtils.getString(row.getCell(47)));
            salesRelease.setSorting_print_status(ExcelUtils.getString(row.getCell(48)));
            salesRelease.setLogistics_num(ExcelUtils.getString(row.getCell(49)));
            salesRelease.setSorting_num(ExcelUtils.getString(row.getCell(50)));
            salesRelease.setDeliver_time(ExcelUtils.getString(row.getCell(51)) == null ?
                    null : LocalDateTime.parse(ExcelUtils.getString(row.getCell(51)), DateUtil.DATE_TIME_FORMATTER));
            salesRelease.setExternal_order_num(ExcelUtils.getString(row.getCell(52)));
            salesRelease.setCheck_out_user(ExcelUtils.getString(row.getCell(53)));
            salesRelease.setPackaging(ExcelUtils.getString(row.getCell(54)));
            salesRelease.setDispose_day(ExcelUtils.getInteger(row.getCell(55)));
            salesRelease.setAudit_create_time(ExcelUtils.getString(row.getCell(56)) == null ?
                    null : LocalDateTime.parse(ExcelUtils.getString(row.getCell(56)), DateUtil.DATE_TIME_FORMATTER));
            salesRelease.setInterception_cause(ExcelUtils.getString(row.getCell(57)));
            salesRelease.setPrint_remark(ExcelUtils.getString(row.getCell(58)));
            salesRelease.setVolume(ExcelUtils.getBigDecimal(row.getCell(59)));
            String distributor = ExcelUtils.getString(row.getCell(60));
            salesRelease.setDistributor(distributor);
            salesRelease.setDistribution_origin_order(ExcelUtils.getString(row.getCell(61)));
            salesRelease.setGoods_shop_num(ExcelUtils.getString(row.getCell(62)));
            salesRelease.setSource_type(StringUtils.hasText(distributor) ? 2 : 1);
            salesReleaseList.add(salesRelease);
        }
        salesReleaseService.insertBatch(salesReleaseList);
    }

    public void uploadSaleReleaseDetail(Sheet sheet, int totalRow) {

        List<SalesReleaseDetail> salesReleaseDetailList = new ArrayList<>(totalRow - 1);
        //最后一行没合计的话需改成 i < totalRow + 1
        for (int i = 1; i < totalRow; i++) {
            Row row = sheet.getRow(i);
            SalesReleaseDetail salesReleaseDetail = new SalesReleaseDetail();
            salesReleaseDetail.setOrder_num(ExcelUtils.getString(row.getCell(0)));
            salesReleaseDetail.setOriginal_order_num(ExcelUtils.getString(row.getCell(1)));
            salesReleaseDetail.setSun_original_no(ExcelUtils.getString(row.getCell(2)));
            salesReleaseDetail.setOriginal_sun_order_no(ExcelUtils.getString(row.getCell(3)));
            salesReleaseDetail.setOrder_type(ExcelUtils.getString(row.getCell(4)));
            salesReleaseDetail.setPay_account(ExcelUtils.getString(row.getCell(5)));
            salesReleaseDetail.setStock_out_num(ExcelUtils.getString(row.getCell(6)));
            salesReleaseDetail.setWarehouse(ExcelUtils.getString(row.getCell(7)));
            salesReleaseDetail.setWarehouse_type(ExcelUtils.getString(row.getCell(8)));
            salesReleaseDetail.setShop_name(ExcelUtils.getString(row.getCell(9)));
            salesReleaseDetail.setDelivery_no_state(ExcelUtils.getString(row.getCell(10)));
            salesReleaseDetail.setDelivery_state(ExcelUtils.getString(row.getCell(11)));
            salesReleaseDetail.setSorting_number(ExcelUtils.getString(row.getCell(12)));
            salesReleaseDetail.setShop_no(ExcelUtils.getString(row.getCell(13)));
            salesReleaseDetail.setGoods_no(ExcelUtils.getString(row.getCell(14)));
            salesReleaseDetail.setGoods_name(ExcelUtils.getString(row.getCell(15)));
            salesReleaseDetail.setGoods_short_name(ExcelUtils.getString(row.getCell(16)));
            salesReleaseDetail.setBrand(ExcelUtils.getString(row.getCell(17)));
            salesReleaseDetail.setCategory(ExcelUtils.getString(row.getCell(18)));
            salesReleaseDetail.setSpecification(ExcelUtils.getString(row.getCell(19)));
            salesReleaseDetail.setSpecification_name(ExcelUtils.getString(row.getCell(20)));
            salesReleaseDetail.setBar_code(ExcelUtils.getString(row.getCell(21)));
            salesReleaseDetail.setGoods_nums(ExcelUtils.getInteger(row.getCell(22)));
            salesReleaseDetail.setGood_origin_unit_price(ExcelUtils.getBigDecimal(row.getCell(23)));
            salesReleaseDetail.setGood_origin_total_pay(ExcelUtils.getBigDecimal(row.getCell(24)));
            salesReleaseDetail.setOrder_discounts(ExcelUtils.getBigDecimal(row.getCell(25)));
            salesReleaseDetail.setPostage(ExcelUtils.getBigDecimal(row.getCell(26)));
            salesReleaseDetail.setGoods_amount(ExcelUtils.getBigDecimal(row.getCell(27)));
            salesReleaseDetail.setGoods_total_amount(ExcelUtils.getBigDecimal(row.getCell(28)));
            salesReleaseDetail.setGoods_discounts(ExcelUtils.getBigDecimal(row.getCell(29)));
            salesReleaseDetail.setGoods_total_pay(ExcelUtils.getBigDecimal(row.getCell(30)));
            salesReleaseDetail.setGoods_cost(ExcelUtils.getBigDecimal(row.getCell(31)));
            salesReleaseDetail.setGoods_total_cost(ExcelUtils.getBigDecimal(row.getCell(32)));
            salesReleaseDetail.setOrder_pay_amount(ExcelUtils.getBigDecimal(row.getCell(33)));
            salesReleaseDetail.setAmount_receivable(ExcelUtils.getBigDecimal(row.getCell(34)));
            salesReleaseDetail.setBefore_refund_amount(ExcelUtils.getBigDecimal(row.getCell(35)));
            salesReleaseDetail.setSingle_pay_amount(ExcelUtils.getBigDecimal(row.getCell(36)));
            salesReleaseDetail.setApportion_postage(ExcelUtils.getBigDecimal(row.getCell(37)));
            salesReleaseDetail.setEstimate_postage(ExcelUtils.getBigDecimal(row.getCell(38)));
            salesReleaseDetail.setPostage_cost(ExcelUtils.getBigDecimal(row.getCell(39)));
            salesReleaseDetail.setOrder_pack_cost(ExcelUtils.getBigDecimal(row.getCell(40)));
            salesReleaseDetail.setOrder_gross_profit(ExcelUtils.getBigDecimal(row.getCell(41)));
            salesReleaseDetail.setGross_profit_rate(ExcelUtils.getBigDecimal(row.getCell(42)));
            salesReleaseDetail.setCustomer_net_name(ExcelUtils.getString(row.getCell(43)));
            salesReleaseDetail.setConsignee(ExcelUtils.getString(row.getCell(44)));
            salesReleaseDetail.setId_card(ExcelUtils.getString(row.getCell(45)));
            salesReleaseDetail.setReceiving_area(ExcelUtils.getString(row.getCell(46)));
            salesReleaseDetail.setReceiver_address(ExcelUtils.getString(row.getCell(47)));
            salesReleaseDetail.setReceiver_mobile_phone(ExcelUtils.getString(row.getCell(48)));
            salesReleaseDetail.setReceiver_phone(ExcelUtils.getString(row.getCell(49)));
            salesReleaseDetail.setLogistics_company(ExcelUtils.getString(row.getCell(50)));
            salesReleaseDetail.setActual_weight(ExcelUtils.getBigDecimal(row.getCell(51)));
            salesReleaseDetail.setEstimate_weight(ExcelUtils.getBigDecimal(row.getCell(52)));
            salesReleaseDetail.setNeed_invoice(ExcelUtils.getString(row.getCell(53)));
            salesReleaseDetail.setMake_order_user(ExcelUtils.getString(row.getCell(54)));
            salesReleaseDetail.setPrint_user(ExcelUtils.getString(row.getCell(55)));
            salesReleaseDetail.setOrder_picking_user(ExcelUtils.getString(row.getCell(56)));
            salesReleaseDetail.setPack_user(ExcelUtils.getString(row.getCell(57)));
            salesReleaseDetail.setInspector_user(ExcelUtils.getString(row.getCell(58)));
            salesReleaseDetail.setSalesman(ExcelUtils.getString(row.getCell(59)));
            salesReleaseDetail.setExamine_goods_user(ExcelUtils.getString(row.getCell(60)));
            salesReleaseDetail.setPrint_num(ExcelUtils.getString(row.getCell(61)));
            salesReleaseDetail.setLogistics_print_status(ExcelUtils.getString(row.getCell(62)));
            salesReleaseDetail.setDeliver_print_status(ExcelUtils.getString(row.getCell(63)));
            salesReleaseDetail.setSorting_print_status(ExcelUtils.getString(row.getCell(64)));
            salesReleaseDetail.setLogistics_num(ExcelUtils.getString(row.getCell(65)));
            salesReleaseDetail.setSorting_num(ExcelUtils.getString(row.getCell(66)));
            salesReleaseDetail.setExternal_order_num(ExcelUtils.getString(row.getCell(67)));
            salesReleaseDetail.setPay_time(ExcelUtils.getString(row.getCell(68)) == null ?
                    null : LocalDateTime.parse(ExcelUtils.getString(row.getCell(68)), DateUtil.DATE_TIME_FORMATTER));
            salesReleaseDetail.setDeliver_time(ExcelUtils.getString(row.getCell(69)) == null ?
                    null : LocalDateTime.parse(ExcelUtils.getString(row.getCell(69)), DateUtil.DATE_TIME_FORMATTER));
            salesReleaseDetail.setGift_way(ExcelUtils.getString(row.getCell(70)));
            salesReleaseDetail.setBuyer_message(ExcelUtils.getString(row.getCell(71)));
            salesReleaseDetail.setCustomer_service_remark(ExcelUtils.getString(row.getCell(72)));
            salesReleaseDetail.setPrint_remark(ExcelUtils.getString(row.getCell(73)));
            salesReleaseDetail.setRemark(ExcelUtils.getString(row.getCell(74)));
            salesReleaseDetail.setPackaging(ExcelUtils.getString(row.getCell(75)));
            salesReleaseDetail.setCombination_package_no(ExcelUtils.getString(row.getCell(76)));
            salesReleaseDetail.setSplit_combination_package(ExcelUtils.getString(row.getCell(77)));
            salesReleaseDetail.setCombination_package_num(ExcelUtils.getInteger(row.getCell(78)));
            salesReleaseDetail.setVolume(ExcelUtils.getBigDecimal(row.getCell(79)));
            String distributor = ExcelUtils.getString(row.getCell(80));
            salesReleaseDetail.setDistributor(distributor);
            salesReleaseDetail.setOrder_time(ExcelUtils.getString(row.getCell(81)) == null ?
                    null : LocalDateTime.parse(ExcelUtils.getString(row.getCell(81)), DateUtil.DATE_TIME_FORMATTER));
            salesReleaseDetail.setAudit_time(ExcelUtils.getString(row.getCell(82)) == null ?
                    null : LocalDateTime.parse(ExcelUtils.getString(row.getCell(82)), DateUtil.DATE_TIME_FORMATTER));
            salesReleaseDetail.setDistributor_no(ExcelUtils.getString(row.getCell(83)));
            salesReleaseDetail.setSource_type(StringUtils.hasText(distributor) ? 2 : 1);
            salesReleaseDetailList.add(salesReleaseDetail);
        }
        salesReleaseDetailService.insertBatch(salesReleaseDetailList);

    }

    private void uploadReturnWarehouseDetail(Sheet sheet, int totalRow) {

        List<ReturnWarehouseDetail> returnWarehouseDetailList = new ArrayList<>(totalRow - 1);
        //最后一行没合计的话需改成 i < totalRow + 1
        for (int i = 1; i < totalRow; i++) {
            Row row = sheet.getRow(i);
            ReturnWarehouseDetail returnWarehouseDetail = new ReturnWarehouseDetail();
            returnWarehouseDetail.setWarehouse_entry_no(ExcelUtils.getString(row.getCell(0)));
            returnWarehouseDetail.setWarehouse_entry_state(ExcelUtils.getString(row.getCell(1)));
            returnWarehouseDetail.setShop_no(ExcelUtils.getString(row.getCell(2)));
            returnWarehouseDetail.setGoods_no(ExcelUtils.getString(row.getCell(3)));
            returnWarehouseDetail.setGoods_name(ExcelUtils.getString(row.getCell(4)));
            returnWarehouseDetail.setSpecification_name(ExcelUtils.getString(row.getCell(5)));
            returnWarehouseDetail.setSpecification(ExcelUtils.getString(row.getCell(6)));
            returnWarehouseDetail.setBrand(ExcelUtils.getString(row.getCell(7)));
            returnWarehouseDetail.setCategory(ExcelUtils.getString(row.getCell(8)));
            returnWarehouseDetail.setSales_return_num(ExcelUtils.getInteger(row.getCell(9)));
            returnWarehouseDetail.setWarehouse(ExcelUtils.getString(row.getCell(10)));
            returnWarehouseDetail.setWarehouse_entry_num(ExcelUtils.getInteger(row.getCell(11)));
            returnWarehouseDetail.setAdjust_num(ExcelUtils.getInteger(row.getCell(12)));
            returnWarehouseDetail.setUnit_price(ExcelUtils.getBigDecimal(row.getCell(13)));
            returnWarehouseDetail.setEntry_total_amount(ExcelUtils.getBigDecimal(row.getCell(14)));
            returnWarehouseDetail.setAdjust_total_amount(ExcelUtils.getBigDecimal(row.getCell(15)));
            returnWarehouseDetail.setEntry_batch(ExcelUtils.getString(row.getCell(16)));
            returnWarehouseDetail.setEntry_goods_allocation(ExcelUtils.getString(row.getCell(17)));
            returnWarehouseDetail.setReturn_no(ExcelUtils.getString(row.getCell(18)));
            returnWarehouseDetail.setReturn_state(ExcelUtils.getString(row.getCell(19)));
            returnWarehouseDetail.setOrder_num(ExcelUtils.getString(row.getCell(20)));
            returnWarehouseDetail.setCustomer_net_name(ExcelUtils.getString(row.getCell(21)));
            returnWarehouseDetail.setConsignee(ExcelUtils.getString(row.getCell(22)));
            returnWarehouseDetail.setShop_name(ExcelUtils.getString(row.getCell(23)));
            returnWarehouseDetail.setReturn_reason(ExcelUtils.getString(row.getCell(24)));
            returnWarehouseDetail.setMake_time(ExcelUtils.getString(row.getCell(25)) == null ?
                    null : LocalDateTime.parse(ExcelUtils.getString(row.getCell(25)), DateUtil.DATE_TIME_FORMATTER));
            returnWarehouseDetail.setOriginal_order_num(ExcelUtils.getString(row.getCell(26)));
            returnWarehouseDetail.setSource_type(1);
            returnWarehouseDetailList.add(returnWarehouseDetail);
        }
        returnWarehouseDetailService.insertBatch(returnWarehouseDetailList);
        //更新订单来源类型为直播
        Integer id = returnWarehouseDetailList.get(0).getId().intValue();
        System.out.println("新增的第一个id为:" + id);
        List<ReturnWarehouseDetail> liveOrder = returnWarehouseDetailService.getLiveOrder(id);
        returnWarehouseDetailService.updateBatch(liveOrder);
    }
}
