package com.sunyang.netty.study;

import lombok.extern.slf4j.Slf4j;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * @Author: sunyang
 * @Date: 2021/8/16
 * @Description:
 */
@Slf4j(topic = "c.Demo")
public class ByteBufferDemo {
    /* 文件读取 */
    public static void main(String[] args) {
        // FileChannel
        // 获得FileChannel可以通过输入输出流间接的获得FileChannel。
        try (FileChannel channel = new FileInputStream("data.txt").getChannel()) {
            // 创建一个大小为10 的字节缓冲区，这样就不能一次读取所有的文件，要通过两次读取（为了测试举例），因为有时候你也不知道要读取的文件有多大，不可能直接定义好。
            ByteBuffer byteBuffer = ByteBuffer.allocate(10);
            int len;
            while ((len = channel.read(byteBuffer)) != -1) {
                // 从channel中读取数据，写入到bytebuffer中.从此通道读取字节序列到给定缓冲区
                log.debug("读取到的字节长度是{}", len);
                // 打印buffer的内容
                byteBuffer.flip(); // 切换至读模式
                while (byteBuffer.hasRemaining()) { // 是否还有剩余的未读数据
                    // 不带参数的get()是一次读取一个字节
                    byte b = byteBuffer.get();
                    log.debug("读取到字符是{}", (char) b);
                }
                // 切换至写模式 但是不会清空缓冲区，只是指针便回到了0的位置。
                byteBuffer.clear();
                byte b = byteBuffer.get();
                // 而因为读取完成之后这个指针在1的位置，并没有调用 byteBuffer.clear()将指针重置为0，
                // 所以下一次写入的时候是从指针为1的位置开始写入的，也就是1被保存了下了，在1之后又写入了abc
                // 所以从channel读取到的字节长度为3，但是从buffer读出来的字节长度为4，包括1，a, b, c，但是之后的数据在从channel读取写入到buffer时被清掉了。
                log.debug("读取到实际的字符是{}", (char) b);
            }
        } catch (IOException e) {
        }
    }
}
