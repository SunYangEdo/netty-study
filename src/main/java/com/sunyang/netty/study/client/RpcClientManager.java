package com.sunyang.netty.study.client;

import com.sunyang.netty.study.client.handler.RpcResponseMessageHandler;
import com.sunyang.netty.study.message.RpcRequestMessage;
import com.sunyang.netty.study.protocol.MessageCodecSharable;
import com.sunyang.netty.study.protocol.ProtocolFrameDecoder;
import com.sunyang.netty.study.protocol.SequenceIdGenerator;
import com.sunyang.netty.study.server.service.HelloService;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.concurrent.DefaultPromise;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Proxy;

/**
 * @Author: sunyang
 * @Date: 2021/8/24
 * @Description:
 */
@Slf4j
public class RpcClientManager {

    private static volatile Channel channel = null;

    public static void main(String[] args) {
        HelloService service  = getProxyService(HelloService.class);
        System.out.println(service.sayHello("zhangsan"));
        System.out.println(service.sayHello("lisi"));
        System.out.println(service.sayHello("wangwu"));
    }

    // 创建代理类
    public static <T> T getProxyService(Class<T> serverClass){
        ClassLoader loader = serverClass.getClassLoader();

        Class<?>[] interfaces = new Class[]{serverClass};
        Object o = Proxy.newProxyInstance(loader, interfaces, ((proxy, method, args) -> {
            // 1. 将方法调用转换成消息对象
            int sequenceId = SequenceIdGenerator.nextId();
            RpcRequestMessage msg = new RpcRequestMessage(
                    sequenceId,
                    serverClass.getName(),
                    method.getName(),
                    method.getReturnType(),
                    method.getParameterTypes(),
                    args
            );
            // 2. 将消息发出去
            getChannel().writeAndFlush(msg);

            // 3. 准备一个空promise对象， 来接收结果                指定promise对象异步接收结果的线程，这里是同步，main线程自己等待，用不到。
            DefaultPromise<Object> promise = new DefaultPromise<>(getChannel().eventLoop());
            RpcResponseMessageHandler.PROMISES.put(sequenceId, promise);

            // 4. 等待promise结果
            promise.await();
            if (promise.isSuccess()) {
                // 调用正常，返回结果
                return promise.getNow();
            } else {
                // 调用失败
                throw new RuntimeException(promise.cause());
            }
        }));
        return (T) o;
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
