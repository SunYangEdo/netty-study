package com.sunyang.netty.study.nettydemo;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

/**
 * @program: netty-study
 * @description: Demo
 * @author: SunYang
 * @create: 2021-08-21 11:05
 **/
@Slf4j(topic = "c.Demo")
public class CloseFutureClient {
    public static void main(String[] args) throws InterruptedException {
        NioEventLoopGroup group = new NioEventLoopGroup();
        ChannelFuture channelFuture = new Bootstrap()
                .group(group)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel nioSocketChannel) throws Exception {
                        // 添加输出日志，监控handler状态
                        nioSocketChannel.pipeline().addLast(new LoggingHandler(LogLevel.DEBUG));
                        nioSocketChannel.pipeline().addLast(new StringEncoder(StandardCharsets.UTF_8));
                    }
                })
                .connect(new InetSocketAddress("localhost", 8080));
        Channel channel = channelFuture.sync().channel();
        log.debug("连接建立--{}", channel);
        // 开启一个新的线程 用来接收用户的输入并发送到服务器端
        new Thread(() -> {
            Scanner scanner = new Scanner(System.in);
            while (true) {
                String line = scanner.nextLine();
                if("q".equals(line)) {
                    channel.close(); // close 异步操作。 其他线程去执行关闭操作。
                    break;
                }
                channel.writeAndFlush(line);
            }
        }, "input").start();
        // 获取closeFuture对象。 方法一：同步处理关闭，方法二：异步处理关闭
        ChannelFuture closeFuture = channel.closeFuture();
//        // 方法一：同步
//        log.debug("waiting close....");
//        closeFuture.sync();
//        log.debug("处理关闭之后的操作");

        // 方法二：异步
        closeFuture.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                log.debug("处理关闭之后的操作");
                // 关闭后需要将NioEventLoopGroup关闭掉（所有的线程停止掉），不再接受新的任务。
                group.shutdownGracefully(); // 先拒绝接收新的任务。然后把现有的任务执行完，把未发完的数据发完。
            }
        });

    }
}
