package com.elasticcloudservice.allocate;

import com.elasticcloudservice.model.Physical;
import com.elasticcloudservice.predict.Assignment3;
import com.elasticcloudservice.predict.Predict;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

public class GetAns2 {
    /**
     * @param flavorNum               虚拟机个数
     * @param maxCpu                  //物理机最大cpu
     * @param maxMem//物理机最大mem
     * @param flavorInfo//二维数组，虚拟机的属性 [falavorNum][2]  ,[falavorNum][0] = cpu [falavorNum][1] = mem mem为（1，2，4，8，16，32，64）即已除以1024;
     * @param name//一维数组，依次对应上述的虚拟机属性
     * @return 返回 <物理机类型名，物理机类型结果列表>
     * <p>
     * 可以调试的参数为temp1、temp2、temp3，大于temp1代表继续粒子群算法，大于temp2代表需要装填，如果利用率为100%，但是虚拟机丢弃率大于temp3,则取下一个粒子群的结果并装填。
     */
    public static Map<String,List<Map<String, Integer>>>  getAnswer(int flavorNum, int minCpu, int minMem, int medCpu, int medMem, int maxCpu, int maxMem, String[] physicalName, int[][] flavorInfo, String[] name) {
        Map<String, List<String>> resultMap = new HashMap<>();

        List<byte[][]> list1 = new ArrayList<>();
        List<Double> list2 = new ArrayList<>();
        List<Integer> list3 = new ArrayList<>();
        List<Integer> list4 = new ArrayList<>();
        List<Integer> list5 = new ArrayList<>();
        List<Integer> list6 = new ArrayList<>();
        byte[][] ans = {};
        int machineNum = 0;
        double temp1 = 0.96;
        double temp2 = 0.9;
        double temp3 = 0.20;
        int temp = 0;
        int i, j, k;
        int minNum = 0;
        int minStart;
        int minStart1 = 0;
        int minStart2 = 0;
        int medNum = 0;
        int medStart;
        int medStart1 = 0;
        int medStart2 = 0;
        int maxNum = 0;
        int maxStart;
        int maxStart1 = 0;
        int maxStart2 = 0;
        Result result = new Result();
        int listIndex = 0;
        List<String> list = new ArrayList<>();
        int sum = 0;
        for (i = 0; i < flavorNum; i++) {
            if (flavorInfo[i][0] * 1.0 / flavorInfo[i][1] == 0.5) {
                minStart1 += flavorInfo[i][0];
                minStart2 += flavorInfo[i][1];
                //minNum++;
            } else if (flavorInfo[i][0] * 1.0 / flavorInfo[i][1] == 0.25) {
                medStart1 += flavorInfo[i][0];
                medStart2 += flavorInfo[i][1];
                //medNum++;
            } else {
                maxStart1 += flavorInfo[i][0];
                maxStart2 += flavorInfo[i][1];
                //maxNum++;
            }
        }

        minStart = Math.max(minStart1 / minCpu, minStart2 / minMem) - 1;
        medStart = Math.max(medStart1 / medCpu, medStart2 / medMem) - 1;
        maxStart = Math.max(maxStart1 / maxCpu, maxStart2 / maxMem) - 1;
        minStart--;
        medStart--;
        maxStart--;
        if (minStart <= 0)
            minStart = 1;
        if (medStart <= 0)
            medStart = 1;
        if (maxStart <= 0)
            maxStart = 1;
        minNum = minStart;
        medNum = medStart;
        maxNum = maxStart;
        int min = Math.min(minNum, Math.min(medNum, maxNum));

        if (min == minNum) {
            if (maxNum > medNum) {
                list.add("max");
                list.add("med");
                list.add("min");
            } else {
                list.add("med");
                list.add("max");
                list.add("min");
            }
        } else if (min == medNum) {
            if (maxNum > minNum) {
                list.add("max");
                list.add("min");
                list.add("med");
            } else {
                list.add("min");
                list.add("max");
                list.add("med");
            }
        } else {
            if (minNum > medNum) {
                list.add("min");
                list.add("med");
                list.add("max");
            } else {
                list.add("med");
                list.add("min");
                list.add("max");
            }
        }
        minNum = minNum / min;
        medNum = medNum / min;
        maxNum = maxNum / min;
        sum = minNum + medNum + maxNum;


        System.out.println(minStart + " " + medStart + " " + maxStart);
        int num = 0;
        if (minStart == 1 && medStart == 1 && maxStart == 1) {
            minStart = 1;
            medStart = 0;
            maxStart = 0;
        }
        PSO2 pso = new PSO2();
        while (true) {
            pso = new PSO2(flavorNum, 10, 20, 0.5f, minStart, medStart, maxStart, minCpu, minMem, medCpu, medMem, maxCpu, maxMem, flavorInfo, name);
            result = pso.solve();
            pso = null;
            System.gc();
            machineNum = minStart + medStart + maxStart;
            ans = result.getPgd();
            temp = 0;
            for (j = 0; j < flavorNum; j++) {
                for (k = 0; k < machineNum; k++) {
                    if (ans[k][j] == 1)
                        break;
                }
                if (k == machineNum)
                    temp++;
            }
            double key = temp * 1.0 / flavorNum;
            if (key == 0) {
                list1.add(ans);
                list2.add(result.getvPgd() * (1 - key));
                list3.add(machineNum);
                list4.add(minStart);
                list5.add(medStart);
                list6.add(maxStart);
                break;
            }
//            if(result.getvPgd()*(1-key) <0.7 && flavorNum>240)
//                i++;
            if (key <= temp3) {   //如果被删除的虚拟机数量大于20%，则忽略。
                list1.add(ans);
                list2.add(result.getvPgd() * (1 - key));
                list3.add(machineNum);
                list4.add(minStart);
                list5.add(medStart);
                list6.add(maxStart);
            }
            num++;
            if (num > 20)
                break;
            listIndex = listIndex % 3;
            String s = list.get(listIndex);
            if (s.equals("max"))
                maxStart++;
            else if (s.equals("med"))
                medStart++;
            else
                minStart++;
            listIndex++;
            if (listIndex == sum)
                listIndex = 0;
            //break;

        }

        if (list1.size() == 0) {
            ans = result.getPgd();
            machineNum = machineNum;
        } else {
            double max = 0;
            int index = 0;
            for (i = 0; i < list2.size(); i++) {
                if (max < list2.get(i)) {
                    index = i;
                    max = list2.get(i);
                }
            }
            ans = list1.get(index);
            machineNum = list3.get(index);
            minStart = list4.get(index);
            medStart = list5.get(index);
            maxStart = list6.get(index);
        }
        System.out.println(minStart + " " + medStart + " " + maxStart);
        // 每种物理机的分配结果放入相应的列表
        Map<String,List<Map<String, Integer>>> resultPhysicalMap = new HashMap<>();
        List<Map<String, Integer>> machine1List = new ArrayList<>();
        List<Map<String, Integer>> machine2List = new ArrayList<>();
        List<Map<String, Integer>> machine3List = new ArrayList<>();

        for (i = 0; i < machineNum; i++) {
            Map<String, Integer> map = new HashMap<>();
            for (j = 0; j < flavorNum; j++) {
                if (ans[i][j] == 1) {
                    if (map.containsKey(name[j])) {
                        int count = map.get(name[j]) + 1;
                        map.put(name[j], count);
                    } else {
                        map.put(name[j], 1);
                    }
                }
            }
            if (i < minStart) {
                if (map.size() > 0)
                    machine1List.add(map);
            } else if (i >= minStart && i < (minStart + medStart)) {
                if (map.size() > 0)
                    machine2List.add(map);
            } else {
                if (map.size() > 0)
                    machine3List.add(map);
            }

        }

        resultPhysicalMap.put(physicalName[0],machine1List);
        resultPhysicalMap.put(physicalName[1],machine2List);
        resultPhysicalMap.put(physicalName[2],machine3List);
        return resultPhysicalMap;
    }


    public static List<Map<String, Integer>> changeAnswer(List<String> answer) {
        List<Map<String, Integer>> resultList = new LinkedList<>();
        for (String answerStr : answer) {
            Map<String, Integer> map = new HashMap<>();
            String[] flavorStrs = answerStr.split("\t");
            for (String fStr : flavorStrs) {
                String[] fStrs = fStr.split(" ");
                map.put(fStrs[0], Integer.parseInt(fStrs[1]));
            }
            resultList.add(map);
        }

        return resultList;
    }

    public static Map<String,List<Map<String, Integer>>> startAssignment(int flavorNum, List<Physical> physicalList, int[][] flavorInfo, String[] name, List<Integer> preFlavorList) {
        Map<String,List<Map<String, Integer>>> resultMap = null;
        List<Physical> pList = new ArrayList<>();
        pList.addAll(physicalList);
        int minCpu;
        int minMem;
        int medCpu;
        int medMem;
        int maxCpu;
        int maxMem;
        String[] physicalName = new String[3];
        // 按 CPU 排序物理机
        Collections.sort(pList);
        minCpu = pList.get(0).getCpu();
        minMem = pList.get(0).getMemory();
        physicalName[0] = pList.get(0).getName();
        medCpu = pList.get(1).getCpu();
        medMem = pList.get(1).getMemory();
        physicalName[1] = pList.get(1).getName();
        maxCpu = pList.get(2).getCpu();
        maxMem = pList.get(2).getMemory();
        physicalName[2] = pList.get(2).getName();

        resultMap = getAnswer(flavorNum, minCpu, minMem, medCpu, medMem, maxCpu, maxMem, physicalName, flavorInfo, name);
        // 填补
        for (Physical physical:physicalList) {
            List<Map<String, Integer>> list = resultMap.get(physical.getName());
            if (list.size()>0){
                List<Map<String,Integer>> newList = Assignment3.refine2(list,null,physical.getCpu(),physical.getMemory(),preFlavorList);
                resultMap.put(physical.getName(),newList);
            }
        }
        return resultMap;
    }



    public static void main(String[] args) throws IOException {
        long startTime = System.currentTimeMillis();   //获取开始时间
        List<Map<String, Integer>> answer = new ArrayList<>();
        String strbuff;
        int flavorNum = 120;
        int minCpu = 56;
        int minMem = 128;
        int medCpu = 84;
        int medMem = 256;
        int maxCpu = 112;
        int maxMem = 192;
        String keyWord = "MEM";
        String filename = "D:\\huawei\\data1.txt";
        int[][] flavorInfo = new int[flavorNum][2];
        String[] name = new String[flavorNum];
        BufferedReader data = new BufferedReader(new InputStreamReader(
                new FileInputStream(filename)));
        flavorInfo = new int[flavorNum][2];
        name = new String[flavorNum];
        System.out.println("ss".toUpperCase());
        for (int i = 0; i < flavorNum; i++) {
            // 读取一行数据，数据格式flacor1 1 1
            strbuff = data.readLine();
            // 字符分割
            String[] strcol = strbuff.split(" ");
            flavorInfo[i][0] = Integer.valueOf(strcol[1]);
            flavorInfo[i][1] = Integer.valueOf(strcol[2]);
            name[i] = strcol[0];
        }
        //  answer = getAnswer2(flavorNum,minCpu,minMem,medCpu,medMem, maxCpu, maxMem, flavorInfo, name);;
        long endTime = System.currentTimeMillis(); //获取结束时间

        System.out.println("程序运行时间： " + (endTime - startTime) + "ms");
//        for(String s:answer)
//            System.out.println(s);
//        if(answer.get(0).equals("change")){
//            answer.remove(0);
//            //装填
//        }else{
//            //直接返回结果
//        }
    }
}
