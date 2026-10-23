package com.soft231.daycarecenterbackend.aspect;

import com.soft231.daycarecenterbackend.entity.User;
import com.soft231.daycarecenterbackend.mapper.OperationLogMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * 操作日志切面 — 环绕通知，记录增删改操作
 */
@Slf4j
@Aspect
@Component
public class OperationLogAspect {

    @Autowired
    private OperationLogMapper operationLogMapper;

    @Around("@annotation(opLog)")
    public Object around(ProceedingJoinPoint joinPoint, OperationLog opLog) throws Throwable {
        long start = System.currentTimeMillis();

        // 执行目标方法
        Object result = joinPoint.proceed();

        long costTime = System.currentTimeMillis() - start;

        try {
            com.soft231.daycarecenterbackend.entity.OperationLog logEntity = new com.soft231.daycarecenterbackend.entity.OperationLog();
            logEntity.setModule(opLog.module());
            logEntity.setOperation(opLog.operation());
            logEntity.setMethod(joinPoint.getSignature().toShortString());
            logEntity.setCostTime(costTime);

            // 获取当前登录用户
            ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attrs != null) {
                HttpServletRequest request = attrs.getRequest();
                HttpSession session = request.getSession(false);
                if (session != null) {
                    User user = (User) session.getAttribute("loginUser");
                    if (user != null) {
                        logEntity.setOperator(user.getUsername());
                    }
                }
                logEntity.setIp(request.getRemoteAddr());
            }

            operationLogMapper.insert(logEntity);
        } catch (Exception e) {
            log.error("记录操作日志失败", e);
        }

        return result;
    }
}
