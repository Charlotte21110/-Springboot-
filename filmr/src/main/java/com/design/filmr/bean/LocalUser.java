package com.design.filmr.bean;

import cn.hutool.core.map.MapUtil;
import com.design.filmr.sys.entity.User;

import java.util.Map;

public class LocalUser {
    private static ThreadLocal<Map<String, Object>> threadLocal = new ThreadLocal();

    public static void set(User user) {
        Map<String, Object> map = MapUtil.newHashMap();
        map.put("user", user);
        LocalUser.threadLocal.set(map);
    }

    public static User getUser() {
        Map<String, Object> map = LocalUser.threadLocal.get();
        if (map==null){
            return new User();
        }
        User user= (User) map.get("user");
        return user;
    }
    public static void clear() {
        LocalUser.threadLocal.remove();
    }

}
