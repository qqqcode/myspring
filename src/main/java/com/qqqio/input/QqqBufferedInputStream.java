package com.qqqio.input;

import com.qqqio.QqqInputStream;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;

/**
 * @author Johnson
 * 2021/8/20
 */
public class QqqBufferedInputStream extends QqqFilterInputStream {

    private static final
    AtomicReferenceFieldUpdater<QqqBufferedInputStream, byte[]> bufUpdater =
            AtomicReferenceFieldUpdater.newUpdater(QqqBufferedInputStream.class, byte[].class, "buf");
    private static int defaultBufferSize = 8192;    //默认缓存的大小
    protected volatile byte buf[];  //内部的缓存
    protected int count;     //buffer的大小
    protected int pos;      //buffer中cursor的位置
    protected int markpos = -1;    //mark的位置
    protected int marklimit;     //mark的范围

    public QqqBufferedInputStream(QqqInputStream in) {
        super(in);
    }

    private QqqInputStream getInIfOpen() throws IOException {
        QqqInputStream input = in;
        if (input == null) throw new IOException("Stream closed");
        return input;
    }

    //检查buffer的状态，同时返回缓存
    private byte[] getBufIfOpen() throws IOException {
        byte[] buffer = buf;
        //不太可能发生的状态
        if (buffer == null) throw new IOException("Stream closed");
        return buffer;
    }

    private void fill() throws IOException {
        //得到buffer
        byte[] buffer = getBufIfOpen();
        if (markpos < 0)
            //mark位置小于0，此时pos为0
            pos = 0;
            //pos大于buffer的长度
        else if (pos >= buffer.length)
            if (markpos > 0) {
                int sz = pos - markpos;
                System.arraycopy(buffer, markpos, buffer, 0, sz);
                pos = sz;
                markpos = 0;
            } else if (buffer.length >= marklimit) {
                //buffer的长度大于marklimit时，mark失效
                markpos = -1;
                //丢弃buffer中的内容
                pos = 0;
            } else {
                //buffer的长度小于marklimit时对buffer扩容
                int nsz = pos * 2;
                if (nsz > marklimit)
                    nsz = marklimit;//扩容为原来的2倍，太大则为marklimit大小
                byte nbuf[] = new byte[nsz];
                //将buffer中的字节拷贝如扩容后的buf中
                System.arraycopy(buffer, 0, nbuf, 0, pos);
                if (!bufUpdater.compareAndSet(this, buffer, nbuf)) {
                    //在buffer在被操作时，不能取代此buffer
                    throw new IOException("Stream closed");
                }
                //将新buf赋值给buffer
                buffer = nbuf;
            }
        count = pos;
        int n = getInIfOpen().read(buffer, pos, buffer.length - pos);
        if (n > 0) count = n + pos;
    }

    //读取下一个字节
    public synchronized int read() throws IOException {
        //到达buffer的末端
        if (pos >= count) {
            //就从流中读取数据，填充buffer
            fill();
            //读过一次，没有数据则返回-1
            if (pos >= count) return -1;
        }
        //返回buffer中下一个位置的字节
        return getBufIfOpen()[pos++] & 0xff;
    }

    private int read1(byte[] b, int off, int len) throws IOException {
        int avail = count - pos; //buffer中还剩的可读字符
        //buffer中没有可以读取的数据时
        if (avail <= 0) {
            //将输入流中的字节读入b中
            if (len >= getBufIfOpen().length && markpos < 0) {
                return getInIfOpen().read(b, off, len);
            }
            fill();//填充
            avail = count - pos;
            if (avail <= 0) return -1;
        }
        //从流中读取后，检查可以读取的数目
        int cnt = (avail < len) ? avail : len;
        //将当前buffer中的字节放入b的末端
        System.arraycopy(getBufIfOpen(), pos, b, off, cnt);
        pos += cnt;
        return cnt;
    }

    public synchronized int read(byte b[], int off, int len) throws IOException {
        getBufIfOpen();
        // 检查buffer是否open
        //检查输入参数是否正确
        if ((off | len | (off + len) | (b.length - (off + len))) < 0) {
            throw new IndexOutOfBoundsException();
        } else if (len == 0) {
            return 0;
        }
        int n = 0;
        for (; ; ) {
            int nread = read1(b, off + n, len - n);
            if (nread <= 0) return (n == 0) ? nread : n;
            n += nread;
            if (n >= len) return n;
            QqqInputStream input = in;
            if (input != null && input.available() <= 0) return n;
        }
    }

}
