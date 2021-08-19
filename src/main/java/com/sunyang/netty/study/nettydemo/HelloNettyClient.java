package com.sunyang.netty.study.nettydemo;

import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringEncoder;

import java.net.InetSocketAddress;

/**
 * @program: netty-study
 * @description: Netty客户端
 * @author: SunYang
 * @create: 2021-08-19 21:02
 **/
public class HelloNettyClient {
    public static void main(String[] args) throws InterruptedException {
        // 1. 创建启动器
        new Bootstrap()
                // 2. 添加 EventLoopGroup  选择器（selector）主要体现在服务器端，其实也可以用NIO的方法来写客户端。
                .group(new NioEventLoopGroup())
                // 3. 选择客户端的channel实现
                .channel(NioSocketChannel.class)
                // 4. 添加处理器
                .handler(
                        new ChannelInitializer<NioSocketChannel>() {
                            @Override // 在连接建立后被初始化
                            protected void initChannel(NioSocketChannel ch) throws Exception {
                                // 5. 添加具体的handler
                                ch.pipeline().addLast(new StringEncoder());
                            }
                        })
                // 6. 连接到服务器
                .connect(new InetSocketAddress("localhost", 8080))
                // 7. 阻塞方法，同步 ，等待连接建立完成 。是为了让客户端先建立了连接以后再发送消息
                .sync()
                // 8. 获取channel
                .channel()
                // 9. 向服务器发送数据  无论是发数据还是收数据都走handler
                .writeAndFlush("hello world");
    }
}
