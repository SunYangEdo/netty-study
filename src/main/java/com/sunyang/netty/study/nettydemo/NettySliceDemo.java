package com.sunyang.netty.study.nettydemo;

import com.sunyang.netty.study.util.LogUntil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;

/**
 * @program: netty-study
 * @description: 零拷贝体现之一，切片
 * @author: SunYang
 * @create: 2021-08-22 12:03
 **/
public class NettySliceDemo {
    public static void main(String[] args) {
        ByteBuf byteBuf = ByteBufAllocator.DEFAULT.buffer(10);
        byteBuf.writeBytes(new byte[]{'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j'});
        LogUntil.log(byteBuf);
        // 在切片过程中，没有发生数据复制
        //  这时调用 slice 进行切片，无参 slice 是从原始 ByteBuf 的 read index 到 write index 之间的内容进行切片，
        ByteBuf s1 = byteBuf.slice(0, 5);
        ByteBuf s2 = byteBuf.slice(5, 5);
        LogUntil.log(s1);
        LogUntil.log(s2);
        // 问题一：切片后的 max capacity 被固定为这个区间的大小，因此不能追加 write，因为如果你追加了，就会影响其他切片，造成问题
//        s2.writeByte(5);  // 此处会抛出异常
        // 问题二： 如果原始ByteBuf调用了release()方法释放内存，那么切片后的ByteBuf将不能使用，就会抛出异常。
//        byteBuf.release();
//        LogUntil.log(s1); // 报错
        // 问题二 解决办法 但这种办法尽量少用，管理不好，就可能会造成内存泄漏，
//        s1.retain();
        ByteBuf duplicate = s2.duplicate();
        duplicate.writeByte('a');
        LogUntil.log(s2);
        LogUntil.log(duplicate);
        System.out.println("===============");
        // 在对切片后的ByteBuf改变时，原ByteBuf也发生了改变，说明共用的是同一块物理内存空间，也就是同一个对象。并没有进行复制
        s1.setByte(0, 'b');
        LogUntil.log(s1);
        LogUntil.log(byteBuf);


    }
}
