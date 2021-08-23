package com.sunyang.netty.study;

/**
 * @program: netty-study
 * @description: Demo
 * @author: SunYang
 * @create: 2021-08-23 07:26
 **/
public class DongTaiGuiHua {
    public int numWays(int n) {
        int a = 1, b =1, sum;
        for (int i = 0; i < n; i++) {
            sum = (a + b) % 1000_000_007;
            a = b;
            b = sum;
        }
        return a;

    }
}
