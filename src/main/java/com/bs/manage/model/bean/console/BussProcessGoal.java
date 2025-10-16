package com.bs.manage.model.bean.console;

import com.bs.manage.model.bean.common.CommonModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;
import org.apache.ibatis.type.Alias;

import java.util.List;

/**
 * 2020/3/12 11:08
 * fzj
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Alias("bussProcessGoal")
@SuperBuilder
@NoArgsConstructor
@Accessors(chain = true)
public class BussProcessGoal extends CommonModel {

    private static final long serialVersionUID = 5495544852626166712L;

    private Long user_id;                        //账号id
    private Long team_id;                        //团队id
    private Long customer_category_id;           //客户一级属性id
    private Integer goal_month;                  //目标月份yyyyMM
    private Integer cover_s;                     //客户分层S-覆盖目标
    private Integer cover_t;                     //客户分层T-覆盖目标
    private Integer cover_y;                     //客户分层Y-覆盖目标
    private Integer cover_c;                     //客户分层C-覆盖目标
    private Integer visit_s;                     //客户分层S-拜访目标
    private Integer visit_t;                     //客户分层T-拜访目标
    private Integer visit_y;                     //客户分层Y-拜访目标
    private Integer visit_c;                     //客户分层C-拜访目标
    private Integer deal_s;                      //客户分层S-成交目标
    private Integer deal_t;                      //客户分层T-成交目标
    private Integer deal_y;                      //客户分层Y-成交目标
    private Integer deal_c;                      //客户分层C-成交目标
    private Integer active_s;                    //客户分层S-活跃目标
    private Integer active_t;                    //客户分层T-活跃目标
    private Integer active_y;                    //客户分层Y-活跃目标
    private Integer active_c;                    //客户分层C-活跃目标


    private List<Long> userIds;                  //账号id列表


}
