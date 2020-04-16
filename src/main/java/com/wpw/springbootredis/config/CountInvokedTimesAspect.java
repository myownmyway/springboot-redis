package com.wpw.springbootredis.config;

import com.wpw.springbootredis.util.RedisUtil;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;

/**
 * @author wpw
 */
@Aspect
@Component
public class CountInvokedTimesAspect {
    private final RedisUtil redisUtil;

    public CountInvokedTimesAspect(RedisUtil redisUtil) {
        this.redisUtil = redisUtil;
    }

    @Pointcut("@annotation(com.wpw.springbootredis.config.CountInvokeTimes)")
    public void countInvokeTimes() {
    }

    @Around(value = "countInvokeTimes()")
    public Object doAround(ProceedingJoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        Class<?>[] argTypes = new Class[args.length];
        for (int i = 0, length = args.length; i < length; i++) {
            argTypes[i] = args[i].getClass();
        }
        try {
            String methodName = joinPoint.getSignature().getName();
            Method method = joinPoint.getTarget().getClass().getMethod(methodName, argTypes);
            boolean isAnnotationPresent = method.isAnnotationPresent(CountInvokeTimes.class);
            if (isAnnotationPresent) {
                CountInvokeTimes methodAnnotation = method.getAnnotation(CountInvokeTimes.class);
                String value = methodAnnotation.value();
                if (StringUtils.isEmpty(value)) {
                    value = methodName;
                }
                if (redisUtil.get(value) == null) {
                    redisUtil.set(value, 1);
                } else {
                    Integer countTimes = (Integer) redisUtil.get(value);
                    countTimes += 1;
                    redisUtil.set(value, countTimes);
                }
            }
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        Object object = null;
        try {
            object = joinPoint.proceed();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        return object;
    }
}
