package com.qqq.service;

import com.qqqspring.*;
import com.qqqspring.beans.annotation.AutoWired;
import com.qqqspring.context.annotation.Scope;

@Component("orderService")
@Scope("prototype")
public class OrderService implements InitializingBean , BeanNameAware {

    @AutoWired
    private UserService userService;

    private String beanName;

    public void test(){
        System.out.println(userService);
    }

    public void afterPropertiesSet() {
        System.out.println("Init Order");
    }

    public void setBeanName(String beanName) {
        this.beanName = beanName;
    }
}
