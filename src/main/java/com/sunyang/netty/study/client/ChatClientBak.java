package com.sunyang.netty.study.client;

import com.sunyang.netty.study.message.ChatRequestMessage;
import com.sunyang.netty.study.message.GroupChatRequestMessage;
import com.sunyang.netty.study.message.GroupCreateRequestMessage;
import com.sunyang.netty.study.message.GroupJoinRequestMessage;
import com.sunyang.netty.study.message.GroupMembersRequestMessage;
import com.sunyang.netty.study.message.GroupQuitRequestMessage;
import com.sunyang.netty.study.message.LoginRequestMessage;
import com.sunyang.netty.study.message.LoginResponseMessage;
import com.sunyang.netty.study.protocol.MessageCodecSharable;
import com.sunyang.netty.study.protocol.ProtocolFrameDecoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j(topic = "c.ChatClient")
public class ChatClientBak {
    public static void main(String[] args) {
        NioEventLoopGroup group = new NioEventLoopGroup();
        LoggingHandler LOGGING_HANDLER = new LoggingHandler(LogLevel.DEBUG);
        MessageCodecSharable MESSAGE_CODEC = new MessageCodecSharable();
        CountDownLatch WAIT_FOR_LOGIN = new CountDownLatch(1);
        AtomicBoolean LOGIN = new AtomicBoolean(false);
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group);
            bootstrap.channel(NioSocketChannel.class);
            bootstrap.handler(new ChannelInitializer<NioSocketChannel>() {
                @Override
                protected void initChannel(NioSocketChannel ch) throws Exception {
                    ch.pipeline().addLast(new ProtocolFrameDecoder());
                    ch.pipeline().addLast(LOGGING_HANDLER);
                    ch.pipeline().addLast(MESSAGE_CODEC);
                    ch.pipeline().addLast("client-handler", new ChannelInboundHandlerAdapter(){
                        // ????????????????????????active??????
                        @Override
                        public void channelActive(ChannelHandlerContext ctx) throws Exception {
                            // ?????????????????????????????????????????????????????????????????????????????????
                            // ??????????????????????????????????????????NIO?????????????????????????????????????????????????????????????????????????????????yongNIO???????????????????????????IO?????????
                            new Thread(() -> {
                                Scanner scanner = new Scanner(System.in);
                                System.out.println("?????????????????????");
                                String userName = scanner.nextLine();
                                System.out.println("??????????????????");
                                String password = scanner.nextLine();
                                 // ??????????????????
                                LoginRequestMessage message = new LoginRequestMessage(userName, password);
                                // ????????????
                                ctx.writeAndFlush(message);
                                System.out.println("??????????????????..");
                                try {
                                    WAIT_FOR_LOGIN.await();
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                // ??????????????????
                                if (!LOGIN.get()) {
                                    ctx.channel().close();
                                    return;
                                }
                                while (true) {
                                    System.out.println("==================================");
                                    System.out.println("send [username] [content]");
                                    System.out.println("gsend [group name] [content]");
                                    System.out.println("gcreate [group name] [m1,m2,m3...]");
                                    System.out.println("gmembers [group name]");
                                    System.out.println("gjoin [group name]");
                                    System.out.println("gquit [group name]");
                                    System.out.println("quit");
                                    System.out.println("==================================");
                                    String command = scanner.nextLine();
                                    String[] s = command.split(" ");
                                    switch(s[0]) {
                                        case "send":
                                            ctx.writeAndFlush(new ChatRequestMessage(userName, s[1], s[2]));
                                            break;
                                        case "gsend":
                                            ctx.writeAndFlush(new GroupChatRequestMessage(userName, s[1], s[2]));
                                            break;
                                        case "gcreate":
                                            Set<String> set = new HashSet<>(Arrays.asList(s[2].split(",")));
                                            set.add(userName);
                                            ctx.writeAndFlush(new GroupCreateRequestMessage(s[1], set));
                                            break;
                                        case "gmembers":
                                            ctx.writeAndFlush(new GroupMembersRequestMessage(s[1]));
                                            break;
                                        case "gjoin":
                                            ctx.writeAndFlush(new GroupJoinRequestMessage(userName, s[1]));
                                            break;
                                        case "gquit":
                                            ctx.writeAndFlush(new GroupQuitRequestMessage(userName, s[1]));
                                            break;
                                        case "quit":
                                            ctx.channel().close();
                                            return;

                                    }
                                }

                            }, "system in").start();
                        }

                        @Override
                        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                            log.debug("msg: {}", msg);
                            if (msg instanceof LoginResponseMessage){
                                LoginResponseMessage response = (LoginResponseMessage) msg;
                                if (response.isSuccess()) {
                                    // ??????????????????
                                    LOGIN.set(true);
                                }
                                // ?????? system in ??????
                                WAIT_FOR_LOGIN.countDown();
                            }
                        }
                    });

                }
            });
            ChannelFuture channelFuture = bootstrap.connect(new InetSocketAddress("127.0.0.1", 8080)).sync();
            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
            log.error("client error", e);
        } finally {
            group.shutdownGracefully();
        }
    }
}
