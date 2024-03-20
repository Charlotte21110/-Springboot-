package com.design.filmr.sys.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.design.filmr.sys.entity.Role;
import com.design.filmr.sys.mapper.RoleMapper;
import com.design.filmr.sys.service.IRoleService;
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
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements IRoleService {

    @Override
    public Role getRoleByName(String roleName) {
        QueryWrapper<Role> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("role_name",roleName);
        return getOne(queryWrapper);
    }
}
