package com.design.filmr.sys.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.design.filmr.sys.entity.User;
import com.design.filmr.sys.mapper.UserMapper;
import com.design.filmr.sys.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author Marisa
 * @since 2023-09-16
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

    @Autowired
    private RedisTemplate redisTemplate;

//    @Override
    public Map<String, Object> login(User user){
        //根据用户名密码查询
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUserName,user.getUserName());
        wrapper.eq(User::getPassword,user.getPassword());
        User loginUser = this.baseMapper.selectOne(wrapper);

        //结果不为空，则生成token，并将用户信息存入redis
        if(loginUser !=null){
            //暂时用UUID，终极方案是jwt
            String key = "user:" + UUID.randomUUID();

            //存入redis
            loginUser.setPassword(null);
            redisTemplate.opsForValue().set(key,loginUser,30, TimeUnit.MINUTES);
            //数据永久有效

            //返回数据
            Map<String, Object> data = new HashMap<>();
            data.put("token",key);
            return data;
        }

        return null;
    }

    @Override
    public Map<String, Object> getUserInfo(String uid) {
        //根据token获取用户信息，redis
//        Object obj = redisTemplate.opsForValue().get(uid);
//        if(obj != null){
            //调用方法实现反序列化，反序列化成我们的user对象！！！！！！！！取出数据封装
//            User loginUser = JSON.parseObject(JSON.toJSONString(obj),User.class);
            User loginUser = getById(uid);
            Map<String,Object> data = new HashMap<>();
            data.put("username",loginUser.getUserName());
            data.put("index_url",loginUser.getIndexUrl());//这个视频是用户头像
//            data.put("userId",loginUser.getUserId());

            List<String> roleList = this.baseMapper.getRoleNameByUserId(loginUser.getUserId().toString());
            data.put("roles",roleList);

            return data;
//        }
//        return null;
        //重要，封装接口
    }

    @Override
    public void logout(String token) {
        redisTemplate.delete(token);
    }
}
