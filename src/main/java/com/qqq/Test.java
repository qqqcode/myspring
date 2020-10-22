package com.qqq;

import com.qqq.service.OrderService;
import com.qqqspring.MyApplicationContext;

public class Test {

    public static void main(String[] args){
        MyApplicationContext applicationContext = new MyApplicationContext(AppConfig.class);
        OrderService order = (OrderService)applicationContext.getBean("orderService");
        order.test();

    }
}
