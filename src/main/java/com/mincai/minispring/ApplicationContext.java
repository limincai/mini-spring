package com.mincai.minispring;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.net.URL;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 应用上下文
 *
 * @author limincai
 */
public class ApplicationContext {

    /**
     * ioc 容器
     */
    Map<String, Object> ioc = new HashMap<>();

    private Map<String, BeanDefinition> beanDefinitionMap = new HashMap();

    /**
     * @param packageName 需要扫描的包名
     */
    public ApplicationContext(String packageName) throws Exception {
        // 初始化容器
        initContext(packageName);
    }

    /**
     * 初始化上下文
     */
    public void initContext(String packageName) throws Exception {
        // 创建 Bean
        scanPackage(packageName)
                .stream()
                .filter(this::scanCreate)
                .map(this::wrapper).forEach(this::creatBean);
    }

    /**
     * type 是否拥有 Component 注解
     *
     * @param type Class 对象
     */
    protected boolean scanCreate(Class<?> type) {
        return type.isAnnotationPresent(Component.class);
    }

    /**
     * 创建 Bean 对象
     */
    protected void creatBean(BeanDefinition beanDefinition) {
        String name = beanDefinition.getName();
        // 如果 ioc 容器中有这个 bean 直接返回
        if (ioc.containsKey(name)) {
            return;
        }
        // 创建 Bean
        doCreateBean(beanDefinition);
    }

    /**
     * 创建 Bean
     */
    private void doCreateBean(BeanDefinition beanDefinition) {
        Constructor<?> constructor = beanDefinition.getConstructor();
        Object bean;
        try {
            bean = constructor.newInstance();
            // 调用注入了 PostConstruct 的函数
            Method postConstructMethod = beanDefinition.getPostConstructMethod();
            if (postConstructMethod != null) {
                postConstructMethod.invoke(bean);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        // 放到 ioc 容器中
        ioc.put(beanDefinition.getName(), bean);
    }

    /**
     * 将 Class 对象包装为 Bean
     *
     * @return 封装好的 Bean
     */
    protected BeanDefinition wrapper(Class<?> type) {
        BeanDefinition beanDefinition = new BeanDefinition(type);
        // 如果 bean 名字相同抛出异常
        if (beanDefinitionMap.containsKey(beanDefinition.getName())) {
            throw new RuntimeException("bean 名重复");
        }
        beanDefinitionMap.put(beanDefinition.getName(), beanDefinition);
        return beanDefinition;
    }

    /**
     * 扫描包获取包中的所有类的 Class 对象
     *
     * @param packageName 包名
     * @return 包下的所有类的 Class 对象
     */
    private List<Class<?>> scanPackage(String packageName) throws IOException {
        List<Class<?>> classList = new ArrayList<>();
        // 获取 packageName 下的资源
        URL resource =
                this.getClass()
                        .getClassLoader()
                        .getResource(packageName.replace(".", File.separator));
        Path path = Paths.get(resource.getFile());
        // 递归遍历所有文件
        Files.walkFileTree(path, new SimpleFileVisitor() {
            @Override
            public FileVisitResult visitFile(Object file, BasicFileAttributes attrs) throws IOException {
                // 文件的绝对路径
                Path absolutePath = ((Path) file).toAbsolutePath();
                // 如果文件是 clas 文件转为 Class 对象
                if (absolutePath.toString().endsWith(".class")) {
                    // 获取类名
                    String replaceStr = absolutePath.toString().replace(File.separator, ".");
                    int packAgeIndex = replaceStr.indexOf(packageName);
                    String className = replaceStr.substring(packAgeIndex, replaceStr.length() - ".class".length());
                    try {
                        classList.add(Class.forName(className));
                    } catch (ClassNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                }
                return FileVisitResult.CONTINUE;
            }
        });
        return classList;
    }

    /**
     * 通过 Bean 对象名称获取 Bean 对象
     *
     * @param name Bean 对象名称
     * @return 所需的 Bean 对象
     */
    public Object getBean(String name) {
        return this.ioc.get(name);
    }

    /**
     * 通过 Bean 对象类型获取 Bean 对象
     * 可以获取实现或继承此类的 Bean 对象
     *
     * @param beanType Bean Class 对象
     * @return 所需的 Bean 对象
     */
    public <T> T getBean(Class<T> beanType) {
        return this.ioc.values().stream().filter(bean -> beanType.isAssignableFrom(bean.getClass())).map(bean -> (T) bean).findAny().orElse(null);
    }

    /**
     * 通过 Bean 对象类型获取 Bean 对象列表
     * 可以获取实现或继承此类的 Bean 对象
     *
     * @param beanType Bean Class 对象
     * @return 所需的 Bean 对象列表
     */
    public <T> List<T> getBeans(Class<T> beanType) {
        return this.ioc.values().stream().filter(bean -> beanType.isAssignableFrom(bean.getClass())).map(bean -> (T) bean).collect(Collectors.toList());
    }
}
