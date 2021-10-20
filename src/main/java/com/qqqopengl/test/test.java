package com.qqqopengl.test;

import java.net.URL;

public class test {

    public static void main(String[] args) {

        URL resource = Thread.currentThread().getContextClassLoader().getResource("Lowpoly_tree_sample.obj");
        System.out.println(resource.getPath());



        int a = 3 ;
        if((a & 1) == 1){
            System.out.println("mes");
        }
        if ((a & 2) == 2) {
            System.out.println("email");
        }
        if ((a&4) ==4) {
            System.out.println("wx");
        }
        if ((a&8) ==8) {
            System.out.println("other");
        }

    }
}
