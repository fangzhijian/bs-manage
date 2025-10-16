package com.bs.manage.service.console.impl;

import com.bs.manage.code.CodeCaption;
import com.bs.manage.constant.Constants;
import com.bs.manage.exception.MyRunException;
import com.bs.manage.intercepter.UserToken;
import com.bs.manage.mapper.console.DateReportMapper;
import com.bs.manage.model.Pair;
import com.bs.manage.model.bean.account.Team;
import com.bs.manage.model.bean.account.User;
import com.bs.manage.model.bean.console.BussProcessSummary;
import com.bs.manage.model.bean.console.DateReport;
import com.bs.manage.model.bean.console.DateReportProduct;
import com.bs.manage.model.bean.console.IdAndAmount;
import com.bs.manage.model.bean.console.IdAndCount;
import com.bs.manage.model.bean.console.ReportSummary;
import com.bs.manage.model.bean.console.TopUser;
import com.bs.manage.model.bean.customer.Customer;
import com.bs.manage.model.json.Page;
import com.bs.manage.model.json.ResponseJson;
import com.bs.manage.model.param.console.BoardParam;
import com.bs.manage.model.param.console.DateReportSearchParam;
import com.bs.manage.service.account.TeamService;
import com.bs.manage.service.account.UserService;
import com.bs.manage.service.common.impl.CommonServiceImpl;
import com.bs.manage.service.configures.ConfiguresService;
import com.bs.manage.service.console.BussProcessSummaryService;
import com.bs.manage.service.console.DateReportProductService;
import com.bs.manage.service.console.DateReportService;
import com.bs.manage.service.customer.CustomerService;
import com.bs.manage.until.CodeUtil;
import com.bs.manage.until.DateUtil;
import com.bs.manage.until.NumberUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 2020/3/4 12:05
 * fzj
 */
@Service
public class DateReportServiceImpl extends CommonServiceImpl<DateReport> implements DateReportService {

    private final DateReportMapper dateReportMapper;
    private final DateReportProductService dateReportProductService;
    private final CustomerService customerService;
    private final UserService userService;
    private final TeamService teamService;
    private final ConfiguresService configuresService;
    private final BussProcessSummaryService bussProcessSummaryService;

    public DateReportServiceImpl(DateReportMapper dateReportMapper, DateReportProductService dateReportProductService, CustomerService customerService, UserService userService, TeamService teamService, ConfiguresService configuresService, BussProcessSummaryService bussProcessSummaryService) {
        this.dateReportMapper = dateReportMapper;
        this.dateReportProductService = dateReportProductService;
        this.customerService = customerService;
        this.userService = userService;
        this.teamService = teamService;
        this.configuresService = configuresService;
        this.bussProcessSummaryService = bussProcessSummaryService;
    }


    /**
     * 初始化日报中的客户信息
     *
     * @param dateReport 日报信息
     * @return 客户信息
     */
    private Pair<User, Customer> initCustomerOfDateReport(DateReport dateReport) {
        if (dateReport.getDate() < 20200301) {
            throw new MyRunException("日报填写时间最早从2020年3月份开始");
        }
        Customer customer = customerService.getById(dateReport.getCustomer_id());
        if (customer == null) {
            throw new MyRunException("该客户不存在");
        }
        User user = UserToken.getContext();
        if (!user.getId().equals(customer.getProcess_user_id())) {
            throw new MyRunException("该客户还不是你的跟进人");
        }
        dateReport.setCustomer_name(customer.getName());
        dateReport.setCustomer_gradation(customer.getCustomer_gradation());
        dateReport.setActiveness(customer.getActiveness());
        dateReport.setCustomer_category_id(customer.getCustomer_category_id());
        return new Pair<>(user, customer);
    }

    @Override
    @Transactional
    public ResponseJson insert(DateReport bean) {


        int date = Integer.parseInt(LocalDateTime.now().format(DateUtil.yyyyMMdd_FORMATTER));
        int codeValue = configuresService.getCodeValue(Constants.CONFIG_REPORT_INSERT);
        if (CodeCaption.TRUE == codeValue && bean.getDate() != null) {
            date = bean.getDate();
        }
        bean.setDate(date);

        if (super.getOneBySelectKey(DateReport.builder().date(date).customer_id(bean.getCustomer_id()).build()) != null) {
            return ResponseJson.success("该客户当天日报已填写");
        }

        //初始化日报中的客户信息
        Pair<User, Customer> pair = initCustomerOfDateReport(bean);
        User user = pair.getKey();
        Customer customer = pair.getValue();

        if (bean.getReportProducts() == null) {
            bean.setReportProducts(new ArrayList<>());
        }
        BigDecimal total = bean.getReportProducts().stream().map(x -> x.getAmount() == null ? BigDecimal.ZERO : x.getAmount())
                .reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, BigDecimal.ROUND_HALF_EVEN);
        //客户总销售额额累加
        if (total.compareTo(BigDecimal.ZERO) > 0) {
            Customer updateCustomer = Customer.builder().id(bean.getCustomer_id()).total_amount(total).build();
            bean.setVisit_result(CodeCaption.VISIT_RESULT_DEAL);
            //当客户为休眠时,修改客户为任意其他状态以被定时任务扫描
            //定时任务会纠正活跃度,日报删除和修改方法不用再管
            if (customer.getActiveness().equals(CodeCaption.ACTIVENESS_SLEEP)) {
                updateCustomer.setActiveness(CodeCaption.ACTIVENESS_INACTIVE);
            }
            //设置最近和前一次交易时间,原最近时间变前次交易时间
            updateCustomer.setPrev_deal_time(customer.getNear_deal_time());
            updateCustomer.setNear_deal_time(LocalDateTime.now());
            customerService.updateOnly(updateCustomer);
        } else {
            if (CodeCaption.VISIT_RESULT_DEAL == bean.getVisit_result()) {
                return ResponseJson.fail("没有成交金额,请选择其他拜访结果");
            }
        }

        //保存日报
        bean.setTeam_id(user.getTeam_id());
        bean.setUser_id(user.getId());
        super.insert(bean);

        //保存日报中的产品信息
        if (bean.getReportProducts().size() > 0) {
            for (DateReportProduct dateReportProduct : bean.getReportProducts()) {
                dateReportProduct.setAmount(dateReportProduct.getAmount() == null ? BigDecimal.ZERO : dateReportProduct.getAmount());
                dateReportProduct.setDate_report_id(bean.getId());
            }
            dateReportProductService.insertBatch(bean.getReportProducts());
        }

        return ResponseJson.success();
    }


    @Override
    @Transactional
    public ResponseJson delete(Long id) {
        DateReport dateReport = checkDateReport(id, "删除");
        super.delete(id);

        //客户减少金额
        List<DateReportProduct> dateReportProducts = dateReportProductService.getAllBySelectKey(DateReportProduct.builder().date_report_id(dateReport.getId()).build());
        BigDecimal total = BigDecimal.ZERO;
        for (DateReportProduct dateReportProduct : dateReportProducts) {
            total = total.add(dateReportProduct.getAmount()).setScale(2, BigDecimal.ROUND_HALF_EVEN);
        }

        //删除日报中的产品
        dateReportProductService.deleteByDateReportId(dateReport.getId());

        if (total.compareTo(BigDecimal.ZERO) > 0) {
            Customer customer = customerService.getById(dateReport.getCustomer_id());
            //新增交易时原最近变前次时间,现恢复为最新
            LocalDateTime near_deal_time = customer.getPrev_deal_time();
            LocalDateTime prevTime = dateReportMapper.getPrevTimeWhenDelete(dateReport.getId(), customer.getId());
            customerService.updateOnly(Customer.builder().id(dateReport.getCustomer_id()).total_amount(total.negate()).
                    prev_deal_time(prevTime).near_deal_time(near_deal_time).build());
        }
        return ResponseJson.success();
    }

    @Override
    @Transactional
    public ResponseJson update(DateReport bean) {

        DateReport dateReport = checkDateReport(bean.getId(), "修改");
        bean.setDate(dateReport.getDate());

        //初始化日报中的客户信息
        Pair<User, Customer> pair = initCustomerOfDateReport(bean);
        Customer customer = pair.getValue();

        super.update(dateReport);
        List<DateReportProduct> dateReportProducts = dateReportProductService.getAllBySelectKey(DateReportProduct.builder().date_report_id(dateReport.getId()).build());

        List<Long> deleteList = new ArrayList<>();
        List<DateReportProduct> updateList = new ArrayList<>();
        List<DateReportProduct> insertList = new ArrayList<>();

        BigDecimal total = BigDecimal.ZERO;

        List<Long> oldIds = dateReportProducts.stream().map(DateReportProduct::getProduct_id).collect(Collectors.toList());
        boolean hasDeal = false; //是否有成交
        for (DateReportProduct newProduct : bean.getReportProducts()) {
            newProduct.setAmount(newProduct.getAmount() == null ? BigDecimal.ZERO : newProduct.getAmount());
            if (!oldIds.contains(newProduct.getProduct_id())) {
                newProduct.setDate_report_id(dateReport.getId());
                insertList.add(newProduct);
                //新增时增加金额
                total = total.add(newProduct.getAmount());
            }
            if (newProduct.getAmount().compareTo(BigDecimal.ZERO) > 0) {
                hasDeal = true;
            }
        }
        if (hasDeal) {
            bean.setVisit_result(CodeCaption.VISIT_RESULT_DEAL);
        } else {
            if (CodeCaption.VISIT_RESULT_DEAL == bean.getVisit_result()) {
                return ResponseJson.fail("没有成交金额,请选择其他拜访结果");
            }
        }

        for (DateReportProduct oldProduct : dateReportProducts) {
            boolean needDelete = true;
            for (DateReportProduct newProduct : bean.getReportProducts()) {
                if (oldProduct.getProduct_id().equals(newProduct.getProduct_id())) {
                    needDelete = false;
                    newProduct.setId(oldProduct.getId());
                    updateList.add(newProduct);
                    //修改时增加(原金额-旧金额)
                    total = total.add(newProduct.getAmount().subtract(oldProduct.getAmount()));
                }
            }
            if (needDelete) {
                deleteList.add(oldProduct.getId());
                //删除时减少金额
                total = total.subtract(oldProduct.getAmount());
            }
        }

        if (insertList.size() > 0) {
            dateReportProductService.insertBatch(insertList);
        }
        if (deleteList.size() > 0) {
            dateReportProductService.deleteByIds(deleteList);
        }
        if (updateList.size() > 0) {
            dateReportProductService.updateBatch(updateList);
        }

        //修改客户总金额
        total = total.setScale(2, BigDecimal.ROUND_HALF_EVEN);
        Customer updateCustomer = Customer.builder().id(dateReport.getCustomer_id()).total_amount(total).build();
        //原日报有交易行为,改成无交易,需修改交易时间
        if (CodeCaption.VISIT_RESULT_DEAL == dateReport.getVisit_result() && CodeCaption.VISIT_RESULT_DEAL != bean.getVisit_result()) {
            //新增交易时原最近变前次时间,现恢复为最新
            LocalDateTime near_deal_time = customer.getPrev_deal_time();
            LocalDateTime prevTime = dateReportMapper.getPrevTimeWhenDelete(dateReport.getId(), customer.getId());
            updateCustomer.setNear_deal_time(near_deal_time);
            updateCustomer.setPrev_deal_time(prevTime);
        }
        customerService.updateOnly(updateCustomer);
        return super.update(bean);
    }


    /**
     * 检查日报能否被改动
     *
     * @param id     日报id
     * @param errMsg 错误提示信息
     * @return 日报信息
     */
    private DateReport checkDateReport(Long id, String errMsg) {
        DateReport dateReport = super.getById(id);
        int codeValue = configuresService.getCodeValue(Constants.CONFIG_REPORT1_UPDATE);
        if (CodeCaption.FALSE == codeValue) {
            String today = LocalDateTime.now().format(DateUtil.yyyyMMdd_FORMATTER);
            if (!today.equals(dateReport.getDate().toString())) {
                throw new MyRunException(String.format("只能%s当天的日报", errMsg));
            }
        }
        User user = UserToken.getContext();
        if (!user.getId().equals(dateReport.getUser_id())) {
            throw new MyRunException(String.format("不能%s他人的日报", errMsg));
        }
        return dateReport;
    }

    @Override
    public void afterPropertiesSet() {
        setCommonMapper(dateReportMapper);
    }

    @Override
    public Page<DateReport> getByPage(DateReportSearchParam param) {
        User user = UserToken.getContext();
        Page<DateReport> page = new Page<>();
        if (CodeCaption.ROLE_SALE != user.getRole() && CodeCaption.ROLE_ADMIN != user.getRole()){
            return page;
        }
        if (CodeCaption.ROLE_SALE == user.getRole() && !param.getQueryByCustomer()) {
            List<Long> user_ids = userService.subordinateForSale(user).stream().map(User::getId).collect(Collectors.toList());
            param.setUser_ids(user_ids);
        }
        int total = dateReportMapper.countByPage(param);
        page.setTotal(total);
        if (total > 0) {
            List<DateReport> dateReports = dateReportMapper.getByPage(param);
            for (DateReport dateReport : dateReports) {
                dateReport.setVisit_result_label(CodeUtil.getCaption(CodeCaption.VISIT_RESULT, dateReport.getVisit_result()));
                dateReport.setAfter_action_label(CodeUtil.getCaption(CodeCaption.AFTER_ACTION, dateReport.getAfter_action()));
            }
            page.setItems(dateReports);
        }
        return page;
    }

    /**
     * 查询当前账号拥有的客户
     *
     * @return 当前账号拥有的客户
     */
    @Override
    public List<Customer> getOwmCustomer() {
        User user = UserToken.getContext();
        return customerService.getAllBySelectKey(Customer.builder().process_user_id(user.getId()).build());
    }


    /**
     * 根据id得到不同的统计
     *
     * @param goal_month 目标月份
     * @param groupId    要统计的id
     * @param userIds    账号id列表
     * @param monthEnd   查询月份最大天数
     * @return 返回统计值
     */
    @Override
    public List<IdAndAmount> summaryGroupId(Integer goal_month, String groupId, List<Long> userIds, Integer monthEnd) {
        return dateReportMapper.summaryGroupId(goal_month, groupId, userIds, monthEnd);
    }

    /**
     * 统计覆盖数、拜访数、成交数、活跃数
     *
     * @param queryType 1-覆盖数 2-拜访数 3-成交数 4-活跃数
     * @param month     查询月份
     * @return 返回统计值
     */
    @Override
    public List<IdAndCount> summaryByType(Integer queryType, Integer month) {
        return dateReportMapper.summaryByType(queryType, month);
    }

    /**
     * 获取客单价
     *
     * @param userIds   账号id列表
     * @param queryType 1-团队 2-客户
     * @param month     查询月份
     * @param monthEnd  查询月的最大天数
     * @return 客单价
     */
    @Override
    public List<IdAndAmount> getUnitPrice(Integer queryType, List<Long> userIds, Integer month, Integer monthEnd) {
        return dateReportMapper.getUnitPrice(queryType, userIds, month, monthEnd);
    }

    /**
     * 统计拜访行为情况
     *
     * @param userIds   账号id列表
     * @param queryType 1-团队 2-客户
     * @param date      查询日期
     * @return 返回统计值
     */
    @Override
    public List<ReportSummary> summaryAction(List<Long> userIds, Integer queryType, Integer date) {
        return dateReportMapper.summaryAction(userIds, queryType, date);
    }

    /**
     * 销售排行
     *
     * @param param 入参
     * @return 销售排行信息
     */
    @Override
    public Page<TopUser> topSale(BoardParam param) {
        if (param.getOrder_type() == null) {
            param.setOrder_type(1);
        }
        List<User> users = userService.getAllBySelectKey(User.builder().role(CodeCaption.ROLE_SALE).team_id(param.getTeam_id()).status(CodeCaption.STATUS_OK).build());
        List<Long> leaderUserIds = teamService.getAll().stream().map(Team::getLeader_id).collect(Collectors.toList());
        List<TopUser> topUsers = new ArrayList<>();
        List<TopUser> prevTopUsers = new ArrayList<>();
        for (User user : users) {
            if (!leaderUserIds.contains(user.getId())) {
                topUsers.add(new TopUser().setId(user.getId()).setName(user.getName()));
                prevTopUsers.add(new TopUser().setId(user.getId()).setName(user.getName()));
            }
        }

        //查询月排行信息
        setTopUsers(topUsers, param, false);
        //查询月的上一个月排行信息
        setTopUsers(prevTopUsers, param, true);

        //设置排序差值
        for (TopUser topUser : topUsers) {
            for (TopUser prevTopUser : prevTopUsers) {
                if (topUser.getId().equals(prevTopUser.getId())) {
                    topUser.setOrder_diff(prevTopUser.getOrder() - topUser.getOrder());
                    break;
                }
            }
        }
        Page<TopUser> page = new Page<>();
        page.setItems(topUsers);
        page.setTotal(topUsers.size());
        return page;
    }

    /**
     * 设置用户排序数据
     *
     * @param topUsers 用户信息
     * @param param    入参
     * @param prev     是否是前一个月
     */
    private void setTopUsers(List<TopUser> topUsers, BoardParam param, boolean prev) {
        List<BussProcessSummary> summaryList;
        List<IdAndAmount> idAndAmountList;
        if (prev) {
            summaryList = bussProcessSummaryService.getAllBySelectKey(BussProcessSummary.builder().team_id(param.getTeam_id()).summary_month(param.getPrevMoth()).build());
            idAndAmountList = dateReportMapper.topSale(param.getPrevMoth(), param.getPrevMoth() * 100 + 31, param.getTeam_id());
        } else {
            summaryList = bussProcessSummaryService.getAllBySelectKey(BussProcessSummary.builder().team_id(param.getTeam_id()).summary_month(param.getMonth()).build());
            idAndAmountList = dateReportMapper.topSale(param.getMonth(), param.getMonthEnd(), param.getTeam_id());
        }

        //设置销售金额、成交数、拜访数
        for (TopUser topUser : topUsers) {
            for (IdAndAmount idAndAmount : idAndAmountList) {
                if (idAndAmount.getId().equals(topUser.getId())) {
                    topUser.setAmount(NumberUtil.toTenThouAmount(idAndAmount.getAmount()));
                    break;
                }
            }
            for (BussProcessSummary summary : summaryList) {
                if (summary.getUser_id().equals(topUser.getId())) {
                    topUser.setDeal_num(topUser.getDeal_num() + summary.getDeal_s() + summary.getDeal_t() + summary.getDeal_y() + summary.getDeal_c());
                    topUser.setVisit_num(topUser.getVisit_num() + summary.getVisit_s() + summary.getVisit_t() + summary.getVisit_y() + summary.getVisit_c());
                }
            }
        }

        //设置成交率
        for (TopUser topUser : topUsers) {
            topUser.setDeal_ratio(NumberUtil.getPercentBigDecimal(topUser.getDeal_num(), topUser.getVisit_num(), 0));
        }

        //倒序排序,相同时id小优先
        if (param.getOrder_type() == 1) {
            topUsers.sort((o1, o2) -> {
                if (o1.getAmount().equals(o2.getAmount())) {
                    return o1.getId().compareTo(o2.getId());
                } else {
                    return o2.getAmount().compareTo(o1.getAmount());
                }
            });
        } else if (param.getOrder_type() == 2) {
            topUsers.sort((o1, o2) -> {
                if (o1.getAmount().equals(o2.getAmount())) {
                    return o1.getId().compareTo(o2.getId());
                } else {
                    return o2.getDeal_num().compareTo(o1.getDeal_num());
                }
            });
        } else if (param.getOrder_type() == 3) {
            topUsers.sort((o1, o2) -> {
                if (o1.getAmount().equals(o2.getAmount())) {
                    return o1.getId().compareTo(o2.getId());
                } else {
                    return o2.getDeal_ratio().compareTo(o1.getDeal_ratio());
                }
            });
        }

        //设置排序号
        int order = 0;
        for (TopUser topUser : topUsers) {
            order++;
            topUser.setOrder(order);
        }
    }


    /**
     * 分组统计日报数与客户数
     *
     * @param userIds   账号id列表
     * @param queryType 1-团队 2-客户
     * @param month     查询月份
     * @param monthEnd  查询月最大天数
     * @return 客户个数
     */
    @Override
    public List<IdAndCount> getCountIdByGroupId(Integer queryType, List<Long> userIds, Integer month, Integer monthEnd) {
        return dateReportMapper.getCountIdByGroupId(queryType, userIds, month, monthEnd);
    }

    /**
     * 获取有成交的客户去重复的日报列表
     *
     * @param userIds  账号id列表
     * @param month    查询月份
     * @param monthEnd 查询月的最大天数
     * @return 日报列表
     */
    @Override
    public List<DateReport> distinctCustomerIdForDeal(List<Long> userIds, Integer month, Integer monthEnd) {
        return dateReportMapper.distinctCustomerIdForDeal(userIds, month, monthEnd);
    }

}
