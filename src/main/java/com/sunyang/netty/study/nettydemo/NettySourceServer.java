package com.sunyang.netty.study.nettydemo;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LoggingHandler;

/**
 * @program: netty-study
 * @description: 源码分析
 * @author: SunYang
 * @create: 2021-08-25 13:52
 **/

public class NettySourceServer {
    public static void main(String[] args) {
        new ServerBootstrap()
                .group(new NioEventLoopGroup()) //  Selector selector = Selector.open();
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<NioSocketChannel>(){
                    @Override
                    protected void initChannel(NioSocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new LoggingHandler());
                    }
                }).bind(8080);
    }
}
