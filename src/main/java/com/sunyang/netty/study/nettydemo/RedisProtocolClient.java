package com.sunyang.netty.study.nettydemo;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.Random;

/**
 * @program: netty-study
 * @description: redis
 * @author: SunYang
 * @create: 2021-08-22 22:03
 **/
@Slf4j(topic = "c.Demo")
public class RedisProtocolClient {
    /**
     *      set name zhangsan  中间要加 /r /n
     *      发送整个数组的长度（也就是元素的个数）  *3  /r /n
     *      第一个元素的长度                    $3  /r /n
     *      值                               set  /r /n
     *      第一个元素的长度                    $4  /r /n
     *      值                               name  /r /n
     *      第一个元素的长度                    $8  /r /n
     *      值                               zhangsan  /r /n
     * **/
    public static void main(String[] args) {
        NioEventLoopGroup worker = new NioEventLoopGroup();
        byte[] LINE = {13, 10};
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(worker);
            bootstrap.channel(NioSocketChannel.class);
            bootstrap.handler(new ChannelInitializer<NioSocketChannel>() {
                @Override
                protected void initChannel(NioSocketChannel ch) throws Exception {
                    ch.pipeline().addLast(new LoggingHandler(LogLevel.DEBUG));
                    ch.pipeline().addLast(new ChannelInboundHandlerAdapter() {
                        // 会在连接 channel 建立成功后，会触发 active 事件
                        @Override
                        public void channelActive(ChannelHandlerContext ctx) {
//                            set(ctx);
//                            get(ctx);
                            ByteBuf buf = ctx.alloc().buffer();
                            buf.writeBytes("*3".getBytes());
                            buf.writeBytes(LINE);
                            buf.writeBytes("$3".getBytes());
                            buf.writeBytes(LINE);
                            buf.writeBytes("set".getBytes());
                            buf.writeBytes(LINE);
                            buf.writeBytes("$4".getBytes());
                            buf.writeBytes(LINE);
                            buf.writeBytes("name".getBytes());
                            buf.writeBytes(LINE);
                            buf.writeBytes("$5".getBytes());
                            buf.writeBytes(LINE);
                            buf.writeBytes("zhangsan".getBytes());
                            buf.writeBytes(LINE);
                            ctx.writeAndFlush(buf);
                        }
//                        private void get(ChannelHandlerContext ctx) {
//                            ByteBuf buf = ctx.alloc().buffer();
//                            buf.writeBytes("*2".getBytes());
//                            buf.writeBytes(LINE);
//                            buf.writeBytes("$3".getBytes());
//                            buf.writeBytes(LINE);
//                            buf.writeBytes("get".getBytes());
//                            buf.writeBytes(LINE);
//                            buf.writeBytes("$4".getBytes());
//                            buf.writeBytes(LINE);
//                            buf.writeBytes("name".getBytes());
//                            buf.writeBytes(LINE);
//                            ctx.writeAndFlush(buf);
//                        }
//                        private void set(ChannelHandlerContext ctx) {
//                            ByteBuf buf = ctx.alloc().buffer();
//                            buf.writeBytes("*3".getBytes());
//                            buf.writeBytes(LINE);
//                            buf.writeBytes("$3".getBytes());
//                            buf.writeBytes(LINE);
//                            buf.writeBytes("set".getBytes());
//                            buf.writeBytes(LINE);
//                            buf.writeBytes("$4".getBytes());
//                            buf.writeBytes(LINE);
//                            buf.writeBytes("name".getBytes());
//                            buf.writeBytes(LINE);
//                            buf.writeBytes("$5".getBytes());
//                            buf.writeBytes(LINE);
//                            buf.writeBytes("zhangsan".getBytes());
//                            buf.writeBytes(LINE);
//                            ctx.writeAndFlush(buf);
//                        }

                        @Override
                        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                            ByteBuf buf = (ByteBuf) msg;
                            System.out.println(buf.toString(StandardCharsets.UTF_8));
                        }
                    });
                }
            });
            ChannelFuture channelFuture = bootstrap.connect(new InetSocketAddress("47.94.209.96", 6379)).sync();
            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
            log.error("client error", e);
        } finally {
            worker.shutdownGracefully();
        }
    }
}
