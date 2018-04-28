package com.elasticcloudservice.allocate;

import com.elasticcloudservice.model.Physical;
import com.elasticcloudservice.predict.Assignment3;

import java.util.*;

public class GetAns4 {
    public static List<Map<String, Integer>> packages(int[][] flavorInfo, int cpu, int mem, String[] name) {
        List<Map<String, Integer>> result = new ArrayList<>();
        int[] res = new int[flavorInfo.length];
        int sum = 0;
        while (sum < flavorInfo.length - 1) {
            int maxCpu = cpu;
            int maxMem = mem;
            int[][] dp = new int[maxCpu + 1][maxMem + 1];
            byte[][][] index = new byte[flavorInfo.length][maxCpu + 1][maxMem + 1];
            int size = flavorInfo.length;
            for (int i = 1; i < size; i++) {
                for (int j = maxCpu; j >= flavorInfo[i][0]; j--) {
                    for (int k = maxMem; k >= flavorInfo[i][1]; k--) {
                        if (dp[j][k] < dp[j - flavorInfo[i][0]][k - flavorInfo[i][1]] + flavorInfo[i][0] + flavorInfo[i][1]) {
                            dp[j][k] = dp[j - flavorInfo[i][0]][k - flavorInfo[i][1]] + flavorInfo[i][0] + flavorInfo[i][1];
                            index[i][j][k] = 1;
                        }
                    }
                }
            }
//            for(int i=1;i<size;i++) {
//                for (int j = 1; j <= maxCpu; j++) {
//                    for(int k= 1; k<=maxMem;k++){
//                        System.out.print(dp[i][j][k]+",");
//                    }
//                    System.out.println();
//                }
//            }
//            System.out.println(dp[size-1][maxCpu][maxMem]);
            int temp = dp[maxCpu][maxMem];
            int count;
            Map<String, Integer> map = new HashMap<>();
            int j = maxCpu;
            int k = maxMem;
            int i = size - 1;
            while (i > 0) {
                if (index[i][j][k] == 1 && res[i] == 0 && flavorInfo[i][0] != 0 && flavorInfo[i][1] != 0) {
            /*if不满足，表示第i件物品没装入背包,
              if条件满足，表示放入背包了*/
                    System.out.println(i);
                    j -= flavorInfo[i][0];//此时容量减少
                    k -= flavorInfo[i][1];
                    if (map.containsKey(name[i])) {
                        count = map.get(name[i]);
                        count = count + 1;
                        map.put(name[i], count);
                    } else {
                        map.put(name[i], 1);
                    }
                    res[i] = 1;
                    sum++;
                    flavorInfo[i][0] = 0;
                    flavorInfo[i][1] = 0;
                }
                i--;
            }
            result.add(map);
//            for(int i=size-1;i>=0;i--) {
//                if(res[i] == 0 && flavorInfo[i][0] !=0 && flavorInfo[i][1] !=0 &&temp - flavorInfo[i][0] - flavorInfo[i][1]>=0 &&maxCpu-flavorInfo[i][0]>=0&& maxMem - flavorInfo[i][1]>=0&& dp[i][maxCpu][maxMem] == dp[i-1][maxCpu-flavorInfo[i][0]][maxMem - flavorInfo[i][1]]+flavorInfo[i][0] + flavorInfo[i][1])
//                {
//                    res[i] = 1;
//                    sum++;
//                    if(map.containsKey(name[i])) {
//                        count = map.get(name[i]);
//                        count = count + 1;
//                        map.put(name[i], count);
//                    }
//                    else {
//                        map.put(name[i], 1);
//                    }
//                    //System.out.println(i+" "+flavorInfo[i][0]+" "+flavorInfo[i][1]+" "+temp+" "+maxCpu+" "+maxMem);
//                    temp = temp - flavorInfo[i][0] - flavorInfo[i][1];
//                    maxCpu -= flavorInfo[i][0];
//                    maxMem -= flavorInfo[i][1];
//                    flavorInfo[i][0] = 0;
//                    flavorInfo[i][1] = 0;
//                }
//            }

            // System.out.println("-----------------------------------");
        }

        return result;
    }
    public static List<List<Map<String, Integer>>> start(int[][] flavorInfo, String[] name, List<Integer> preFlavorList,List<Physical> ps) {
        List<Map<String, Integer>> resultList;
        List<List<Map<String, Integer>>> resultLists = new LinkedList<>();
        //List<String> answer  = getAnswer(flavorNum,maxCpu, maxMem, keyWord, flavorInfo, name);
        //开始，这个放在这里
        if(ps.size() == 3){
            int a = 1;
            int b = 1;
            int c = 1;
            for (int i = 0; i < flavorInfo.length; i++) {
                if (flavorInfo[i][0] / flavorInfo[i][1] == 1)
                    a++;
                else if (flavorInfo[i][0] * 1.0 / flavorInfo[i][1] == 0.5)
                    b++;
                else
                    c++;
            }
            int[][] minInfo = new int[b][2];
            int[][] medInfo = new int[c][2];
            int[][] maxInfo = new int[a][2];
            String[] minName = new String[b];
            String[] medName = new String[c];
            String[] maxName = new String[a];
            a = 1;
            b = 1;
            c = 1;
            for (int i = 0; i < flavorInfo.length; i++) {
                if (flavorInfo[i][0] / flavorInfo[i][1] == 1) {
                    maxInfo[a][0] = flavorInfo[i][0];
                    maxInfo[a][1] = flavorInfo[i][1];
                    maxName[a] = name[i];
                    a++;
                } else if (flavorInfo[i][0] * 1.0 / flavorInfo[i][1] == 0.5) {
                    minInfo[b][0] = flavorInfo[i][0];
                    minInfo[b][1] = flavorInfo[i][1];
                    minName[b] = name[i];
                    b++;
                } else {
                    medInfo[c][0] = flavorInfo[i][0];
                    medInfo[c][1] = flavorInfo[i][1];
                    medName[c] = name[i];
                    c++;
                }
            }
            String keyWord = null;
            if (minInfo.length > 0) {
                resultList = packages(minInfo, ps.get(0).getCpu(), ps.get(0).getMemory(),minName);
                resultList = Assignment3.refine2(resultList, keyWord, ps.get(0).getCpu(), ps.get(0).getMemory(), preFlavorList);
                resultLists.add(resultList);
            } else {
                resultList = new ArrayList<>();
                resultLists.add(resultList);
            }
            if (medInfo.length > 0) {
                resultList = packages(medInfo, ps.get(1).getCpu(), ps.get(1).getMemory(),medName);
                resultList = Assignment3.refine2(resultList, keyWord, ps.get(1).getCpu(), ps.get(1).getMemory(), preFlavorList);
                resultLists.add(resultList);
            } else {
                resultList = new ArrayList<>();
                resultLists.add(resultList);
            }
            if (maxInfo.length > 0) {
                resultList = packages(maxInfo, ps.get(2).getCpu(), ps.get(2).getMemory(),maxName);
                resultList = Assignment3.refine2(resultList, keyWord, ps.get(2).getCpu(), ps.get(2).getMemory(), preFlavorList);
                resultLists.add(resultList);
            } else {
                resultList = new ArrayList<>();
                resultLists.add(resultList);
            }
        }
        else if(ps.size()==2){
            if(ps.get(0).getName().equals("General") && ps.get(1).getName().equals("Large-Memory")){
                //int a = 0;
                int b = 1;
                int c = 1;
                for (int i = 0; i < flavorInfo.length; i++) {
                    if (flavorInfo[i][0] / flavorInfo[i][1] == 1 || flavorInfo[i][0]*1.0 / flavorInfo[i][1] == 0.5)
                        b++;
                    else
                        c++;
                }
                int[][] minInfo = new int[b][2];
                int[][] medInfo = new int[c][2];
                //int[][] maxInfo = new int[a][2];
                String[] minName = new String[b];
                String[] medName = new String[c];
                //[] maxName = new String[a];
                // a = 0;
                b = 1;
                c = 1;
                for (int i = 0; i < flavorInfo.length; i++) {
                    if (flavorInfo[i][0] / flavorInfo[i][1] == 1|| flavorInfo[i][0]*1.0 / flavorInfo[i][1] == 0.5) {
                        minInfo[b][0] = flavorInfo[i][0];
                        minInfo[b][1] = flavorInfo[i][1];
                        minName[b] = name[i];
                        b++;
                    } else {
                        medInfo[c][0] = flavorInfo[i][0];
                        medInfo[c][1] = flavorInfo[i][1];
                        medName[c] = name[i];
                        c++;
                    }
                }
                String keyWord = null;
                if (minInfo.length > 0) {
                    resultList = packages(minInfo, ps.get(0).getCpu(), ps.get(0).getMemory(),minName);
                    resultList = Assignment3.refine2(resultList, keyWord, ps.get(0).getCpu(), ps.get(0).getMemory(), preFlavorList);
                    resultLists.add(resultList);
                } else {
                    resultList = new ArrayList<>();
                    resultLists.add(resultList);
                }
                if (medInfo.length > 0) {
                    resultList = packages(medInfo, ps.get(1).getCpu(), ps.get(1).getMemory(),medName);
                    resultList = Assignment3.refine2(resultList, keyWord, ps.get(1).getCpu(), ps.get(1).getMemory(), preFlavorList);
                    resultLists.add(resultList);
                } else {
                    resultList = new ArrayList<>();
                    resultLists.add(resultList);
                }
            }
            else if(ps.get(0).getName().equals("General") && ps.get(1).getName().equals("High-Performance")){
                int a = 1;
                int b = 1;
                //int c = 0;
                for (int i = 0; i < flavorInfo.length; i++) {
                    if (flavorInfo[i][0] / flavorInfo[i][1] == 1)
                        a++;
                    else
                        b++;
                }
                int[][] minInfo = new int[b][2];
                //int[][] medInfo = new int[c][2];
                int[][] maxInfo = new int[a][2];
                String[] minName = new String[b];
                // String[] medName = new String[c];
                String[] maxName = new String[a];
                a = 1;
                b = 1;
                //c = 0;
                for (int i = 0; i < flavorInfo.length; i++) {
                    if (flavorInfo[i][0] / flavorInfo[i][1] == 1) {
                        maxInfo[a][0] = flavorInfo[i][0];
                        maxInfo[a][1] = flavorInfo[i][1];
                        maxName[a] = name[i];
                        a++;
                    } else {
                        minInfo[b][0] = flavorInfo[i][0];
                        minInfo[b][1] = flavorInfo[i][1];
                        minName[b] = name[i];
                        b++;
                    }
                }
                String keyWord = null;
                if (minInfo.length > 0) {
                    resultList = packages(minInfo, ps.get(0).getCpu(), ps.get(0).getMemory(),minName);
                    resultList = Assignment3.refine2(resultList, keyWord, ps.get(0).getCpu(), ps.get(0).getMemory(), preFlavorList);
                    resultLists.add(resultList);
                } else {
                    resultList = new ArrayList<>();
                    resultLists.add(resultList);
                }
                if (maxInfo.length > 0) {
                    resultList = packages(maxInfo, ps.get(1).getCpu(), ps.get(1).getMemory(),maxName);
                    resultList = Assignment3.refine2(resultList, keyWord, ps.get(1).getCpu(), ps.get(1).getMemory(), preFlavorList);
                    resultLists.add(resultList);
                } else {
                    resultList = new ArrayList<>();
                    resultLists.add(resultList);
                }
            }
            else {
                int a = 1;
                //int b = 0;
                int c = 1;
                for (int i = 0; i < flavorInfo.length; i++) {
                    if (flavorInfo[i][0] / flavorInfo[i][1] == 1|| flavorInfo[i][0] * 1.0 / flavorInfo[i][1] == 0.5)
                        a++;
                    else
                        c++;
                }
                //int[][] minInfo = new int[b][2];
                int[][] medInfo = new int[c][2];
                int[][] maxInfo = new int[a][2];
                // String[] minName = new String[b];
                String[] medName = new String[c];
                String[] maxName = new String[a];
                a = 1;
                // b = 0;
                c = 1;
                for (int i = 0; i < flavorInfo.length; i++) {
                    if (flavorInfo[i][0] / flavorInfo[i][1] == 1 || flavorInfo[i][0] * 1.0 / flavorInfo[i][1] == 0.5) {
                        maxInfo[a][0] = flavorInfo[i][0];
                        maxInfo[a][1] = flavorInfo[i][1];
                        maxName[a] = name[i];
                        a++;
                    } else {
                        medInfo[c][0] = flavorInfo[i][0];
                        medInfo[c][1] = flavorInfo[i][1];
                        medName[c] = name[i];
                        c++;
                    }
                }
                String keyWord = null;
                if (medInfo.length > 0) {
                    resultList = packages(medInfo, ps.get(0).getCpu(), ps.get(0).getMemory(),medName);
                    resultList = Assignment3.refine2(resultList, keyWord, ps.get(0).getCpu(), ps.get(0).getMemory(), preFlavorList);
                    resultLists.add(resultList);
                } else {
                    resultList = new ArrayList<>();
                    resultLists.add(resultList);
                }
                if (maxInfo.length > 0) {
                    resultList = packages(maxInfo, ps.get(1).getCpu(), ps.get(1).getMemory(),maxName);
                    resultList = Assignment3.refine2(resultList, keyWord, ps.get(1).getCpu(), ps.get(1).getMemory(), preFlavorList);
                    resultLists.add(resultList);
                } else {
                    resultList = new ArrayList<>();
                    resultLists.add(resultList);
                }
            }
        }
        else {
            String keyWord = null;
            resultList = packages(flavorInfo,ps.get(0).getCpu(), ps.get(0).getMemory(),name);
            resultList = Assignment3.refine2(resultList, keyWord, ps.get(0).getCpu(), ps.get(0).getMemory(), preFlavorList);
            resultLists.add(resultList);
        }
        return resultLists;
    }

    public static Map<String,List<Map<String, Integer>>> startAssignment(int flavorNum, List<Physical> physicalList, int[][] flavorInfo, String[] name, List<Integer> preFlavorList) {
        List<Physical> pList = new ArrayList<>();
        pList.addAll(physicalList);
        // 按 CPU 排序物理机
        Collections.sort(pList);
        List<List<Map<String, Integer>>> lists = start(flavorInfo,name,preFlavorList,pList);
        Map<String,List<Map<String, Integer>>> map = new HashMap<>();

        int index = 0;
        for (Physical physical:pList) {
            map.put(physical.getName(),lists.get(index++));
        }
        return map;
    }
    public static void main(String[] args) {
        int [][] a = new int[7][2];
        int [][][] dp = new int[7][16][26];
        int [][] dp1 = new int[7][11];
        int [][] dp2 = new int[7][11];
        a[0][0] = 1;
        a[0][1] = 1;
        a[1][0] = 1;
        a[1][1] = 1;
        a[2][0] = 1;
        a[2][1] = 4;
        a[3][0] = 2;
        a[3][1] = 8;
        a[4][0] = 4;
        a[4][1] = 16;
        a[5][0] = 8;
        a[5][1] = 8;
        a[6][0] = 15;
        a[6][1] = 16;
        String[] name = new String[7];
        name[1] = "1";
        name[2] = "2";
        name[3] = "3";
        name[4] = "4";
        name[5] = "5";
        name[6] = "6";
        int[] res = new int[7];
//        for(int i=1;i<7;i++)
////            for(int j = 1;j<10;j++){
////            if(a[i][0]+a[i][1]<=j){
////
////                dp[i][j] = Math.max(dp[i-1][j-a[i][0]-a[i][1]]+a[i][0]+a[i][1],dp[i-1][j]);
////            }else {
////                dp[i][j] = dp[i-1][j];
////            }
////        }
////
////        for(int i=0;i<7;i++){
////            for(int j=0;j<10;j++)
////                System.out.print(dp[i][j]+",");
////            System.out.println();
////        }
        packages(a,15,25,name);
    }


}
