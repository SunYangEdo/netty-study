package com.sunyang.netty.study.nettydemo;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;

/**
 * @Author: sunyang
 * @Date: 2021/8/20
 * @Description:
 */
@Slf4j(topic = "c.Demo")
public class EventLoopServer {
    public static void main(String[] args) {
        // 细分2： 如果某个handler的处理时间很长，那么在一个worker管理很多个channel的时候，就会拖慢其他channel的IO操作。
        // 因为是在一个worker中多个channel的IO操作是串行化的。所以就可以在创建一个独立的EventLoopGroup,用来处理这个handler
        // 应用的场景就是，由NioEventLoopGroup来处理IO操作，等到数据读取到了，然后再将数据转给DefaultEventLoop，让他去对数据进行具体的处理。
        EventLoopGroup group = new DefaultEventLoopGroup();
        new ServerBootstrap()
                // 创建两个EventLoopGroup  一个是用来创建Boss 一个是用来创建worker
                // boss  只负责NioServerSocketChannel上 的accept事件，worker 只负责NioSocketChannel上的读写
                // 这里Boss也不用设置线程数为1 是因为只有一个服务器端NioServerSocketChannel，又因为线程池中的线程为懒加载，所以他其实只会创建一个线程Boss
                .group(new NioEventLoopGroup(), new NioEventLoopGroup(2))
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel nioSocketChannel) throws Exception {
                        nioSocketChannel.pipeline().addLast("handler1", new ChannelInboundHandlerAdapter() {
                            @Override
                            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                ByteBuf byteBuf = (ByteBuf) msg;
                                log.debug(byteBuf.toString(StandardCharsets.UTF_8));
                                ctx.fireChannelRead(msg); // 让消息传递给下一个handler
                            }
                        }).addLast(group, "handler2", new ChannelInboundHandlerAdapter() {
                            @Override
                            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                ByteBuf byteBuf = (ByteBuf) msg;
                                log.debug(byteBuf.toString(StandardCharsets.UTF_8));
                            }
                        });
                    }
                })
                .bind(8080);
    }
}
