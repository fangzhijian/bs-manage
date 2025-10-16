package com.bs.manage.service.console.impl;

import com.bs.manage.code.CodeCaption;
import com.bs.manage.intercepter.UserToken;
import com.bs.manage.model.bean.account.Team;
import com.bs.manage.model.bean.account.User;
import com.bs.manage.model.bean.common.CommonModel;
import com.bs.manage.model.bean.console.BussProcessGoal;
import com.bs.manage.model.bean.console.BussProcessSummary;
import com.bs.manage.model.bean.console.BussResultGoal;
import com.bs.manage.model.bean.console.DateReport;
import com.bs.manage.model.bean.console.IdAndAmount;
import com.bs.manage.model.bean.console.IdAndCount;
import com.bs.manage.model.bean.customer.CustomerCategory;
import com.bs.manage.model.bean.product.ProductBrand;
import com.bs.manage.model.bean.product.ProductProject;
import com.bs.manage.model.json.Page;
import com.bs.manage.model.json.ResponseJson;
import com.bs.manage.model.param.console.Board;
import com.bs.manage.model.param.console.BoardContent;
import com.bs.manage.model.param.console.BoardTable;
import com.bs.manage.model.param.console.BoardParam;
import com.bs.manage.model.bean.console.ReportSummary;
import com.bs.manage.service.account.TeamService;
import com.bs.manage.service.account.UserService;
import com.bs.manage.service.console.BoardService;
import com.bs.manage.service.console.BussProcessGoalService;
import com.bs.manage.service.console.BussProcessSummaryService;
import com.bs.manage.service.console.BussResultGoalService;
import com.bs.manage.service.console.DateReportService;
import com.bs.manage.service.customer.CustomerCategoryService;
import com.bs.manage.service.product.ProductBrandService;
import com.bs.manage.service.product.ProductProjectService;
import com.bs.manage.until.DateUtil;
import com.bs.manage.until.NumberUtil;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.OptionalDouble;
import java.util.stream.Collectors;

/**
 * 2020/3/16 11:24
 * fzj
 */
@Service
public class BoardServiceImpl implements BoardService {

    private final UserService userService;
    private final TeamService teamService;
    private final ProductProjectService productProjectService;
    private final ProductBrandService productBrandService;
    private final CustomerCategoryService customerCategoryService;
    private final DateReportService dateReportService;
    private final BussResultGoalService bussResultGoalService;
    private final BussProcessSummaryService bussProcessSummaryService;
    private final BussProcessGoalService bussProcessGoalService;

    public BoardServiceImpl(UserService userService, TeamService teamService, ProductProjectService productProjectService, ProductBrandService productBrandService, CustomerCategoryService customerCategoryService,
                            DateReportService dateReportService, BussResultGoalService bussResultGoalService, BussProcessSummaryService bussProcessSummaryService, BussProcessGoalService bussProcessGoalService) {
        this.userService = userService;
        this.teamService = teamService;
        this.productProjectService = productProjectService;
        this.productBrandService = productBrandService;
        this.customerCategoryService = customerCategoryService;
        this.dateReportService = dateReportService;
        this.bussResultGoalService = bussResultGoalService;
        this.bussProcessSummaryService = bussProcessSummaryService;
        this.bussProcessGoalService = bussProcessGoalService;
    }


    /**
     * 初始化参数
     *
     * @param param 入参
     */
    private Integer initBoardParam(BoardParam param) {
        //查询月份
        LocalDateTime now = LocalDateTime.now();
        int month = Integer.parseInt(now.format(DateUtil.yyyyMM_FORMATTER));
        int prevMonth;
        if (param.getMonth() == null) {
            param.setMonth(month);
            prevMonth = Integer.parseInt(now.minusMonths(1).format(DateUtil.yyyyMM_FORMATTER));
        } else {
            prevMonth = Integer.parseInt(LocalDate.parse(param.getMonth() + "01", DateUtil.yyyyMMdd_FORMATTER).minusMonths(1).format(DateUtil.yyyyMM_FORMATTER));
        }
        param.setPrevMoth(prevMonth);
        if (param.getMonth() == month) {
            if (now.getDayOfMonth() == 1) {
                param.setMonthEnd(param.getMonth() * 100);
            } else {
                param.setMonthEnd(Integer.parseInt(now.minusDays(1).format(DateUtil.yyyyMMdd_FORMATTER)));
            }
        } else {
            param.setMonthEnd(param.getMonth() * 100 + 31);
        }
        if (param.getDate() == null) {
            param.setDate(Integer.parseInt(now.minusDays(1).format(DateUtil.yyyyMMdd_FORMATTER)));
        }

        //查询自己的下级或者自定账号,为空查看全部
        User user = UserToken.getContext();
        List<Long> userIds = null;
        if (NumberUtil.isNotBlank(param.getUser_id())) {
            userIds = new ArrayList<>();
            userIds.add(param.getUser_id());
        } else if (CodeCaption.ROLE_SALE == user.getRole()) {
            List<User> subordinate = userService.subordinateForSale(user);
            userIds = subordinate.stream().map(CommonModel::getId).collect(Collectors.toList());
        }
        param.setRole(user.getRole());
        param.setUserIds(userIds);

        //用于只查看自己的团队,为空查看全部团队
        Long teamId = null;
        if (CodeCaption.ROLE_SALE == user.getRole()) {
            teamId = user.getTeam_id();
        }
        param.setTeam_id(teamId);

        //返回原查询类型,用于个性化默认查询类型
        Integer type = param.getType();
        if (type == null) {
            param.setType(CodeCaption.ROLE_SALE == user.getRole() ? 4 : 1);
        }
        return type;
    }

    /**
     * 初始化
     *
     * @param boards 集合
     * @param title  标题
     * @return 结果
     */
    private List<BoardContent> setBoardContents(List<Board> boards, String title) {
        Board board = new Board();
        board.setTitle(title);
        List<BoardContent> boardContents = new ArrayList<>();
        board.setContentList(boardContents);
        boards.add(board);
        return boardContents;
    }

    /**
     * 仪表盘-金额
     *
     * @param param 入参
     * @return 各个金额
     */
    @Override
    public ResponseJson amount(BoardParam param) {
        Integer type = initBoardParam(param);
        if (type == null) {
            param.setType(CodeCaption.ROLE_SALE == param.getRole() ? 2 : 1);
        }

        List<Board> boards = new ArrayList<>();
        List<BoardContent> goalContents = setBoardContents(boards, "月度销售目标（万元）");   //月度销售目标
        List<BoardContent> finishContents = setBoardContents(boards, "已完成（万元）");       //已完成
        List<BoardContent> planContents = setBoardContents(boards, "完成进度（%）");          //完成进度
        List<BoardContent> proportionContents = setBoardContents(boards, "生意占比（%）");    //生意占比

        List<BussResultGoal> resultGoals = bussResultGoalService.getListByUserIds(param.getMonth(), param.getUserIds());
        String groupId = "team_id";
        if (param.getType() == 2) {
            groupId = "product_project_id";
        } else if (param.getType() == 3) {
            groupId = "product_brand_id";
        } else if (param.getType() == 4) {
            groupId = "customer_gradation,customer_category_id";
        }
        List<IdAndAmount> idAndAmounts = dateReportService.summaryGroupId(param.getMonth(), groupId, param.getUserIds(), param.getMonthEnd());
        //总目标
        BigDecimal totalGoal = resultGoals.stream().map(x -> x.getAmount_s().add(x.getAmount_t()).add(x.getAmount_y()).add(x.getAmount_c())).reduce(BigDecimal.ZERO, BigDecimal::add);
        //总完成
        BigDecimal totalValue = idAndAmounts.stream().map(x -> x.getAmount() == null ? BigDecimal.ZERO : x.getAmount()).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, BigDecimal.ROUND_HALF_UP);

        //设置合计
        addBoardContent(false, "合计", NumberUtil.toTenThouAmount(totalGoal).toString(), null, goalContents);
        addBoardContent(false, "合计", NumberUtil.toTenThouAmount(totalValue).toString(), null, finishContents);
        addBoardContent(false, "合计", NumberUtil.getPercentString(totalValue, totalGoal, "无目标"), null, planContents);
        addBoardContent(false, "合计", NumberUtil.getPercentString(totalValue, totalValue, "0"), null, proportionContents);

        //设置合计下面每个标签的值
        if (param.getType() == 1) {
            List<Team> teams = teamService.getAll();
            for (Team team : teams) {
                if (param.getTeam_id() != null && !param.getTeam_id().equals(team.getId())) {
                    continue;
                }
                setResultBoardContent(team.getId(), team.getName(), false, totalValue,
                        resultGoals.stream().filter(x -> team.getId().equals(x.getTeam_id())).map(x -> x.getAmount_s().add(x.getAmount_t()).add(x.getAmount_y()).add(x.getAmount_c())).reduce(BigDecimal.ZERO, BigDecimal::add),
                        idAndAmounts, goalContents, finishContents, planContents, proportionContents);
            }

        } else if (param.getType() == 2) {
            List<ProductProject> projects = productProjectService.getAll();
            for (ProductProject project : projects) {
                setResultBoardContent(project.getId(), project.getName(), false, totalValue,
                        resultGoals.stream().filter(x -> project.getId().equals(x.getProduct_project_id())).map(x -> x.getAmount_s().add(x.getAmount_t()).add(x.getAmount_y()).add(x.getAmount_c())).reduce(BigDecimal.ZERO, BigDecimal::add),
                        idAndAmounts, goalContents, finishContents, planContents, proportionContents);
            }

        } else if (param.getType() == 3) {
            List<ProductBrand> brands = productBrandService.getAll();
            for (ProductBrand brand : brands) {
                setResultBoardContent(brand.getId(), brand.getName(), false, totalValue,
                        resultGoals.stream().filter(x -> brand.getId().equals(x.getProduct_brand_id())).map(x -> x.getAmount_s().add(x.getAmount_t()).add(x.getAmount_y()).add(x.getAmount_c())).reduce(BigDecimal.ZERO, BigDecimal::add),
                        idAndAmounts, goalContents, finishContents, planContents, proportionContents);
            }
        } else {
            List<CustomerCategory> categories = customerCategoryService.getAllBySelectKey(CustomerCategory.builder().parent_id(0L).build());
            for (CustomerCategory category : categories) {
                setResultBoardContent(category.getId(), category.getName(), false, totalValue,
                        resultGoals.stream().filter(x -> category.getId().equals(x.getCustomer_category_id())).map(x -> x.getAmount_s().add(x.getAmount_t()).add(x.getAmount_y()).add(x.getAmount_c())).reduce(BigDecimal.ZERO, BigDecimal::add),
                        idAndAmounts, goalContents, finishContents, planContents, proportionContents);

                setResultBoardContent(category.getId(), "S", true, totalValue,
                        resultGoals.stream().filter(x -> category.getId().equals(x.getCustomer_category_id())).map(BussResultGoal::getAmount_s).reduce(BigDecimal.ZERO, BigDecimal::add),
                        idAndAmounts, goalContents, finishContents, planContents, proportionContents);

                setResultBoardContent(category.getId(), "T", true, totalValue,
                        resultGoals.stream().filter(x -> category.getId().equals(x.getCustomer_category_id())).map(BussResultGoal::getAmount_t).reduce(BigDecimal.ZERO, BigDecimal::add),
                        idAndAmounts, goalContents, finishContents, planContents, proportionContents);

                setResultBoardContent(category.getId(), "Y", true, totalValue,
                        resultGoals.stream().filter(x -> category.getId().equals(x.getCustomer_category_id())).map(BussResultGoal::getAmount_y).reduce(BigDecimal.ZERO, BigDecimal::add),
                        idAndAmounts, goalContents, finishContents, planContents, proportionContents);

                setResultBoardContent(category.getId(), "C", true, totalValue,
                        resultGoals.stream().filter(x -> category.getId().equals(x.getCustomer_category_id())).map(BussResultGoal::getAmount_c).reduce(BigDecimal.ZERO, BigDecimal::add),
                        idAndAmounts, goalContents, finishContents, planContents, proportionContents);

            }
        }

        return ResponseJson.success(boards);
    }


    /**
     * 业务过程加拜访率 成交率 客单价
     *
     * @param param 入参
     * @return 各结果
     */
    @Override
    public ResponseJson process(BoardParam param) {
        initBoardParam(param);

        List<Board> boards = new ArrayList<>();
        List<BoardContent> coverContents = setBoardContents(boards, "覆盖客户数");
        List<BoardContent> visitContents = setBoardContents(boards, "拜访客户数");
        List<BoardContent> dealContents = setBoardContents(boards, "成交客户数");
        List<BoardContent> activeContents = setBoardContents(boards, "活跃客户数");
        List<BoardContent> releaseContents = setBoardContents(boards, "释放客户数");
        List<BoardContent> visitRatioContents = setBoardContents(boards, "拜访率(%)");
        List<BoardContent> dealRatioContents = setBoardContents(boards, "成交率(%)");
        List<BoardContent> unitPriceContents = setBoardContents(boards, "客单价(元)");

        List<BussProcessSummary> summaryList = bussProcessSummaryService.getAllBySelectKey(BussProcessSummary.builder().summary_month(param.getMonth()).userIds(param.getUserIds()).build());

        //各个总值
        Integer coverTotal = summaryList.stream().map(x -> x.getCover_s() + x.getCover_t() + x.getCover_y() + x.getCover_c()).reduce(0, Integer::sum);
        Integer visitTotal = summaryList.stream().map(x -> x.getVisit_s() + x.getVisit_t() + x.getVisit_y() + x.getVisit_c()).reduce(0, Integer::sum);
        Integer dealTotal = summaryList.stream().map(x -> x.getDeal_s() + x.getDeal_t() + x.getDeal_y() + x.getDeal_c()).reduce(0, Integer::sum);
        Integer activeTotal = summaryList.stream().map(x -> x.getActive_s() + x.getActive_t() + x.getActive_y() + x.getActive_c()).reduce(0, Integer::sum);
        Integer releaseTotal = summaryList.stream().map(x -> x.getRelease_s() + x.getRelease_t() + x.getRelease_y() + x.getRelease_c()).reduce(0, Integer::sum);

        //设置合计
        addBoardContent(false, "合计", coverTotal.toString(), null, coverContents);
        addBoardContent(false, "合计", visitTotal.toString(), null, visitContents);
        addBoardContent(false, "合计", dealTotal.toString(), null, dealContents);
        addBoardContent(false, "合计", activeTotal.toString(), null, activeContents);
        addBoardContent(false, "合计", releaseTotal.toString(), null, releaseContents);
        addBoardContent(false, "平均", NumberUtil.getPercentString(visitTotal, coverTotal, "无客户"), null, visitRatioContents);
        addBoardContent(false, "平均", NumberUtil.getPercentString(dealTotal, visitTotal, "无拜访"), null, dealRatioContents);
        BoardContent boardContent = new BoardContent().setLabel("平均");
        unitPriceContents.add(boardContent);

        List<IdAndAmount> unitPrice = dateReportService.getUnitPrice(param.getType(), param.getUserIds(), param.getMonth(), param.getMonthEnd());

        //设置合计下面各个标签的值
        BigDecimal totalAmount = BigDecimal.ZERO;
        int totalCount = 0;
        if (param.getType() == 1) {
            List<Team> teams = teamService.getAll();
            for (Team team : teams) {
                if (param.getTeam_id() != null && !param.getTeam_id().equals(team.getId())) {
                    continue;
                }
                BigDecimal teamAmount = BigDecimal.ZERO;
                int teamCount = 0;
                for (IdAndAmount idAndAmount : unitPrice) {
                    totalAmount = totalAmount.add(idAndAmount.getAmount());
                    totalCount = totalCount + idAndAmount.getCount();
                    if (team.getId().equals(idAndAmount.getTeam_id())) {
                        teamAmount = totalAmount.add(idAndAmount.getAmount());
                        teamCount = totalCount + idAndAmount.getCount();
                    }
                }
                //设置业务过程内容
                setProcessBoardContent(team.getName(), false,
                        summaryList.stream().filter(x -> team.getId().equals(x.getTeam_id())).map(x -> x.getCover_s() + x.getCover_t() + x.getCover_y() + x.getCover_c()).reduce(0, Integer::sum),
                        summaryList.stream().filter(x -> team.getId().equals(x.getTeam_id())).map(x -> x.getVisit_s() + x.getVisit_t() + x.getVisit_y() + x.getVisit_c()).reduce(0, Integer::sum),
                        summaryList.stream().filter(x -> team.getId().equals(x.getTeam_id())).map(x -> x.getDeal_s() + x.getDeal_t() + x.getDeal_y() + x.getDeal_c()).reduce(0, Integer::sum),
                        summaryList.stream().filter(x -> team.getId().equals(x.getTeam_id())).map(x -> x.getActive_s() + x.getActive_t() + x.getActive_y() + x.getActive_c()).reduce(0, Integer::sum),
                        summaryList.stream().filter(x -> team.getId().equals(x.getTeam_id())).map(x -> x.getRelease_s() + x.getRelease_t() + x.getRelease_y() + x.getRelease_c()).reduce(0, Integer::sum),
                        coverTotal, visitTotal, dealTotal, activeTotal, releaseTotal, teamAmount, teamCount,
                        coverContents, visitContents, dealContents, activeContents, releaseContents, visitRatioContents, dealRatioContents, unitPriceContents);
            }
        } else {
            List<CustomerCategory> categories = customerCategoryService.getAllBySelectKey(CustomerCategory.builder().parent_id(0L).build());
            for (CustomerCategory category : categories) {
                BigDecimal categoryAmount = BigDecimal.ZERO;
                int categoryCount = 0;
                BigDecimal categoryAmountS = BigDecimal.ZERO;
                int categoryCountS = 0;
                BigDecimal categoryAmountT = BigDecimal.ZERO;
                int categoryCountT = 0;
                BigDecimal categoryAmountY = BigDecimal.ZERO;
                int categoryCountY = 0;
                BigDecimal categoryAmountC = BigDecimal.ZERO;
                int categoryCountC = 0;
                for (IdAndAmount idAndAmount : unitPrice) {
                    totalAmount = totalAmount.add(idAndAmount.getAmount());
                    totalCount = totalCount + idAndAmount.getCount();
                    if (category.getId().equals(idAndAmount.getCustomer_category_id())) {
                        categoryAmount = categoryAmount.add(idAndAmount.getAmount());
                        categoryCount = categoryCount + idAndAmount.getCount();
                        if ("S".equals(idAndAmount.getCustomer_gradation())) {
                            categoryAmountS = categoryAmountS.add(idAndAmount.getAmount());
                            categoryCountS = categoryCountS + idAndAmount.getCount();
                        } else if ("T".equals(idAndAmount.getCustomer_gradation())) {
                            categoryAmountT = categoryAmountT.add(idAndAmount.getAmount());
                            categoryCountT = categoryCountT + idAndAmount.getCount();
                        } else if ("Y".equals(idAndAmount.getCustomer_gradation())) {
                            categoryAmountY = categoryAmountY.add(idAndAmount.getAmount());
                            categoryCountY = categoryCountY + idAndAmount.getCount();
                        } else if ("C".equals(idAndAmount.getCustomer_gradation())) {
                            categoryAmountC = categoryAmountC.add(idAndAmount.getAmount());
                            categoryCountC = categoryCountC + idAndAmount.getCount();
                        }
                    }
                }
                //设置业务过程内容
                setProcessBoardContent(category.getName(), false,
                        summaryList.stream().filter(x -> category.getId().equals(x.getCustomer_category_id())).map(x -> x.getCover_s() + x.getCover_t() + x.getCover_y() + x.getCover_c()).reduce(0, Integer::sum),
                        summaryList.stream().filter(x -> category.getId().equals(x.getCustomer_category_id())).map(x -> x.getVisit_s() + x.getVisit_t() + x.getVisit_y() + x.getVisit_c()).reduce(0, Integer::sum),
                        summaryList.stream().filter(x -> category.getId().equals(x.getCustomer_category_id())).map(x -> x.getDeal_s() + x.getDeal_t() + x.getDeal_y() + x.getDeal_c()).reduce(0, Integer::sum),
                        summaryList.stream().filter(x -> category.getId().equals(x.getCustomer_category_id())).map(x -> x.getActive_s() + x.getActive_t() + x.getActive_y() + x.getActive_c()).reduce(0, Integer::sum),
                        summaryList.stream().filter(x -> category.getId().equals(x.getCustomer_category_id())).map(x -> x.getRelease_s() + x.getRelease_t() + x.getRelease_y() + x.getRelease_c()).reduce(0, Integer::sum),
                        coverTotal, visitTotal, dealTotal, activeTotal, releaseTotal, categoryAmount, categoryCount,
                        coverContents, visitContents, dealContents, activeContents, releaseContents, visitRatioContents, dealRatioContents, unitPriceContents);

                setProcessBoardContent("S", true,
                        summaryList.stream().filter(x -> category.getId().equals(x.getCustomer_category_id())).map(BussProcessSummary::getCover_s).reduce(0, Integer::sum),
                        summaryList.stream().filter(x -> category.getId().equals(x.getCustomer_category_id())).map(BussProcessSummary::getVisit_s).reduce(0, Integer::sum),
                        summaryList.stream().filter(x -> category.getId().equals(x.getCustomer_category_id())).map(BussProcessSummary::getDeal_s).reduce(0, Integer::sum),
                        summaryList.stream().filter(x -> category.getId().equals(x.getCustomer_category_id())).map(BussProcessSummary::getActive_s).reduce(0, Integer::sum),
                        summaryList.stream().filter(x -> category.getId().equals(x.getCustomer_category_id())).map(BussProcessSummary::getRelease_s).reduce(0, Integer::sum),
                        coverTotal, visitTotal, dealTotal, activeTotal, releaseTotal, categoryAmountS, categoryCountS,
                        coverContents, visitContents, dealContents, activeContents, releaseContents, visitRatioContents, dealRatioContents, unitPriceContents);

                setProcessBoardContent("T", true,
                        summaryList.stream().filter(x -> category.getId().equals(x.getCustomer_category_id())).map(BussProcessSummary::getCover_t).reduce(0, Integer::sum),
                        summaryList.stream().filter(x -> category.getId().equals(x.getCustomer_category_id())).map(BussProcessSummary::getVisit_t).reduce(0, Integer::sum),
                        summaryList.stream().filter(x -> category.getId().equals(x.getCustomer_category_id())).map(BussProcessSummary::getDeal_t).reduce(0, Integer::sum),
                        summaryList.stream().filter(x -> category.getId().equals(x.getCustomer_category_id())).map(BussProcessSummary::getActive_t).reduce(0, Integer::sum),
                        summaryList.stream().filter(x -> category.getId().equals(x.getCustomer_category_id())).map(BussProcessSummary::getRelease_t).reduce(0, Integer::sum),
                        coverTotal, visitTotal, dealTotal, activeTotal, releaseTotal, categoryAmountT, categoryCountT,
                        coverContents, visitContents, dealContents, activeContents, releaseContents, visitRatioContents, dealRatioContents, unitPriceContents);

                setProcessBoardContent("Y", true,
                        summaryList.stream().filter(x -> category.getId().equals(x.getCustomer_category_id())).map(BussProcessSummary::getCover_y).reduce(0, Integer::sum),
                        summaryList.stream().filter(x -> category.getId().equals(x.getCustomer_category_id())).map(BussProcessSummary::getVisit_y).reduce(0, Integer::sum),
                        summaryList.stream().filter(x -> category.getId().equals(x.getCustomer_category_id())).map(BussProcessSummary::getDeal_y).reduce(0, Integer::sum),
                        summaryList.stream().filter(x -> category.getId().equals(x.getCustomer_category_id())).map(BussProcessSummary::getActive_y).reduce(0, Integer::sum),
                        summaryList.stream().filter(x -> category.getId().equals(x.getCustomer_category_id())).map(BussProcessSummary::getRelease_y).reduce(0, Integer::sum),
                        coverTotal, visitTotal, dealTotal, activeTotal, releaseTotal, categoryAmountY, categoryCountY,
                        coverContents, visitContents, dealContents, activeContents, releaseContents, visitRatioContents, dealRatioContents, unitPriceContents);

                setProcessBoardContent("C", true,
                        summaryList.stream().filter(x -> category.getId().equals(x.getCustomer_category_id())).map(BussProcessSummary::getCover_c).reduce(0, Integer::sum),
                        summaryList.stream().filter(x -> category.getId().equals(x.getCustomer_category_id())).map(BussProcessSummary::getVisit_c).reduce(0, Integer::sum),
                        summaryList.stream().filter(x -> category.getId().equals(x.getCustomer_category_id())).map(BussProcessSummary::getDeal_c).reduce(0, Integer::sum),
                        summaryList.stream().filter(x -> category.getId().equals(x.getCustomer_category_id())).map(BussProcessSummary::getActive_c).reduce(0, Integer::sum),
                        summaryList.stream().filter(x -> category.getId().equals(x.getCustomer_category_id())).map(BussProcessSummary::getRelease_c).reduce(0, Integer::sum),
                        coverTotal, visitTotal, dealTotal, activeTotal, releaseTotal, categoryAmountC, categoryCountC,
                        coverContents, visitContents, dealContents, activeContents, releaseContents, visitRatioContents, dealRatioContents, unitPriceContents);
            }
        }
        boardContent.setValue(NumberUtil.getAverageString(totalAmount, totalCount, "0"));
        return ResponseJson.success(boards);
    }

    /**
     * 覆盖分表
     *
     * @param param 入参
     * @return 各结果
     */
    @Override
    public ResponseJson cover(BoardParam param) {
        initBoardParam(param);
        List<BussProcessSummary> summaryList = bussProcessSummaryService.getAllBySelectKey(BussProcessSummary.builder().summary_month(param.getMonth()).userIds(param.getUserIds()).build());
        List<BussProcessSummary> prevSummaryList = bussProcessSummaryService.getAllBySelectKey(BussProcessSummary.builder().summary_month(param.getPrevMoth()).userIds(param.getUserIds()).build());
        List<BussProcessGoal> goalList = bussProcessGoalService.getAllBySelectKey(BussProcessGoal.builder().goal_month(param.getMonth()).userIds(param.getUserIds()).build());


        Integer coverTotal = summaryList.stream().map(x -> x.getCover_s() + x.getCover_t() + x.getCover_y() + x.getCover_c()).reduce(0, Integer::sum);
        Integer prevCoverTotal = prevSummaryList.stream().map(x -> x.getCover_s() + x.getCover_t() + x.getCover_y() + x.getCover_c()).reduce(0, Integer::sum);

        List<BoardTable> boardTableList = new ArrayList<>();
        if (param.getType() == 1) {
            List<Team> teams = teamService.getAll();
            for (Team team : teams) {
                if (param.getTeam_id() != null && !param.getTeam_id().equals(team.getId())) {
                    continue;
                }
                setCoverBoardTable(team.getName(), false,
                        summaryList.stream().filter(x -> team.getId().equals(x.getTeam_id())).map(x -> x.getCover_s() + x.getCover_t() + x.getCover_y() + x.getCover_c()).reduce(0, Integer::sum),
                        prevSummaryList.stream().filter(x -> team.getId().equals(x.getTeam_id())).map(x -> x.getCover_s() + x.getCover_t() + x.getCover_y() + x.getCover_c()).reduce(0, Integer::sum),
                        goalList.stream().filter(x -> team.getId().equals(x.getTeam_id())).map(x -> x.getCover_s() + x.getCover_t() + x.getCover_y() + x.getCover_c()).reduce(0, Integer::sum),
                        coverTotal, prevCoverTotal, boardTableList);

            }
        } else {
            List<CustomerCategory> categories = customerCategoryService.getAllBySelectKey(CustomerCategory.builder().parent_id(0L).build());
            for (CustomerCategory category : categories) {
                setCoverBoardTable(category.getName(), false,
                        summaryList.stream().filter(x -> category.getId().equals(x.getCustomer_category_id())).map(x -> x.getCover_s() + x.getCover_t() + x.getCover_y() + x.getCover_c()).reduce(0, Integer::sum),
                        prevSummaryList.stream().filter(x -> category.getId().equals(x.getCustomer_category_id())).map(x -> x.getCover_s() + x.getCover_t() + x.getCover_y() + x.getCover_c()).reduce(0, Integer::sum),
                        goalList.stream().filter(x -> category.getId().equals(x.getCustomer_category_id())).map(x -> x.getCover_s() + x.getCover_t() + x.getCover_y() + x.getCover_c()).reduce(0, Integer::sum),
                        coverTotal, prevCoverTotal, boardTableList);

                setCoverBoardTable("S", true,
                        summaryList.stream().filter(x -> category.getId().equals(x.getCustomer_category_id())).map(BussProcessSummary::getCover_s).reduce(0, Integer::sum),
                        prevSummaryList.stream().filter(x -> category.getId().equals(x.getCustomer_category_id())).map(BussProcessSummary::getCover_s).reduce(0, Integer::sum),
                        goalList.stream().filter(x -> category.getId().equals(x.getCustomer_category_id())).map(BussProcessGoal::getCover_s).reduce(0, Integer::sum),
                        coverTotal, prevCoverTotal, boardTableList);

                setCoverBoardTable("T", true,
                        summaryList.stream().filter(x -> category.getId().equals(x.getCustomer_category_id())).map(BussProcessSummary::getCover_t).reduce(0, Integer::sum),
                        prevSummaryList.stream().filter(x -> category.getId().equals(x.getCustomer_category_id())).map(BussProcessSummary::getCover_t).reduce(0, Integer::sum),
                        goalList.stream().filter(x -> category.getId().equals(x.getCustomer_category_id())).map(BussProcessGoal::getCover_t).reduce(0, Integer::sum),
                        coverTotal, prevCoverTotal, boardTableList);

                setCoverBoardTable("Y", true,
                        summaryList.stream().filter(x -> category.getId().equals(x.getCustomer_category_id())).map(BussProcessSummary::getCover_y).reduce(0, Integer::sum),
                        prevSummaryList.stream().filter(x -> category.getId().equals(x.getCustomer_category_id())).map(BussProcessSummary::getCover_y).reduce(0, Integer::sum),
                        goalList.stream().filter(x -> category.getId().equals(x.getCustomer_category_id())).map(BussProcessGoal::getCover_y).reduce(0, Integer::sum),
                        coverTotal, prevCoverTotal, boardTableList);

                setCoverBoardTable("C", true,
                        summaryList.stream().filter(x -> category.getId().equals(x.getCustomer_category_id())).map(BussProcessSummary::getCover_c).reduce(0, Integer::sum),
                        prevSummaryList.stream().filter(x -> category.getId().equals(x.getCustomer_category_id())).map(BussProcessSummary::getCover_c).reduce(0, Integer::sum),
                        goalList.stream().filter(x -> category.getId().equals(x.getCustomer_category_id())).map(BussProcessGoal::getCover_c).reduce(0, Integer::sum),
                        coverTotal, prevCoverTotal, boardTableList);
            }
        }
        return ResponseJson.success(boardTableList);
    }


    /**
     * 拜访分表
     *
     * @param param 入参
     * @return 各结果
     */
    @Override
    public ResponseJson visit(BoardParam param) {
        initBoardParam(param);
        List<BussProcessSummary> summaryList = bussProcessSummaryService.getAllBySelectKey(BussProcessSummary.builder().summary_month(param.getMonth()).userIds(param.getUserIds()).build());
        List<BussProcessSummary> prevSummaryList = bussProcessSummaryService.getAllBySelectKey(BussProcessSummary.builder().summary_month(param.getPrevMoth()).userIds(param.getUserIds()).build());
        List<BussProcessGoal> goalList = bussProcessGoalService.getAllBySelectKey(BussProcessGoal.builder().goal_month(param.getMonth()).userIds(param.getUserIds()).build());
        List<IdAndCount> customerAndReport = dateReportService.getCountIdByGroupId(param.getType(), param.getUserIds(), param.getMonth(), param.getMonthEnd());
        List<IdAndCount> prevCustomerAndReport = dateReportService.getCountIdByGroupId(param.getType(), param.getUserIds(), param.getPrevMoth(), param.getPrevMoth() * 100 + 31);
        IdAndCount nullCount = new IdAndCount().setTotal(0).setAnther_total(0);

        List<BoardTable> boardTableList = new ArrayList<>();
        if (param.getType() == 1) {
            List<Team> teams = teamService.getAll();
            for (Team team : teams) {
                if (param.getTeam_id() != null && !param.getTeam_id().equals(team.getId())) {
                    continue;
                }
                setVisitBoardTable(team.getName(), false, boardTableList,
                        summaryList.stream().filter(x -> team.getId().equals(x.getTeam_id())).map(x -> x.getVisit_s() + x.getVisit_t() + x.getVisit_y() + x.getVisit_c()).reduce(0, Integer::sum),
                        prevSummaryList.stream().filter(x -> team.getId().equals(x.getTeam_id())).map(x -> x.getVisit_s() + x.getVisit_t() + x.getVisit_y() + x.getVisit_c()).reduce(0, Integer::sum),
                        goalList.stream().filter(x -> team.getId().equals(x.getTeam_id())).map(x -> x.getVisit_s() + x.getVisit_t() + x.getVisit_y() + x.getVisit_c()).reduce(0, Integer::sum),
                        summaryList.stream().filter(x -> team.getId().equals(x.getTeam_id())).map(x -> x.getCover_s() + x.getCover_t() + x.getCover_y() + x.getCover_c()).reduce(0, Integer::sum),
                        prevSummaryList.stream().filter(x -> team.getId().equals(x.getTeam_id())).map(x -> x.getCover_s() + x.getCover_t() + x.getCover_y() + x.getCover_c()).reduce(0, Integer::sum),
                        customerAndReport.stream().filter(x -> team.getId().equals(x.getTeam_id())).findFirst().orElse(nullCount),
                        prevCustomerAndReport.stream().filter(x -> team.getId().equals(x.getTeam_id())).findFirst().orElse(nullCount)
                );
            }
        } else {
            List<CustomerCategory> categories = customerCategoryService.getAllBySelectKey(CustomerCategory.builder().parent_id(0L).build());
            for (CustomerCategory category : categories) {

                int countReport = 0;
                int countCustomer = 0;
                for (IdAndCount idAndCount : customerAndReport) {
                    if (category.getId().equals(idAndCount.getCustomer_category_id())) {
                        countReport = countReport + idAndCount.getTotal();
                        countCustomer = countCustomer + idAndCount.getAnther_total();
                    }
                }

                int prevCountReport = 0;
                int prevCountCustomer = 0;
                for (IdAndCount idAndCount : prevCustomerAndReport) {
                    if (category.getId().equals(idAndCount.getCustomer_category_id())) {
                        prevCountReport = prevCountReport + idAndCount.getTotal();
                        prevCountCustomer = prevCountCustomer + idAndCount.getAnther_total();
                    }
                }


                setVisitBoardTable(category.getName(), false, boardTableList,
                        summaryList.stream().filter(x -> category.getId().equals(x.getCustomer_category_id())).map(x -> x.getVisit_s() + x.getVisit_t() + x.getVisit_y() + x.getVisit_c()).reduce(0, Integer::sum),
                        prevSummaryList.stream().filter(x -> category.getId().equals(x.getCustomer_category_id())).map(x -> x.getVisit_s() + x.getVisit_t() + x.getVisit_y() + x.getVisit_c()).reduce(0, Integer::sum),
                        goalList.stream().filter(x -> category.getId().equals(x.getCustomer_category_id())).map(x -> x.getVisit_s() + x.getVisit_t() + x.getVisit_y() + x.getVisit_c()).reduce(0, Integer::sum),
                        summaryList.stream().filter(x -> category.getId().equals(x.getCustomer_category_id())).map(x -> x.getCover_s() + x.getCover_t() + x.getCover_y() + x.getCover_c()).reduce(0, Integer::sum),
                        prevSummaryList.stream().filter(x -> category.getId().equals(x.getCustomer_category_id())).map(x -> x.getCover_s() + x.getCover_t() + x.getCover_y() + x.getCover_c()).reduce(0, Integer::sum),
                        new IdAndCount().setTotal(countReport).setAnther_total(countCustomer),
                        new IdAndCount().setTotal(prevCountReport).setAnther_total(prevCountCustomer));

                setVisitBoardTable("S", true, boardTableList,
                        summaryList.stream().filter(x -> category.getId().equals(x.getCustomer_category_id())).map(BussProcessSummary::getVisit_s).reduce(0, Integer::sum),
                        prevSummaryList.stream().filter(x -> category.getId().equals(x.getCustomer_category_id())).map(BussProcessSummary::getVisit_s).reduce(0, Integer::sum),
                        goalList.stream().filter(x -> category.getId().equals(x.getCustomer_category_id())).map(BussProcessGoal::getVisit_s).reduce(0, Integer::sum),
                        summaryList.stream().filter(x -> category.getId().equals(x.getCustomer_category_id())).map(BussProcessSummary::getCover_s).reduce(0, Integer::sum),
                        prevSummaryList.stream().filter(x -> category.getId().equals(x.getCustomer_category_id())).map(BussProcessSummary::getCover_s).reduce(0, Integer::sum),
                        customerAndReport.stream().filter(x -> category.getId().equals(x.getCustomer_category_id()) && "S".equals(x.getCustomer_gradation())).findFirst().orElse(nullCount),
                        prevCustomerAndReport.stream().filter(x -> category.getId().equals(x.getCustomer_category_id()) && "S".equals(x.getCustomer_gradation())).findFirst().orElse(nullCount));

                setVisitBoardTable("T", true, boardTableList,
                        summaryList.stream().filter(x -> category.getId().equals(x.getCustomer_category_id())).map(BussProcessSummary::getVisit_t).reduce(0, Integer::sum),
                        prevSummaryList.stream().filter(x -> category.getId().equals(x.getCustomer_category_id())).map(BussProcessSummary::getVisit_t).reduce(0, Integer::sum),
                        goalList.stream().filter(x -> category.getId().equals(x.getCustomer_category_id())).map(BussProcessGoal::getVisit_t).reduce(0, Integer::sum),
                        summaryList.stream().filter(x -> category.getId().equals(x.getCustomer_category_id())).map(BussProcessSummary::getCover_t).reduce(0, Integer::sum),
                        prevSummaryList.stream().filter(x -> category.getId().equals(x.getCustomer_category_id())).map(BussProcessSummary::getCover_t).reduce(0, Integer::sum),
                        customerAndReport.stream().filter(x -> category.getId().equals(x.getCustomer_category_id()) && "T".equals(x.getCustomer_gradation())).findFirst().orElse(nullCount),
                        prevCustomerAndReport.stream().filter(x -> category.getId().equals(x.getCustomer_category_id()) && "T".equals(x.getCustomer_gradation())).findFirst().orElse(nullCount));

                setVisitBoardTable("Y", true, boardTableList,
                        summaryList.stream().filter(x -> category.getId().equals(x.getCustomer_category_id())).map(BussProcessSummary::getVisit_y).reduce(0, Integer::sum),
                        prevSummaryList.stream().filter(x -> category.getId().equals(x.getCustomer_category_id())).map(BussProcessSummary::getVisit_y).reduce(0, Integer::sum),
                        goalList.stream().filter(x -> category.getId().equals(x.getCustomer_category_id())).map(BussProcessGoal::getVisit_y).reduce(0, Integer::sum),
                        summaryList.stream().filter(x -> category.getId().equals(x.getCustomer_category_id())).map(BussProcessSummary::getCover_y).reduce(0, Integer::sum),
                        prevSummaryList.stream().filter(x -> category.getId().equals(x.getCustomer_category_id())).map(BussProcessSummary::getCover_y).reduce(0, Integer::sum),
                        customerAndReport.stream().filter(x -> category.getId().equals(x.getCustomer_category_id()) && "Y".equals(x.getCustomer_gradation())).findFirst().orElse(nullCount),
                        prevCustomerAndReport.stream().filter(x -> category.getId().equals(x.getCustomer_category_id()) && "Y".equals(x.getCustomer_gradation())).findFirst().orElse(nullCount));

                setVisitBoardTable("C", true, boardTableList,
                        summaryList.stream().filter(x -> category.getId().equals(x.getCustomer_category_id())).map(BussProcessSummary::getVisit_c).reduce(0, Integer::sum),
                        prevSummaryList.stream().filter(x -> category.getId().equals(x.getCustomer_category_id())).map(BussProcessSummary::getVisit_c).reduce(0, Integer::sum),
                        goalList.stream().filter(x -> category.getId().equals(x.getCustomer_category_id())).map(BussProcessGoal::getVisit_c).reduce(0, Integer::sum),
                        summaryList.stream().filter(x -> category.getId().equals(x.getCustomer_category_id())).map(BussProcessSummary::getCover_c).reduce(0, Integer::sum),
                        prevSummaryList.stream().filter(x -> category.getId().equals(x.getCustomer_category_id())).map(BussProcessSummary::getCover_c).reduce(0, Integer::sum),
                        customerAndReport.stream().filter(x -> category.getId().equals(x.getCustomer_category_id()) && "C".equals(x.getCustomer_gradation())).findFirst().orElse(nullCount),
                        prevCustomerAndReport.stream().filter(x -> category.getId().equals(x.getCustomer_category_id()) && "C".equals(x.getCustomer_gradation())).findFirst().orElse(nullCount));
            }


        }

        return ResponseJson.success(boardTableList);
    }


    /**
     * 成交分表
     *
     * @param param 入参
     * @return 各结果
     */
    @Override
    public ResponseJson deal(BoardParam param) {
        initBoardParam(param);
        List<BussProcessSummary> summaryList = bussProcessSummaryService.getAllBySelectKey(BussProcessSummary.builder().summary_month(param.getMonth()).userIds(param.getUserIds()).build());
        List<BussProcessSummary> prevSummaryList = bussProcessSummaryService.getAllBySelectKey(BussProcessSummary.builder().summary_month(param.getPrevMoth()).userIds(param.getUserIds()).build());
        List<BussProcessGoal> goalList = bussProcessGoalService.getAllBySelectKey(BussProcessGoal.builder().goal_month(param.getMonth()).userIds(param.getUserIds()).build());

        String groupId = "team_id";
        if (param.getType() == 4) {
            groupId = "customer_gradation,customer_category_id";
        }
        List<IdAndAmount> idAndAmounts = dateReportService.summaryGroupId(param.getMonth(), groupId, param.getUserIds(), param.getMonthEnd());

        //总成交金额
        BigDecimal totalValue = idAndAmounts.stream().map(x -> x.getAmount() == null ? BigDecimal.ZERO : x.getAmount()).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, BigDecimal.ROUND_HALF_UP);

        List<BoardTable> boardTableList = new ArrayList<>();
        if (param.getType() == 1) {
            List<Team> teams = teamService.getAll();
            for (Team team : teams) {
                if (param.getTeam_id() != null && !param.getTeam_id().equals(team.getId())) {
                    continue;
                }
                setDealBoardTable(team.getName(), false, boardTableList, team.getId(), totalValue, idAndAmounts,
                        summaryList.stream().filter(x -> team.getId().equals(x.getTeam_id())).map(x -> x.getDeal_s() + x.getDeal_t() + x.getDeal_y() + x.getDeal_c()).reduce(0, Integer::sum),
                        prevSummaryList.stream().filter(x -> team.getId().equals(x.getTeam_id())).map(x -> x.getDeal_s() + x.getDeal_t() + x.getDeal_y() + x.getDeal_c()).reduce(0, Integer::sum),
                        goalList.stream().filter(x -> team.getId().equals(x.getTeam_id())).map(x -> x.getDeal_s() + x.getDeal_t() + x.getDeal_y() + x.getDeal_c()).reduce(0, Integer::sum),
                        summaryList.stream().filter(x -> team.getId().equals(x.getTeam_id())).map(x -> x.getVisit_s() + x.getVisit_t() + x.getVisit_y() + x.getVisit_c()).reduce(0, Integer::sum),
                        prevSummaryList.stream().filter(x -> team.getId().equals(x.getTeam_id())).map(x -> x.getVisit_s() + x.getVisit_t() + x.getVisit_y() + x.getVisit_c()).reduce(0, Integer::sum));
            }
        } else {
            List<CustomerCategory> categories = customerCategoryService.getAllBySelectKey(CustomerCategory.builder().parent_id(0L).build());
            for (CustomerCategory category : categories) {
                setDealBoardTable(category.getName(), false, boardTableList, category.getId(), totalValue, idAndAmounts,
                        summaryList.stream().filter(x -> category.getId().equals(x.getCustomer_category_id())).map(x -> x.getDeal_s() + x.getDeal_t() + x.getDeal_y() + x.getDeal_c()).reduce(0, Integer::sum),
                        prevSummaryList.stream().filter(x -> category.getId().equals(x.getCustomer_category_id())).map(x -> x.getDeal_s() + x.getDeal_t() + x.getDeal_y() + x.getDeal_c()).reduce(0, Integer::sum),
                        goalList.stream().filter(x -> category.getId().equals(x.getCustomer_category_id())).map(x -> x.getDeal_s() + x.getDeal_t() + x.getDeal_y() + x.getDeal_c()).reduce(0, Integer::sum),
                        summaryList.stream().filter(x -> category.getId().equals(x.getCustomer_category_id())).map(x -> x.getVisit_s() + x.getVisit_t() + x.getVisit_y() + x.getVisit_c()).reduce(0, Integer::sum),
                        prevSummaryList.stream().filter(x -> category.getId().equals(x.getCustomer_category_id())).map(x -> x.getVisit_s() + x.getVisit_t() + x.getVisit_y() + x.getVisit_c()).reduce(0, Integer::sum));

                setDealBoardTable("S", true, boardTableList, category.getId(), totalValue, idAndAmounts,
                        summaryList.stream().filter(x -> category.getId().equals(x.getCustomer_category_id())).map(BussProcessSummary::getDeal_s).reduce(0, Integer::sum),
                        prevSummaryList.stream().filter(x -> category.getId().equals(x.getCustomer_category_id())).map(BussProcessSummary::getDeal_s).reduce(0, Integer::sum),
                        goalList.stream().filter(x -> category.getId().equals(x.getCustomer_category_id())).map(BussProcessGoal::getDeal_s).reduce(0, Integer::sum),
                        summaryList.stream().filter(x -> category.getId().equals(x.getCustomer_category_id())).map(BussProcessSummary::getVisit_s).reduce(0, Integer::sum),
                        prevSummaryList.stream().filter(x -> category.getId().equals(x.getCustomer_category_id())).map(BussProcessSummary::getVisit_s).reduce(0, Integer::sum));

                setDealBoardTable("T", true, boardTableList, category.getId(), totalValue, idAndAmounts,
                        summaryList.stream().filter(x -> category.getId().equals(x.getCustomer_category_id())).map(BussProcessSummary::getDeal_t).reduce(0, Integer::sum),
                        prevSummaryList.stream().filter(x -> category.getId().equals(x.getCustomer_category_id())).map(BussProcessSummary::getDeal_t).reduce(0, Integer::sum),
                        goalList.stream().filter(x -> category.getId().equals(x.getCustomer_category_id())).map(BussProcessGoal::getDeal_t).reduce(0, Integer::sum),
                        summaryList.stream().filter(x -> category.getId().equals(x.getCustomer_category_id())).map(BussProcessSummary::getVisit_t).reduce(0, Integer::sum),
                        prevSummaryList.stream().filter(x -> category.getId().equals(x.getCustomer_category_id())).map(BussProcessSummary::getVisit_t).reduce(0, Integer::sum));

                setDealBoardTable("Y", true, boardTableList, category.getId(), totalValue, idAndAmounts,
                        summaryList.stream().filter(x -> category.getId().equals(x.getCustomer_category_id())).map(BussProcessSummary::getDeal_y).reduce(0, Integer::sum),
                        prevSummaryList.stream().filter(x -> category.getId().equals(x.getCustomer_category_id())).map(BussProcessSummary::getDeal_y).reduce(0, Integer::sum),
                        goalList.stream().filter(x -> category.getId().equals(x.getCustomer_category_id())).map(BussProcessGoal::getDeal_y).reduce(0, Integer::sum),
                        summaryList.stream().filter(x -> category.getId().equals(x.getCustomer_category_id())).map(BussProcessSummary::getVisit_y).reduce(0, Integer::sum),
                        prevSummaryList.stream().filter(x -> category.getId().equals(x.getCustomer_category_id())).map(BussProcessSummary::getVisit_y).reduce(0, Integer::sum));

                setDealBoardTable("C", true, boardTableList, category.getId(), totalValue, idAndAmounts,
                        summaryList.stream().filter(x -> category.getId().equals(x.getCustomer_category_id())).map(BussProcessSummary::getDeal_c).reduce(0, Integer::sum),
                        prevSummaryList.stream().filter(x -> category.getId().equals(x.getCustomer_category_id())).map(BussProcessSummary::getDeal_c).reduce(0, Integer::sum),
                        goalList.stream().filter(x -> category.getId().equals(x.getCustomer_category_id())).map(BussProcessGoal::getDeal_c).reduce(0, Integer::sum),
                        summaryList.stream().filter(x -> category.getId().equals(x.getCustomer_category_id())).map(BussProcessSummary::getVisit_c).reduce(0, Integer::sum),
                        prevSummaryList.stream().filter(x -> category.getId().equals(x.getCustomer_category_id())).map(BussProcessSummary::getVisit_c).reduce(0, Integer::sum));
            }
        }
        return ResponseJson.success(boardTableList);

    }


    /**
     * 活跃分表
     *
     * @param param 入参
     * @return 各结果
     */
    @Override
    public ResponseJson active(BoardParam param) {
        initBoardParam(param);
        List<BussProcessSummary> summaryList = bussProcessSummaryService.getAllBySelectKey(BussProcessSummary.builder().summary_month(param.getMonth()).userIds(param.getUserIds()).build());
        List<BussProcessSummary> prevSummaryList = bussProcessSummaryService.getAllBySelectKey(BussProcessSummary.builder().summary_month(param.getPrevMoth()).userIds(param.getUserIds()).build());
        List<BussProcessGoal> goalList = bussProcessGoalService.getAllBySelectKey(BussProcessGoal.builder().goal_month(param.getMonth()).userIds(param.getUserIds()).build());

        String groupId = "activeness,team_id";
        if (param.getType() == 4) {
            groupId = "activeness,customer_gradation,customer_category_id";
        }

        List<IdAndAmount> idAndAmounts = dateReportService.summaryGroupId(param.getMonth(), groupId, param.getUserIds(), param.getMonthEnd());
        List<IdAndAmount> prevIdAndAmounts = dateReportService.summaryGroupId(param.getPrevMoth(), groupId, param.getUserIds(), param.getPrevMoth() * 100 + 31);

        List<BoardTable> boardTableList = new ArrayList<>();
        if (param.getType() == 1) {
            List<Team> teams = teamService.getAll();
            for (Team team : teams) {
                if (param.getTeam_id() != null && !param.getTeam_id().equals(team.getId())) {
                    continue;
                }

                setActiveBoardTable(team.getName(), false, boardTableList, team.getId(), idAndAmounts, prevIdAndAmounts,
                        summaryList.stream().filter(x -> team.getId().equals(x.getTeam_id())).map(x -> x.getActive_s() + x.getActive_t() + x.getActive_y() + x.getActive_c()).reduce(0, Integer::sum),
                        prevSummaryList.stream().filter(x -> team.getId().equals(x.getTeam_id())).map(x -> x.getActive_s() + x.getActive_t() + x.getActive_y() + x.getActive_c()).reduce(0, Integer::sum),
                        goalList.stream().filter(x -> team.getId().equals(x.getTeam_id())).map(x -> x.getActive_s() + x.getActive_t() + x.getActive_y() + x.getActive_c()).reduce(0, Integer::sum),
                        summaryList.stream().filter(x -> team.getId().equals(x.getTeam_id())).map(x -> x.getCover_s() + x.getCover_t() + x.getCover_y() + x.getCover_c()).reduce(0, Integer::sum),
                        prevSummaryList.stream().filter(x -> team.getId().equals(x.getTeam_id())).map(x -> x.getCover_s() + x.getCover_t() + x.getCover_y() + x.getCover_c()).reduce(0, Integer::sum));
            }
        } else {
            List<CustomerCategory> categories = customerCategoryService.getAllBySelectKey(CustomerCategory.builder().parent_id(0L).build());
            for (CustomerCategory category : categories) {
                setActiveBoardTable(category.getName(), false, boardTableList, category.getId(), idAndAmounts, prevIdAndAmounts,
                        summaryList.stream().filter(x -> category.getId().equals(x.getCustomer_category_id())).map(x -> x.getActive_s() + x.getActive_t() + x.getActive_y() + x.getActive_c()).reduce(0, Integer::sum),
                        prevSummaryList.stream().filter(x -> category.getId().equals(x.getCustomer_category_id())).map(x -> x.getActive_s() + x.getActive_t() + x.getActive_y() + x.getActive_c()).reduce(0, Integer::sum),
                        goalList.stream().filter(x -> category.getId().equals(x.getCustomer_category_id())).map(x -> x.getActive_s() + x.getActive_t() + x.getActive_y() + x.getActive_c()).reduce(0, Integer::sum),
                        summaryList.stream().filter(x -> category.getId().equals(x.getCustomer_category_id())).map(x -> x.getCover_s() + x.getCover_t() + x.getCover_y() + x.getCover_c()).reduce(0, Integer::sum),
                        prevSummaryList.stream().filter(x -> category.getId().equals(x.getCustomer_category_id())).map(x -> x.getCover_s() + x.getCover_t() + x.getCover_y() + x.getCover_c()).reduce(0, Integer::sum));

                setActiveBoardTable("S", true, boardTableList, category.getId(), idAndAmounts, prevIdAndAmounts,
                        summaryList.stream().filter(x -> category.getId().equals(x.getCustomer_category_id())).map(BussProcessSummary::getActive_s).reduce(0, Integer::sum),
                        prevSummaryList.stream().filter(x -> category.getId().equals(x.getCustomer_category_id())).map(BussProcessSummary::getActive_s).reduce(0, Integer::sum),
                        goalList.stream().filter(x -> category.getId().equals(x.getCustomer_category_id())).map(BussProcessGoal::getActive_s).reduce(0, Integer::sum),
                        summaryList.stream().filter(x -> category.getId().equals(x.getCustomer_category_id())).map(BussProcessSummary::getCover_s).reduce(0, Integer::sum),
                        prevSummaryList.stream().filter(x -> category.getId().equals(x.getCustomer_category_id())).map(BussProcessSummary::getCover_s).reduce(0, Integer::sum));

                setActiveBoardTable("T", true, boardTableList, category.getId(), idAndAmounts, prevIdAndAmounts,
                        summaryList.stream().filter(x -> category.getId().equals(x.getCustomer_category_id())).map(BussProcessSummary::getActive_t).reduce(0, Integer::sum),
                        prevSummaryList.stream().filter(x -> category.getId().equals(x.getCustomer_category_id())).map(BussProcessSummary::getActive_t).reduce(0, Integer::sum),
                        goalList.stream().filter(x -> category.getId().equals(x.getCustomer_category_id())).map(BussProcessGoal::getActive_t).reduce(0, Integer::sum),
                        summaryList.stream().filter(x -> category.getId().equals(x.getCustomer_category_id())).map(BussProcessSummary::getCover_t).reduce(0, Integer::sum),
                        prevSummaryList.stream().filter(x -> category.getId().equals(x.getCustomer_category_id())).map(BussProcessSummary::getCover_t).reduce(0, Integer::sum));

                setActiveBoardTable("Y", true, boardTableList, category.getId(), idAndAmounts, prevIdAndAmounts,
                        summaryList.stream().filter(x -> category.getId().equals(x.getCustomer_category_id())).map(BussProcessSummary::getActive_y).reduce(0, Integer::sum),
                        prevSummaryList.stream().filter(x -> category.getId().equals(x.getCustomer_category_id())).map(BussProcessSummary::getActive_y).reduce(0, Integer::sum),
                        goalList.stream().filter(x -> category.getId().equals(x.getCustomer_category_id())).map(BussProcessGoal::getActive_y).reduce(0, Integer::sum),
                        summaryList.stream().filter(x -> category.getId().equals(x.getCustomer_category_id())).map(BussProcessSummary::getCover_y).reduce(0, Integer::sum),
                        prevSummaryList.stream().filter(x -> category.getId().equals(x.getCustomer_category_id())).map(BussProcessSummary::getCover_y).reduce(0, Integer::sum));

                setActiveBoardTable("C", true, boardTableList, category.getId(), idAndAmounts, prevIdAndAmounts,
                        summaryList.stream().filter(x -> category.getId().equals(x.getCustomer_category_id())).map(BussProcessSummary::getActive_c).reduce(0, Integer::sum),
                        prevSummaryList.stream().filter(x -> category.getId().equals(x.getCustomer_category_id())).map(BussProcessSummary::getActive_c).reduce(0, Integer::sum),
                        goalList.stream().filter(x -> category.getId().equals(x.getCustomer_category_id())).map(BussProcessGoal::getActive_c).reduce(0, Integer::sum),
                        summaryList.stream().filter(x -> category.getId().equals(x.getCustomer_category_id())).map(BussProcessSummary::getCover_c).reduce(0, Integer::sum),
                        prevSummaryList.stream().filter(x -> category.getId().equals(x.getCustomer_category_id())).map(BussProcessSummary::getCover_c).reduce(0, Integer::sum));
            }
        }
        return ResponseJson.success(boardTableList);
    }


    /**
     * 释放分表
     *
     * @param param 入参
     * @return 各结果
     */
    @Override
    public ResponseJson release(BoardParam param) {
        initBoardParam(param);
        List<BussProcessSummary> summaryList = bussProcessSummaryService.getAllBySelectKey(BussProcessSummary.builder().summary_month(param.getMonth()).userIds(param.getUserIds()).build());
        List<BussProcessSummary> prevSummaryList = bussProcessSummaryService.getAllBySelectKey(BussProcessSummary.builder().summary_month(param.getPrevMoth()).userIds(param.getUserIds()).build());

        List<BoardTable> boardTableList = new ArrayList<>();
        if (param.getType() == 1) {
            List<Team> teams = teamService.getAll();
            for (Team team : teams) {
                if (param.getTeam_id() != null && !param.getTeam_id().equals(team.getId())) {
                    continue;
                }
                setReleaseBoardTable(team.getName(), false, boardTableList,
                        summaryList.stream().filter(x -> team.getId().equals(x.getTeam_id())).map(x -> x.getRelease_visit_s() + x.getRelease_visit_t() + x.getRelease_visit_y() + x.getRelease_visit_c()).reduce(0, Integer::sum),
                        summaryList.stream().filter(x -> team.getId().equals(x.getTeam_id())).map(x -> x.getCover_s() + x.getCover_t() + x.getCover_y() + x.getCover_c()).reduce(0, Integer::sum),
                        prevSummaryList.stream().filter(x -> team.getId().equals(x.getTeam_id())).map(x -> x.getRelease_visit_s() + x.getRelease_visit_t() + x.getRelease_visit_y() + x.getRelease_visit_c()).reduce(0, Integer::sum),
                        prevSummaryList.stream().filter(x -> team.getId().equals(x.getTeam_id())).map(x -> x.getCover_s() + x.getCover_t() + x.getCover_y() + x.getCover_c()).reduce(0, Integer::sum),
                        summaryList.stream().filter(x -> team.getId().equals(x.getTeam_id())).map(x -> x.getRelease_deal_s() + x.getRelease_deal_t() + x.getRelease_deal_y() + x.getRelease_deal_c()).reduce(0, Integer::sum),
                        summaryList.stream().filter(x -> team.getId().equals(x.getTeam_id())).map(x -> x.getVisit_s() + x.getVisit_t() + x.getVisit_y() + x.getVisit_c()).reduce(0, Integer::sum),
                        prevSummaryList.stream().filter(x -> team.getId().equals(x.getTeam_id())).map(x -> x.getRelease_deal_s() + x.getRelease_deal_t() + x.getRelease_deal_y() + x.getRelease_deal_c()).reduce(0, Integer::sum),
                        prevSummaryList.stream().filter(x -> team.getId().equals(x.getTeam_id())).map(x -> x.getVisit_s() + x.getVisit_t() + x.getVisit_y() + x.getVisit_c()).reduce(0, Integer::sum),
                        summaryList.stream().filter(x -> team.getId().equals(x.getTeam_id())).map(x -> x.getRelease_repeat_s() + x.getRelease_repeat_t() + x.getRelease_repeat_y() + x.getRelease_repeat_c()).reduce(0, Integer::sum),
                        summaryList.stream().filter(x -> team.getId().equals(x.getTeam_id())).map(x -> x.getDeal_s() + x.getDeal_t() + x.getDeal_y() + x.getDeal_c()).reduce(0, Integer::sum),
                        prevSummaryList.stream().filter(x -> team.getId().equals(x.getTeam_id())).map(x -> x.getRelease_repeat_s() + x.getRelease_repeat_t() + x.getRelease_repeat_y() + x.getRelease_repeat_c()).reduce(0, Integer::sum),
                        prevSummaryList.stream().filter(x -> team.getId().equals(x.getTeam_id())).map(x -> x.getDeal_s() + x.getDeal_t() + x.getDeal_y() + x.getDeal_c()).reduce(0, Integer::sum));
            }
        } else {
            List<CustomerCategory> categories = customerCategoryService.getAllBySelectKey(CustomerCategory.builder().parent_id(0L).build());
            for (CustomerCategory category : categories) {
                setReleaseBoardTable(category.getName(), false, boardTableList,
                        summaryList.stream().filter(x -> category.getId().equals(x.getCustomer_category_id())).map(x -> x.getRelease_visit_s() + x.getRelease_visit_t() + x.getRelease_visit_y() + x.getRelease_visit_c()).reduce(0, Integer::sum),
                        summaryList.stream().filter(x -> category.getId().equals(x.getCustomer_category_id())).map(x -> x.getCover_s() + x.getCover_t() + x.getCover_y() + x.getCover_c()).reduce(0, Integer::sum),
                        prevSummaryList.stream().filter(x -> category.getId().equals(x.getCustomer_category_id())).map(x -> x.getRelease_visit_s() + x.getRelease_visit_t() + x.getRelease_visit_y() + x.getRelease_visit_c()).reduce(0, Integer::sum),
                        prevSummaryList.stream().filter(x -> category.getId().equals(x.getCustomer_category_id())).map(x -> x.getCover_s() + x.getCover_t() + x.getCover_y() + x.getCover_c()).reduce(0, Integer::sum),
                        summaryList.stream().filter(x -> category.getId().equals(x.getCustomer_category_id())).map(x -> x.getRelease_deal_s() + x.getRelease_deal_t() + x.getRelease_deal_y() + x.getRelease_deal_c()).reduce(0, Integer::sum),
                        summaryList.stream().filter(x -> category.getId().equals(x.getCustomer_category_id())).map(x -> x.getVisit_s() + x.getVisit_t() + x.getVisit_y() + x.getVisit_c()).reduce(0, Integer::sum),
                        prevSummaryList.stream().filter(x -> category.getId().equals(x.getCustomer_category_id())).map(x -> x.getRelease_deal_s() + x.getRelease_deal_t() + x.getRelease_deal_y() + x.getRelease_deal_c()).reduce(0, Integer::sum),
                        prevSummaryList.stream().filter(x -> category.getId().equals(x.getCustomer_category_id())).map(x -> x.getVisit_s() + x.getVisit_t() + x.getVisit_y() + x.getVisit_c()).reduce(0, Integer::sum),
                        summaryList.stream().filter(x -> category.getId().equals(x.getCustomer_category_id())).map(x -> x.getRelease_repeat_s() + x.getRelease_repeat_t() + x.getRelease_repeat_y() + x.getRelease_repeat_c()).reduce(0, Integer::sum),
                        summaryList.stream().filter(x -> category.getId().equals(x.getCustomer_category_id())).map(x -> x.getDeal_s() + x.getDeal_t() + x.getDeal_y() + x.getDeal_c()).reduce(0, Integer::sum),
                        prevSummaryList.stream().filter(x -> category.getId().equals(x.getCustomer_category_id())).map(x -> x.getRelease_repeat_s() + x.getRelease_repeat_t() + x.getRelease_repeat_y() + x.getRelease_repeat_c()).reduce(0, Integer::sum),
                        prevSummaryList.stream().filter(x -> category.getId().equals(x.getCustomer_category_id())).map(x -> x.getDeal_s() + x.getDeal_t() + x.getDeal_y() + x.getDeal_c()).reduce(0, Integer::sum));

                setReleaseBoardTable("S", true, boardTableList,
                        summaryList.stream().filter(x -> category.getId().equals(x.getCustomer_category_id())).map(BussProcessSummary::getRelease_visit_s).reduce(0, Integer::sum),
                        summaryList.stream().filter(x -> category.getId().equals(x.getCustomer_category_id())).map(BussProcessSummary::getCover_s).reduce(0, Integer::sum),
                        prevSummaryList.stream().filter(x -> category.getId().equals(x.getCustomer_category_id())).map(BussProcessSummary::getRelease_visit_s).reduce(0, Integer::sum),
                        prevSummaryList.stream().filter(x -> category.getId().equals(x.getCustomer_category_id())).map(BussProcessSummary::getCover_s).reduce(0, Integer::sum),
                        summaryList.stream().filter(x -> category.getId().equals(x.getCustomer_category_id())).map(BussProcessSummary::getRelease_deal_s).reduce(0, Integer::sum),
                        summaryList.stream().filter(x -> category.getId().equals(x.getCustomer_category_id())).map(BussProcessSummary::getVisit_s).reduce(0, Integer::sum),
                        prevSummaryList.stream().filter(x -> category.getId().equals(x.getCustomer_category_id())).map(BussProcessSummary::getRelease_deal_s).reduce(0, Integer::sum),
                        prevSummaryList.stream().filter(x -> category.getId().equals(x.getCustomer_category_id())).map(BussProcessSummary::getVisit_s).reduce(0, Integer::sum),
                        summaryList.stream().filter(x -> category.getId().equals(x.getCustomer_category_id())).map(BussProcessSummary::getRelease_repeat_s).reduce(0, Integer::sum),
                        summaryList.stream().filter(x -> category.getId().equals(x.getCustomer_category_id())).map(BussProcessSummary::getDeal_s).reduce(0, Integer::sum),
                        prevSummaryList.stream().filter(x -> category.getId().equals(x.getCustomer_category_id())).map(BussProcessSummary::getRelease_repeat_s).reduce(0, Integer::sum),
                        prevSummaryList.stream().filter(x -> category.getId().equals(x.getCustomer_category_id())).map(BussProcessSummary::getDeal_s).reduce(0, Integer::sum));

                setReleaseBoardTable("T", true, boardTableList,
                        summaryList.stream().filter(x -> category.getId().equals(x.getCustomer_category_id())).map(BussProcessSummary::getRelease_visit_t).reduce(0, Integer::sum),
                        summaryList.stream().filter(x -> category.getId().equals(x.getCustomer_category_id())).map(BussProcessSummary::getCover_t).reduce(0, Integer::sum),
                        prevSummaryList.stream().filter(x -> category.getId().equals(x.getCustomer_category_id())).map(BussProcessSummary::getRelease_visit_t).reduce(0, Integer::sum),
                        prevSummaryList.stream().filter(x -> category.getId().equals(x.getCustomer_category_id())).map(BussProcessSummary::getCover_t).reduce(0, Integer::sum),
                        summaryList.stream().filter(x -> category.getId().equals(x.getCustomer_category_id())).map(BussProcessSummary::getRelease_deal_t).reduce(0, Integer::sum),
                        summaryList.stream().filter(x -> category.getId().equals(x.getCustomer_category_id())).map(BussProcessSummary::getVisit_t).reduce(0, Integer::sum),
                        prevSummaryList.stream().filter(x -> category.getId().equals(x.getCustomer_category_id())).map(BussProcessSummary::getRelease_deal_t).reduce(0, Integer::sum),
                        prevSummaryList.stream().filter(x -> category.getId().equals(x.getCustomer_category_id())).map(BussProcessSummary::getVisit_t).reduce(0, Integer::sum),
                        summaryList.stream().filter(x -> category.getId().equals(x.getCustomer_category_id())).map(BussProcessSummary::getRelease_repeat_t).reduce(0, Integer::sum),
                        summaryList.stream().filter(x -> category.getId().equals(x.getCustomer_category_id())).map(BussProcessSummary::getDeal_t).reduce(0, Integer::sum),
                        prevSummaryList.stream().filter(x -> category.getId().equals(x.getCustomer_category_id())).map(BussProcessSummary::getRelease_repeat_t).reduce(0, Integer::sum),
                        prevSummaryList.stream().filter(x -> category.getId().equals(x.getCustomer_category_id())).map(BussProcessSummary::getDeal_t).reduce(0, Integer::sum));

                setReleaseBoardTable("Y", true, boardTableList,
                        summaryList.stream().filter(x -> category.getId().equals(x.getCustomer_category_id())).map(BussProcessSummary::getRelease_visit_y).reduce(0, Integer::sum),
                        summaryList.stream().filter(x -> category.getId().equals(x.getCustomer_category_id())).map(BussProcessSummary::getCover_y).reduce(0, Integer::sum),
                        prevSummaryList.stream().filter(x -> category.getId().equals(x.getCustomer_category_id())).map(BussProcessSummary::getRelease_visit_y).reduce(0, Integer::sum),
                        prevSummaryList.stream().filter(x -> category.getId().equals(x.getCustomer_category_id())).map(BussProcessSummary::getCover_y).reduce(0, Integer::sum),
                        summaryList.stream().filter(x -> category.getId().equals(x.getCustomer_category_id())).map(BussProcessSummary::getRelease_deal_y).reduce(0, Integer::sum),
                        summaryList.stream().filter(x -> category.getId().equals(x.getCustomer_category_id())).map(BussProcessSummary::getVisit_y).reduce(0, Integer::sum),
                        prevSummaryList.stream().filter(x -> category.getId().equals(x.getCustomer_category_id())).map(BussProcessSummary::getRelease_deal_y).reduce(0, Integer::sum),
                        prevSummaryList.stream().filter(x -> category.getId().equals(x.getCustomer_category_id())).map(BussProcessSummary::getVisit_y).reduce(0, Integer::sum),
                        summaryList.stream().filter(x -> category.getId().equals(x.getCustomer_category_id())).map(BussProcessSummary::getRelease_repeat_y).reduce(0, Integer::sum),
                        summaryList.stream().filter(x -> category.getId().equals(x.getCustomer_category_id())).map(BussProcessSummary::getDeal_y).reduce(0, Integer::sum),
                        prevSummaryList.stream().filter(x -> category.getId().equals(x.getCustomer_category_id())).map(BussProcessSummary::getRelease_repeat_y).reduce(0, Integer::sum),
                        prevSummaryList.stream().filter(x -> category.getId().equals(x.getCustomer_category_id())).map(BussProcessSummary::getDeal_y).reduce(0, Integer::sum));

                setReleaseBoardTable("C", true, boardTableList,
                        summaryList.stream().filter(x -> category.getId().equals(x.getCustomer_category_id())).map(BussProcessSummary::getRelease_visit_c).reduce(0, Integer::sum),
                        summaryList.stream().filter(x -> category.getId().equals(x.getCustomer_category_id())).map(BussProcessSummary::getCover_c).reduce(0, Integer::sum),
                        prevSummaryList.stream().filter(x -> category.getId().equals(x.getCustomer_category_id())).map(BussProcessSummary::getRelease_visit_c).reduce(0, Integer::sum),
                        prevSummaryList.stream().filter(x -> category.getId().equals(x.getCustomer_category_id())).map(BussProcessSummary::getCover_c).reduce(0, Integer::sum),
                        summaryList.stream().filter(x -> category.getId().equals(x.getCustomer_category_id())).map(BussProcessSummary::getRelease_deal_c).reduce(0, Integer::sum),
                        summaryList.stream().filter(x -> category.getId().equals(x.getCustomer_category_id())).map(BussProcessSummary::getVisit_c).reduce(0, Integer::sum),
                        prevSummaryList.stream().filter(x -> category.getId().equals(x.getCustomer_category_id())).map(BussProcessSummary::getRelease_deal_c).reduce(0, Integer::sum),
                        prevSummaryList.stream().filter(x -> category.getId().equals(x.getCustomer_category_id())).map(BussProcessSummary::getVisit_c).reduce(0, Integer::sum),
                        summaryList.stream().filter(x -> category.getId().equals(x.getCustomer_category_id())).map(BussProcessSummary::getRelease_repeat_c).reduce(0, Integer::sum),
                        summaryList.stream().filter(x -> category.getId().equals(x.getCustomer_category_id())).map(BussProcessSummary::getDeal_c).reduce(0, Integer::sum),
                        prevSummaryList.stream().filter(x -> category.getId().equals(x.getCustomer_category_id())).map(BussProcessSummary::getRelease_repeat_c).reduce(0, Integer::sum),
                        prevSummaryList.stream().filter(x -> category.getId().equals(x.getCustomer_category_id())).map(BussProcessSummary::getDeal_c).reduce(0, Integer::sum));

            }
        }
        return ResponseJson.success(boardTableList);
    }

    /**
     * 日报汇总
     *
     * @param param 入参
     * @return 个结果
     */
    @Override
    public ResponseJson report(BoardParam param) {
        initBoardParam(param);
        //拜访结果和后续行动汇总
        List<ReportSummary> querySummaryList = dateReportService.summaryAction(param.getUserIds(), param.getType(), param.getDate());
        List<ReportSummary> responseSummaryList = new ArrayList<>();
        if (param.getType() == 1) {
            List<Team> teams = teamService.getAll();
            for (Team team : teams) {
                if (param.getTeam_id() != null && !param.getTeam_id().equals(team.getId())) {
                    continue;
                }
                ReportSummary reportSummary = new ReportSummary().setLabel(team.getName()).setAverage_sale(BigDecimal.ZERO);
                for (ReportSummary summary : querySummaryList) {
                    if (team.getId().equals(summary.getTeam_id())) {
                        summary.setLabel(team.getName());
                        initReportSummary(summary);
                        reportSummary = summary;
                    }
                }
                responseSummaryList.add(reportSummary);
            }
        } else {
            List<CustomerCategory> categories = customerCategoryService.getAllBySelectKey(CustomerCategory.builder().parent_id(0L).build());
            for (CustomerCategory category : categories) {
                ReportSummary reportSummary = new ReportSummary().setLabel(category.getName()).setAverage_sale(BigDecimal.ZERO);
                ReportSummary reportSummaryS = new ReportSummary().setLabel("S").setAverage_sale(BigDecimal.ZERO);
                ReportSummary reportSummaryT = new ReportSummary().setLabel("T").setAverage_sale(BigDecimal.ZERO);
                ReportSummary reportSummaryY = new ReportSummary().setLabel("Y").setAverage_sale(BigDecimal.ZERO);
                ReportSummary reportSummaryC = new ReportSummary().setLabel("C").setAverage_sale(BigDecimal.ZERO);
                for (ReportSummary summary : querySummaryList) {
                    if (category.getId().equals(summary.getCustomer_category_id())) {
                        if ("S".equals(summary.getCustomer_gradation())) {
                            summary.setLabel("S");
                            initReportSummary(summary);
                            reportSummaryS = summary;
                            addParentReportSummary(reportSummary, reportSummaryS);
                        } else if ("T".equals(summary.getCustomer_gradation())) {
                            summary.setLabel("T");
                            initReportSummary(summary);
                            reportSummaryT = summary;
                            addParentReportSummary(reportSummary, reportSummaryT);
                        } else if ("Y".equals(summary.getCustomer_gradation())) {
                            summary.setLabel("Y");
                            initReportSummary(summary);
                            reportSummaryY = summary;
                            addParentReportSummary(reportSummary, reportSummaryY);
                        } else if ("C".equals(summary.getCustomer_gradation())) {
                            summary.setLabel("C");
                            initReportSummary(summary);
                            reportSummaryC = summary;
                            addParentReportSummary(reportSummary, reportSummaryC);
                        }
                    }
                }
                List<ReportSummary> childList = new ArrayList<>();
                childList.add(reportSummaryS);
                childList.add(reportSummaryT);
                childList.add(reportSummaryY);
                childList.add(reportSummaryC);
                reportSummary.setChild(childList);

                initReportSummary(reportSummary);
                responseSummaryList.add(reportSummary);
            }
        }
        return ResponseJson.success(responseSummaryList);
    }

    /**
     * 销售排行
     *
     * @param param 入参
     * @return 个结果
     */
    @Override
    public ResponseJson topSale(BoardParam param) {
        User user = UserToken.getContext();
        if (CodeCaption.ROLE_SALE != user.getRole() && CodeCaption.ROLE_ADMIN != user.getRole()){
            return ResponseJson.success(new Page<>());
        }
        initBoardParam(param);
        return ResponseJson.success(dateReportService.topSale(param));
    }

    @Override
    public ResponseJson dealClassify(BoardParam param) {
        initBoardParam(param);
        List<DateReport> dateReports = dateReportService.distinctCustomerIdForDeal(param.getUserIds(), param.getMonth(), param.getMonthEnd());
        List<DateReport> prevDateReports = dateReportService.distinctCustomerIdForDeal(param.getUserIds(), param.getPrevMoth(), param.getPrevMoth() * 100 + 31);
        //设置是否上月购买过
        for (DateReport dateReport : dateReports) {
            for (DateReport prevDateReport : prevDateReports) {
                if (dateReport.getCustomer_id().equals(prevDateReport.getCustomer_id())) {
                    dateReport.setEver_buy(true);
                    break;
                }

            }
        }

        List<BoardTable> boardTableList = new ArrayList<>();
        if (param.getType() == 1) {
            List<Team> teams = teamService.getAll();
            for (Team team : teams) {
                if (param.getTeam_id() != null && !param.getTeam_id().equals(team.getId())) {
                    continue;
                }
                setDealClassify(team.getName(), false, boardTableList,
                        prevDateReports.stream().filter(x -> team.getId().equals(x.getTeam_id())).count(),
                        dateReports.stream().filter(x -> team.getId().equals(x.getTeam_id()) && x.getEver_buy()).count(),
                        dateReports.stream().filter(x -> team.getId().equals(x.getTeam_id()) && !x.getEver_buy()).count());
            }
        } else {
            List<CustomerCategory> categories = customerCategoryService.getAllBySelectKey(CustomerCategory.builder().parent_id(0L).build());
            for (CustomerCategory category : categories) {
                setDealClassify(category.getName(), false, boardTableList,
                        prevDateReports.stream().filter(x -> category.getId().equals(x.getTeam_id())).count(),
                        dateReports.stream().filter(x -> category.getId().equals(x.getTeam_id()) && x.getEver_buy()).count(),
                        dateReports.stream().filter(x -> category.getId().equals(x.getTeam_id()) && !x.getEver_buy()).count());
                setDealClassify("S", true, boardTableList,
                        prevDateReports.stream().filter(x -> category.getId().equals(x.getTeam_id()) && "S".equals(x.getCustomer_gradation())).count(),
                        dateReports.stream().filter(x -> category.getId().equals(x.getTeam_id()) && "S".equals(x.getCustomer_gradation()) && x.getEver_buy()).count(),
                        dateReports.stream().filter(x -> category.getId().equals(x.getTeam_id()) && "S".equals(x.getCustomer_gradation()) && !x.getEver_buy()).count());
                setDealClassify("T", true, boardTableList,
                        prevDateReports.stream().filter(x -> category.getId().equals(x.getTeam_id()) && "T".equals(x.getCustomer_gradation())).count(),
                        dateReports.stream().filter(x -> category.getId().equals(x.getTeam_id()) && "T".equals(x.getCustomer_gradation()) && x.getEver_buy()).count(),
                        dateReports.stream().filter(x -> category.getId().equals(x.getTeam_id()) && "T".equals(x.getCustomer_gradation()) && !x.getEver_buy()).count());
                setDealClassify("Y", true, boardTableList,
                        prevDateReports.stream().filter(x -> category.getId().equals(x.getTeam_id()) && "Y".equals(x.getCustomer_gradation())).count(),
                        dateReports.stream().filter(x -> category.getId().equals(x.getTeam_id()) && "Y".equals(x.getCustomer_gradation()) && x.getEver_buy()).count(),
                        dateReports.stream().filter(x -> category.getId().equals(x.getTeam_id()) && "Y".equals(x.getCustomer_gradation()) && !x.getEver_buy()).count());
                setDealClassify("C", true, boardTableList,
                        prevDateReports.stream().filter(x -> category.getId().equals(x.getTeam_id()) && "C".equals(x.getCustomer_gradation())).count(),
                        dateReports.stream().filter(x -> category.getId().equals(x.getTeam_id()) && "C".equals(x.getCustomer_gradation()) && x.getEver_buy()).count(),
                        dateReports.stream().filter(x -> category.getId().equals(x.getTeam_id()) && "C".equals(x.getCustomer_gradation()) && !x.getEver_buy()).count());
            }
        }
        return ResponseJson.success(boardTableList);
    }


    /**
     * 初始日报中的其他属性
     *
     * @param reportSummary 日报统计
     */
    public void initReportSummary(ReportSummary reportSummary) {
        reportSummary.setVisit(reportSummary.getResult_deal() + reportSummary.getResult_mind() + reportSummary.getResult_no_mind() + reportSummary.getResult_other());
        reportSummary.setAverage_sale(NumberUtil.getAverageBigDecimal(reportSummary.getProduct_num(), reportSummary.getVisit(), 0));
    }

    /**
     * 计算日报统计中父节点的值
     * 父节点的值从子节点中累加
     *
     * @param reportSummary      日报统计
     * @param reportSummaryChild 日报统计下的子节点
     */
    public void addParentReportSummary(ReportSummary reportSummary, ReportSummary reportSummaryChild) {
        reportSummary.setResult_deal(reportSummary.getResult_deal() + reportSummaryChild.getResult_deal());
        reportSummary.setResult_mind(reportSummary.getResult_mind() + reportSummaryChild.getResult_mind());
        reportSummary.setResult_no_mind(reportSummary.getResult_no_mind() + reportSummaryChild.getResult_no_mind());
        reportSummary.setResult_other(reportSummary.getResult_other() + reportSummaryChild.getResult_other());
        reportSummary.setAction_deal(reportSummary.getAction_deal() + reportSummaryChild.getAction_deal());
        reportSummary.setAction_continue(reportSummary.getAction_continue() + reportSummaryChild.getAction_continue());
        reportSummary.setAction_not(reportSummary.getAction_not() + reportSummaryChild.getAction_not());
        reportSummary.setAction_other(reportSummary.getAction_other() + reportSummaryChild.getAction_other());
        reportSummary.setProduct_num(reportSummary.getProduct_num() + reportSummaryChild.getProduct_num());
    }

    /**
     * 设置销售目标、已完成、完成进度、生意占比
     *
     * @param id                 ID
     * @param label              标签
     * @param isChild            是否下级值
     * @param totalValue         已完成总金额
     * @param goalValue          目标金额
     * @param idAndAmounts       已完成的内容
     * @param goalContents       销售目标
     * @param finishContents     已完成
     * @param planContents       已完成占比
     * @param proportionContents 生意占比
     */
    private void setResultBoardContent(Long id, String label, boolean isChild, BigDecimal totalValue, BigDecimal goalValue, List<IdAndAmount> idAndAmounts,
                                       List<BoardContent> goalContents, List<BoardContent> finishContents, List<BoardContent> planContents, List<BoardContent> proportionContents) {
        BigDecimal amount = BigDecimal.ZERO;
        for (IdAndAmount andAmount : idAndAmounts) {
            if (id.equals(andAmount.getId()) && (!isChild || andAmount.getCustomer_gradation().equals(label))) {
                amount = amount.add(andAmount.getAmount());
            }
        }
        addBoardContent(isChild, label, NumberUtil.toTenThouAmount(goalValue).toString(), null, goalContents);
        addBoardContent(isChild, label, NumberUtil.toTenThouAmount(amount).toString(), null, finishContents);
        addBoardContent(isChild, label, NumberUtil.getPercentString(amount, goalValue, "无目标"), null, planContents);
        addBoardContent(isChild, label, NumberUtil.getPercentString(amount, totalValue, "0"), null, proportionContents);
    }


    /**
     * 设置业务过程内容
     *
     * @param label              标签
     * @param isChild            是否下级值
     * @param cover              覆盖数
     * @param visit              拜访数
     * @param deal               成交数
     * @param active             活跃数
     * @param release            释放数
     * @param coverTotal         覆盖总数
     * @param visitTotal         拜访总数
     * @param dealTotal          成交总数
     * @param activeTotal        活跃总数
     * @param releaseTotal       释放总数
     * @param amount             销售金额
     * @param countCustomer      销售的客户数
     * @param coverContents      覆盖列表
     * @param visitContents      拜访列表
     * @param dealContents       成交列表
     * @param activeContents     活跃列表
     * @param releaseContents    释放列表
     * @param visitRatioContents 拜访率列表
     * @param dealRatioContents  成交率列表
     * @param unitPriceContents  客单价列表
     */
    private void setProcessBoardContent(String label, boolean isChild, Integer cover, Integer visit, Integer deal, Integer active, Integer release,
                                        Integer coverTotal, Integer visitTotal, Integer dealTotal, Integer activeTotal, Integer releaseTotal, BigDecimal amount, Integer countCustomer,
                                        List<BoardContent> coverContents, List<BoardContent> visitContents, List<BoardContent> dealContents, List<BoardContent> activeContents,
                                        List<BoardContent> releaseContents, List<BoardContent> visitRatioContents, List<BoardContent> dealRatioContents, List<BoardContent> unitPriceContents) {
        addBoardContent(isChild, label, cover.toString(), NumberUtil.getPercentString(cover, coverTotal, "0") + '%', coverContents);
        addBoardContent(isChild, label, visit.toString(), NumberUtil.getPercentString(visit, visitTotal, "0") + '%', visitContents);
        addBoardContent(isChild, label, deal.toString(), NumberUtil.getPercentString(deal, dealTotal, "0") + '%', dealContents);
        addBoardContent(isChild, label, active.toString(), NumberUtil.getPercentString(active, activeTotal, "0") + '%', activeContents);
        addBoardContent(isChild, label, release.toString(), NumberUtil.getPercentString(release, releaseTotal, "0") + '%', releaseContents);
        addBoardContent(isChild, label, NumberUtil.getPercentString(visit, cover, "无客户"), null, visitRatioContents);
        addBoardContent(isChild, label, NumberUtil.getPercentString(deal, visit, "无拜访"), null, dealRatioContents);
        addBoardContent(isChild, label, NumberUtil.getAverageString(amount, countCustomer, "0"), null, unitPriceContents);

    }

    /**
     * 将统计数据放入统计集合汇总
     *
     * @param isChild       是否是子标签
     * @param label         标签
     * @param value         值
     * @param percent       百分比
     * @param boardContents 数据集合
     */
    private void addBoardContent(boolean isChild, String label, String value, String percent, List<BoardContent> boardContents) {
        BoardContent boardContent = new BoardContent().setLabel(label).setValue(value).setPercent(percent);
        if (isChild) {
            int order = boardContents.size() - 1;
            if ("S".equals(label)) {
                boardContents.get(order).setChild(new ArrayList<>()).getChild().add(boardContent);
            } else {
                boardContents.get(order).getChild().add(boardContent);
            }
        } else {
            boardContents.add(boardContent);
        }

    }


    /**
     * 设置覆盖数内容
     *
     * @param label          标签
     * @param isChild        是否下级值
     * @param cover          查询月覆盖数
     * @param prevCover      上月覆盖数
     * @param goal           查询月覆盖目标
     * @param coverTotal     查询月总覆盖数
     * @param prevCoverTotal 上月总覆盖数
     * @param boardTableList 覆盖内容列表
     */
    private void setCoverBoardTable(String label, boolean isChild,
                                    int cover, int prevCover, int goal, int coverTotal, int prevCoverTotal,
                                    List<BoardTable> boardTableList) {

        BoardTable boardTable = new BoardTable().setLabel(label);
        //覆盖数
        boardTable.setColumn_1(new BigDecimal(cover));
        //覆盖数差值
        boardTable.setColumn_2(new BigDecimal(cover - prevCover));
        //目标达成率(-1时显示适当的文字,比如无目标)
        boardTable.setColumn_3(NumberUtil.getPercentBigDecimal(cover, goal, -1));
        //数量占比
        BigDecimal ratio = NumberUtil.getPercentBigDecimal(cover, coverTotal, 0);
        boardTable.setColumn_4(ratio);
        //数量占比差值
        boardTable.setColumn_5(ratio.subtract(NumberUtil.getPercentBigDecimal(prevCover, prevCoverTotal, 0)));
        addBoardTable(boardTableList, boardTable, isChild, label);
    }

    private void setVisitBoardTable(String label, boolean isChild, List<BoardTable> boardTableList,
                                    int visit, int prevVisit, int goal, int cover, int prevCover, IdAndCount customerAndReport, IdAndCount prevCustomerAndReport) {
        BoardTable boardTable = new BoardTable().setLabel(label);
        //拜访数
        boardTable.setColumn_1(new BigDecimal(visit));
        //拜访数差值
        boardTable.setColumn_2(new BigDecimal(visit - prevVisit));
        //目标达成率(-1时显示适当的文字,比如无目标)
        boardTable.setColumn_3(NumberUtil.getPercentBigDecimal(visit, goal, -1));
        //拜访率
        BigDecimal visitRatio = NumberUtil.getPercentBigDecimal(visit, cover, 0);
        boardTable.setColumn_4(visitRatio);
        //拜访率差值
        boardTable.setColumn_5(visitRatio.subtract(NumberUtil.getPercentBigDecimal(prevVisit, prevCover, 0)));
        //平均拜访次数(拜访总次数/拜服客户数去重复,客户数不包括未被拜访的,即日报中的客户数去重复)
        BigDecimal averageVisit = NumberUtil.getAverageBigDecimal(customerAndReport.getTotal(), customerAndReport.getAnther_total(), 0);
        boardTable.setColumn_6(averageVisit);
        //平均拜访次数差值
        boardTable.setColumn_7(averageVisit.subtract(NumberUtil.getAverageBigDecimal(prevCustomerAndReport.getTotal(), prevCustomerAndReport.getAnther_total(), 0)));
        addBoardTable(boardTableList, boardTable, isChild, label);

    }


    private void setDealBoardTable(String label, boolean isChild, List<BoardTable> boardTableList, Long id, BigDecimal totalValue, List<IdAndAmount> idAndAmounts,
                                   int deal, int prevDeal, int goal, int visit, int preVisit) {
        BigDecimal amount = BigDecimal.ZERO;
        for (IdAndAmount andAmount : idAndAmounts) {
            if (id.equals(andAmount.getId()) && (!isChild || andAmount.getCustomer_gradation().equals(label))) {
                amount = amount.add(andAmount.getAmount());
            }
        }
        BoardTable boardTable = new BoardTable().setLabel(label);
        //成交数
        boardTable.setColumn_1(new BigDecimal(deal));
        //成交数差值
        boardTable.setColumn_2(new BigDecimal(deal - prevDeal));
        //目标达成率(-1时显示适当的文字,比如无目标)
        boardTable.setColumn_3(NumberUtil.getPercentBigDecimal(deal, goal, -1));
        //成交率 (成交数/拜访客户数)
        BigDecimal dealRatio = NumberUtil.getPercentBigDecimal(deal, visit, 0);
        boardTable.setColumn_4(dealRatio);
        //成交率差值
        boardTable.setColumn_5(dealRatio.subtract(NumberUtil.getPercentBigDecimal(prevDeal, preVisit, 0)));
        //成交金额
        boardTable.setColumn_6(NumberUtil.toTenThouAmount(amount));
        //占比
        boardTable.setColumn_7(NumberUtil.getPercentBigDecimal(amount, totalValue, 0));
        addBoardTable(boardTableList, boardTable, isChild, label);
    }

    private void setActiveBoardTable(String label, boolean isChild, List<BoardTable> boardTableList, Long id, List<IdAndAmount> idAndAmounts, List<IdAndAmount> prevIdAndAmounts,
                                     int active, int prevActive, int goal, int cover, int preCover) {
        BigDecimal amount = BigDecimal.ZERO;
        BigDecimal activeAmount = BigDecimal.ZERO;
        for (IdAndAmount andAmount : idAndAmounts) {
            if (id.equals(andAmount.getId()) && (!isChild || andAmount.getCustomer_gradation().equals(label))) {
                amount = amount.add(andAmount.getAmount());
                if (andAmount.getActiveness() != null && andAmount.getActiveness() == CodeCaption.ACTIVENESS_ACTIVE) {
                    activeAmount = activeAmount.add(andAmount.getAmount());
                }
            }
        }
        BigDecimal prevAmount = BigDecimal.ZERO;
        BigDecimal prevActiveAmount = BigDecimal.ZERO;
        for (IdAndAmount andAmount : prevIdAndAmounts) {
            if (id.equals(andAmount.getId()) && (!isChild || andAmount.getCustomer_gradation().equals(label))) {
                prevAmount = prevAmount.add(andAmount.getAmount());
                if (andAmount.getActiveness() != null && andAmount.getActiveness() == CodeCaption.ACTIVENESS_ACTIVE) {
                    prevActiveAmount = prevActiveAmount.add(andAmount.getAmount());
                }
            }
        }

        BoardTable boardTable = new BoardTable().setLabel(label);
        //活跃数
        boardTable.setColumn_1(new BigDecimal(active));
        //活跃数差值
        boardTable.setColumn_2(new BigDecimal(active - prevActive));
        //目标达成率(-1时显示适当的文字,比如无目标)
        boardTable.setColumn_3(NumberUtil.getPercentBigDecimal(active, goal, -1));
        //数量占比 (活跃数/覆盖客户数)
        BigDecimal activeRatio = NumberUtil.getPercentBigDecimal(active, cover, 0);
        boardTable.setColumn_4(activeRatio);
        //数量占比差值
        boardTable.setColumn_5(activeRatio.subtract(NumberUtil.getPercentBigDecimal(prevActive, preCover, 0)));
        //生意占比
        BigDecimal amountRation = NumberUtil.getPercentBigDecimal(activeAmount, amount, 0);
        boardTable.setColumn_6(amountRation);
        //生意占比差值
        boardTable.setColumn_7(amountRation.subtract(NumberUtil.getPercentBigDecimal(prevActiveAmount, prevAmount, 0)));
        addBoardTable(boardTableList, boardTable, isChild, label);
    }


    private void setReleaseBoardTable(String label, boolean isChild, List<BoardTable> boardTableList,
                                      int releaseVisit, int cover, int prevReleaseVisit, int prevCover,
                                      Integer releaseDeal, Integer visit, Integer prevReleaseDeal, Integer prevVisit,
                                      Integer releaseRepeat, Integer deal, Integer prevReleaseRepeat, Integer prevDeal) {
        BoardTable boardTable = new BoardTable().setLabel(label);

        //未拜访释放客户占比 (未拜访释放数/覆盖客户数)
        BigDecimal visitRatio = NumberUtil.getPercentBigDecimal(releaseVisit, cover, 0);
        boardTable.setColumn_1(visitRatio);
        //未拜访释放客户占比差值
        boardTable.setColumn_2(visitRatio.subtract(NumberUtil.getPercentBigDecimal(prevReleaseVisit, prevCover, 0)));
        //未成交释放客户占比 (未成交释放数/拜访客户数)
        BigDecimal dealRatio = NumberUtil.getPercentBigDecimal(releaseDeal, visit, 0);
        boardTable.setColumn_3(dealRatio);
        //未成交释放客户占比差值
        boardTable.setColumn_4(dealRatio.subtract(NumberUtil.getPercentBigDecimal(prevReleaseDeal, prevVisit, 0)));
        //未复购释放客户占比 (未复购释放数/交易客户数)
        BigDecimal repeatRatio = NumberUtil.getPercentBigDecimal(releaseRepeat, deal, 0);
        boardTable.setColumn_5(repeatRatio);
        //未复购释放客户占比差值
        boardTable.setColumn_6(repeatRatio.subtract(NumberUtil.getPercentBigDecimal(prevReleaseRepeat, prevDeal, 0)));
        //未拜访释放数
        boardTable.setColumn_7(new BigDecimal(releaseVisit));
        //未成交释放数
        boardTable.setColumn_8(new BigDecimal(releaseDeal));
        //未复购释放数
        boardTable.setColumn_9(new BigDecimal(releaseRepeat));
        addBoardTable(boardTableList, boardTable, isChild, label);
    }

    private void setDealClassify(String label, boolean isChild, List<BoardTable> boardTableList, long prevDeal, long everDeal, long newDeal) {
        BoardTable boardTable = new BoardTable().setLabel(label);
        //上月成交客户数
        boardTable.setColumn_1(new BigDecimal(prevDeal));
        //本月老客户成交数
        boardTable.setColumn_2(new BigDecimal(everDeal));
        //本月新客户成交数
        boardTable.setColumn_3(new BigDecimal(newDeal));
        //复购率
        boardTable.setColumn_4(NumberUtil.getPercentBigDecimal(everDeal, prevDeal, 0));
        addBoardTable(boardTableList, boardTable, isChild, label);
    }

    private void addBoardTable(List<BoardTable> boardTableList, BoardTable boardTable, boolean isChild, String label) {
        if (isChild) {
            int order = boardTableList.size() - 1;
            if ("S".equals(label)) {
                boardTableList.get(order).setChild(new ArrayList<>()).getChild().add(boardTable);
            } else {
                boardTableList.get(order).getChild().add(boardTable);
            }
        } else {
            boardTableList.add(boardTable);
        }
    }


}
