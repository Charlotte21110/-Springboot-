package com.design.filmr.sys.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.design.filmr.common.vo.Result;
import com.design.filmr.interceptor.AuthCheck;
import com.design.filmr.sys.entity.Role;
import com.design.filmr.sys.service.IRoleService;
import com.design.filmr.sys.service.IUserRoleService;
import io.swagger.annotations.Api;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author Marisa
 * @since 2023-09-16
 */
@Api(tags="角色管理")
@RestController
@RequestMapping("/role")
public class RoleController {
    private static final Logger logger = LoggerFactory.getLogger(RoleController.class);

    @Autowired
    private IRoleService roleService;

    @Autowired
    private IUserRoleService userRoleService;

    @GetMapping("/list")
    @AuthCheck
    public Result<Map<String,Object>> getRoleList(
            @RequestParam(value="roleName",required = false) String roleName,
            @RequestParam(value = "roleDesc",required = false) String roleDesc,
            @RequestParam(value="pageNo") Long pageNo,
            @RequestParam(value="pageSize") Long pageSize){
        LambdaQueryWrapper<Role> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(StringUtils.hasLength(roleName),Role::getRoleName,roleName);
        wrapper.eq(StringUtils.hasLength(roleDesc),Role::getRoleDesc,roleDesc);
        wrapper.orderByDesc(Role::getRoleId); //每一次新增都会倒序排列在前面
        //使用Lambda表达式的方法引用
        Page<Role> page = new Page<>(pageNo,pageSize);
        roleService.page(page,wrapper);
        Map<String,Object> data = new HashMap<>();
        data.put("total",page.getTotal());
        data.put("rows",page.getRecords());
        return Result.success(data);
    }

    @PostMapping
    @AuthCheck
    public  Result<?> addRole(@RequestBody Role role){
        logger.info("添加" + role.toString());
        roleService.save(role);
        return Result.success("新增角色成功");
    }

    @PutMapping
    @AuthCheck
    public  Result<?> updateUser(@RequestBody Role role){
        roleService.updateById(role);
        return Result.success("修改角色成功");
    }

    @GetMapping("/{roleId}")
    @AuthCheck
    public Result<Role> getRoleById(@PathVariable("roleId") Integer roleId){
        logger.info("Id" + roleId);
        Role role = roleService.getById(roleId);
        logger.info("Role found: " + role);
        System.out.println(role);
        return Result.success(role);
    }

    @Transactional
    @DeleteMapping("/{roleId}")
    @AuthCheck
    public Result<Role> deleteRoleById(@PathVariable("roleId") Integer roleId){
        roleService.removeById(roleId);
        logger.info("Id" + roleId);
        logger.info("Role found: " + roleId);
        userRoleService.removeByRoleId(roleId);
        return Result.success("删除角色成功");
    }


    @GetMapping("/all")
    public Result<List<Role>> getAllRole(){
        List<Role> list = roleService.list();
        return Result.success(list);
    }

}
