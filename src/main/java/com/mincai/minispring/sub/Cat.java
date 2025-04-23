package com.mincai.minispring.sub;

import com.mincai.minispring.AutoWired;
import com.mincai.minispring.Component;
import com.mincai.minispring.PostConstruct;

/**
 * 我是一只猫
 * 测试类
 *
 * @author limincai
 */
@Component
public class Cat {

    @AutoWired
    private Dog dog;

    @PostConstruct
    public void init() {
        System.out.println("cat 喵喵" + dog);
    }
}
