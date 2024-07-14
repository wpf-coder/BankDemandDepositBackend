package com.dcits.project.aop;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

//权限限制主要由注释承当，而核心是其中的注释等级。
@Target({ElementType.METHOD,ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Authority {
    Role role() default Role.CLIENT;
}
