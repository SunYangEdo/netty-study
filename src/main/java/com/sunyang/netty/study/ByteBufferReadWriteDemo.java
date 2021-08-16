package com.sunyang.netty.study;

import java.nio.ByteBuffer;

import static com.sunyang.netty.study.ByteBufferUtil.debugAll;

/**
 * @Author: sunyang
 * @Date: 2021/8/16
 * @Description:
 */
public class ByteBufferReadWriteDemo {
    public static void main(String[] args) {
        ByteBuffer buffer = ByteBuffer.allocate(10);
        buffer.put((byte) 97); // a
        debugAll(buffer);
        buffer.put(new byte[]{98,99,100});// b c d
        debugAll(buffer);
//        System.out.println(buffer.get());
        buffer.flip();
        System.out.println(buffer.get());
        debugAll(buffer);
        buffer.compact();
        debugAll(buffer);
    }
}
