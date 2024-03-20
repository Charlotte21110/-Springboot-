package com.design.filmr.sys.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.design.filmr.sys.entity.UserRole;
import com.design.filmr.sys.mapper.UserRoleMapper;
import com.design.filmr.sys.service.IUserRoleService;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author Marisa
 * @since 2023-09-16
 */
@Service
public class UserRoleServiceImpl extends ServiceImpl<UserRoleMapper, UserRole> implements IUserRoleService {
    @Override
    public void removeByRoleId(Integer roleId) {
        LambdaQueryWrapper<UserRole> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.eq(UserRole::getRoleId, roleId);
        baseMapper.delete(queryWrapper);
    }
}
