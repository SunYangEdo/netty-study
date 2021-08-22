package com.sunyang.netty.study.nettydemo;

import io.netty.channel.EventLoop;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * @program: netty-study
 * @description: Demo
 * @author: SunYang
 * @create: 2021-08-21 15:42
 **/
@Slf4j(topic = "c.Demo")
public class NettyFutureDemo {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        NioEventLoopGroup group = new NioEventLoopGroup();
        EventLoop next = group.next();
        Future<Integer> future = next.submit(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                log.debug("执行计算");
                TimeUnit.SECONDS.sleep(1);
                return 100;
            }
        });
//        log.debug("等待结果...");
//        // 主线程通过future同步等待执行结果
//        future.sync();
//        log.debug("结果是{}", future.getNow()); //  两个加一起等于  log.debug("结果是{}", future.get());
//        log.debug("结束！");

        // 异步
        log.debug("等待结果...");
        // 主线程通过future同步等待执行结果
        future.addListener(new GenericFutureListener<Future<? super Integer>>() {
            @Override
            public void operationComplete(Future future) throws Exception {
                log.debug("结果是:{}", future.getNow());
            }
        });
        log.debug("结束！");

        group.shutdownGracefully();

    }
}
