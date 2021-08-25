package com.sunyang.netty.study.leecode;

/**
 * @program: netty-study
 * @description: 旋转数组的最小数字
 * @author: SunYang
 * @create: 2021-08-24 07:29
 **/
public class offer2_11 {
    public int minArray(int[] numbers) {
        int i = 0, j = numbers.length - 1;
        while (i < j) {
            int m = (i + j) / 2;
            // 如果 m > j 的值 说明 中线m 在做左排序数组中， 旋转点 在[m + 1] ~ j 之间
            if (numbers[m] > numbers[j]) {
                i = m + 1;
            }
            // 如果 m < j 的值 说明 中线m 在做右排序数组中递增， 旋转点 在[i ~ m]  之间
            else if (numbers[m] < numbers[j]) {
                j = m;
            }
            else {
                int x = i;
                for (int k = i + 1; k < j; k++) {
                    if (numbers[k] < numbers[i]) {
                        x = k;
                    }
                }
                return numbers[x];
            }
        }
        return numbers[i];
    }

}
