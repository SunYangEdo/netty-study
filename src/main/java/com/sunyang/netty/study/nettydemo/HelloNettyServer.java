package com.sunyang.netty.study.nettydemo;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import sun.nio.cs.SingleByte;

/**
 * @program: netty-study
 * @description: Netty服务器端
 * @author: SunYang
 * @create: 2021-08-19 20:37
 **/
public class HelloNettyServer {
    public static void main(String[] args) {
        // 1. 创建服务端启动器 ，负责组装 Netty 组件，协调工作，启动服务器
        new ServerBootstrap()
                // 2. 添加了一个group组，创建一个EventLoopGroup 包括 BossEventLoop和 WorkerEventLoop(selector thread)
                .group(new NioEventLoopGroup())  // accept 事件是netty内部自己处理的，accept处理器会调用initChannel 方法
                // 3. 选择服务器的ServerSocketChannel实现
                // 指定Channel的类型：EpollServerSocketChannel,KQueueServerSocketChannel,NioServerSocketChannel
                .channel(NioServerSocketChannel.class) // Netty对原生的ServerSocketChannel做了封装
                // 4. 添加处理器，或者叫过滤器
                // boss 负责处理链接，worker（child）负责处理读写的 告诉WorkerEventLoop将来要做哪些事，具体的业务处理。
                // 决定了worker（child）能执行哪些操作（handler-处理器）
                .childHandler(
                        // 5. channel 代表和客户端进行数据读写的通道 ，Initalizer 初始化，初始channel通道。
                        // 本身也是一个handler，负责添加别的handler 在创建时只是添加了初始化器，并未执行初始化器的内容，
                        // 也就是initChannel方法，创建了连接后才执行的初始化器的初始化方法
                        new ChannelInitializer<NioSocketChannel>() {
                            // 具体添加了那些handler就在initChannel中添加
                            @Override  // 在连接建立后被初始化
                            protected void initChannel(NioSocketChannel nioSocketChannel) throws Exception {
                                // 6. 添加具体的handler 为nioSocketChannel上加处理器类
                                // 解码 ，客户端发过来的都是ByteBuf , 要将ByteBuf转换为字符串
                                // 在连接建立后的初始化时会执行addLast()方法，但是不会执行处理器，处理器是在收发数据时才走的。
                                nioSocketChannel.pipeline().addLast(new StringDecoder());
                                nioSocketChannel.pipeline().addLast(new ChannelInboundHandlerAdapter() { // 自定义handler
                                    // 触发了读事件以后要执行的操作。类似于之前的channel.isReadable()
                                    @Override
                                    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                        // 对数据的具体操作。
                                        System.out.println(msg);
                                    }
                                });
                            }
                        })
                // 7. 绑定监听端口
                .bind(8080);

    }
}
