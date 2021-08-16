package com.sunyang.netty.study;

import java.nio.ByteBuffer;

/**
 * @program: netty-study
 * @description: Dmeo
 * @author: SunYang
 * @create: 2021-08-16 19:43
 **/
public class ByteBufferAllocateDemo {
    public static void main(String[] args) {
        System.out.println(ByteBuffer.allocate(16).getClass());
        System.out.println(ByteBuffer.allocateDirect(16).getClass());
    }
}
