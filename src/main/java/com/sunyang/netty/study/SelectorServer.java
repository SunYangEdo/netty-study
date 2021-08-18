package com.sunyang.netty.study;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;

import static com.sunyang.netty.study.ByteBufferUtil.debugAll;
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
//            log.debug("select 前 keys大小：{}", selector.keys().size());
//            log.debug("select 前 selectedKeys 大小: {}", selector.selectedKeys().size());
            selector.select();
//            log.debug("select 后 keys大小：{}", selector.keys().size());
//            log.debug("select 后 selectedKeys 大小: {}", selector.selectedKeys().size());
            // 有事件发生，处理事件
            // 获取注册到selector中所有的key
            Iterator<SelectionKey> iterator = selector.selectedKeys().iterator(); // accept, read
//            log.debug("遍历 keys大小：{}", selector.keys().size());
//            log.debug("遍历 selectedKeys 大小: {}", selector.selectedKeys().size());
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
                    // 在注册时添加附件 可以解决消息边界问题，但不完美，如果数据很大会一直扩容，数据少时不会缩减，不能动态调整，Netty可以
                    // 附件 设置一个SocketChannel专属的Buffer，可以解决局部buffer变量再数据大时，第一次读取数据丢失的情况，然后，通过扩容拷贝的方式将数据组合起来。
                    ByteBuffer buffer = ByteBuffer.allocate(16);
                    SelectionKey scSelectionKey = socketChannel.register(selector, SelectionKey.OP_READ, buffer);
//                    SelectionKey scSelectionKey = socketChannel.register(selector, SelectionKey.OP_READ, null); // 测试没有专属buffer时使用
                    log.debug("socketChannel register read key: {}", scSelectionKey);
//                    log.debug("remove() 前 keys 大小: {}", selector.keys().size());
//                    log.debug("remove() 前 selectedKeys 大小: {}", selector.selectedKeys().size());
//                    iterator.remove();
//                    log.debug("remove() 后 keys 大小: {}", selector.keys().size());
//                    log.debug("remove() 后 selectedKeys 大小: {}", selector.selectedKeys().size());
                } else if (key.isReadable()) { // read事件
                    try {
//                        ByteBuffer buffer = ByteBuffer.allocate(16); // 准备一个缓冲区，用于存放channel读取的数据 ， 将局部变量转为附件，作为channel的专属buffer
                        ByteBuffer buffer = (ByteBuffer) key.attachment();
                        // 获取 发生读事件的socketChannel，因为可能会有很多客户端（socketChannel）发生读事件，要确定是哪个
                        SelectableChannel channel = key.channel();
                        SocketChannel socketChannel = (SocketChannel) channel;
                        // 如果是正常断开，read的方法返回值是-1，如果有数据就继续处理
                        // 非阻塞模式下没有数据为0，但是因为有selector，没有数据也不会触发读事件，所以页进不到这里
                        int read = socketChannel.read(buffer);
                        if (read == -1) {
                            key.cancel();
                        } else {
                            // 切换读模式
//                            buffer.flip();
//                            debugRead(buffer);
                            // 替换成之前写好的
                            split(buffer);
                            // 判断 如果position 和 limit 相等，说明没有找到\n 消息分隔符，这时说明一条消息没有结束，buffer不够大了，需要扩容。
                            // 这时就需要对原buffer进行扩容拷贝
                            if (buffer.position() == buffer.limit()) {
                                ByteBuffer newBuffer = ByteBuffer.allocate(buffer.capacity() * 2);
                                // 切换成读模式
                                buffer.flip();
                                newBuffer.put(buffer);
                                // 将新buffer 与key 绑定。
                                key.attach(newBuffer);
                            }
                        }
                    } catch (IOException e) {
                        // 如果不抛出异常，客户端断开连接后，服务器也会报错退出，
                        // 因为客户端在断开连接后，会发出一个读事件，又因为读事件内容为空，所以会报错，
                        // 但是捕获后异常后我们还要对产生的这个key的读事件做一个处理，如果不处理，就会陷入非阻塞状态，死循环。
                        // 因为抛出异常后，等于没对这个读事件做处理，所以会再次产生一个读事件，即使之前已经删除了
                        e.printStackTrace();
                        // 正确处理完可以直接删除，如果没有处理，就删除，他还会在产生一个新的事件
                        // 所以，需要将key取消，（）
//                        log.debug("cancel() 前 keys 大小: {}", selector.keys().size());
//                        log.debug("cancel() 前 selectedKeys 大小: {}", selector.selectedKeys().size());
                        key.cancel();
//                        log.debug("cancel() 后 keys 大小: {}", selector.keys().size());
//                        log.debug("cancel() 后 selectedKeys 大小: {}", selector.selectedKeys().size());
                    }
                }

                // 如果不想处理，可以取消，不然会陷入非阻塞，一直循环
//                key.cancel();
            }

        }

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
