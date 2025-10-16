package com.bs.manage.mapper.account;

import com.bs.manage.model.bean.account.UserRole;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 2020/2/25 9:52
 * fzj
 */
@Repository
public interface UserRoleMapper {

    List<UserRole> getAll();
}
