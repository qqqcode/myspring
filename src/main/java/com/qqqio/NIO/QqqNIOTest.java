package com.qqqio.NIO;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * @author Johnson
 * 2021/8/20
 */
public class QqqNIOTest {


    public static void main(String[] args) {

        try {
            FileInputStream fileInputStream = new FileInputStream("D://abc.txt");
            FileChannel inChannel = fileInputStream.getChannel();


            FileOutputStream fileOutputStream = new FileOutputStream("D://def.txt");
            FileChannel outChannel = fileOutputStream.getChannel();

            ByteBuffer byteBuffer = ByteBuffer.allocateDirect(1024);

            while (true) {
                /* 从输入通道中读取数据到缓冲区中 */
                int r = inChannel.read(byteBuffer);
                /* read() 返回 -1 表示 EOF */
                if (r == -1) {
                    break;
                }
                /* 切换读写 */
                byteBuffer.flip();

                /* 把缓冲区的内容写入输出文件中 */
                outChannel.write(byteBuffer);

                /* 清空缓冲区 */
                byteBuffer.clear();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
