package com.design.filmr.interceptor;

import cn.hutool.core.util.StrUtil;
import com.design.filmr.bean.LocalUser;
import com.design.filmr.handle.TokenException;
import com.design.filmr.sys.entity.User;
import com.design.filmr.utils.RedisUtil;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

public class PermissionInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        Optional<AuthCheck> auth = getAuth(handler);
        if (!auth.isPresent()){
            return true;
        }
        String token = request.getHeader("X-Token");
        if (StrUtil.isBlank(token)){
            throw new TokenException("没有传Token");
        }
        User user = (User) RedisUtil.get(token);
        if (user==null){
            throw new TokenException("请重新登陆！");
        }
        LocalUser.set(user);
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        HandlerInterceptor.super.postHandle(request, response, handler, modelAndView);
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        LocalUser.clear();
        HandlerInterceptor.super.afterCompletion(request,response,handler,ex);
    }

    private Optional<AuthCheck> getAuth(Object handler) {
        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            AuthCheck authCheck = handlerMethod.getMethod().getAnnotation(AuthCheck.class);
            if (authCheck == null) {
                return Optional.empty();
            } else {
                return Optional.of(authCheck);
            }
        }
        return Optional.empty();
    }
}
