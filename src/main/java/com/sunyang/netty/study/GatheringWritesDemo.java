package com.sunyang.netty.study;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;

import static com.sunyang.netty.study.ByteBufferUtil.debugAll;

/**
 * @program: netty-study
 * @description: 集中写
 * @author: SunYang
 * @create: 2021-08-16 21:06
 **/
public class GatheringWritesDemo {
    public static void main(String[] args) {
        ByteBuffer b1 = StandardCharsets.UTF_8.encode("hello");
        ByteBuffer b2 = StandardCharsets.UTF_8.encode("word");
        ByteBuffer b3 = StandardCharsets.UTF_8.encode("你好");
        try (FileChannel channel = new RandomAccessFile("words2.txt", "rw").getChannel()) {
            channel.write(new ByteBuffer[]{b1, b2, b3});

        } catch (IOException e) {
        };
    }
}
