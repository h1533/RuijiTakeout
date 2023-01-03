package com.jh.reggie.filter;

import com.alibaba.fastjson2.JSON;
import com.jh.reggie.commons.BaseContext;
import com.jh.reggie.commons.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author JH
 * @description 检查用户是否登录
 * @date 2022-12-06 14:28:14
 */
@Slf4j
@Component
@WebFilter(filterName = "LoginCheck", urlPatterns = "/*")
public class LoginCheckFilter implements Filter {

    /**
     * 路径匹配器
     */
    private static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest servletRequest = (HttpServletRequest) request;
        HttpServletResponse servletResponse = (HttpServletResponse) response;
        // 1、获取本次请求的URI
        String requestURI = servletRequest.getRequestURI();

        // 2、判断本次请求是否需要处理
        // 定义不需要处理的请求路径
        String[] urls = new String[]{
                "/employee/login", "/employee/logout",
                "/backend/**", "/front/**",
                "/common/**", "/user/sendMsg",
                "/user/login"
        };
        boolean check = check(urls, requestURI);

        // 3、如果不需要处理，则直接放行
        if (check) {
            log.info("Request {} not processed", requestURI);
            chain.doFilter(servletRequest, servletResponse);
            return;
        }

        // 4-1、判断登录状态，如果已登录，则直接放行
        if (servletRequest.getSession().getAttribute("employee") != null) {
            log.info("Employee logged in, id:{}", servletRequest.getSession().getAttribute("employee"));
            Long empId = (Long) servletRequest.getSession().getAttribute("employee");
            BaseContext.setCurrentId(empId);
            chain.doFilter(servletRequest, servletResponse);
            return;
        }

        // 4-2、判断登录状态，如果已登录，则直接放行
        if (servletRequest.getSession().getAttribute("user") != null) {
            log.info("User logged in, id:{}", servletRequest.getSession().getAttribute("user"));
            Long userId = (Long) servletRequest.getSession().getAttribute("user");
            BaseContext.setCurrentId(userId);
            chain.doFilter(servletRequest, servletResponse);
            return;
        }

        // 5、如果未登录则返回未登录结果
        log.info("Request processed {}", requestURI);
        servletResponse.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
        return;
    }

    /**
     * 路径匹配，检查本次请求是否放行
     *
     * @param uris
     * @param requestURI
     * @return
     */
    private boolean check(String[] uris, String requestURI) {
        for (String uri : uris) {
            boolean match = PATH_MATCHER.match(uri, requestURI);
            if (match) {
                return true;
            }
        }
        return false;
    }
}
