package com.qqqio;

import java.io.IOException;

/**
 * @author Johnson
 * 2021/8/20
 */
public abstract class QqqInputStream {

    private static final int SKIP_BUFFER_SIZE = 2048;

    private static byte[] skipBuffer;

    public abstract int read() throws IOException;

    public int read(byte b[]) throws IOException {
        read(b, 0, b.length);
        return 0;
    }

    public int read(byte b[], int off, int len) throws IOException {
        if (b == null) {
            throw new NullPointerException();
        } else if (off < 0 || len < 0 || len > b.length - off) {

        } else if (len == 0) {
            return 0;
        }
        int c = read();

        if (c == -1) {
            return -1;
        }

        b[off] = (byte) c;

        int i = 1;
        try {
            for (; i < len; i++) {
                c = read();
                if (c == -1) {
                    return -1;
                }
                b[off + 1] = (byte) c;
            }
        } catch (Exception e) {

        }
        return i;
    }

    public long skip(long n) throws IOException{
        long remaining = n;
        int nr;
        if (skipBuffer == null)
            //初始化一个跳转的缓存
            skipBuffer = new byte[SKIP_BUFFER_SIZE];
        //本地化的跳转缓存
        byte[] localSkipBuffer = skipBuffer;
        //检查输入参数，应该放在方法的开始
        if (n <= 0) {    return 0;      }
        //一共要跳过n个，每次跳过部分，循环
        while (remaining > 0) {
            nr = read(localSkipBuffer, 0, (int) Math.min(SKIP_BUFFER_SIZE, remaining));
            //利用上面的read(byte[],int,int)方法尽量读取n个字节
            //读到流的末端，则返回
            if (nr < 0) {  break;    }
            //没有完全读到需要的，则继续循环
            remaining -= nr;
        }
        return n - remaining;//返回时要么全部读完，要么因为到达文件末端，读取了部分
    }

    public int available() {
        return 0;
    }

    public void close() {

    }

    public synchronized void mark(int readlimit) {

    }

    public synchronized void reset() {

    }

    public boolean markSupported() {
        return false;
    }
}
