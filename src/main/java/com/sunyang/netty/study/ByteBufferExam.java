package com.sunyang.netty.study;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

import static com.sunyang.netty.study.ByteBufferUtil.debugAll;

/**
 * @program: netty-study
 * @description: 粘包半包问题
 * @author: SunYang
 * @create: 2021-08-16 21:47
 **/
public class ByteBufferExam {
    public static void main(String[] args) {
        /**
         * 网络上有多条数据发送给服务器端，为了进行区分在数据之间加了\n 进行区分
         * 但由于某种原因（ByteBuffer大小等等。）这些数据在接收时，别进行了重新组合，例如原始数据有三条为
         * Hello,word\n
         * I`m zhangsan\n
         * How are you?\n
         * 变成了下面的两个ByteBuffer(粘包，半包)
         * Hello,word\nI`m zhangsan\nHo
         * w are you?\n
         * 现在编写程序，将错乱的数据恢复成原始的按\n 分隔数据
         * **/
        ByteBuffer source = ByteBuffer.allocate(32);
        source.put("Hello,word\nI`m zhangsan\nHo".getBytes());
        split(source);
        source.put("w are you?\n".getBytes());
        split(source);

    }

    private static void split(ByteBuffer source) {
        // 切换成读模式
        source.flip();
        for (int i = 0; i < source.limit(); i++) {
            // 找到一条完整消息 以后会有更高效的方法，这里要一个字节一个字节去遍历一条消息的结束。浪费时间和资源
            if (source.get(i) == '\n') {
                int length = i + 1 - source.position();
                // 把这条完整消息存入新的ByteBuffer
                ByteBuffer target = ByteBuffer.allocate(length);
                // 从source读，向target写
                for (int j = 0; j < length; j++) {
                    target.put(source.get());
                }
                debugAll(target);
            }
        }
        // 切换成写模式 但是不能用clear 因为clear会从头写，那么未读取完的部分就会被丢弃，所以得用compacct()
        source.compact();
    }
}
