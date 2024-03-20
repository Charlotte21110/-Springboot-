package com.design.filmr.sys.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.design.filmr.bean.LocalUser;
import com.design.filmr.common.vo.Result;
import com.design.filmr.interceptor.AuthCheck;
import com.design.filmr.sys.entity.Role;
import com.design.filmr.sys.entity.User;
import com.design.filmr.sys.entity.UserRole;
import com.design.filmr.sys.service.IRoleService;
import com.design.filmr.sys.service.IUserRoleService;
import com.design.filmr.sys.service.IUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author Marisa
 * @since 2023-09-16
 */
@Api(tags = "用户管理")
@RestController
//默认返回计算处理
@RequestMapping("/user")
@AllArgsConstructor
public class UserController {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);


    private final IUserService userService;
    private final IRoleService roleService;
    private final IUserRoleService userRoleService;
    @ApiOperation("拿到全部用户列表")
    @AuthCheck
    @GetMapping("/all")
    public Result<List<User>> getAllUser(){
        List<User> list = userService.list();
        return Result.success(list,"查询成功");
    }
    @PostMapping("/login")
    public Result<Map<String,Object>> login(@RequestBody User user){
        Map<String,Object> data = userService.login(user);
        if(data != null){
            return  Result.success(data);

        }
        return Result.fail(20002,"用户名或密码错误");
    }
    @PostMapping("sign")
    public  Result<?> signUser(@RequestBody User user){
        logger.info("添加" + user.toString());
        userService.save(user);

        //设置新增加的用户角色为user
        Role defaultUserRole = roleService.getRoleByName("user");
        if(defaultUserRole != null) {
            UserRole userRole = new UserRole();
            userRole.setUserId(user.getUserId());
            userRole.setRoleId(defaultUserRole.getRoleId());
            userRole.setRoleDesc(defaultUserRole.getRoleName());
            userRoleService.save(userRole);
        }
        return Result.success("注册用户成功");
    }
    @GetMapping("/info")
    @AuthCheck
    public Result<Map<String,Object>> getUserInfo(){
      //根据token获取用户信息，redis

      Map<String,Object> data =  userService.getUserInfo(LocalUser.getUser().getUserId().toString());
      if(data != null){
          return Result.success(data);
      }
      return Result.fail(20003,"登陆信息无效，重新登陆");
    }
    @PostMapping("/logout")
    @AuthCheck
    public Result<?> logout(@RequestHeader("X-Token") String token){
         userService.logout(token);
         return Result.success();
    }

    @GetMapping("/list")
    @AuthCheck
//    上面那个是传token的别注销
    public Result<Map<String,Object>> getUserList(
            @RequestParam(value="userName",required = false) String userName,
            @RequestParam(value="userAccount",required = false) String userAccount,
            @RequestParam(value="gender",required = false) String gender,
            @RequestParam(value="description",required = false) String description,
            @RequestParam(value="indexUrl",required = false) String indexUrl,
            @RequestParam(value="artist",required = false) Integer artist,
            @RequestParam(value="vip",required = false) Integer vip,
            @RequestParam(value="pageNo",defaultValue = "1") Long pageNo,
            @RequestParam(value="pageSize",defaultValue = "10") Long pageSize){
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(StringUtils.hasLength(userName),User::getUserName,userName);
        wrapper.eq(StringUtils.hasLength(userAccount),User::getUserAccount,userAccount);
        wrapper.orderByDesc(User::getUserId); //每一次新增都会倒序排列在前面
        //使用Lambda表达式的方法引用
        Page<User> page = new Page<>(pageNo,pageSize);
        userService.page(page,wrapper);
        Map<String,Object> data = new HashMap<>();
        data.put("total",page.getTotal());
        data.put("rows",page.getRecords());
        return Result.success(data);
    }

    @PostMapping
    @AuthCheck
    public  Result<?> addUser(@RequestBody User user){
        logger.info("添加" + user.toString());
        userService.save(user);


        if (!user.getRoleIdList().isEmpty()){
            List<UserRole> userRoleList = new ArrayList<>();
            List<Role> roleList = roleService.listByIds(user.getRoleIdList());
            for (Role role:roleList){
                UserRole userRole = new UserRole();
                userRole.setUserId(user.getUserId());
                userRole.setRoleId(role.getRoleId());
                userRole.setRoleDesc(role.getRoleName());
                userRoleList.add(userRole);

            }
            userRoleService.saveBatch(userRoleList);
        }
        return Result.success("新增用户成功");
    }

    @PutMapping
    @AuthCheck
    public  Result<?> updateUser(@RequestBody User user){
        userService.updateById(user);
        LambdaQueryWrapper lq=   new LambdaQueryWrapper<UserRole>().eq(UserRole::getUserId,user.getUserId());
        userRoleService.remove(lq);
        if (!user.getRoleIdList().isEmpty()){
            List<UserRole> userRoleList = new ArrayList<>();

            List<Role> roleList = roleService.listByIds(user.getRoleIdList());
            for (Role role:roleList){
                UserRole userRole = new UserRole();
                userRole.setUserId(user.getUserId());
                userRole.setRoleId(role.getRoleId());
                userRole.setRoleDesc(role.getRoleName());
                userRoleList.add(userRole);
            }
            userRoleService.saveBatch(userRoleList);
        }
        return Result.success("修改用户成功");
        //更新后端接口就这上下一点点）
    }

    @GetMapping("/{userId}")
    @AuthCheck
    public Result<User> getUserById(@PathVariable("userId") Integer userId){
        logger.info("Id" + userId);
        User user = userService.getById(userId);
        logger.info("User found: " + user);
        LambdaQueryWrapper<UserRole> lq=   new LambdaQueryWrapper();
        lq.eq(UserRole::getUserId,user.getUserId());
        List<UserRole> list = userRoleService.list(lq);
        if (list.isEmpty()){
            user.setRoleIdList(new ArrayList<>());
        }else {
            user.setRoleIdList(list.stream().map(UserRole::getRoleId).collect(Collectors.toList()));
        }
        return Result.success(user);
    }

    @DeleteMapping("/{userId}")
    @AuthCheck
    public Result<User> deleteUserById(@PathVariable("userId") Integer userId){
        userService.removeById(userId);
        logger.info("Id" + userId);
        logger.info("User found: " + userId);
        LambdaQueryWrapper lq=   new LambdaQueryWrapper<UserRole>().eq(UserRole::getUserId,userId);
        userRoleService.remove(lq);
        return Result.success("删除用户成功");
    }

    @GetMapping("/current")
    @AuthCheck
    public Result<?> getCurrentUser(){
        User user = LocalUser.getUser();
        return Result.success(user);
    }


}
