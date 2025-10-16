package com.bs.manage.mapper.account;

import com.bs.manage.mapper.common.CommonMapper;
import com.bs.manage.model.bean.account.User;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


/**
 * 2020/1/15 10:38
 * fzj
 */
@Repository
public interface UserMapper extends CommonMapper<User> {

    /**
     * 团队在账号中是否存
     *
     * @param team_id 团IDid
     * @return true或false
     */
    Boolean existUserInTeam(Long team_id);

    List<User> getByPage(@Param("limit") Integer limit, @Param("offset") Integer offset,
                         @Param("team_id") Long team_id, @Param("keyword") String keyword,
                         @Param("parent_id") Long parentId);

    Integer countByPage(@Param("team_id") Long team_id, @Param("keyword") String keyword,@Param("parent_id") Long parentId);

    //将所有下级id置空,是否有下级置为false
    void updateParentIdByUsers(@Param("parentId") Long parentId,@Param("users") List<User> users);

    void updateParentIdByParentId(@Param("newParentId")Long newParentId,@Param("oldParentId")Long oldParentId,@Param("teamId")Long teamId);

    List<User> getByIds(List<Long> list);


}

