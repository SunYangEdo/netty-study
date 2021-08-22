package com.sunyang.netty.study.nettydemo;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringEncoder;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

/**
 * @program: netty-study
 * @description: ChannelFuture
 * @author: SunYang
 * @create: 2021-08-21 10:21
 **/
@Slf4j(topic = "c.Demo")
public class ChannelFutureClient {
    public static void main(String[] args) throws InterruptedException {
        // 带有Future,Promise 的类型都是和异步方法配套使用，用来正确的处理结果。
        ChannelFuture channelFuture = new Bootstrap()
                .group(new NioEventLoopGroup())
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel nioSocketChannel) throws Exception {
                        nioSocketChannel.pipeline().addLast(new StringEncoder(StandardCharsets.UTF_8));
                    }
                })
                // connect方法是一个异步非阻塞方法 连接是由主线程main发起调用的，真正执行底层connect连接操作的是另一个线程 (NioEventLoopGroup循环事件组中的一个线程).
                .connect(new InetSocketAddress("localhost", 8080));
        // 方法一 ： 使用sync() 方法同步处理结果
//        channelFuture.sync(); // 阻塞住当前线程，直到Nio线程连接建立完毕
        // 如果没有channelFuture.sync();那么主线程会无阻塞向下执行 获取channel，
        // 这个时候虽然channel已经创建了好了，但是并没有进行连接，所以数据是发送不出去的。
//        Channel channel = channelFuture.channel();
//        log.debug("{}", channel);
//        channel.writeAndFlush("hello");
//        channel.close();

        // 方法二：使用addListener（回调对象） 方法异步处理结果 。只要告诉将来连接执行完成了，返回结果后要做什么操作，回调.把回调对象传递给NIo线程
        // sync()           是 当前线程        等待异步执行连接的线程返回结果。
        // addListener（）  是  再来一个新的线程  等待异步执行连接的线程返回结果。
        // 然后main线程继续向下执行，不用main线程自己等待连接结果返回。
        // 三个线程 ： 第一个（main）: 发起连接调用，继续向下执行；第二个（connect线程）：执行真正的连接操作。第三个（等待connect线程）： 等待connect线程返回连接结果。
        channelFuture.addListener(new ChannelFutureListener() {
            @Override
            // 在Nio线程连接建立好后，会调用operationComplete方法
            public void operationComplete(ChannelFuture future) throws Exception {
                Channel channel = future.channel();
                log.debug("{}", channel);
                channel.writeAndFlush("hello");
                channel.close();
            }
        });
    }
}
