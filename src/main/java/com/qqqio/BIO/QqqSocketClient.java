package com.qqqio.BIO;

import java.util.concurrent.CountDownLatch;

/**
 * @author Johnson
 * 2021/8/20
 */
public class QqqSocketClient {

    public static void main(String[] args) {
        int clientNumber = 20;
        CountDownLatch countDownLatch = new CountDownLatch(clientNumber);

        for (int i = 0; i < clientNumber; i++, countDownLatch.countDown()) {
            QqqSocketClientThread clientThread = new QqqSocketClientThread(countDownLatch, i);
            new Thread(clientThread).start();
        }
    }
}
