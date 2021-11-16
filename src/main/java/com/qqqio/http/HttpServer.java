package com.qqqio.http;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Iterator;

public class HttpServer {

    public static void main(String[] args) throws IOException {

        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();

        serverSocketChannel.bind(new InetSocketAddress(8888));

        serverSocketChannel.configureBlocking(false);

        Selector selector = Selector.open();

        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

        while (true) {
            if (selector.select(3000) == 0) {
                continue;
            }

            Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();

            while (iterator.hasNext()) {
                SelectionKey next = iterator.next();
                new Thread(new HttpHandle(next)).start();
                iterator.remove();
            }

        }
    }

    private static class HttpHandle implements Runnable {

        private int bufferSize = 1024;

        private String localCharset = "UTF-8";

        private SelectionKey selectionKey;

        private HttpHandle(SelectionKey key) {
            this.selectionKey = key;
        }

        public void handleAccept() throws IOException {
            SocketChannel accept = ((ServerSocketChannel) selectionKey.channel()).accept();
            accept.configureBlocking(false);
            accept.register(selectionKey.selector(),SelectionKey.OP_READ, ByteBuffer.allocate(bufferSize));
        }

        public void handleRead() throws IOException {

            SocketChannel channel = (SocketChannel) selectionKey.channel();
            ByteBuffer byteBuffer = (ByteBuffer)selectionKey.attachment();
            byteBuffer.clear();

            if (channel.read(byteBuffer) == -1) {
                channel.close();
            } else {
                byteBuffer.flip();
                String s = Charset.forName(localCharset).newDecoder().decode(byteBuffer).toString();
                String[] split = s.split("\r\n");
                for (String s1 : split) {
                    System.out.println(s1);
                    if (s.isEmpty()) {
                        break;
                    }
                }

                String[] firstLine = split[0].split("  ");
                System.out.println();
                System.out.println("Method"+firstLine[0]);
                System.out.println("url:"+firstLine[1]);
                System.out.println("HTTP Version:\t"+firstLine[2]);
                System.out.println();

                StringBuilder sendBuilder = new StringBuilder();
                sendBuilder.append("HTTP/1.1 200 OK\r\n");
                sendBuilder.append("Content-Type:text/html;charset="+localCharset+"\r\n");
                sendBuilder.append("\r\n");

                sendBuilder.append("<html><head><title>显示报文</title></head><body>");
                sendBuilder.append("接受到的报文：<br/>");
                for (String s1 : split) {
                    sendBuilder.append(s1+"<br/>");
                }

                sendBuilder.append("</body></html>");

                byteBuffer = ByteBuffer.wrap(sendBuilder.toString().getBytes(localCharset));

                channel.write(byteBuffer);
                channel.close();
            }
        }

        @Override
        public void run() {
            try {
                if (selectionKey.isAcceptable()) {
                    handleAccept();
                }

                if (selectionKey.isReadable()) {
                    handleRead();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
