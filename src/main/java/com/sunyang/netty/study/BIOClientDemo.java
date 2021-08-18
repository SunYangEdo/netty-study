package com.sunyang.netty.study;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;

/**
 * @Author: sunyang
 * @Date: 2021/8/17
 * @Description:
 */
public class BIOClientDemo {
    public static void main(String[] args) throws IOException {
        SocketChannel channel = SocketChannel.open();
        channel.connect(new InetSocketAddress("localhost", 8080));
        // 这一步用debug模式实现  来直观感受阻塞模式
        channel.write(StandardCharsets.UTF_8.encode("0123456789123456hello\nworld\n"));
        System.in.read();
//        channel.close();
//        System.out.println("waiting....");
    }
}
