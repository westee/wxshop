package com.westee.wxshop.config;

import com.westee.wxshop.generate.User;
import com.westee.wxshop.service.CheckAuthCodeService;
import com.westee.wxshop.service.ShiroRealm;
import com.westee.wxshop.service.UserContext;
import com.westee.wxshop.service.UserService;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.cache.MemoryConstrainedCacheManager;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerAdapter;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class ShiroConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(userLoginInterceptor());
    }

    @Bean
    public UserLoginInterceptor userLoginInterceptor(UserService userService){
        return new UserLoginInterceptor();
    }

    @Bean
    public ShiroFilterFactoryBean shiroFilter(SecurityManager securityManager){
        ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
        shiroFilterFactoryBean.setSecurityManager(securityManager);

        Map<String, String> pattern = new HashMap<>();
        pattern.put("/api/code", "anon");
        pattern.put("/api/login", "anon");

        shiroFilterFactoryBean.setFilterChainDefinitionMap(pattern);
        return shiroFilterFactoryBean;
    }

    @Bean
    public SecurityManager securityManager (ShiroRealm shiroRealm){
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();

        securityManager.setRealm(shiroRealm);
        securityManager.setCacheManager(new MemoryConstrainedCacheManager());
        securityManager.setSessionManager(new DefaultWebSessionManager());
        return securityManager;
    }

    @Bean
    public ShiroRealm myShiroRealm(CheckAuthCodeService checkAuthCodeService){
        return new ShiroRealm(checkAuthCodeService);
    }


    private static class UserLoginInterceptor  implements HandlerInterceptor {
        private UserService userService;

        public UserLoginInterceptor(UserService userService) {
            this.userService = userService;
        }

        @Override
        public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
            System.out.println("pre-------------");
            Object tel = SecurityUtils.getSubject().getPrincipal();
            if(tel != null){
                User user = userService.getUserByTel(tel.toString());
                UserContext.setCurrentUser(user);
                // 已登录
            }
            return true;
        }

        @Override
        public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
            System.out.println("post-------------");

        }
    }
}
