package com.sunyang.netty;

import java.util.LinkedList;
import java.util.Stack;

/**
 * @program: netty-study
 * @description: 用两个栈实现一个队列
 * @author: SunYang
 * @create: 2021-08-19 07:34
 **/
public class TwoStackQueue {
    // 测试分支冲突2
    Stack<Integer> A, B;
    public TwoStackQueue() {
        A = new Stack<>();
        B = new Stack<>();
    }

    public void appendTail(int value) {
        A.push(value);
    }

    public Integer deleteHead() {
        if (!B.isEmpty()) {
            return B.pop();
        }
        if (A.isEmpty()) {
            return -1;
        }

        while (!A.isEmpty()) {
            B.push(A.pop());
        }
        return B.pop();
    }

    public static void main(String[] args) {

        TwoStackQueue queue = new TwoStackQueue();
        queue.appendTail(1);
        queue.appendTail(2);
        System.out.println(queue.deleteHead());
        System.out.println(queue.deleteHead());
        System.out.println(queue.deleteHead());

    }
}
