package com.qqqio.NIO;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author Johnson
 * 2021/8/23
 */

public class QqqSocketServer {

    private static final ConcurrentMap<Integer, StringBuffer> MESSAGEHASHCONTEXT = new ConcurrentHashMap<Integer, StringBuffer>();

    public static void main(String[] args) throws IOException {
        ServerSocketChannel channel = ServerSocketChannel.open();
        channel.configureBlocking(false);
        ServerSocket socket = channel.socket();
        socket.setReuseAddress(true);
        socket.bind(new InetSocketAddress(5555));

        Selector selector = Selector.open();
        channel.register(selector, SelectionKey.OP_ACCEPT);

        try {
            while (true) {
                if (selector.select(100) == 0) {
                    continue;
                }


                Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();

                while (iterator.hasNext()) {
                    SelectionKey selectionKey = iterator.next();
                    iterator.remove();
                    SelectableChannel selectableChannel = selectionKey.channel();
                    if (selectionKey.isValid() && selectionKey.isAcceptable()) {
                        System.out.println("======channel通道已经准备好=======");
                        SocketChannel accept = ((ServerSocketChannel) selectableChannel).accept();
                        accept.configureBlocking(false);
                        accept.register(selector, SelectionKey.OP_READ, ByteBuffer.allocate(2048));
                    } else if (selectionKey.isValid() && selectionKey.isConnectable()) {
                        System.out.println("======socket channel 建立连接=======");
                    } else if (selectionKey.isValid() && selectionKey.isReadable()) {
                        System.out.println("======socket channel 数据准备完成，可以去读==读取=======");
                        SocketChannel clientSocketChannel = (SocketChannel) selectionKey.channel();
                        InetSocketAddress clientAddress = (InetSocketAddress) clientSocketChannel.getRemoteAddress();
                        int port = clientAddress.getPort();

                        ByteBuffer buffer = (ByteBuffer) selectionKey.attachment();
                        int read = 0;

                        StringBuffer message = new StringBuffer();
                        while ((read = clientSocketChannel.read(buffer)) != 0) {
                            buffer.flip();
                            int position = buffer.position();
                            int capacity = buffer.capacity();
                            byte[] messageBytes = new byte[capacity];
                            buffer.get(messageBytes, position, read);

                            String messageEncode = new String(messageBytes, 0, read, "UTF-8");
                            message.append(messageEncode);

                            //再切换成“写”模式，直接清除缓存的方式，最快捷
                            buffer.clear();
                        }
                        if (URLDecoder.decode(message.toString(), "UTF-8").indexOf("over") != -1) {
                            //则从messageHashContext中，取出之前已经收到的信息，组合成完整的信息
                            Integer channelUUID = clientSocketChannel.hashCode();
                            System.out.println("端口:" + port + "客户端发来的信息======message : " + message);
                            StringBuffer completeMessage;
                            //清空MESSAGEHASHCONTEXT中的历史记录
                            StringBuffer historyMessage = MESSAGEHASHCONTEXT.remove(channelUUID);
                            if (historyMessage == null) {
                                completeMessage = new StringBuffer(message);
                            } else {
                                completeMessage = historyMessage.append(message);
                            }
                            System.out.println("端口:" + port + "客户端发来的完整信息======completeMessage : " + URLDecoder.decode(completeMessage.toString(), "UTF-8"));

                            //======================================================
                            //          当然接受完成后，可以在这里正式处理业务了
                            //======================================================

                            //回发数据，并关闭channel
                            ByteBuffer sendBuffer = ByteBuffer.wrap(URLEncoder.encode("回发处理结果", "UTF-8").getBytes());
                            clientSocketChannel.write(sendBuffer);
                            clientSocketChannel.close();
                        } else {
                            //如果没有发现有“over”关键字，说明还没有接受完，则将本次接受到的信息存入messageHashContext
                            System.out.println("端口:" + port + "客户端信息还未接受完，继续接受======message : " + URLDecoder.decode(message.toString(), "UTF-8"));
                            //每一个channel对象都是独立的，所以可以使用对象的hash值，作为唯一标示
                            Integer channelUUID = clientSocketChannel.hashCode();

                            //然后获取这个channel下以前已经达到的message信息
                            StringBuffer historyMessage = MESSAGEHASHCONTEXT.get(channelUUID);
                            if (historyMessage == null) {
                                historyMessage = new StringBuffer();
                                MESSAGEHASHCONTEXT.put(channelUUID, historyMessage.append(message));
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            socket.close();
        }

    }
}
