package com.evan.androiddemos.codekk.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Retention(RetentionPolicy.CLASS) // 解析 see MethodInfoProcessor
//@Retention(RetentionPolicy.RUNTIME)

@Target(ElementType.METHOD)
@Inherited
public @interface MethodInfo {
    String author()  default "evan";
    String date();
    int version() default 1;
    String[] arrays();
}


