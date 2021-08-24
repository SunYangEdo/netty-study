package com.sunyang.netty.study.client;

import com.sunyang.netty.study.client.handler.RpcResponseMessageHandler;
import com.sunyang.netty.study.message.RpcRequestMessage;
import com.sunyang.netty.study.protocol.MessageCodecSharable;
import com.sunyang.netty.study.protocol.ProtocolFrameDecoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * @Author: sunyang
 * @Date: 2021/8/24
 * @Description:
 */
@Slf4j
public class RpcClientManager {

    private static volatile Channel channel = null;

    public static void main(String[] args) {
        getChannel().writeAndFlush(new RpcRequestMessage(
                1,
                "com.sunyang.netty.study.server.service.HelloService",
                "sayHello",
                String.class,
                new Class[]{String.class},
                new Object[]{"张三"}
        ));
    }


    public static Channel getChannel() {
        if (channel == null) {
            synchronized (RpcClientManager.class) {
                if (channel == null) {
                    initChannel();
                    return channel;
                }
            }
        }
        return channel;
    }

    // 初始化channel方法
    private static void initChannel() {
        NioEventLoopGroup group = new NioEventLoopGroup();
        LoggingHandler LOGGING_HANDLER = new LoggingHandler(LogLevel.DEBUG);
        MessageCodecSharable MESSAGE_CODEC = new MessageCodecSharable();
        RpcResponseMessageHandler RPC_HANDLER = new RpcResponseMessageHandler();
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.channel(NioSocketChannel.class);
        bootstrap.group(group);
        bootstrap.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                ch.pipeline().addLast(new ProtocolFrameDecoder());
                ch.pipeline().addLast(LOGGING_HANDLER);
                ch.pipeline().addLast(MESSAGE_CODEC);
                ch.pipeline().addLast(RPC_HANDLER);
            }
        });
        try {
            channel = bootstrap.connect("localhost", 8080).sync().channel();
            channel.closeFuture().addListener(future -> {
                group.shutdownGracefully();
            });
        } catch (Exception e) {
            log.error("client error", e);
        }
    }
}
