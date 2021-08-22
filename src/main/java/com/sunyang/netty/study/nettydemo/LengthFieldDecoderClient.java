package com.sunyang.netty.study.nettydemo;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;

/**
 * @program: netty-study
 * @description: 粘包半包解决方法四-LTC解码器客户端
 * @author: SunYang
 * @create: 2021-08-22 21:21
 **/
@Slf4j(topic = "c.Demo")
public class LengthFieldDecoderClient {
    public static void main(String[] args) {
        EmbeddedChannel channel = new EmbeddedChannel(
                new LengthFieldBasedFrameDecoder(1024, 0, 4, 1, 4),
                new LoggingHandler(LogLevel.DEBUG)
        );

        // 4个字节的内容长度，实际内容
        ByteBuf byteBuf = ByteBufAllocator.DEFAULT.buffer();
        send(byteBuf, "Hello, world");
        send(byteBuf, "Hi!");
        channel.writeInbound(byteBuf);
    }

    private static void send(ByteBuf byteBuf, String content) {
        byte[] bytes = content.getBytes(StandardCharsets.UTF_8); // 实际内容
        int length = bytes.length;
        byteBuf.writeInt(length); // 字段长度
        byteBuf.writeByte(97); // 用一个字节表示版本号 a
        byteBuf.writeBytes(bytes);
    }
}
