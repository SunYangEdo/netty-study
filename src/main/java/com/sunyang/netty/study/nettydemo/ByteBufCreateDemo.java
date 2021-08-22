package com.sunyang.netty.study.nettydemo;

import com.sunyang.netty.study.util.LogUntil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;

import java.nio.charset.StandardCharsets;

/**
 * @program: netty-study
 * @description: Demo
 * @author: SunYang
 * @create: 2021-08-22 10:19
 **/
public class ByteBufCreateDemo {
    public static void main(String[] args) {
        // 默认容量 256
        ByteBuf byteBuf = ByteBufAllocator.DEFAULT.buffer();
//        System.out.println(byteBuf.getClass());
//        LogUntil.log(byteBuf);
//        StringBuilder sb = new StringBuilder();
//        for (int i = 0; i < 300; i++) {
//            sb.append("a");
//        }
//        byteBuf.writeBytes(sb.toString().getBytes(StandardCharsets.UTF_8));
        byteBuf.writeInt(5);
        System.out.println(byteBuf.readByte());
        System.out.println(byteBuf.readByte());
        System.out.println(byteBuf.readByte());
        byteBuf.markReaderIndex();
        System.out.println(byteBuf.readByte());
        byteBuf.resetReaderIndex();
//        System.out.println(byteBuf.readByte());
        LogUntil.log(byteBuf);
    }
}
