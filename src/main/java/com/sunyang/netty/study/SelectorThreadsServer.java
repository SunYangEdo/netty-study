package com.sunyang.netty.study;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static com.sunyang.netty.study.ByteBufferUtil.debugAll;

/**
 * @Author: sunyang
 * @Date: 2021/8/19
 * @Description: 多线程版selector服务器端
 */
@Slf4j(topic = "c.Demo")
public class SelectorThreadsServer {

    public static void main(String[] args) throws IOException {
        AtomicInteger index = new AtomicInteger();
        Thread.currentThread().setName("Boss");
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.bind(new InetSocketAddress(8080));
        serverSocketChannel.configureBlocking(false);

        Selector selector = Selector.open();
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        log.debug("Boss-serverSocketChannel 注册， 等待连接.....");
        WorkerEventLoop[] workers = initEventLoops();

        while (true) {
            selector.select();
            Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
            while (iterator.hasNext()) {
                SelectionKey key = iterator.next();
                iterator.remove();

                if (key.isAcceptable()) {
                    ServerSocketChannel channel = (ServerSocketChannel) key.channel();
                    SocketChannel socketChannel = channel.accept();
                    socketChannel.configureBlocking(false);
                    log.debug("connected....{}", socketChannel.getRemoteAddress());

                    log.debug("before register ....{}", socketChannel.getRemoteAddress());
                    // 负载均衡 轮询
                    workers[index.getAndIncrement() % workers.length].register(socketChannel);
                    log.debug("after register ....{}", socketChannel.getRemoteAddress());
                    // 因为是 多线程， 所以这里要再一个新的线程中注册，而每个线程拥有一个自己selector
                    // 所以创建一个内部类，用来创建线程和selector
//                    socketChannel.register(selector, SelectionKey.OP_READ);

                }
            }
        }
    }

    public static WorkerEventLoop[] initEventLoops() {
        // 如果是IO密集型可以利用阿姆达尔定律 如果是CPU密集型可以用内核数+1 防止缺页
        // Runtime.getRuntime().availableProcessors() 再Docker容器中可能不准确，因为他算是的你物理机的真实核心数，
        // 但如果你的Docker核心数只分配了一个，那么就会出问题。
//        WorkerEventLoop[] workerEventLoops = new WorkerEventLoop[Runtime.getRuntime().availableProcessors()];
        WorkerEventLoop[] workerEventLoops = new WorkerEventLoop[8];
        for (int i = 0; i < workerEventLoops.length; i++) {
            workerEventLoops[i] = new WorkerEventLoop("worker-" + i);
        }
        return workerEventLoops;
    }

    static class WorkerEventLoop implements Runnable {
        private String threadName;
        private Thread thread;
        private Selector selector;
        private volatile boolean start = false;
        // 线程安全的队列，用于两个线程间传递数据并且想让代码不是立刻执行，在某个位置执行，就可以用这个。作为两个线程间数据的通道。
        private ConcurrentLinkedQueue<Runnable> tasks = new ConcurrentLinkedQueue<>();

        public WorkerEventLoop(String threadName) {
            this.threadName = threadName;
        }

        public void register(SocketChannel socketChannel) throws IOException {
            if (!start) {
                selector = Selector.open();
                thread = new Thread(this,threadName);
                thread.start();
                start = true;
            }

            // 向任务队列中添加任务，但未执行，可以在想执行任务的地方调用任务的run方法
            tasks.add(() -> {
                try {
                    socketChannel.register(selector, SelectionKey.OP_READ);
                } catch (ClosedChannelException e) {
                    e.printStackTrace();
                }
            });
            // 唤醒 selector 类似于 park unpark
            selector.wakeup();
        }

        @Override
        public void run() {
            while (true) {
                try {
                    selector.select();
                    Runnable task = tasks.poll();
                    if (task != null) {
                        task.run();
                    }
                    Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                    while (iterator.hasNext()) {
                        SelectionKey key = iterator.next();
                        iterator.remove();
                        if (key.isReadable()) {
                            SocketChannel socketChannel = (SocketChannel) key.channel();
                            ByteBuffer buffer = ByteBuffer.allocate(32);
                            try {
                                int read = socketChannel.read(buffer);
                                if (read == -1) {
                                    key.cancel();
                                    socketChannel.close();
                                } else {
                                    buffer.flip();
                                    log.debug("{} message:", socketChannel.getRemoteAddress());
                                    debugAll(buffer);
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                                key.cancel();
                                socketChannel.close();
                            }
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }
    }

}
























