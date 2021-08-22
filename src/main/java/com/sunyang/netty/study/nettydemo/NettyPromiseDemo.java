package com.sunyang.netty.study.nettydemo;

import io.netty.channel.EventLoop;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.util.concurrent.DefaultPromise;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * @program: netty-study
 * @description: Demo
 * @author: SunYang
 * @create: 2021-08-21 16:03
 **/
@Slf4j(topic = "c.Demo")
public class NettyPromiseDemo {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        NioEventLoopGroup group = new NioEventLoopGroup();
        // 1. 准备EventLoop对象
        EventLoop eventLoop = group.next();
        // 2. 可以主动创建promise,结果容器
        DefaultPromise<Integer> promise = new DefaultPromise<>(eventLoop);
        new Thread(() -> {
            // 3. 任意一个线程执行计算，计算完毕后向promise填充结果
            log.debug("计算结果..");
            try {
                TimeUnit.SECONDS.sleep(1);
                promise.setSuccess(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
                promise.setFailure(e);
            }
        }).start();
        log.debug("等待结果");
        log.debug("结果是:{}", promise.get());
        log.debug("结束");
        group.shutdownGracefully();
    }
}
