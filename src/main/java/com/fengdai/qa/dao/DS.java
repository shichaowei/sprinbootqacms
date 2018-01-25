package com.fengdai.qa.dao;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.fengdai.qa.constants.DataSourceConsts;

/**
 * 数据源注解
 * @author Lucian
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface DS {
  String value() default DataSourceConsts.DEFAULT;
}