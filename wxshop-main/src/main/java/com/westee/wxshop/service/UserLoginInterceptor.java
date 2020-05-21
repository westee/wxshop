package com.westee.wxshop.service;

import org.apache.shiro.SecurityUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class UserLoginInterceptor implements HandlerInterceptor {
    private UserService userService;

    public UserLoginInterceptor(UserService userService) {
        this.userService = userService;
    }

    // 返回true才会执行postHandle;接受请求，处理请求之前。
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 获取用户手机号
        Object tel = SecurityUtils.getSubject().getPrincipal();
        if (tel != null) {
            User user = userService.getUserByTel(tel.toString());
            UserContext.setCurrentUser(user);
            // 说明已经登录
        }
        return true;
    }

    // 处理完请求之后。
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        UserContext.setCurrentUser(null);
    }

}
