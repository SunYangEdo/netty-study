package com.sunyang.netty.study;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;

/**
 * @Author: sunyang
 * @Date: 2021/8/18
 * @Description:
 */
@Slf4j(topic = "c.Demo")
public class SelectorWriteServer {
    public static void main(String[] args) throws IOException {
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.configureBlocking(false);
        serverSocketChannel.bind(new InetSocketAddress(8080));

        Selector selector = Selector.open();
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

        while (true) {
            selector.select();
            Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
            while (iterator.hasNext()) {
                SelectionKey key = iterator.next();
                iterator.remove();
                if (key.isAcceptable()) {
                    ServerSocketChannel channel = (ServerSocketChannel) key.channel();
                    SocketChannel socketChannel = channel.accept();
                    socketChannel.configureBlocking(false);
                    SelectionKey scSelectionKey = socketChannel.register(selector, 0);
                    StringBuilder builder = new StringBuilder();
                    for (int i = 0; i < 300000000; i++) {
                        builder.append("a");
                    }
                    ByteBuffer buffer = StandardCharsets.UTF_8.encode(builder.toString());
                    // 有的网卡缓冲区比较小的情况会出现不能一次性将数据发送过去的现象，会分多次发送，
                    // 所以为了性能，让缓冲区满了的时候去执行别的操作，等缓冲区有空间时再来执行。
//                    while (buffer.hasRemaining()) {
                    int write = socketChannel.write(buffer);
                    System.out.println(write);
//                    }
                    // 第一次没读完的，等待缓冲区有空间再触发可写事件。再进行读取
                    if (buffer.hasRemaining()) {
                        // 注册可写事件
                        scSelectionKey.interestOps(SelectionKey.OP_READ | SelectionKey.OP_WRITE);
                        // 把未写完的buffer挂在key的附件上。
                        scSelectionKey.attach(buffer);
                    }
                } else if (key.isWritable()) {
                    ByteBuffer byteBuffer = (ByteBuffer) key.attachment();
                    SocketChannel socketChannel = (SocketChannel) key.channel();
                    int write = socketChannel.write(byteBuffer);
                    System.out.println(write);
                    // 清理操作
                    if (!byteBuffer.hasRemaining()) {
                        // 清除掉附件，让垃圾回收回收掉不用的Buffer
                        key.attach(null);
                        // 写完之后清除掉关注的可写事件
                        key.interestOps(key.interestOps() - SelectionKey.OP_WRITE);
                    }
                }
            }

        }

    }
}
