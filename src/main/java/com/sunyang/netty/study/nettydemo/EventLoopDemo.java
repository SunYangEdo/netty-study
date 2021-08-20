package com.sunyang.netty.study.nettydemo;

import io.netty.channel.DefaultEventLoop;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.MultithreadEventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * @Author: sunyang
 * @Date: 2021/8/20
 * @Description:
 */
@Slf4j(topic = "c.Demo")
public class EventLoopDemo {
    public static void main(String[] args) {
        // 创建事件循环组，如果不传参数，默认线程数（也就是EventLoop事件循环对象）为电脑核心数 * 2
        EventLoopGroup eventLoopGroup = new NioEventLoopGroup(2);
//        EventLoopGroup eventLoopGroup = new DefaultEventLoop(); // 普通任务，定时任务
        // 负载均衡，轮询
        System.out.println(eventLoopGroup.next());
        System.out.println(eventLoopGroup.next());
        System.out.println(eventLoopGroup.next());
        System.out.println(eventLoopGroup.next());

        // 异步执行任务，accept以后在做一些事件分发的时候，会用到提交任务的方法，把接下来代码的执行权从一个线程转移到另一个线程
//        eventLoopGroup.submit(() ->{
//            try {
//                TimeUnit.SECONDS.sleep(1);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//            log.debug("ok");
//        });
        // 周期性 定时任务 做keepLive时可以实现连接的保护
        eventLoopGroup.scheduleAtFixedRate(() -> {
            log.debug("周期任务");
        }, 0, 1, TimeUnit.SECONDS);

        // 延迟
        eventLoopGroup.scheduleWithFixedDelay(() -> {
            log.debug("延迟任务");
        }, 0, 1, TimeUnit.SECONDS);
        log.debug("main");

    }
}
