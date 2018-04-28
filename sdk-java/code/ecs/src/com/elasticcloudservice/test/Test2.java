package com.elasticcloudservice.test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @Author: Skye
 * @Date: 15:20 2018/4/19
 * @Description:
 */
public class Test2 {
    public static void main(String[] args) {
        List<String> list = new ArrayList<>();
        List<String> list1 = new ArrayList<>();
        list.add(null);
        System.out.println(list.size());
        byte[] b = new byte[5];
        b[0] = 1;
        System.out.println(b[0]);

        List<Map<String, Integer>> list2 = new ArrayList<>();
        System.out.println(list2.size());
    }
}
