package com.sunyang.netty.study;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.List;

import static com.sunyang.netty.study.ByteBufferUtil.debugRead;

/**
 * @Author: sunyang
 * @Date: 2021/8/17
 * @Description:
 */
@Slf4j(topic = "c.Demo")
public class NIOServerDemo {
    public static void main(String[] args) throws IOException {
        // 创建一个Socket服务器
        ServerSocketChannel serverChannel = ServerSocketChannel.open();
        serverChannel.configureBlocking(false); // 设置Socket服务器为非阻塞，这个控制的就是accept()方法为非阻塞
        // 绑定端口 用来客户端请求的端口
        serverChannel.bind(new InetSocketAddress(8080));
        // 创建一个Buffer缓冲区用来存储客户端发来的数据（从Channel中读取的数据）
        ByteBuffer buffer = ByteBuffer.allocate(16);
        List<SocketChannel> channelList = new ArrayList<>();
        while (true) {
            // accept 建立与客户连接  SocketChannel用来和客户端之间通信传输数据
            SocketChannel channel = serverChannel.accept(); // 变为非阻塞，没有连接时返回null，然后继续向下运行。
            if (channel != null) {
                log.debug("接受连接---> {}", channel);
                channel.configureBlocking(false); // 设置socketChanel为非阻塞，也就是socketChannel.read(buffer); 为非阻塞。
                channelList.add(channel);
            }
            for (SocketChannel socketChannel : channelList) {
                // 从channel中读取数据写入到buffer中
                int read = socketChannel.read(buffer); // 变为非阻塞，没有数据时返回0。继续向下运行
                if(read > 0) {
                    log.debug("接收到的数据为下面.....{}", socketChannel);
                    // 切换成读模式
                    buffer.flip();
                    // 打印读取到的数据
                    debugRead(buffer);
                    // 切换成写模式 等待下一次写入
                    buffer.clear();
                    log.debug("读取数据结束....{}", socketChannel);
                }
            }
        }

    }
}
