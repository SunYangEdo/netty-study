package com.sunyang.netty.study;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import static com.sunyang.netty.study.ByteBufferUtil.debugAll;

/**
 * @program: netty-study
 * @description: Demo
 * @author: SunYang
 * @create: 2021-08-16 20:30
 **/
public class StringAndByteBuffer {
    public static void main(String[] args) {
        // 1. 字符串转为 ByteBuffer
        ByteBuffer byteBuffer1 = ByteBuffer.allocate(16);
        // put 方式在写入之后，还是写模式  position不为0
        byteBuffer1.put("hello".getBytes());
//        byteBuffer1.put("hello".getBytes(StandardCharsets.UTF_8));
        debugAll(byteBuffer1);

        // 2. Charset  在写入之后 自动转为读模式  position 为 0
        ByteBuffer byteBuffer2 = StandardCharsets.UTF_8.encode("hello");
        debugAll(byteBuffer2);

        // 3. wrap         // 2. Charset  在写入之后 自动转为读模式  position 为 0
        ByteBuffer byteBuffer3 = ByteBuffer.wrap("hello".getBytes(StandardCharsets.UTF_8));
        debugAll(byteBuffer3);

        // ByteBuffer转为String
        CharBuffer decode = StandardCharsets.UTF_8.decode(byteBuffer2); // 返回的是一个CharBuffer
        System.out.println(decode.toString());

        //  读取byteBuffer1 需要转为读模式
        byteBuffer1.flip();
        CharBuffer decode1 = StandardCharsets.UTF_8.decode(byteBuffer1); // 返回的是一个CharBuffer
        System.out.println(decode.toString());
    }
}
