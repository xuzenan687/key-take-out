package com.sky.aspect;

import com.alibaba.fastjson.JSONPatch;
import com.sky.annotation.AutoFill;
import com.sky.constant.AutoFillConstant;
import com.sky.context.BaseContext;
import com.sky.enumeration.OperationType;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.time.LocalDateTime;

import static org.apache.ibatis.ognl.OgnlRuntime.setFieldValue;

@Aspect
@Component
@Slf4j
public class AutoFillAspect {
    @Pointcut("execution(* com.sky.mapper.*.*(..)) && @annotation(com.sky.annotation.AutoFill)")
    public void atuoFillPointCut(){}
    @Before("atuoFillPointCut()")
    public  void autoFill(JoinPoint joinPoint) {
        MethodSignature signature=(MethodSignature)joinPoint.getSignature();//获得方法签名
        AutoFill autoFill=signature.getMethod().getAnnotation(AutoFill.class);//获得当前被拦截的方法的类型
        OperationType operationType=autoFill.value();//获得数据库的操作类型

        Object[] args=joinPoint.getArgs();//获得方法参数
        if(args==null||args.length==0){
            return;
        }
        Object object=args[0];
        if(operationType== OperationType.INSERT){
            log.info("开始进行插入操作的公共字段填充...");
            try {
                Method setUpdateTime = object.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class);
                Method setUpdateUser = object.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_USER, Long.class);
                Method setCreateTime = object.getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_TIME, LocalDateTime.class);
                Method setCreateUser = object.getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_USER, Long.class);
                //通过反射赋值
                setUpdateTime.invoke(object, LocalDateTime.now());
                setUpdateUser.invoke(object, BaseContext.getCurrentId());
                setCreateTime.invoke(object, LocalDateTime.now());
                setCreateUser.invoke(object, BaseContext.getCurrentId());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            log.info("完成插入操作的公共字段填充...");
        }else if(operationType== OperationType.UPDATE){
            log.info("开始进行更新操作的公共字段填充...");
            try {
                Method setUpdateTime = object.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class);
                Method setUpdateUser = object.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_USER, Long.class);
                //通过反射赋值
                setUpdateTime.invoke(object, LocalDateTime.now());
                setUpdateUser.invoke(object, BaseContext.getCurrentId());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            log.info("完成更新操作的公共字段填充...");
        }


    }
}
