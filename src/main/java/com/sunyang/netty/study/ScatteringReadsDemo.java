package com.sunyang.netty.study;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

import static com.sunyang.netty.study.ByteBufferUtil.debugAll;

/**
 * @program: netty-study
 * @description: 分散读
 * @author: SunYang
 * @create: 2021-08-16 20:59
 **/
public class ScatteringReadsDemo {
    public static void main(String[] args) {
        try (FileChannel channel = new RandomAccessFile("words.txt", "r").getChannel()) {
            ByteBuffer b1 = ByteBuffer.allocate(3); //  one
            ByteBuffer b2 = ByteBuffer.allocate(3); //  two
            ByteBuffer b3 = ByteBuffer.allocate(5); //  three
            channel.read(new ByteBuffer[]{b1, b2, b3});
            b1.flip();
            b2.flip();
            b3.flip();
            debugAll(b1);
            debugAll(b2);
            debugAll(b3);

        } catch (IOException e) {
        };
    }
}
