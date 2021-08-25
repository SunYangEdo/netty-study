package com.sunyang.netty.study.protocol;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @program: netty-study
 * @description:
 * @author: SunYang
 * @create: 2021-08-24 20:36
 **/
public abstract class SequenceIdGenerator {
    private static final AtomicInteger id = new AtomicInteger();

    public static int nextId() {
        return id.incrementAndGet();
    }
}
