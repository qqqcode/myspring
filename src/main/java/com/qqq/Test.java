package com.qqq;


import java.util.HashMap;
import java.util.Map;

public class Test {

    public static void main(String[] args){
//        ApplicationContext applicationContext = new ApplicationContext(AppConfig.class);
//        OrderService order = (OrderService)applicationContext.getBean("orderService");
//        order.test();
//        System.out.println(applicationContext.getBean("orderService"));
        Map<String,String> stringStringMap = new HashMap<>();

        stringStringMap.put(null,"dsads");

        System.out.println(stringStringMap.get(null));


    }
}
