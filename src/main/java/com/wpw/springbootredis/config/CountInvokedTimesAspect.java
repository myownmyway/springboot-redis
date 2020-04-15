package com.wpw.springbootredis.config;

import com.wpw.springbootredis.util.RedisUtil;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.concurrent.atomic.AtomicInteger;

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
                if (redisUtil.get(methodName) == null) {
                    redisUtil.set(methodName, 1);
                } else {
                    AtomicInteger countTimes = (AtomicInteger) redisUtil.get(methodName);
                    redisUtil.set(methodName, countTimes.incrementAndGet());
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
