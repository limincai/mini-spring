package com.mincai.minispring;

import com.mincai.minispring.sub.Cat;

/**
 * @author limincai
 */
public class Main {

    public static void main(String[] args) throws Exception {
        ApplicationContext ioc = new ApplicationContext("com.mincai.minispring");
        Object cat1 = ioc.getBean("Cat");
        Cat cat2 = ioc.getBean(Cat.class);
        Object mydog = ioc.getBean("mydog");
        System.out.println(cat1 == cat2);
        System.out.println(mydog);
    }
}
