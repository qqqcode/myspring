package com.qqqio.input;

import com.qqqio.QqqInputStream;

import java.io.IOException;

/**
 * @author Johnson
 * 2021/8/20
 */
public class QqqFilterInputStream extends QqqInputStream {

    protected volatile QqqInputStream in;

    public QqqFilterInputStream(QqqInputStream in) {
        this.in = in;
    }

    @Override
    public int read() throws IOException {
        return in.read();
    }

    @Override
    public int read(byte[] b) throws IOException {
        return in.read(b);
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        return in.read(b, off, len);
    }

    @Override
    public long skip(long n) throws IOException {
        return in.skip(n);
    }

    @Override
    public int available() {
        return in.available();
    }

    @Override
    public void close() {
        in.close();
    }

    @Override
    public synchronized void mark(int readlimit) {
        in.mark(readlimit);
    }

    @Override
    public synchronized void reset() {
        in.reset();
    }

    @Override
    public boolean markSupported() {
        return in.markSupported();
    }
}
