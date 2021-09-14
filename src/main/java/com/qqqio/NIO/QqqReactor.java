package com.qqqio.NIO;

import com.sun.org.apache.bcel.internal.generic.Select;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;

/**
 * @author Johnson
 * 2021/9/13
 */
public class QqqReactor implements Runnable {

    private final Selector selector;

    private final ServerSocketChannel serverSocketChannel;

    public QqqReactor(int port) throws IOException {
        serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.configureBlocking(false);
        selector = Selector.open();
        SelectionKey key = serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        serverSocketChannel.bind(new InetSocketAddress(9898));
    }

    @Override
    public void run() {

    }
}
