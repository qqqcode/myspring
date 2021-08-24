package com.qqqio.BIO;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URLDecoder;
import java.util.concurrent.CountDownLatch;

/**
 * @author Johnson
 * 2021/8/20
 */
public class QqqSocketClientThread implements Runnable {
    private CountDownLatch countDownLatch;
    private Integer threadIndex;

    public QqqSocketClientThread(CountDownLatch countDownLatch, Integer threadIndex) {
        this.countDownLatch = countDownLatch;
        this.threadIndex = threadIndex;
    }

    @Override
    public void run() {
        Socket socket = null;
        InputStream inputStream = null;
        OutputStream outputStream = null;
        try {
            socket = new Socket("localhost", 5555);
            inputStream = socket.getInputStream();
            outputStream = socket.getOutputStream();

            this.countDownLatch.await();
            outputStream.write(("这是第" + this.threadIndex + " 个客户端的请求。over").getBytes());
            outputStream.flush();

            //在这里等待，直到服务器返回信息
            System.out.println("第" + this.threadIndex + "个客户端的请求发送完成，等待服务器返回信息");
            int maxLen = 1024;
            byte[] contextBytes = new byte[maxLen];
            int realLen;
            String message = "";
            //程序执行到这里，会一直等待服务器返回信息(注意，前提是in和out都不能close，如果close了就收不到服务器的反馈了)
            while ((realLen = inputStream.read(contextBytes, 0, maxLen)) != -1) {
                String messageEncode = new String(contextBytes , "UTF-8");
                String message1 = URLDecoder.decode(messageEncode, "UTF-8");
                message += message1;
            }
            System.out.println("收到服务信息:" + message);
        } catch (Exception e) {

        } finally {
            try {
                inputStream.close();
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
