package com.sunyang.netty.study;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @Author: sunyang
 * @Date: 2021/8/17
 * @Description:
 */

public class FileChannelTransferTo {
    public static void main(String[] args) {
//        try (FileChannel from = new FileInputStream("data.txt").getChannel();
//             FileChannel to = new FileOutputStream("to.txt").getChannel();
//        ) {
//            // 创数数据上限，最多2G，那么如果数据过大，就需要分段传输。
//            for (long left = from.size(); left > 0;) {
//                left -= from.transferTo(from.size() - left, left, to);
//            }
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        };
//        Path source = Paths.get("1.txt");  // 相对路径 使用user.dir环境变量来定位1.txt
//        System.out.println(source.normalize());
//
//        Path source1 = Paths.get("d:\\1.txt"); // 绝对路径 代表了 d:\1.txt  用\ 需要转义
//        System.out.println(source1.normalize());
//
//        Path source3 = Paths.get("d:/1.txt"); //  绝对路径 同样代表了 d:\1.txt
//        System.out.println(source3.normalize());
//
//        Path projects = Paths.get("d:\\data", "projects"); //  代表了d"\data\projects
//        System.out.println(projects.normalize());

//        Path path = Paths.get("d:\\data\\projects\\a\\..\\b");
//        System.out.println(path);
//        System.out.println(path.normalize()); // 正常化路径


    }
}
