package com.qqq;

import com.qqq.service.OrderService;
import com.qqqspring.ApplicationContext;

public class Test {

    public static void main(String[] args){
        ApplicationContext applicationContext = new ApplicationContext(AppConfig.class);
        OrderService order = (OrderService)applicationContext.getBean("orderService");
        order.test();
//        System.out.println(applicationContext.getBean("orderService"));


    }
}
