package com.design.filmr.sys.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.design.filmr.sys.entity.User;

import java.util.Map;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author Marisa
 * @since 2023-09-16
 */
public interface IUserService extends IService<User> {

    Map<String, Object> login(User user);

    Map<String, Object> getUserInfo(String token);

    void logout(String token);
}
