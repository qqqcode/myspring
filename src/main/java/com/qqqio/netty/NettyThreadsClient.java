package com.qqqio.netty;

import com.qqqio.BIO.QqqSocketClientThread;

import java.util.concurrent.CountDownLatch;

/**
 * @author Johnson
 * 2021/8/24
 */
public class NettyThreadsClient {

    public static void main(String[] args) {
        int clientNumber = 20;
        CountDownLatch countDownLatch = new CountDownLatch(clientNumber);

        for (int i = 0; i < clientNumber; i++, countDownLatch.countDown()) {
            NettyRunnableClient clientThread = new NettyRunnableClient(5555, i + 1 + "");
            new Thread(clientThread).start();
        }
    }
}
