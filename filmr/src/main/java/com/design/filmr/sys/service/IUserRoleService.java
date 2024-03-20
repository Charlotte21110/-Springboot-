package com.design.filmr.sys.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.design.filmr.sys.entity.UserRole;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author Marisa
 * @since 2023-09-16
 */
public interface IUserRoleService extends IService<UserRole> {
    void removeByRoleId(Integer roleId);
}
