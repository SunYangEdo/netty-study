package com.sunyang.netty.study.nettydemo;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.*;

/**
 * @program: netty-study
 * @description: Demo
 * @author: SunYang
 * @create: 2021-08-21 15:31
 **/
@Slf4j(topic = "c.Demo")
public class JDKFutureDemo {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        ExecutorService service = Executors.newFixedThreadPool(2);
        Future<Integer> future = service.submit(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                log.debug("获取结果");
                TimeUnit.SECONDS.sleep(1);
                return 100;
            }
        });
        log.debug("等待结果...");
        // 主线程通过future同步等待执行结果
        log.debug("结果是{}", future.get());
        log.debug("结束！");
    }
}
