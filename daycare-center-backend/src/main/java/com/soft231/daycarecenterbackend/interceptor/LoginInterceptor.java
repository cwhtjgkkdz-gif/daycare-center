package com.soft231.daycarecenterbackend.interceptor;

import com.soft231.daycarecenterbackend.entity.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 登录拦截器 — 检查Session中是否有用户信息
 */
@Component
public class LoginInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // Knife4j文档放行
        String uri = request.getRequestURI();
        if (uri.contains("/doc.html") || uri.contains("/v3/api-docs") || uri.contains("/webjars")) {
            return true;
        }

        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("loginUser") != null) {
            return true;
        }

        // 未登录，返回401
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(401);
        response.getWriter().write("{\"code\":401,\"message\":\"未登录，请先登录\"}");
        return false;
    }
}
