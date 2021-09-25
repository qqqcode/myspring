package com.qqqthread;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ThreadTest {

    static  ExecutorService executorService = Executors.newSingleThreadExecutor();
    public static void main(String[] args) {
        Object lock1 = new Object();
        Object lock2 = new Object();

        new Thread(()->{
            synchronized (lock1) {
                System.out.println("get lock1");
                try {
                    TimeUnit.SECONDS.sleep(3);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                synchronized (lock2){
                    System.out.println(Thread.currentThread().getName());
                }
            }
        }).start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                synchronized (lock2) {
                    System.out.println("get lock1");
                    try {
                        TimeUnit.SECONDS.sleep(3);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    synchronized (lock1){
                        System.out.println(Thread.currentThread().getName());
                    }
                }
            }
        }).start();
    }

}
