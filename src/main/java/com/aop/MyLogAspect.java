package com.aop;

import java.lang.reflect.Method;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import com.annotation.MyLog;

@Aspect
@Component
public class MyLogAspect {
	
	@Pointcut("@annotation(com.annotation.MyLog)")
	public void pointCut() {
		
	}
	
	@Before("pointCut()")
	public void before(JoinPoint point) {
		MethodSignature sign =  (MethodSignature)point.getSignature();
	    Method method = sign.getMethod();
	    MyLog myLog = method.getAnnotation(MyLog.class);
		System.out.println("before " + sign.getDeclaringTypeName() + ":" + myLog.value());
	}
	
	@After("pointCut()")
	public void after(JoinPoint point) {
		MethodSignature sign =  (MethodSignature)point.getSignature();
	    Method method = sign.getMethod();
	    MyLog myLog = method.getAnnotation(MyLog.class);
		System.out.println("after " + sign.getDeclaringTypeName() + ":" + myLog.value());
	}
	
	
	@AfterReturning(returning = "ret" , pointcut = "pointCut()")
	public void afterReturing(Object ret) {
		System.out.println("afterReturing:" + ret);
	}
	
	@AfterThrowing(throwing = "e", pointcut = "pointCut()")
	public void afterThrowing(Exception e) {
		System.out.println("afterThrowing:" + e.getMessage());
	}

}
