package com.sunyang.netty.study.protocol;

import com.sunyang.netty.study.config.Config;
import com.sunyang.netty.study.message.Message;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

/**
 * @Author: sunyang
 * @Date: 2021/8/23
 * @Description:  可共享的编解码器
 */
@Slf4j(topic = "c.MessageCodecSharable")
@ChannelHandler.Sharable
/**
 * 必须和LengthFieldBasedFrameDecoder 一起使用，确保接到的 ByteBuf消息是完整的，
 * **/
public class MessageCodecSharable extends MessageToMessageCodec<ByteBuf, Message> {

    @Override
    protected void encode(ChannelHandlerContext ctx, Message msg, List<Object> outList) throws Exception {
        ByteBuf out = ctx.alloc().buffer();
        // 1. 4个字节魔数  yang
        out.writeBytes(new byte[]{'y', 'a', 'n', 'g'});
        // 2. 1个字节版本号
        out.writeByte(1);
        // 3. 1个字节序列化算法方式 jdk 0, json 1
        out.writeByte(Config.getSerializerAlgorithm().ordinal());
        // 4. 1个字节指令类型
        out.writeByte(msg.getMessageType());
        // 5. 4个字节请求序号
        out.writeInt(msg.getSequenceId());

        // 因为加一起一共是15个字节，因为要尽量要数据大小为2的n次方倍，所以加一个字节的填充位
        out.writeByte(0xff);

        // 6. 获取内容的字节数组
        byte[] bytes = Config.getSerializerAlgorithm().serialize(msg);

        // 7. 获取内容的长度
        out.writeInt(bytes.length);

        // 8. 写入内容
        out.writeBytes(bytes);
        outList.add(out);

    }

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf in, List<Object> out) throws Exception {
        int magicNum = in.readInt();
        byte version = in.readByte();
        byte serializerAlgorithm = in.readByte();
        byte messageType = in.readByte();
        int sequenceId = in.readInt();
        in.readByte();
        int length = in.readInt();
        byte[] bytes = new byte[length];
        in.readBytes(bytes, 0, length);

        // 找到反序列化算法
        Serializer.Algorithm algorithm = Serializer.Algorithm.values()[serializerAlgorithm];
        // 确定具体消息类型。
        Class<? extends Message> messageClass = Message.getMessageClass(messageType);
        Message message = algorithm.deserialize(messageClass, bytes);
//        log.debug("{}, {}, {}, {}, {}, {}", magicNum, version, serializerType, messageType, sequenceId, length);
//        log.debug("{}", message);
        out.add(message);
    }
}
