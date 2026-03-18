package com.example.j2n.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class LoggerAspect {

    @Around("within(com.example.j2n..*) && @annotation(logAroundAnnotation)")
    public Object logAround(ProceedingJoinPoint joinPoint, LogAround logAroundAnnotation) throws Throwable {
        String message = getMessage(joinPoint, logAroundAnnotation);
        log.info("===== Start: {} =====", message);
        try {
            Object returning = joinPoint.proceed();
            log.info("===== End with success: {} =====", message);
            return returning;
        } catch (Throwable e) {
            log.error("===== End with failure: {} =====", message);
            throw e;
        }
    }

    private String getMessage(JoinPoint joinPoint, LogAround logAroundAnnotation) {
        String message = logAroundAnnotation.message();
        if (message == null || message.isEmpty()) {
            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            message = signature.getMethod().getName();
        }
        return message;
    }
}
