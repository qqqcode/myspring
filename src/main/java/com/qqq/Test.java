package com.qqq;

import com.qqq.service.OrderService;
import com.qqqspring.QqqApplicationContext;

public class Test {

    public static void main(String[] args){
        QqqApplicationContext applicationContext = new QqqApplicationContext(AppConfig.class);
        OrderService order = (OrderService)applicationContext.getBean("orderService");
        order.test();
//        System.out.println(applicationContext.getBean("orderService"));


    }
}
