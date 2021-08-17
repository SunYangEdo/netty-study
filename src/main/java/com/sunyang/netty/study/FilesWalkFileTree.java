package com.sunyang.netty.study;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.LongAdder;

/**
 * @Author: sunyang
 * @Date: 2021/8/17
 * @Description:  遍历文件夹树
 */
public class FilesWalkFileTree {
    public static void main(String[] args) throws IOException {
//        fileTree();
//        findFile();
//        deleteFile();
//        createFile();
        String source = "E:\\Git";
        String target = "E:\\GitTest";

        Files.walk(Paths.get(source)).forEach(path -> {
            String targetName = path.toString().replace(source, target);
            try {
                if (Files.isDirectory(path)) {
                    Files.createDirectory(Paths.get(targetName));
                }
                else if (Files.isRegularFile(path)) {
                    Files.copy(path, Paths.get(targetName));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        });
    }

    private static void createFile() throws IOException {
        String source = "E:\\Git";
        String target = "E:\\GitTest";
        Files.walkFileTree(Paths.get("E:\\Git"), new SimpleFileVisitor<Path>(){
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                String targetName = dir.toString().replace(source, target);
                Files.createDirectory(Paths.get(targetName));
                return super.preVisitDirectory(dir, attrs);
            }

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                String targetName = file.toString().replace(source, target);
                Files.copy(file, Paths.get(targetName));
                return super.visitFile(file, attrs);
            }

        });
    }


    private static void deleteFile() throws IOException {
        Files.walkFileTree(Paths.get("E:\\lianxi"), new SimpleFileVisitor<Path>(){
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                // 可以不用重写此方法，只是为了便于理解
                System.out.println("=====> 进入" + dir);
                return super.preVisitDirectory(dir, attrs);
            }

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                // 先删除文件夹中的文件，然后再删除外层文件夹
                System.out.println("删除文件---> " + file);
                Files.delete(file);
                return super.visitFile(file, attrs);
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                // 删除文件夹中的文件后，此文件夹就为空，所以就可以在删除文件夹
                System.out.println("<======== 退出" + dir);
                System.out.println("开始删除文件夹--->" + dir);
                Files.delete(dir);
                return super.postVisitDirectory(dir, exc);
            }
        });
    }

    private static void findFile() throws IOException {
        AtomicInteger jarCount = new AtomicInteger();
        Files.walkFileTree(Paths.get("E:\\Java\\jdk1.8.0_261"), new SimpleFileVisitor<Path>(){
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                if (file.toString().endsWith(".jar")) {
                    System.out.println(file);
                    jarCount.incrementAndGet();
                }
                return super.visitFile(file, attrs);
            }
        });
        System.out.println("jar count: " + jarCount);
    }

    private static void fileTree() throws IOException {
        LongAdder fileCount = new LongAdder();
        LongAdder dirCount = new LongAdder();
        Files.walkFileTree(Paths.get("E:\\Java\\jdk1.8.0_261"), new SimpleFileVisitor<Path>(){
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                System.out.println("=======>" + dir);
                dirCount.increment();
                return super.preVisitDirectory(dir, attrs);
            }

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                System.out.println(file);
                fileCount.increment();
                return super.visitFile(file, attrs);
            }
        });
        System.out.println("dir count: " + dirCount);
        System.out.println("file count: " + fileCount);
    }
}
