package com.qqq.service;

import com.qqqspring.AutoWired;
import com.qqqspring.Component;
import com.qqqspring.Scope;

@Component("orderService")
@Scope("prototype")
public class OrderService {

    @AutoWired
    private UserService userService;

    public void test(){
        System.out.println(userService);
    }
}
