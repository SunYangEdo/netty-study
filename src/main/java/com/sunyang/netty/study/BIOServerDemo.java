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
public class BIOServerDemo {
    /**
     * 使用NIO来理解阻塞模式 单线程
     * **/
    public static void main(String[] args) throws IOException {
        // 创建一个Socket服务器
        ServerSocketChannel serverChannel = ServerSocketChannel.open();
        // 绑定端口 用来客户端请求的端口
        serverChannel.bind(new InetSocketAddress(8080));
        // 创建一个Buffer缓冲区用来存储客户端发来的数据（从Channel中读取的数据）
        ByteBuffer buffer = ByteBuffer.allocate(16);
        List<SocketChannel> channelList = new ArrayList<>();
        while (true) {
            // accept 建立与客户连接  SocketChannel用来和客户端之间通信传输数据
            log.debug("等待新的连接.....");
            SocketChannel channel = serverChannel.accept(); // 阻塞方法，线程停止运行
            channelList.add(channel);
            log.debug("接受连接---> {}", channel);
            for (SocketChannel socketChannel : channelList) {
                log.debug("等待接受数据......{}", socketChannel);
                // 从channel中读取数据写入到buffer中
                socketChannel.read(buffer); // 阻塞方法，线程停止运行
                log.debug("接收到的数据为下面.....{}", socketChannel);
                // 切换成读模式
                buffer.flip();
                // 打印读取到的数据
                debugRead(buffer);
                // 切换成写模式 等待下一次写入
                buffer.clear();
            }
        }

    }
}
