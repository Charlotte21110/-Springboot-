package com.design.filmr.sys.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.design.filmr.sys.entity.User;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author Marisa
 * @since 2023-09-16
 */
public interface UserMapper extends BaseMapper<User> {
    //也可以用注解但是不建议
    List<String> getRoleNameByUserId(String userId);
}
