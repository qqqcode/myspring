package com.qqq.service;

import com.qqqspring.AutoWired;
import com.qqqspring.Component;

@Component("orderService")
public class OrderService {

    @AutoWired
    private UserService userService;

    public void test(){
        System.out.println(userService);
    }
}
