package com.sunyang.netty.study.nettydemo;

import io.netty.channel.EventLoop;
import io.netty.channel.nio.NioEventLoopGroup;

/**
 * @program: netty-study
 * @description: EventLoop何时启动
 * @author: SunYang
 * @create: 2021-08-25 16:41
 **/
public class EventLoopStartDemo {
    public static void main(String[] args) {
        EventLoop eventLoop = new NioEventLoopGroup().next();
        eventLoop.execute(() -> {
            System.out.println("hello");
        });
    }
}
