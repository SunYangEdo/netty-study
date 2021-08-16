package com.sunyang.netty.study;

import java.nio.ByteBuffer;

import static com.sunyang.netty.study.ByteBufferUtil.debugAll;

/**
 * @program: netty-study
 * @description:
 * @author: SunYang
 * @create: 2021-08-16 20:02
 **/
public class ByteBufferReadDemo {
    public static void main(String[] args) {
        ByteBuffer byteBuffer = ByteBuffer.allocate(10);
        byteBuffer.put(new byte[]{'a', 'b', 'c', 'd'});
        byteBuffer.flip();

//        byteBuffer.get(new byte[4]);
//        debugAll(byteBuffer);
//        // rewind 从头开始读
//        byteBuffer.rewind();
//        System.out.println((char)byteBuffer.get());

//        System.out.println((char) byteBuffer.get()); // 读取 a
//        System.out.println((char) byteBuffer.get()); // 读取 b
//        byteBuffer.mark(); // 加标记  索引为2 的位置
//        System.out.println((char) byteBuffer.get()); // 读取 c
//        System.out.println((char) byteBuffer.get()); // 读取  d
//        byteBuffer.reset(); // 将position 重置到索引为2的位置
//        System.out.println((char) byteBuffer.get()); // 读取 c
//        System.out.println((char) byteBuffer.get()); // 读取 d
        // get(i) 不会改变读索引的位置
        System.out.println((char) byteBuffer.get(3));
        debugAll(byteBuffer);
    }

}
