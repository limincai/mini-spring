package com.mincai.minispring;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * Bean 定义类
 *
 * @author limincai
 */
public class BeanDefinition {

    private final String name;

    private final Constructor<?> constructor;

    private final Method postConstructMethod;

    public BeanDefinition(Class<?> type) {
        Component component = type.getDeclaredAnnotation(Component.class);
        this.name = component.name().isEmpty() ? type.getSimpleName() : component.name();
        try {
            this.constructor = type.getConstructor();
            // 获取添加了 PostConstruct 注解的方法
            this.postConstructMethod = Arrays.stream(type.getDeclaredMethods()).filter(method -> method.isAnnotationPresent(PostConstruct.class)).findFirst().orElse(null);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    public String getName() {
        return name;
    }

    public Constructor<?> getConstructor() {
        return constructor;
    }

    public Method getPostConstructMethod() {
        return postConstructMethod;
    }
}
