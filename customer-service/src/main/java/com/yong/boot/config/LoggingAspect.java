package com.yong.boot.config;

import brave.baggage.BaggageField;
import com.yong.boot.util.LogUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.logging.log4j.Logger;
import org.apache.logging.slf4j.SLF4JLogger;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Method;
import java.util.Objects;

import static com.yong.boot.constant.BusinessConstant.BIZ_FUNC;


@Aspect
@Component
public class LoggingAspect {

    @Pointcut("within(@org.springframework.web.bind.annotation.RestController *)")
    public void springBeanPointcut() {
    }

    @Around("springBeanPointcut()")
    public Object aroundDelegate(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();
        Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
        String methodName = method.getName();
        BaggageField.create(BIZ_FUNC).updateValue(methodName);
        HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
        this.logMethodEntry(joinPoint, request);
        Object result;
        try {
            result = joinPoint.proceed();
            long finish = System.currentTimeMillis();
            long timeElapsed = finish - start;
            this.logMethodReturn(joinPoint, timeElapsed, request);
            return result;
        } catch (Exception e) {
            long finish = System.currentTimeMillis();
            long timeElapsed = finish - start;
            this.logMethodThrowException(joinPoint, e, timeElapsed, request);
            throw e;
        } finally {
            LogUtils.cleanSeqNo();
        }
    }

    protected void logMethodEntry(JoinPoint joinPoint, HttpServletRequest request) {
        if (!(joinPoint.getSignature() instanceof MethodSignature))
            return;
        Logger log = logger(joinPoint);
        LogUtils.info(log, "start working API {}", request.getRequestURI());

    }

    protected void logMethodReturn(JoinPoint joinPoint, long timeElapsed, HttpServletRequest request) {
        if (!(joinPoint.getSignature() instanceof MethodSignature))
            return;
        Logger log = logger(joinPoint);
        LogUtils.info(log, "end working API {} success, time {}(ms)", request.getRequestURI(), timeElapsed);
    }

    protected void logMethodThrowException(JoinPoint joinPoint, Throwable e, long timeElapsed, HttpServletRequest request) {
        if (!(joinPoint.getSignature() instanceof MethodSignature))
            return;
        Logger log = logger(joinPoint);
        LogUtils.info(log, "end working API {} exception {}, time {}(ms)", request.getRequestURI(), e.getMessage(), timeElapsed);

    }

    @AfterThrowing(pointcut = "springBeanPointcut()", throwing = "e")
    public void logAfterThrowing(JoinPoint joinPoint, Throwable e) {
        logger(joinPoint)
                .error(
                        "Exception in {}() with cause = '{}' and exception = '{}'",
                        joinPoint.getSignature().getName(),
                        e.getCause() != null ? e.getCause() : "NULL",
                        e.getMessage()
                );
    }

    private org.apache.logging.log4j.Logger logger(JoinPoint joinPoint) {
        String name = joinPoint.getSignature().getDeclaringTypeName();
        return new SLF4JLogger(name, LoggerFactory.getLogger(name));
    }
}