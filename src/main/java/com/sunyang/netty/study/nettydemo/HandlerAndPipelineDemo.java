package com.sunyang.netty.study.nettydemo;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;

/**
 * @program: netty-study
 * @description: Demo
 * @author: SunYang
 * @create: 2021-08-21 16:26
 **/
@Slf4j(topic = "c.Demo")
public class HandlerAndPipelineDemo {
    public static void main(String[] args) {
        new ServerBootstrap()
                .group(new NioEventLoopGroup())
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel ch) throws Exception {
                        // 1. 通过channel拿到Pipeline
                        ChannelPipeline pipeline = ch.pipeline();
                        // 2. 添加处理器Handler ,Netty在创建流水线时直接帮我们创建两个Handler分别为，Head和Tail。
                        // Pipeline是个双向链表
                        // addLast()方法是将handler加入到流水线tail的前一个位置  head -> <- h1 -> <- h2 -> <- h3 -> <- h4 -> <- h5 -> <- h6 tail
                        pipeline.addLast("入站h1", new ChannelInboundHandlerAdapter(){
                            @Override
                            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                log.debug("入站1");
                                super.channelRead(ctx, msg);
                            }
                        });
                        pipeline.addLast("入站h2", new ChannelInboundHandlerAdapter(){
                            @Override
                            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                log.debug("入站2");
                                super.channelRead(ctx, msg);
                            }
                        });
                        pipeline.addLast("入站h3", new ChannelInboundHandlerAdapter(){
                            @Override
                            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                log.debug("入站3");
                                super.channelRead(ctx, msg);
                                ch.writeAndFlush(ctx.alloc().buffer().writeBytes("server..".getBytes(StandardCharsets.UTF_8)));
                            }
                        });
                        pipeline.addLast("出站h4", new ChannelOutboundHandlerAdapter(){
                            @Override
                            public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
                                log.debug("出站4");
                                super.write(ctx, msg, promise);
                            }
                        });
                        pipeline.addLast("出站h5", new ChannelOutboundHandlerAdapter(){
                            @Override
                            public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
                                log.debug("出站5");
                                super.write(ctx, msg, promise);
                            }
                        });
                        pipeline.addLast("出站h6", new ChannelOutboundHandlerAdapter(){
                            @Override
                            public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
                                log.debug("出站6");
                                super.write(ctx, msg, promise);
                            }
                        });
                    }
                })
                .bind(8080);
    }
}