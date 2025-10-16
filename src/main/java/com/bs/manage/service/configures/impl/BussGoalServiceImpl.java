package com.bs.manage.service.configures.impl;

import com.bs.manage.code.CodeCaption;
import com.bs.manage.exception.MyRunException;
import com.bs.manage.model.bean.account.Team;
import com.bs.manage.model.bean.account.User;
import com.bs.manage.model.bean.common.CommonModel;
import com.bs.manage.model.bean.console.BussProcessGoal;
import com.bs.manage.model.bean.console.BussProcessSummary;
import com.bs.manage.model.bean.console.BussResultGoal;
import com.bs.manage.model.bean.customer.CustomerCategory;
import com.bs.manage.model.bean.product.ProductProject;
import com.bs.manage.model.json.ResponseJson;
import com.bs.manage.model.param.configures.ProcessGoalDisplay;
import com.bs.manage.model.param.configures.ProcessGoalInfo;
import com.bs.manage.model.param.configures.ResultGoalDisplay;
import com.bs.manage.model.param.configures.ResultGoalInfo;
import com.bs.manage.model.param.configures.ResultGoalResult;
import com.bs.manage.service.account.TeamService;
import com.bs.manage.service.account.UserService;
import com.bs.manage.service.configures.BussGoalService;
import com.bs.manage.service.console.BussProcessGoalService;
import com.bs.manage.service.console.BussProcessSummaryService;
import com.bs.manage.service.console.BussResultGoalService;
import com.bs.manage.service.customer.CustomerCategoryService;
import com.bs.manage.service.product.ProductProjectService;
import com.bs.manage.until.DateUtil;
import com.bs.manage.until.ExcelUtils;
import com.bs.manage.until.NumberUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 2020/3/12 10:49
 * fzj
 */
@Service
public class BussGoalServiceImpl implements BussGoalService {


    private final UserService userService;
    private final BussResultGoalService bussResultGoalService;
    private final BussProcessSummaryService bussProcessSummaryService;
    private final BussProcessGoalService bussProcessGoalService;
    private final TeamService teamService;
    private final CustomerCategoryService customerCategoryService;
    private final ProductProjectService productProjectService;

    public BussGoalServiceImpl(UserService userService, BussResultGoalService bussResultGoalService, BussProcessSummaryService bussProcessSummaryService, BussProcessGoalService bussProcessGoalService,
                               TeamService teamService, CustomerCategoryService customerCategoryService, ProductProjectService productProjectService) {
        this.userService = userService;
        this.bussResultGoalService = bussResultGoalService;
        this.bussProcessSummaryService = bussProcessSummaryService;
        this.bussProcessGoalService = bussProcessGoalService;
        this.teamService = teamService;
        this.customerCategoryService = customerCategoryService;
        this.productProjectService = productProjectService;
    }

    /**
     * 业务过程目标导入
     *
     * @param file excel文件
     * @return 是否导入成功
     */
    @Override
    public ResponseJson processImport(MultipartFile file) {

        //1 设置全局的覆盖百分比
        Sheet sheet = ExcelUtils.getSheet(file);
        int goal_month = 0;                 //目标月份
        long team_id = 0;                   //团队id
        //零售的百分比
        BussProcessGoal retailerGoalPercent = new BussProcessGoal();
        retailerGoalPercent.setCustomer_category_id(CustomerCategory.RETAILER);
        //经销商的百分比
        BussProcessGoal dealerGoalPercent = new BussProcessGoal();
        dealerGoalPercent.setCustomer_category_id(CustomerCategory.DEALER);
        for (int i = 0; i < 12; i++) {
            Row row = sheet.getRow(i);
            if (row == null) {
                continue;
            }
            if (i == 0) {
                goal_month = ExcelUtils.getInteger(row.getCell(0));
                team_id = ExcelUtils.getInteger(row.getCell(1));
            } else if (i >= 4 && i <= 7) {
                setGoalValue(retailerGoalPercent, row, i);
            } else if (i >= 8) {
                setGoalValue(dealerGoalPercent, row, i);
            }
        }

        //2 保存当前团队的所有销售专员业务过程目标(全局百分比乘历史值)
        List<User> users = userService.getAllBySelectKey(User.builder().team_id(team_id).role(CodeCaption.ROLE_SALE).build());
        //上一个月的业务过程
        int pre_month = Integer.parseInt(LocalDate.parse(String.valueOf(goal_month + "01"), DateUtil.yyyyMMdd_FORMATTER).minusMonths(1).format(DateUtil.yyyyMM_FORMATTER));
        List<BussProcessSummary> summaryList = bussProcessSummaryService.getAllBySelectKey(BussProcessSummary.builder().summary_month(pre_month).build());

        List<BussProcessGoal> bussProcessGoalList = new ArrayList<>(users.size());
        for (User user : users) {
            //零售
            bussProcessGoalList.add(setProcessGoal(CustomerCategory.RETAILER, goal_month, user.getId(), team_id, summaryList, retailerGoalPercent));
            //经销商
            bussProcessGoalList.add(setProcessGoal(CustomerCategory.DEALER, goal_month, user.getId(), team_id, summaryList, dealerGoalPercent));
        }

        //3 修改单个销售专员指定的几个目标
        int total = sheet.getLastRowNum();
        Long userId = null;
        BussProcessGoal processGoal = null;
        if (total > 15) {
            for (int i = 15; i < total + 1; i++) {
                Row row = sheet.getRow(i);
                if (row == null) {
                    continue;
                }
                if ((i - 15) % 12 == 0) {
                    userId = ExcelUtils.getLong(row.getCell(0));
                    continue;
                }
                if (userId != null) {
                    if ((i - 16) % 12 == 0) {
                        final long user_id = userId;
                        Optional<BussProcessGoal> first = bussProcessGoalList.stream().filter(x -> x.getUser_id().equals(user_id) && x.getCustomer_category_id().equals(1L)).findFirst();
                        if (first.isPresent()) {
                            processGoal = first.get();
                        }
                    }
                    if ((i - 20) % 12 == 0) {
                        final long user_id = userId;
                        Optional<BussProcessGoal> first = bussProcessGoalList.stream().filter(x -> x.getUser_id().equals(user_id) && x.getCustomer_category_id().equals(2L)).findFirst();
                        if (first.isPresent()) {
                            processGoal = first.get();
                        }
                    }
                    if (processGoal == null) {
                        throw new MyRunException(String.format("用户id%s不在整体团队中", userId));
                    }
                    if ((i - 16) % 12 == 0 || (i - 17) % 12 == 0 || (i - 18) % 12 == 0 || (i - 19) % 12 == 0) {
                        setGoalValue(processGoal, row, i);
                    } else if ((i - 20) % 12 == 0 || (i - 21) % 12 == 0 || (i - 22) % 12 == 0 || (i - 23) % 12 == 0) {
                        setGoalValue(processGoal, row, i);
                        if ((i - 23) % 12 == 0) {
                            userId = null;
                        }
                    }
                }
            }
        }
        bussProcessGoalService.insertDuplicateBatch(bussProcessGoalList);
        return ResponseJson.success();
    }

    /**
     * 设置各目标及全局百分比,全局百分比不会为空
     *
     * @param processGoal 过程目标
     * @param row         excel行
     * @param i           excel第i行
     */
    private void setGoalValue(BussProcessGoal processGoal, Row row, int i) {
        //第3列到第6列分别为 覆盖、拜访、成交、活跃
        Integer cover = ExcelUtils.getInteger(row.getCell(2));
        Integer visit = ExcelUtils.getInteger(row.getCell(3));
        Integer deal = ExcelUtils.getInteger(row.getCell(4));
        Integer active = ExcelUtils.getInteger(row.getCell(5));
        if (i == 4 || i == 8 || (i - 16) % 12 == 0 || (i - 20) % 12 == 0) {
            if (cover != null) {
                processGoal.setCover_s(cover);
            }
            if (visit != null) {
                processGoal.setVisit_s(visit);
            }
            if (deal != null) {
                processGoal.setDeal_s(deal);
            }
            if (active != null) {
                processGoal.setActive_s(active);
            }
        } else if (i == 5 || i == 9 || (i - 17) % 12 == 0 || (i - 21) % 12 == 0) {
            if (cover != null) {
                processGoal.setCover_t(cover);
            }
            if (visit != null) {
                processGoal.setVisit_t(visit);
            }
            if (deal != null) {
                processGoal.setDeal_t(deal);
            }
            if (active != null) {
                processGoal.setActive_t(active);
            }
        } else if (i == 6 || i == 10 || (i - 18) % 12 == 0 || (i - 22) % 12 == 0) {
            if (cover != null) {
                processGoal.setCover_y(cover);
            }
            if (visit != null) {
                processGoal.setVisit_y(visit);
            }
            if (deal != null) {
                processGoal.setDeal_y(deal);
            }
            if (active != null) {
                processGoal.setActive_y(active);
            }
        } else if (i == 7 || i == 11 || (i - 19) % 12 == 0 || (i - 23) % 12 == 0) {
            if (cover != null) {
                processGoal.setCover_c(cover);
            }
            if (visit != null) {
                processGoal.setVisit_c(visit);
            }
            if (deal != null) {
                processGoal.setDeal_c(deal);
            }
            if (active != null) {
                processGoal.setActive_c(active);
            }
        }
    }

    /**
     * 配置各目标
     *
     * @param category_id 客户一级属性id
     * @param goal_month  目标月份
     * @param userId      账号id
     * @param team_id     团队id
     * @param summaryList 上个月的汇总数据
     * @param goalPercent 整体目标百分比
     * @return 配置后的目标
     */
    private BussProcessGoal setProcessGoal(long category_id, int goal_month, long userId, long team_id, List<BussProcessSummary> summaryList, BussProcessGoal goalPercent) {
        Optional<BussProcessSummary> first = summaryList.stream().filter(x -> x.getCustomer_category_id().equals(category_id) && x.getUser_id().equals(userId)).findFirst();
        BussProcessGoal goal = BussProcessGoal.builder().user_id(userId).team_id(team_id).customer_category_id(category_id).goal_month(goal_month).build();
        BussProcessSummary summary = null;
        if (first.isPresent()) {
            summary = first.get();
        }
        //覆盖客户数目标=实际覆盖客户数*覆盖率目标
        goal.setCover_s(summary == null ? 0 : new BigDecimal(summary.getCover_s() * goalPercent.getCover_s()).divide(NumberUtil.HUNDRED, BigDecimal.ROUND_HALF_UP).intValue());
        goal.setCover_t(summary == null ? 0 : new BigDecimal(summary.getCover_t() * goalPercent.getCover_t()).divide(NumberUtil.HUNDRED, BigDecimal.ROUND_HALF_UP).intValue());
        goal.setCover_y(summary == null ? 0 : new BigDecimal(summary.getCover_y() * goalPercent.getCover_y()).divide(NumberUtil.HUNDRED, BigDecimal.ROUND_HALF_UP).intValue());
        goal.setCover_c(summary == null ? 0 : new BigDecimal(summary.getCover_c() * goalPercent.getCover_c()).divide(NumberUtil.HUNDRED, BigDecimal.ROUND_HALF_UP).intValue());
        //拜访客户数目标=覆盖客户数目标*拜访率目标
        goal.setVisit_s(summary == null ? 0 : new BigDecimal(goal.getCover_s() * goalPercent.getVisit_s()).divide(NumberUtil.HUNDRED, BigDecimal.ROUND_HALF_UP).intValue());
        goal.setVisit_t(summary == null ? 0 : new BigDecimal(goal.getCover_t() * goalPercent.getVisit_t()).divide(NumberUtil.HUNDRED, BigDecimal.ROUND_HALF_UP).intValue());
        goal.setVisit_y(summary == null ? 0 : new BigDecimal(goal.getCover_y() * goalPercent.getVisit_y()).divide(NumberUtil.HUNDRED, BigDecimal.ROUND_HALF_UP).intValue());
        goal.setVisit_c(summary == null ? 0 : new BigDecimal(goal.getCover_c() * goalPercent.getVisit_c()).divide(NumberUtil.HUNDRED, BigDecimal.ROUND_HALF_UP).intValue());
        //成交客户数目标=拜访客户数目标*成交率目标
        goal.setDeal_s(summary == null ? 0 : new BigDecimal(goal.getVisit_s() * goalPercent.getDeal_s()).divide(NumberUtil.HUNDRED, BigDecimal.ROUND_HALF_UP).intValue());
        goal.setDeal_t(summary == null ? 0 : new BigDecimal(goal.getVisit_t() * goalPercent.getDeal_t()).divide(NumberUtil.HUNDRED, BigDecimal.ROUND_HALF_UP).intValue());
        goal.setDeal_y(summary == null ? 0 : new BigDecimal(goal.getVisit_y() * goalPercent.getDeal_y()).divide(NumberUtil.HUNDRED, BigDecimal.ROUND_HALF_UP).intValue());
        goal.setDeal_c(summary == null ? 0 : new BigDecimal(goal.getVisit_c() * goalPercent.getDeal_c()).divide(NumberUtil.HUNDRED, BigDecimal.ROUND_HALF_UP).intValue());
        //活跃客户数目标=覆盖客户数目标*活跃客户占比目标
        goal.setActive_s(summary == null ? 0 : new BigDecimal(goal.getCover_s() * goalPercent.getActive_s()).divide(NumberUtil.HUNDRED, BigDecimal.ROUND_HALF_UP).intValue());
        goal.setActive_t(summary == null ? 0 : new BigDecimal(goal.getCover_t() * goalPercent.getActive_t()).divide(NumberUtil.HUNDRED, BigDecimal.ROUND_HALF_UP).intValue());
        goal.setActive_y(summary == null ? 0 : new BigDecimal(goal.getCover_y() * goalPercent.getActive_y()).divide(NumberUtil.HUNDRED, BigDecimal.ROUND_HALF_UP).intValue());
        goal.setActive_c(summary == null ? 0 : new BigDecimal(goal.getCover_c() * goalPercent.getActive_c()).divide(NumberUtil.HUNDRED, BigDecimal.ROUND_HALF_UP).intValue());
        return goal;
    }


    /**
     * 业务结果目标导入
     *
     * @param file excel文件
     * @return 是否导入成功
     */
    @Override
    public ResponseJson resultImport(MultipartFile file) {
        Sheet sheet = ExcelUtils.getSheet(file);
        int totalRow = sheet.getLastRowNum();
        int goal_month = 0;         //目标月份
        long userId = 0;            //账号id
        long productProjectId;      //项目id
        long teamId = 0;
        int groupRow = 10;          //每组数据有几行
        List<BussResultGoal> bussResultGoalList = new ArrayList<>();
        for (int i = 0; i < totalRow + 1; i++) {
            Row row = sheet.getRow(i);
            if (row == null) {
                continue;
            }
            if (i == 0) {
                goal_month = ExcelUtils.getInteger(row.getCell(0));
                continue;
            }
            if ((i - 4) % groupRow == 0) {
                userId = ExcelUtils.getLong(row.getCell(0));
                User user = userService.getById(userId);
                teamId = user.getTeam_id();
                continue;
            }
            if ((i - 5) % groupRow == 0) {
                productProjectId = 1L;  //JayJun常规id
            } else if ((i - 6) % groupRow == 0) {
                productProjectId = 2L;  //紫苏id
            } else if ((i - 7) % groupRow == 0) {
                productProjectId = 3L;  //香蒲丽常规id
            } else if ((i - 8) % groupRow == 0) {
                productProjectId = 4L;  //防晒id
            } else if ((i - 9) % groupRow == 0) {
                productProjectId = 5L;  //DVQid
            } else if ((i - 10) % groupRow == 0) {
                productProjectId = 6L;  //SR
            } else {
                continue;
            }
            //零售id 1L
            BussResultGoal retailerGoal = BussResultGoal.builder().goal_month(goal_month).user_id(userId).team_id(teamId).customer_category_id(CustomerCategory.RETAILER).product_project_id(productProjectId).build();
            bussResultGoalList.add(retailerGoal);
            retailerGoal.setAmount_s(ExcelUtils.getBigDecimal(row.getCell(1)));
            retailerGoal.setAmount_t(ExcelUtils.getBigDecimal(row.getCell(2)));
            retailerGoal.setAmount_y(ExcelUtils.getBigDecimal(row.getCell(3)));
            retailerGoal.setAmount_c(ExcelUtils.getBigDecimal(row.getCell(4)));
            //经销商id 2L
            BussResultGoal dealerGoal = BussResultGoal.builder().goal_month(goal_month).user_id(userId).team_id(teamId).customer_category_id(CustomerCategory.DEALER).product_project_id(productProjectId).build();
            bussResultGoalList.add(dealerGoal);
            dealerGoal.setAmount_s(ExcelUtils.getBigDecimal(row.getCell(5)));
            dealerGoal.setAmount_t(ExcelUtils.getBigDecimal(row.getCell(6)));
            dealerGoal.setAmount_y(ExcelUtils.getBigDecimal(row.getCell(7)));
            dealerGoal.setAmount_c(ExcelUtils.getBigDecimal(row.getCell(8)));
        }
        bussResultGoalService.insertDuplicateBatch(bussResultGoalList);
        return ResponseJson.success();
    }

    @Override
    public List<ProcessGoalDisplay> getAllProcess(BussProcessGoal bussProcessGoal) {
        if (bussProcessGoal.getGoal_month() == null) {
            bussProcessGoal.setGoal_month(Integer.valueOf(LocalDateTime.now().format(DateUtil.yyyyMM_FORMATTER)));
        }

        List<ProcessGoalDisplay> displays = new ArrayList<>();
        Map<Long, List<BussProcessGoal>> processGoalMap = bussProcessGoalService.getAllBySelectKey(bussProcessGoal).stream()
                .sorted(((o1, o2) -> {
                    if (o1.getUser_id().equals(o2.getUser_id())) {
                        return o1.getCustomer_category_id().compareTo(o2.getCustomer_category_id());
                    } else {
                        return o1.getUser_id().compareTo(o2.getUser_id());
                    }
                })).collect(Collectors.groupingBy(BussProcessGoal::getUser_id));
        if (processGoalMap.isEmpty()) {
            return displays;
        }
        Map<Long, String> categoryMap = customerCategoryService.getAllBySelectKey(CustomerCategory.builder().parent_id(0L).build()).stream().collect(Collectors.toMap(CommonModel::getId, CustomerCategory::getName));
        Map<Long, String> userMap = userService.getAllBySelectKey(User.builder().role(CodeCaption.ROLE_SALE).build()).stream().collect(Collectors.toMap(CommonModel::getId, User::getName));
        Map<Long, String> teamMap = teamService.getAll().stream().collect(Collectors.toMap(CommonModel::getId, Team::getName));

        for (Long userId : processGoalMap.keySet()) {
            ProcessGoalDisplay display = new ProcessGoalDisplay();
            display.setUser_id(userId);
            display.setUser_name(userMap.get(userId));
            List<ProcessGoalInfo> infos = new ArrayList<>();
            List<BussProcessGoal> processGoals = processGoalMap.get(userId);
            for (BussProcessGoal processGoal : processGoals) {
                ProcessGoalInfo info = new ProcessGoalInfo();
                BeanUtils.copyProperties(processGoal, info);
                info.setTeam_name(teamMap.get(info.getTeam_id()));
                info.setCustomer_category_name(categoryMap.get(info.getCustomer_category_id()));
                infos.add(info);
            }
            display.setInfos(infos);

            displays.add(display);
        }

        return displays;

    }

    @Override
    @Transactional
    public ResponseJson updateProcess(BussProcessGoal bussProcessGoal) {
        return bussProcessGoalService.update(bussProcessGoal);
    }


    @Override
    public ResultGoalResult getAllResult(BussResultGoal bussResultGoal) {
        if (bussResultGoal.getGoal_month() == null) {
            bussResultGoal.setGoal_month(Integer.valueOf(LocalDateTime.now().format(DateUtil.yyyyMM_FORMATTER)));
        }
        if (bussResultGoal.getTeam_id() == null) {
            bussResultGoal.setTeam_id(1L); //默认使用第一个团队
        }

        ResultGoalResult result = new ResultGoalResult();
        List<ResultGoalDisplay> displays = new ArrayList<>();
        result.setDisplays(displays);
        result.setTeam_total(bussResultGoalService.getTotalMoneyBySelectKey(bussResultGoal));

        Map<Long, List<BussResultGoal>> resultGoalMap = bussResultGoalService.getAllBySelectKey(bussResultGoal).stream()
                .sorted((o1, o2) -> {
                    if (o1.getUser_id().equals(o2.getUser_id())) {
                        if (o1.getCustomer_category_id().equals(o2.getCustomer_category_id())) {
                            return o1.getProduct_project_id().compareTo(o2.getProduct_project_id());
                        } else {
                            return o1.getCustomer_category_id().compareTo(o2.getCustomer_category_id());
                        }
                    } else {
                        return o1.getUser_id().compareTo(o2.getUser_id());
                    }
                }).collect(Collectors.groupingBy(BussResultGoal::getUser_id));
        if (resultGoalMap.isEmpty()) {
            return result;
        }

        Map<Long, String> userMap = userService.getAllBySelectKey(User.builder().role(CodeCaption.ROLE_SALE).build()).stream().collect(Collectors.toMap(CommonModel::getId, User::getName));
        Map<Long, String> teamMap = teamService.getAll().stream().collect(Collectors.toMap(CommonModel::getId, Team::getName));
        Map<Long, String> categoryMap = customerCategoryService.getAllBySelectKey(CustomerCategory.builder().parent_id(0L).build()).stream().collect(Collectors.toMap(CommonModel::getId, CustomerCategory::getName));
        Map<Long, String> projectMap = productProjectService.getAll().stream().collect(Collectors.toMap(CommonModel::getId, ProductProject::getName));

        for (Long userId : resultGoalMap.keySet()) {
            ResultGoalDisplay display = new ResultGoalDisplay();

            display.setUser_id(userId);
            display.setUser_name(userMap.get(userId));

            BigDecimal user_total = BigDecimal.ZERO;
            List<ResultGoalInfo> infos = new ArrayList<>();
            List<BussResultGoal> resultGoals = resultGoalMap.get(userId);
            for (BussResultGoal resultGoal : resultGoals) {
                ResultGoalInfo info = new ResultGoalInfo();
                BeanUtils.copyProperties(resultGoal, info);
                info.setTeam_name(teamMap.get(info.getTeam_id()));
                info.setCustomer_category_name(categoryMap.get(info.getCustomer_category_id()));
                info.setProduct_project_name(projectMap.get(info.getProduct_project_id()));
                infos.add(info);
                user_total = user_total.add(resultGoal.getAmount_s()).add(resultGoal.getAmount_t()).add(resultGoal.getAmount_y()).add(resultGoal.getAmount_c());
            }
            display.setUser_total(user_total);
            //再列表里面再次放进个人总金额目标,前端需要
            for (ResultGoalInfo info:infos){
                info.setUser_total(user_total);
            }

            display.setInfos(infos);
            displays.add(display);
        }
        return result;

    }

    @Override
    @Transactional
    public ResponseJson updateResult(BussResultGoal bussResultGoal) {
        return bussResultGoalService.update(bussResultGoal);
    }


}
