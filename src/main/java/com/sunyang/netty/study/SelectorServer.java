package com.sunyang.netty.study;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;

import static com.sunyang.netty.study.ByteBufferUtil.debugRead;

/**
 * @program: netty-study
 * @description: Selector非阻塞单线程服务端
 * @author: SunYang
 * @create: 2021-08-17 20:00
 **/
@Slf4j(topic = "c.Demo")
public class SelectorServer {

    @SuppressWarnings("InfiniteLoopStatement")
    public static void main(String[] args) throws IOException {
        // 1. 创建服务端
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        // 2. 绑定监听端口
        serverSocketChannel.bind(new InetSocketAddress(8080));
        // 使用selector必须是非阻塞，否则报错。
        serverSocketChannel.configureBlocking(false);

        // 3. 定义selector  管理多个channel
        Selector selector = Selector.open();

        // 注册通道，返回键值， 建立selector与channel的联系（注册）
        // selectionKey 就是事件发生后，通过它可以知道是什么事件，和哪个channel发生的事件
        // 类似于管理员，这个selectionKey管理的是serverSocketChannel
        // 当需要读取数据时，还需要在注册一个selectionKey用来管理SocketChannel
        // 0 表示不关注任何事件
        SelectionKey sscSelectionKey = serverSocketChannel.register(selector, 0, null);

        // 注册感兴趣的事件 这里sscSelectionKey只关注accept事件 多个客户端连接所返回的key都为同一个key 都是这个key，因为是同一个serverSocketChannel的同一个事件accept所以key相同。
        // 四种事件类型
        // accept  会在有连接请求时触发
        // connect 客户端和服务端连接建立后触发的事件，客户端 channel.connect(new InetSocketAddress("localhost", 8080));
        // read 可读事件 表示有数据了，可读
        // write 可写事件

        sscSelectionKey.interestOps(SelectionKey.OP_ACCEPT);
        log.debug("serverSocketChannel register accept key: {}", sscSelectionKey);

        while (true) {
            // 无事件发生时 会在此阻塞，有事件发生时，会获取事件的key然后进行相应的处理
            // select 在事件发生后，但未处理时，他不会阻塞
            // 所以事件发生后，要么处理，要么取消，不能置之不理
            selector.select();
            // 有事件发生，处理事件
            // 获取注册到selector中所有的key
            Iterator<SelectionKey> iterator = selector.selectedKeys().iterator(); // accept, read
            log.debug("selectedKeys 大小: {}", selector.selectedKeys().size());
            // 迭代所有的key
            // 拿到所有可用事件集合
            // 增强for循环不能再遍历时删除。
            while (iterator.hasNext()) {
                SelectionKey key = iterator.next();
                // 处理完key 要手动删除 开始删，结束删都可以，因为这个key已经拿到了。
                iterator.remove();
                log.debug("key: {}", key);
                // 判断key的类型
                if (key.isAcceptable()) { // accept事件
                    // 根据key 获取到 发生事件的ServerSocketChannel，和发生的事件
                    SelectableChannel selectableChannel = key.channel();
                    // 拿到事件发生的channel
                    ServerSocketChannel channel = (ServerSocketChannel) selectableChannel;
                    // 然后接受创建连接。
                    SocketChannel socketChannel = channel.accept();
                    socketChannel.configureBlocking(false);
                    log.debug("建立连接的socketChannel---->{}", socketChannel);
                    // 注册到selector  并设置感兴趣的事件为read(可读事件)
                    SelectionKey scSelectionKey = socketChannel.register(selector, SelectionKey.OP_READ, null);
                    log.debug("socketChannel register read key: {}", scSelectionKey);

                } else if (key.isReadable()) { // read事件
                    ByteBuffer buffer = ByteBuffer.allocate(16); // 准备一个缓冲区，用于存放channel读取的数据
                    // 获取 发生读事件的socketChannel，因为可能会有很多客户端（socketChannel）发生读事件，要确定是哪个
                    SelectableChannel channel = key.channel();
                    SocketChannel socketChannel = (SocketChannel) channel;
                    socketChannel.read(buffer);
                    // 切换读模式
                    buffer.flip();
                    debugRead(buffer);

                }

                // 如果不想处理，可以取消，不然会陷入非阻塞，一直循环
//                key.cancel();
            }

        }

    }
}
