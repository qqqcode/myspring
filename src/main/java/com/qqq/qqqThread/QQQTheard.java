package com.qqq.qqqThread;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.LockSupport;

/**
 * @author Johnson
 * 2021/7/29
 */
public class QQQTheard {
    public static void main(String[] args) {

        QQQRunnable qqqRunnable = new QQQRunnable();
        Thread thread = new Thread(qqqRunnable);
        while (true) {
            ExecutorService executorService = Executors.newCachedThreadPool();
        }
    }
}
