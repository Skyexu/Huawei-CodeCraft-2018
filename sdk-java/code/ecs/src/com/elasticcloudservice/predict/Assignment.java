package com.elasticcloudservice.predict;

import java.util.*;

/**
 *
 * @author Team-"自然醒"
 * 解决思路为背包问题的思想
 * input: step1输入至step2的应该包含：三个List,分别对应虚拟机的cpu核数，虚拟机的内存，虚拟机的名字，以及keyword，以及物理机的最大cpu核数和最大内存。
 * keyword为"CPU"或者"MEM";
 */
public class Assignment {

    public static Map<String, Integer> myPackage(List<Integer> value,List<Integer> weight,List<String> name,int[] res,int[][] dp,int max,int maxV,int num) {


        Map<String,Integer> map = new HashMap<>();

        int count;
        int size = value.size();
        int t = 0;//以便于删除list的正确位置，list删除一个值，后面会往前进一
        for(int i=1;i<size;i++) {
            for(int j=1;j<=max;j++) {
                if(j > weight.get(i)) {
//					if(dp[i-1][j-weight.get(i)]+value.get(i)<=maxV)
                    dp[i][j] = dp[i-1][j-weight.get(i)]+value.get(i);
//					else
//						dp[i][j] = dp[i-1][j];
                }
                else
                    dp[i][j] = dp[i-1][j];
            }
        }

        int w = max;
        if(dp[size-1][max]>maxV) {
            for( w = w-1;w>0;w--) {
                for(int i=1;i<size;i++) {
                    for(int j=1;j<=w;j++) {
                        if(j > weight.get(i)) {
                            dp[i][j] = dp[i-1][j-weight.get(i)]+value.get(i);
                        }
                        else
                            dp[i][j] = dp[i-1][j];
                    }
                }
                //System.out.println(dp[size-1][w]);
                if(dp[size-1][w] <=maxV)
                    break;
            }
        }
        //System.out.println(dp[size-1][max]);
//        if(dp[size-1][max]<maxV/3) {
//            value.clear();
//            return ;// 最后一个物理服务器待改进
//        }
        int temp = w;
        for(int i=size-1;i>0;i--) {
            if(temp-weight.get(i)>0 && dp[i][temp] == dp[i-1][temp-weight.get(i)]+value.get(i))
            {
                res[i] = 1;
                //System.out.println(i);
                if(map.containsKey(name.get(i))) {
                    count = map.get(name.get(i));
                    count = count + 1;
                    map.put(name.get(i), count);
                }
                else {
                    map.put(name.get(i), 1);
                }
                temp = temp - weight.get(i);
            }
        }
        //System.out.print(num+" ");
        /*
        StringBuilder flavorLine = new StringBuilder();
        flavorLine.append(num+" ");
        int index = 0;

        for (Map.Entry<String, Integer> entry : map.entrySet()) {
            //System.out.print(entry.getKey() + " " + entry.getValue()+" ");
            index++;
            if (index == map.size()){
                flavorLine.append(entry.getKey() + " " + entry.getValue());
            }else {
                flavorLine.append(entry.getKey() + " " + entry.getValue()+" ");
            }

        }
        //System.out.println();
*/
        for(int i=1;i<size;i++) {
            if(res[i] == 1) {
                value.remove(i-t);
                weight.remove(i-t);
                name.remove(i-t);
                t++;
            }
        }
        //return flavorLine;
        return map;
    }

    public static List<Map<String,Integer>> startAssignment(String keyword,int maxCpu,int maxMemory,List<Integer> cpu,List<Integer> memory,List<String> name){
        //List<String> resultList = new LinkedList<>();
        List<Map<String,Integer>> resultList = new LinkedList<>();
        int maxV;
        int maxW;
        List<Integer> value = new ArrayList<>();
        List<Integer> weight = new ArrayList<>();

        if(keyword!=null && keyword.equals("CPU")) {
            value = cpu;
            weight = memory;
            maxW = maxMemory;
            maxV = maxCpu;
        }
        else {
            value = memory;
            weight = cpu;
            maxW = maxCpu;
            maxV = maxMemory;
        }

        int num =0;
        while(value.size()>1) {
            num = num+1;
            int[][]	dp = new int[value.size()][maxW+1];
            int[] res = new int[value.size()];
            //StringBuilder flavorLine = myPackage(value, weight, name, res, dp, maxW,maxV, num);
            Map<String,Integer> flavorMap = myPackage(value, weight, name, res, dp, maxW,maxV, num);
            resultList.add(flavorMap);
            //resultList.add(flavorLine.toString());
        }
/*
        // 增加最后一个物理机的资源利用率，以最小的虚拟机补充
        Map<String,Integer> lastFlavorMap = resultList.get(resultList.size() -1);

        int[][] flavorInfo = Predict.flavorInfo;
        int useCpu = 0;
        int useMemory = 0;
        int minFlavorNum = 15;
        int maxCount = 0;
        for (Map.Entry<String,Integer> entry:
        lastFlavorMap.entrySet()) {
            int flavorNum = Integer.parseInt(entry.getKey().substring(6));
            if (flavorNum < minFlavorNum){
                minFlavorNum = flavorNum;
            }
            int count = entry.getValue();
            if (count > maxCount){
                maxCount = count;
            }
            useCpu += flavorInfo[flavorNum][0]*count;
            useMemory += flavorInfo[flavorNum][1]/1024*count;
        }
        int addNum = 0;
        while (useCpu <= maxCpu && useMemory <= maxMemory && addNum < maxCount ){
            useCpu += flavorInfo[minFlavorNum][0];
            useMemory += flavorInfo[minFlavorNum][1]/1024;
            addNum++;
        }
        if (useCpu > maxCpu || useMemory > maxMemory)
            addNum--;

        lastFlavorMap.put("flavor"+minFlavorNum,lastFlavorMap.get("flavor"+minFlavorNum) + addNum);
        */
        return resultList;
    }
    public static void main(String[] args) {
        String keyword = "MEM";
        int maxCpu = 56;
        int maxMemory = 128;
        int maxV;
        int maxW;
        List<Integer> value = new ArrayList<>();
        List<Integer> weight = new ArrayList<>();

        List<Integer> cpu = new ArrayList<>();
        List<Integer> memory = new ArrayList<>();
        List<String> name = new ArrayList<>();

        //例子
        cpu.add(0);
        memory.add(0);
        name.add("null");

        cpu.add(1);
        memory.add(1);
        name.add("flavor1");
        cpu.add(1);
        memory.add(2);
        name.add("flavor2");
        cpu.add(1);
        memory.add(4);
        name.add("flavor3");
        cpu.add(2);
        memory.add(2);
        name.add("flavor4");
        cpu.add(2);
        memory.add(4);
        name.add("flavor5");
        cpu.add(8);
        memory.add(8);
        name.add("flavor10");
        cpu.add(8);
        memory.add(16);
        name.add("flavor11");
        cpu.add(8);
        memory.add(32);
        name.add("flavor12");
        cpu.add(2);
        memory.add(8);
        name.add("flavor6");
        cpu.add(4);
        memory.add(4);
        name.add("flavor7");
        cpu.add(4);
        memory.add(8);
        name.add("flavor8");
        cpu.add(4);
        memory.add(16);
        name.add("flavor9");

        cpu.add(16);
        memory.add(16);
        name.add("flavor13");
        cpu.add(16);
        memory.add(32);
        name.add("flavor14");
        cpu.add(16);
        memory.add(65);
        name.add("flavor15");

        if(keyword!=null && keyword.equals("CPU")) {
            value = cpu;
            weight = memory;
            maxW = maxMemory;
            maxV = maxCpu;
        }
        else {
            value = memory;
            weight = cpu;
            maxW = maxCpu;
            maxV = maxMemory;
        }

        int num =0;
        while(value.size()>1) {
            num = num+1;
            int[][]	dp = new int[value.size()][maxW+1];
            int[] res = new int[value.size()];
            myPackage(value, weight, name, res, dp, maxW,maxV, num);
            //System.out.println(flavorLine);
        }
    }

}
