package com.mincai.minispring;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 标记为 Spring 容器的组件
 * 使用此注解的类会被加载到 Spring 容器中
 *
 * @author limincai
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Component {
    /**
     * bean 名，如果没有设置，默认为当前类名
     */
    String name() default "";
}
