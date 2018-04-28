package com.elasticcloudservice.predict;

import java.io.*;
import java.util.*;

/**
 *
 * @author Team-"自然醒"
 * 解决思路为背包问题的思想
 * input: step1输入至step2的应该包含：三个List,分别对应虚拟机的cpu核数，虚拟机的内存，虚拟机的名字，以及keyword，以及物理机的最大cpu核数和最大内存。
 * keyword为"CPU"或者"MEM";
 */
public class Assignment3 {

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
        //System.out.println(dp[size - 1][max]+"ddddddddddd");
        int w = max;
        if(dp[size-1][max]>=maxV) {
            for( w = w;w>0;w--) {
                for(int k=0;k<dp.length;k++)
                    for(int m=0;m<dp[k].length;m++)
                        dp[k][m] = 0;
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
                if(dp[size-1][w] <=maxV){
                    break;
                }
            }
           // System.out.println(dp[size-1][w]);
        }
        else {
            for (w = w; w > 0; w--) {
                for(int k=0;k<dp.length;k++)
                    for(int m=0;m<dp[k].length;m++)
                        dp[k][m] = 0;
                for (int i = 1; i < size; i++) {
                    for (int j = 1; j <= w; j++) {
                        if (j > weight.get(i)) {
                            if(dp[i - 1][j - weight.get(i)] + value.get(i)<=maxV)
                                dp[i][j] = Math.max(dp[i - 1][j - weight.get(i)] + value.get(i),dp[i - 1][j]);
                            else
                                dp[i][j] = dp[i - 1][j];
                        } else
                            dp[i][j] = dp[i - 1][j];
                    }
                }
               // System.out.println(dp[size - 1][w]);
                if (dp[size - 1][w] <= maxV)
                    break;
            }
            //System.out.println(dp[size - 1][w]+"sssssssss");
        }
//        System.out.println(dp[size-1][max]);
//        if(value.size()<4)
//        {
//            value.clear();
//            return null;
//        }
//        if(dp[size-1][max]<maxV/3) {
//            value.clear();
//            return ;// 最后一个物理服务器待改进
//        }
        int temp = w;
        int labelWeight = 0;
        for(int i=size-1;i>0;i--) {
            if(temp-weight.get(i)>=0 && dp[i][temp] == dp[i-1][temp-weight.get(i)]+value.get(i))
            {
                res[i] = 1;
                //System.out.println(i);
                labelWeight += weight.get(i);
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

        //修正
        if(dp[size-1][w]<maxV && labelWeight<max){
            for(int i = 1;i<value.size();i++){
                if(res[i]!=1){
                    if(dp[size-1][w]+value.get(i)<=maxV && labelWeight+weight.get(i)<=max)
                    {
                        res[i] = 1;
                        dp[size-1][w] = dp[size-1][w]+value.get(i);
                        labelWeight = labelWeight+weight.get(i);
                        if(map.containsKey(name.get(i))) {
                            count = map.get(name.get(i));
                            count = count + 1;
                            map.put(name.get(i), count);
                        }
                        else {
                            map.put(name.get(i), 1);
                        }
                    }
                }
            }
        }

       // System.out.println(dp[size-1][w]+" "+labelWeight);
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

    public static List<Map<String,Integer>> startAssignment(String keyword,int maxCpu,int maxMemory,List<Integer> cpu,List<Integer> memory,List<String> name,List<Integer> preFlavorList){
        //List<String> resultList = new LinkedList<>();
        List<Map<String, Integer>> resultList = new LinkedList<>();
        int maxV;
        int maxW;
        List<Integer> value = new ArrayList<>();
        List<Integer> weight = new ArrayList<>();

        if (keyword != null && keyword.equals("CPU")) {
            value = cpu;
            weight = memory;
            maxW = maxMemory;
            maxV = maxCpu;
        } else {
            value = memory;
            weight = cpu;
            maxW = maxCpu;
            maxV = maxMemory;
        }
        //开始，这个放在这里
        List<String> nameInfo = new ArrayList<>();
        List<Integer> avaliableW = new ArrayList<>();
        List<Integer> avaliableV = new ArrayList<>();
        int key = 0;
        for (int i = 0; i < preFlavorList.size(); i++) {
            if (nameInfo.contains("flavor"+preFlavorList.get(i)))
                continue;
            else {
                nameInfo.add("flavor"+preFlavorList.get(i));
                avaliableV.add(Predict.flavorInfo[preFlavorList.get(i)][0]);
                avaliableW.add(Predict.flavorInfo[preFlavorList.get(i)][1]);
            }
        }
        //结束
        int num = 0;
        while (value.size() > 1) {
            num = num + 1;
            int[][] dp = new int[value.size()][maxW + 1];
            int[] res = new int[value.size()];
            Map<String, Integer> flavorMap = myPackage(value, weight, name, res, dp, maxW, maxV, num);
//            if(flavorLine == null)
//                continue;
//            resultList.add(flavorLine.toString());
            resultList.add(flavorMap);
        }


        //开始，获取到resultList后，对倒数第二个map进行change。
        List<Integer> changeValue = new ArrayList<>();
        List<Integer> changeWeight = new ArrayList<>();
        List<String> changeName = new ArrayList<>();
        if (keyword != null && keyword.equals("CPU")) {
            key = 1;
            if (nameInfo.contains("flavor1")) {
                changeValue.add(1);
                changeWeight.add(1);
                changeName.add("flavor1");
            } else if (nameInfo.contains("flavor2")) {
                changeValue.add(1);
                changeWeight.add(2);
                changeName.add("flavor2");
            } else {
                changeValue.add(1);
                changeWeight.add(4);
                changeName.add("flavor3");
            }

            if (nameInfo.contains("flavor4")) {
                changeValue.add(2);
                changeWeight.add(2);
                changeName.add("flavor4");
            } else if (nameInfo.contains("flavor5")) {
                changeValue.add(2);
                changeWeight.add(4);
                changeName.add("flavor5");
            } else {
                changeValue.add(2);
                changeWeight.add(8);
                changeName.add("flavor6");
            }

            if (nameInfo.contains("flavor7")) {
                changeValue.add(4);
                changeWeight.add(4);
                changeName.add("flavor7");
            } else if (nameInfo.contains("flavor8")) {
                changeValue.add(4);
                changeWeight.add(8);
                changeName.add("flavor8");
            } else {
                changeValue.add(4);
                changeWeight.add(16);
                changeName.add("flavor9");
            }
            if (nameInfo.contains("flavor10")) {
                changeValue.add(8);
                changeWeight.add(8);
                changeName.add("flavor10");
            } else if (nameInfo.contains("flavor11")) {
                changeValue.add(8);
                changeWeight.add(16);
                changeName.add("flavor11");
            } else {
                changeValue.add(8);
                changeWeight.add(32);
                changeName.add("flavor12");
            }
            if (nameInfo.contains("flavor13")) {
                changeValue.add(16);
                changeWeight.add(16);
                changeName.add("flavor13");
            } else if (nameInfo.contains("flavor14")) {
                changeValue.add(16);
                changeWeight.add(32);
                changeName.add("flavor14");
            } else {
                changeValue.add(16);
                changeWeight.add(64);
                changeName.add("flavor15");
            }
        } else {
            key = 4;
            if (nameInfo.contains("flavor1")) {
                changeWeight.add(1);
                changeValue.add(1);
                changeName.add("flavor1");
            }
            if (nameInfo.contains("flavor2")) {
                changeWeight.add(1);
                changeValue.add(2);
                changeName.add("flavor2");
            }
            if (nameInfo.contains("flavor3")) {
                changeWeight.add(1);
                changeValue.add(4);
                changeName.add("flavor3");
            } else if (nameInfo.contains("flavor5")) {
                changeWeight.add(2);
                changeValue.add(4);
                changeName.add("flavor5");
            } else {
                changeWeight.add(4);
                changeValue.add(4);
                changeName.add("flavor7");
            }
            if (nameInfo.contains("flavor6")) {
                changeWeight.add(2);
                changeValue.add(8);
                changeName.add("flavor6");
            } else if (nameInfo.contains("flavor8")) {
                changeWeight.add(4);
                changeValue.add(8);
                changeName.add("flavor8");
            } else {
                changeWeight.add(8);
                changeValue.add(8);
                changeName.add("flavor10");
            }
            if (nameInfo.contains("flavor9")) {
                changeWeight.add(4);
                changeValue.add(16);
                changeName.add("flavor9");
            } else if (nameInfo.contains("flavor11")) {
                changeWeight.add(8);
                changeValue.add(16);
                changeName.add("flavor11");
            } else {
                changeWeight.add(16);
                changeValue.add(16);
                changeName.add("flavor13");
            }
            if (nameInfo.contains("flavor12")) {
                changeWeight.add(8);
                changeValue.add(32);
                changeName.add("flavor12");
            } else {
                changeWeight.add(16);
                changeValue.add(32);
                changeName.add("flavor14");
            }
            if (nameInfo.contains("flavor15")) {
                changeWeight.add(16);
                changeValue.add(64);
                changeName.add("flavor15");
            }
        }
        for (int p = 1; p < 3; p++) {
            if (resultList.size() - p < 0 )
                break;
            List<String> vmName = new ArrayList<>();
            int vmValue = 0;
            int vmWeight = 0;
            Map<String, Integer> map = resultList.get(resultList.size() - p);
            for (Map.Entry<String, Integer> entry : map.entrySet()) {

               // System.out.print(entry.getKey() + " " + entry.getValue() + " ");
              //  System.out.println();
                for (int i = 0; i < entry.getValue(); i++) {
                    vmName.add(entry.getKey());
                    int k = nameInfo.indexOf(entry.getKey());
                    vmValue += avaliableV.get(k);
                    vmWeight += avaliableW.get(k);
                }

            }
            int t = 0;
            List<Integer> index = new ArrayList<>();
            int size = vmName.size();
            for (int j = 4; j > 1; j = j / 4) {
                for (int i = 0; i < size; i++) {
                    int k = nameInfo.indexOf(vmName.get(i));
                    Double b = avaliableV.get(k) * 1.0 / avaliableW.get(k);
                    int s = (int) (key / b);
                    if (s == j) {
                        index.add(i);
                        vmName.add(changeName.get(changeValue.indexOf(avaliableV.get(k))));
                        vmWeight = vmWeight - avaliableW.get(k) + changeWeight.get(changeValue.indexOf(avaliableV.get(k)));
                    }
                }
            }
            Collections.sort(index);
            for (int i = 0; i < index.size(); i++) {
                vmName.remove(index.get(i) - t);
                t++;
            }
            Map<String, Integer> vmMap = new HashMap<>();
            int count;
            for (int i = 0; i < vmName.size(); i++) {
                if (vmMap.containsKey(vmName.get(i))) {
                    count = vmMap.get(vmName.get(i));
                    count = count + 1;
                    vmMap.put(vmName.get(i), count);
                } else {
                    vmMap.put(vmName.get(i), 1);
                }
            }
            for (Map.Entry<String, Integer> entry : vmMap.entrySet()) {
                //System.out.print(entry.getKey() + " " + entry.getValue() + " ");
            }
          //  System.out.println();
         //   System.out.println(resultList);
            resultList.set(resultList.size() - p, vmMap);
          //  System.out.println(resultList);
          //  System.out.println(vmName);
            //结束
        }


        //这个后面放你对resultList的所有map进行装填的代码
        //return refine6(resultList,keyword,maxCpu,maxMemory,preFlavorList);
        //return resultList;
        //return refine7(resultList,keyword,maxV,maxW,avaliableV,avaliableW,nameInfo);
        // 每个方法进行投票，选择利用率最高的方法

        List<List<Map<String,Integer>> > allResult = new ArrayList<>();
        //allResult.add(refine(resultList,keyword,maxCpu,maxMemory,preFlavorList)); // 效果不好

        allResult.add(refine7(resultList,keyword,maxV,maxW,avaliableV,avaliableW,nameInfo));
        allResult.add(refine8(resultList,keyword,maxV,maxW,avaliableV,avaliableW,nameInfo));

        allResult.add(refine2(resultList,keyword,maxCpu,maxMemory,preFlavorList));
        allResult.add(refine3(resultList,keyword,maxCpu,maxMemory,preFlavorList));
        //allResult.add(refine4(resultList,keyword,maxCpu,maxMemory,preFlavorList));
        allResult.add(refine5(resultList,keyword,maxCpu,maxMemory,preFlavorList));   // 效果不好
        //allResult.add(refine6(resultList,keyword,maxCpu,maxMemory,preFlavorList));  // 效果不好


        double[] sores = new double[7];
        double maxScore = 0;
        int index = 0;
        for (int i = 0; i < allResult.size(); i++) {
            sores[i] = getScore(allResult.get(i),keyword,maxCpu,maxMemory);
            System.out.println(i+": " +sores[i]);
            if (sores[i] > maxScore){
                maxScore = sores[i];
                index = i;
            }
        }

        System.out.println("choose method index: " + index);
        return allResult.get(index);

    //return resultList;

    }

    public static <T> List<T> deepCopy(List<T> src) throws IOException, ClassNotFoundException {
        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(byteOut);
        out.writeObject(src);

        ByteArrayInputStream byteIn = new ByteArrayInputStream(byteOut.toByteArray());
        ObjectInputStream in = new ObjectInputStream(byteIn);
        @SuppressWarnings("unchecked")
        List<T> dest = (List<T>) in.readObject();
        return dest;
    }
    /**
     *  计算资源利用率
     * @param resultList
     * @param keyword
     * @param maxCpu
     * @param maxMemory
     * @return
     */
    public  static double getScore(List<Map<String,Integer>> resultList,String keyword,int maxCpu,int maxMemory){
        double score = 0;
        int size = resultList.size();
        boolean isCpu = false;
        if ("CPU".equals(keyword))
            isCpu = true;

        double numerator = 0.0;
        double denominator = 0.0;

        if (isCpu){
            denominator = maxCpu * size;
        }else {
            denominator = maxMemory * size;
        }

        int[][] flavorInfo = Predict.flavorInfo;

        for (int i = 0; i < resultList.size() ; i++) {
            Map<String,Integer> flavorMap = resultList.get(i);

            for (Map.Entry<String,Integer> entry:
                    flavorMap.entrySet()) {
                int flavorNum = Integer.parseInt(entry.getKey().substring(6));
                int count = entry.getValue();
                if (isCpu){
                    numerator += flavorInfo[flavorNum][0]*count;
                }else {
                    numerator += flavorInfo[flavorNum][1]*count;
                }

            }
        }
        score = numerator / denominator;

        return score;
    }
    /**
     * 增加物理机的资源利用率，以当前物理机最小的虚拟机补充， 设置所有虚拟机都补全效果最好，88.646
     * @param resultListInput
     * @param keyword
     * @param maxCpu
     * @param maxMemory
     * @param preFlavorList
     */
    public static List<Map<String,Integer>> refine(List<Map<String,Integer>> resultListInput,String keyword,int maxCpu,int maxMemory,List<Integer> preFlavorList){
        List<Map<String,Integer>> resultList = null;
        try {
            resultList = deepCopy(resultListInput);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        Integer[] flavors = preFlavorList.toArray(new Integer[preFlavorList.size()]);
        Arrays.sort(flavors);

        // 增加所有物理机的资源利用率，以当前物理机最小的虚拟机补充   // 最后两个补全结果 是 88.024 所有补全是 88.076
        for (int i = 0; i < resultList.size() ; i++) {
            Map<String,Integer> lastFlavorMap = resultList.get(resultList.size() - i - 1);

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
            while (useCpu <= maxCpu && useMemory <= maxMemory ){
            //while (useCpu <= maxCpu && useMemory <= maxMemory && addNum < maxCount ){
                useCpu += flavorInfo[minFlavorNum][0];
                useMemory += flavorInfo[minFlavorNum][1]/1024;
                addNum++;
            }
            if (useCpu > maxCpu || useMemory > maxMemory)
                addNum--;

            lastFlavorMap.put("flavor"+minFlavorNum,lastFlavorMap.get("flavor"+minFlavorNum) + addNum);
        }
        return resultList;
    }

    /**
     * 从预测列表小到大补全，设置所有虚拟机都改变，效果最好 87.284
     *
     * @param resultListInput
     * @param keyword
     * @param maxCpu
     * @param maxMemory
     * @param preFlavorList
     */
    public static List<Map<String,Integer>> refine2(List<Map<String,Integer>> resultListInput,String keyword,int maxCpu,int maxMemory,List<Integer> preFlavorList){
//        List<Map<String,Integer>> resultList = new ArrayList<>();
//        resultList.addAll(resultListInput);

        List<Map<String,Integer>> resultList = null;
        try {
            resultList = deepCopy(resultListInput);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        List<Integer> flavorsList = new ArrayList<>();
        flavorsList.addAll(preFlavorList);
        Collections.sort(flavorsList);

        // 增加最后两个物理机的资源利用率，以从小到大的虚拟机补充
        for (int i = 0; i < resultList.size() ; i++) {
            Map<String,Integer> lastFlavorMap = resultList.get(resultList.size() - i - 1);

            int[][] flavorInfo = Predict.flavorInfo;
            int useCpu = 0;
            int useMemory = 0;
            int minFlavorNum = 15;
            int maxCount = 0;
            int[] addFlavor = new int[flavorsList.size()];
            for (Map.Entry<String,Integer> entry:
                    lastFlavorMap.entrySet()) {
                int flavorNum = Integer.parseInt(entry.getKey().substring(6));
                int count = entry.getValue();
                addFlavor[flavorsList.indexOf(flavorNum)] = count;
                useCpu += flavorInfo[flavorNum][0]*count;
                useMemory += flavorInfo[flavorNum][1]/1024*count;
            }

            int flavorIndex = 0; // 从最小的开始逐个加
            int repeat = 0;  // 轮数  最大执行 5 轮
            while (useCpu <= maxCpu && useMemory <= maxMemory  ){
                if (useCpu + flavorInfo[flavorsList.get(flavorIndex)][0] <= maxCpu && useMemory +flavorInfo[flavorsList.get(flavorIndex)][1]/1024 <= maxMemory){
                    useCpu += flavorInfo[flavorsList.get(flavorIndex)][0];
                    useMemory += flavorInfo[flavorsList.get(flavorIndex)][1]/1024;
                    addFlavor[flavorIndex]++;
                    flavorIndex++;
                    if (flavorIndex == flavorsList.size())
                        flavorIndex = 0;
                }else {
                    if (repeat == 5)
                        break;
                    flavorIndex = 0;
                    repeat++;
                }
            }
            for (int j = 0; j < addFlavor.length; j++) {
                if (addFlavor[j] > 0){
                    lastFlavorMap.put("flavor"+flavorsList.get(j),addFlavor[j]);
                }
            }

        }
        return resultList;
    }

    /**
     * 从预测列表大到小逐个补全
     *
     * @param resultListInput
     * @param keyword
     * @param maxCpu
     * @param maxMemory
     * @param preFlavorList
     */
    public static List<Map<String,Integer>> refine3(List<Map<String,Integer>> resultListInput,String keyword,int maxCpu,int maxMemory,List<Integer> preFlavorList){
        List<Map<String,Integer>> resultList = null;
        try {
            resultList = deepCopy(resultListInput);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        List<Integer> flavorsList = new ArrayList<>();
        flavorsList.addAll(preFlavorList);
        Collections.sort(flavorsList);

        // 增加物理机的资源利用率，以从大到小的虚拟机补充
        for (int i = 0; i < resultList.size(); i++) {
            Map<String,Integer> lastFlavorMap = resultList.get(resultList.size() - i - 1);

            int[][] flavorInfo = Predict.flavorInfo;
            int useCpu = 0;
            int useMemory = 0;
            int minFlavorNum = 15;
            int maxCount = 0;
            int[] addFlavor = new int[flavorsList.size()];
            for (Map.Entry<String,Integer> entry:
                    lastFlavorMap.entrySet()) {
                int flavorNum = Integer.parseInt(entry.getKey().substring(6));
                int count = entry.getValue();
                addFlavor[flavorsList.indexOf(flavorNum)] = count;
                useCpu += flavorInfo[flavorNum][0]*count;
                useMemory += flavorInfo[flavorNum][1]/1024*count;
            }

            int flavorIndex = flavorsList.size() -1; // 从最大的开始逐个加
            int repeat = 0;  // 轮数  最大执行 5 轮
            while (useCpu <= maxCpu && useMemory <= maxMemory  ){
                if (useCpu + flavorInfo[flavorsList.get(flavorIndex)][0] <= maxCpu && useMemory +flavorInfo[flavorsList.get(flavorIndex)][1]/1024 <= maxMemory){
                    useCpu += flavorInfo[flavorsList.get(flavorIndex)][0];
                    useMemory += flavorInfo[flavorsList.get(flavorIndex)][1]/1024;
                    addFlavor[flavorIndex]++;
                    flavorIndex--;
                }else {
                    flavorIndex--;
                }

                if (flavorIndex == -1){
                    flavorIndex = flavorsList.size() -1;
                    if (repeat == 5)
                        break;
                    repeat++;
                }
            }
            for (int j = 0; j < addFlavor.length; j++) {
                if (addFlavor[j] > 0){
                    lastFlavorMap.put("flavor"+flavorsList.get(j),addFlavor[j]);
                }
            }

        }
        return resultList;
    }

    /**
     * 从预测列表大到小补全，大的补不上了再补小的
     *
     * @param resultListInput
     * @param keyword
     * @param maxCpu
     * @param maxMemory
     * @param preFlavorList
     */
    public static List<Map<String,Integer>> refine4(List<Map<String,Integer>> resultListInput,String keyword,int maxCpu,int maxMemory,List<Integer> preFlavorList){
        List<Map<String,Integer>> resultList = null;
        try {
            resultList = deepCopy(resultListInput);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        List<Integer> flavorsList = new ArrayList<>();
        flavorsList.addAll(preFlavorList);
        Collections.sort(flavorsList);

        // 增加物理机的资源利用率，以从大到小的虚拟机补充
        for (int i = 0; i < resultList.size(); i++) {
            Map<String,Integer> lastFlavorMap = resultList.get(resultList.size() - i - 1);

            int[][] flavorInfo = Predict.flavorInfo;
            int useCpu = 0;
            int useMemory = 0;
            int minFlavorNum = 15;
            int maxCount = 0;
            int[] addFlavor = new int[flavorsList.size()];
            for (Map.Entry<String,Integer> entry:
                    lastFlavorMap.entrySet()) {
                int flavorNum = Integer.parseInt(entry.getKey().substring(6));
                int count = entry.getValue();
                addFlavor[flavorsList.indexOf(flavorNum)] = count;
                useCpu += flavorInfo[flavorNum][0]*count;
                useMemory += flavorInfo[flavorNum][1]/1024*count;
            }

            int flavorIndex = flavorsList.size() -1; // 从最大的开始加
            int repeat = 0;  // 轮数  最大执行 5 轮
            while (useCpu + flavorInfo[flavorsList.get(0)][0]<= maxCpu && useMemory +flavorInfo[flavorsList.get(0)][1]/1024 <= maxMemory && flavorIndex >=0 ){
                while (useCpu + flavorInfo[flavorsList.get(flavorIndex)][0] <= maxCpu && useMemory +flavorInfo[flavorsList.get(flavorIndex)][1]/1024 <= maxMemory){
                    useCpu += flavorInfo[flavorsList.get(flavorIndex)][0];
                    useMemory += flavorInfo[flavorsList.get(flavorIndex)][1]/1024;
                    addFlavor[flavorIndex]++;
                }
                flavorIndex--;
            }
            for (int j = 0; j < addFlavor.length; j++) {
                if (addFlavor[j] > 0){
                    lastFlavorMap.put("flavor"+flavorsList.get(j),addFlavor[j]);
                }
            }

        }
        return resultList;
    }


    /**
     * 增加物理机的资源利用率，以当前物理机种从小到大的虚拟机补充
     * @param resultListInput
     * @param keyword
     * @param maxCpu
     * @param maxMemory
     * @param preFlavorList
     */
    public static List<Map<String,Integer>> refine5(List<Map<String,Integer>> resultListInput,String keyword,int maxCpu,int maxMemory,List<Integer> preFlavorList){
        List<Map<String,Integer>> resultList = null;
        try {
            resultList = deepCopy(resultListInput);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        List<Integer> flavorsList = null;

        try {
            flavorsList = deepCopy(preFlavorList);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        Collections.sort(flavorsList);

        // 增加所有物理机的资源利用率，以当前物理机最小的虚拟机补充   // 最后两个补全结果 是 88.024 所有补全是 88.076
        int[][] flavorInfo = Predict.flavorInfo;
        for (int i = 0; i < resultList.size() ; i++) {
            Map<String,Integer> lastFlavorMap = resultList.get(resultList.size() - i - 1);
            int[][] addFlavor = new int[lastFlavorMap.size()][2];
            int useCpu = 0;
            int useMemory = 0;
            int maxCount = 0;
            int index = 0;
            for (Map.Entry<String,Integer> entry:
                    lastFlavorMap.entrySet()) {
                int flavorNum = Integer.parseInt(entry.getKey().substring(6));

                int count = entry.getValue();
                if (count > maxCount){
                    maxCount = count;
                }
                addFlavor[index][0] = flavorNum;
                addFlavor[index][1] = count;
                index ++;
                useCpu += flavorInfo[flavorNum][0]*count;
                useMemory += flavorInfo[flavorNum][1]/1024*count;
            }

            index = 0;
            int repeat = 0;
            while (useCpu + flavorInfo[addFlavor[index][0]][0]<= maxCpu && useMemory +flavorInfo[addFlavor[index][0]][1]/1024 <= maxMemory ){

                useCpu += flavorInfo[addFlavor[index][0]][0];
                useMemory += flavorInfo[addFlavor[index][0]][1]/1024;
                addFlavor[index++][1]++;

                if (index == addFlavor.length){
                    index = 0;
                    if (repeat == 5)
                        break;
                    repeat++;
                }
            }

            for (int j = 0; j < addFlavor.length; j++) {
                if (addFlavor[j][1] > 0){
                    lastFlavorMap.put("flavor"+addFlavor[j][0],addFlavor[j][1]);
                }
            }
        }
        return resultList;
    }

    /**
     * 从给定的最优资源列表大到小补全，大的补不上了再补小的
     *
     * @param resultListInput
     * @param keyword
     * @param maxCpu
     * @param maxMemory
     * @param preFlavorList
     */
    public static List<Map<String,Integer>> refine6(List<Map<String,Integer>> resultListInput,String keyword,int maxCpu,int maxMemory,List<Integer> preFlavorList){
        List<Map<String,Integer>> resultList = null;
        try {
            resultList = deepCopy(resultListInput);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        //int[] cpuAdd = {{1,1,1024}, {4,2,2048},{7,4,4096}, {10, 8 ,8192},{13, 16 ,16384}};   // 优化 CPU 时可填补的内容
        //int[] memoryAdd = {{3,1,4096}, {6,2,8192},{9,4,16384}, {12, 8 ,32768},{15, 16 ,65536}};   // 优化 memory 时可填补的内容
        int[] cpuAdd = {1,4,7,10,13};   // 优化 CPU 时可填补的内容
        int[] memoryAdd = {3,6,9,12,15};   // 优化 memory 时可填补的内容
        int[] memoryAgain = {1,2};
        int[][] flavorInfo = Predict.flavorInfo;
        List<Integer> flavorsList = new ArrayList<>();
        flavorsList.addAll(preFlavorList);
        Collections.sort(flavorsList);

        List<Integer> addFlavorList = new ArrayList<>();

        boolean isCpu = false;
        if ("CPU".equals(keyword)){
            for (int i = 0; i < cpuAdd.length; i++) {
                if (preFlavorList.contains(cpuAdd[i])){
                    addFlavorList.add(cpuAdd[i]);
                }
            }
            isCpu = true;
        }else {
            for (int i = 0; i < memoryAdd.length; i++) {
                if (preFlavorList.contains(memoryAdd[i])){
                    addFlavorList.add(memoryAdd[i]);
                }
            }
            for (int i = 0; i < memoryAgain.length; i++) {
                if (preFlavorList.contains(memoryAgain[i])){
                    addFlavorList.add(memoryAgain[i]);
                }
            }
        }

        Collections.sort(addFlavorList);

        if (addFlavorList.size() == 0){
            addFlavorList = flavorsList;
        }
       // System.out.println("addFlavorList: "+addFlavorList.size());
        // 增加物理机的资源利用率，以从大到小的虚拟机补充
        for (int i = 0; i < resultList.size() && i < 0; i++) {
            Map<String,Integer> lastFlavorMap = resultList.get(resultList.size() - i - 1);

            int useCpu = 0;
            int useMemory = 0;
            int[] addFlavor = new int[addFlavorList.size()];
            for (Map.Entry<String,Integer> entry:
                    lastFlavorMap.entrySet()) {
                int flavorNum = Integer.parseInt(entry.getKey().substring(6));
                int count = entry.getValue();
                if (addFlavorList.contains(flavorNum)){
                    addFlavor[addFlavorList.indexOf(flavorNum)] = count;
                }
                useCpu += flavorInfo[flavorNum][0]*count;
                useMemory += flavorInfo[flavorNum][1]/1024*count;
            }
            int flavorIndex = addFlavorList.size() -1; // 从最大的开始加
            int repeat = 0;  // 轮数  最大执行 5 轮
            while (useCpu + flavorInfo[addFlavorList.get(0)][0]<= maxCpu && useMemory +flavorInfo[addFlavorList.get(0)][1]/1024 <= maxMemory && flavorIndex >=0 ){
                while (useCpu + flavorInfo[addFlavorList.get(flavorIndex)][0] <= maxCpu && useMemory +flavorInfo[addFlavorList.get(flavorIndex)][1]/1024 <= maxMemory){
                    useCpu += flavorInfo[addFlavorList.get(flavorIndex)][0];
                    useMemory += flavorInfo[addFlavorList.get(flavorIndex)][1]/1024;
                    addFlavor[flavorIndex]++;
                }
                flavorIndex--;
            }
            for (int j = 0; j < addFlavor.length; j++) {
                if (addFlavor[j] > 0){
                    lastFlavorMap.put("flavor"+addFlavorList.get(j),addFlavor[j]);
                }
            }

        }
        return resultList;
    }
    /**
     *
     * @param resultList 结果集
     * @param keyword 关键字
     * @param maxV 最大Value  例如cpu是keyword，则maxV = 56 maxW = 128 ，反之
     * @param maxW 最大Weight
     * @param avaliableV 能够使用的虚拟机对应的value
     * @param avaliableW 能够使用的虚拟机对应的weight
     * @param nameInfo //和前面的avaliable和VavaliableW相对应  //应该是输入文件，即需要预测的虚拟机，没有重复的。
     * @return
     */
    public static List<Map<String,Integer>> refine7(List<Map<String,Integer>> resultList,String keyword,int maxV,int maxW,List<Integer> avaliableV,List<Integer> avaliableW,List<String> nameInfo){
        List<Map<String,Integer>> resultListInput = null;
        try {
            resultListInput = deepCopy(resultList);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        List<Integer> changeValue = new ArrayList<>();
        List<Integer> changeWeight = new ArrayList<>();
        List<String> changeName = new ArrayList<>();
        List<Integer> useTime = new ArrayList<>();
        if (keyword != null && keyword.equals("CPU")) {

            if (nameInfo.contains("flavor2")) {
                changeValue.add(1);
                changeWeight.add(2);
                changeName.add("flavor2");
            } else if(nameInfo.contains("flavor3")){
                changeValue.add(1);
                changeWeight.add(4);
                changeName.add("flavor3");
            }
            if (nameInfo.contains("flavor1")) {
                changeValue.add(1);
                changeWeight.add(1);
                changeName.add("flavor1");
                //useTime.add(6);
            }

            if (nameInfo.contains("flavor5")) {
                changeValue.add(2);
                changeWeight.add(4);
                changeName.add("flavor5");
            } else  if(nameInfo.contains("flavor6")){
                changeValue.add(2);
                changeWeight.add(8);
                changeName.add("flavor6");
            }
            if (nameInfo.contains("flavor4")) {
                changeValue.add(2);
                changeWeight.add(2);
                changeName.add("flavor4");
            }
            if (nameInfo.contains("flavor8")) {
                changeValue.add(4);
                changeWeight.add(8);
                changeName.add("flavor8");
            } else if(nameInfo.contains("flavor9")){
                changeValue.add(4);
                changeWeight.add(16);
                changeName.add("flavor9");
            }
            if (nameInfo.contains("flavor7")) {
                changeValue.add(4);
                changeWeight.add(4);
                changeName.add("flavor7");
            }

            if (nameInfo.contains("flavor11")) {
                changeValue.add(8);
                changeWeight.add(16);
                changeName.add("flavor11");
            } else if(nameInfo.contains("flavor12")){
                changeValue.add(8);
                changeWeight.add(32);
                changeName.add("flavor12");
            }
            if (nameInfo.contains("flavor10")) {
                changeValue.add(8);
                changeWeight.add(8);
                changeName.add("flavor10");
            }

            if (nameInfo.contains("flavor13")) {
                changeValue.add(16);
                changeWeight.add(16);
                changeName.add("flavor13");
            } else if (nameInfo.contains("flavor14")) {
                changeValue.add(16);
                changeWeight.add(32);
                changeName.add("flavor14");
            }
//            else if(nameInfo.contains("flavor15")){
//                changeValue.add(16);
//                changeWeight.add(64);
//                changeName.add("flavor15");
//            }
        } else {
            if (nameInfo.contains("flavor1")) {
                changeWeight.add(1);
                changeValue.add(1);
                changeName.add("flavor1");
            }

            if(nameInfo.contains("flavor4")){
                changeWeight.add(2);
                changeValue.add(2);
                changeName.add("flavor4");
            }
            if (nameInfo.contains("flavor2")) {
                changeWeight.add(1);
                changeValue.add(2);
                changeName.add("flavor2");
            }
            if (nameInfo.contains("flavor5")) {
                changeWeight.add(2);
                changeValue.add(4);
                changeName.add("flavor5");
            } else if(nameInfo.contains("flavor7")){
                changeWeight.add(4);
                changeValue.add(4);
                changeName.add("flavor7");
            }
            if (nameInfo.contains("flavor3")) {
                changeWeight.add(1);
                changeValue.add(4);
                changeName.add("flavor3");
            }
            if (nameInfo.contains("flavor8")) {
                changeWeight.add(4);
                changeValue.add(8);
                changeName.add("flavor8");
            } else if(nameInfo.contains("flavor10")){
                changeWeight.add(8);
                changeValue.add(8);
                changeName.add("flavor10");
            }
            if (nameInfo.contains("flavor6")) {
                changeWeight.add(2);
                changeValue.add(8);
                changeName.add("flavor6");
            }
            if (nameInfo.contains("flavor11")) {
                changeWeight.add(8);
                changeValue.add(16);
                changeName.add("flavor11");
            } else if(nameInfo.contains("flavor13")){
                changeWeight.add(16);
                changeValue.add(16);
                changeName.add("flavor13");
            }
            if (nameInfo.contains("flavor9")) {
                changeWeight.add(4);
                changeValue.add(16);
                changeName.add("flavor9");
            }
            if (nameInfo.contains("flavor12")) {
                changeWeight.add(8);
                changeValue.add(32);
                changeName.add("flavor12");
            } else if(nameInfo.contains("flavor14")){
                changeWeight.add(16);
                changeValue.add(32);
                changeName.add("flavor14");
            }
            if (nameInfo.contains("flavor15")) {
                changeWeight.add(16);
                changeValue.add(64);
                changeName.add("flavor15");
            }
        }

        for(int i=0;i<resultListInput.size();i++){
            int value = 0;
            int weight = 0;
            Map<String,Integer> FlavorMap = resultListInput.get(i);
            for(Map.Entry<String,Integer>entry : FlavorMap.entrySet()){
                value += avaliableV.get(nameInfo.indexOf(entry.getKey()))*entry.getValue();
                weight += avaliableW.get(nameInfo.indexOf(entry.getKey()))*entry.getValue();
            }
            if(value<maxV && weight<maxW){
                while(true){
                    for(int j=changeName.size()-1;j>=0;j--){
                        if(value+changeValue.get(j)<=maxV && weight+changeWeight.get(j)<=maxW){
                            value += changeValue.get(j);
                            weight += changeWeight.get(j);
                            if(FlavorMap.containsKey(changeName.get(j))){
                                int count = FlavorMap.get(changeName.get(j))+1;
                                FlavorMap.remove(changeName.get(j));
                                FlavorMap.put(changeName.get(j),count);
                            }else{
                                FlavorMap.put(changeName.get(j),1);
                            }
                        }
                    }
                    if(value+changeValue.get(0)>maxV || weight+changeWeight.get(0)>maxW)
                        break;
                }
            }
            resultListInput.set(i,FlavorMap);
        }
        return resultListInput;
    }
    public static List<Map<String,Integer>> refine8(List<Map<String,Integer>> resultList,String keyword,int maxV,int maxW,List<Integer> avaliableV,List<Integer> avaliableW,List<String> nameInfo){
        List<Map<String,Integer>> resultListInput = null;
        try {
            resultListInput = deepCopy(resultList);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        List<Integer> changeValue = new ArrayList<>();
        List<Integer> changeWeight = new ArrayList<>();
        List<String> changeName = new ArrayList<>();
        List<Integer> useTime = new ArrayList<>();
        if (keyword != null && keyword.equals("CPU")) {
            if (nameInfo.contains("flavor1")) {
                changeValue.add(1);
                changeWeight.add(1);
                changeName.add("flavor1");
                //useTime.add(6);
            }
            if (nameInfo.contains("flavor2")) {
                changeValue.add(1);
                changeWeight.add(2);
                changeName.add("flavor2");
            } else if(nameInfo.contains("flavor3")){
                changeValue.add(1);
                changeWeight.add(4);
                changeName.add("flavor3");
            }

            if (nameInfo.contains("flavor4")) {
                changeValue.add(2);
                changeWeight.add(2);
                changeName.add("flavor4");
            }
            if (nameInfo.contains("flavor5")) {
                changeValue.add(2);
                changeWeight.add(4);
                changeName.add("flavor5");
            } else  if(nameInfo.contains("flavor6")){
                changeValue.add(2);
                changeWeight.add(8);
                changeName.add("flavor6");
            }
            if (nameInfo.contains("flavor7")) {
                changeValue.add(4);
                changeWeight.add(4);
                changeName.add("flavor7");
            }
            if (nameInfo.contains("flavor8")) {
                changeValue.add(4);
                changeWeight.add(8);
                changeName.add("flavor8");
            } else if(nameInfo.contains("flavor9")){
                changeValue.add(4);
                changeWeight.add(16);
                changeName.add("flavor9");
            }

            if (nameInfo.contains("flavor10")) {
                changeValue.add(8);
                changeWeight.add(8);
                changeName.add("flavor10");
            }
            if (nameInfo.contains("flavor11")) {
                changeValue.add(8);
                changeWeight.add(16);
                changeName.add("flavor11");
            } else if(nameInfo.contains("flavor12")){
                changeValue.add(8);
                changeWeight.add(32);
                changeName.add("flavor12");
            }


            if (nameInfo.contains("flavor13")) {
                changeValue.add(16);
                changeWeight.add(16);
                changeName.add("flavor13");
            } else if (nameInfo.contains("flavor14")) {
                changeValue.add(16);
                changeWeight.add(32);
                changeName.add("flavor14");
            }
//            else if(nameInfo.contains("flavor15")){
//                changeValue.add(16);
//                changeWeight.add(64);
//                changeName.add("flavor15");
//            }
        } else {
            if (nameInfo.contains("flavor1")) {
                changeWeight.add(1);
                changeValue.add(1);
                changeName.add("flavor1");
            }

            if(nameInfo.contains("flavor4")){
                changeWeight.add(2);
                changeValue.add(2);
                changeName.add("flavor4");
            }
            if (nameInfo.contains("flavor2")) {
                changeWeight.add(1);
                changeValue.add(2);
                changeName.add("flavor2");
            }
            if (nameInfo.contains("flavor3")) {
                changeWeight.add(1);
                changeValue.add(4);
                changeName.add("flavor3");
            }
            if (nameInfo.contains("flavor5")) {
                changeWeight.add(2);
                changeValue.add(4);
                changeName.add("flavor5");
            } else if(nameInfo.contains("flavor7")){
                changeWeight.add(4);
                changeValue.add(4);
                changeName.add("flavor7");
            }
            if (nameInfo.contains("flavor6")) {
                changeWeight.add(2);
                changeValue.add(8);
                changeName.add("flavor6");
            }
            if (nameInfo.contains("flavor8")) {
                changeWeight.add(4);
                changeValue.add(8);
                changeName.add("flavor8");
            } else if(nameInfo.contains("flavor10")){
                changeWeight.add(8);
                changeValue.add(8);
                changeName.add("flavor10");
            }
            if (nameInfo.contains("flavor9")) {
                changeWeight.add(4);
                changeValue.add(16);
                changeName.add("flavor9");
            }
            if (nameInfo.contains("flavor11")) {
                changeWeight.add(8);
                changeValue.add(16);
                changeName.add("flavor11");
            } else if(nameInfo.contains("flavor13")){
                changeWeight.add(16);
                changeValue.add(16);
                changeName.add("flavor13");
            }

            if (nameInfo.contains("flavor12")) {
                changeWeight.add(8);
                changeValue.add(32);
                changeName.add("flavor12");
            } else if(nameInfo.contains("flavor14")){
                changeWeight.add(16);
                changeValue.add(32);
                changeName.add("flavor14");
            }
            if (nameInfo.contains("flavor15")) {
                changeWeight.add(16);
                changeValue.add(64);
                changeName.add("flavor15");
            }
        }

        for(int i=0;i<resultListInput.size();i++){
            int value = 0;
            int weight = 0;
            Map<String,Integer> FlavorMap = resultListInput.get(i);
            for(Map.Entry<String,Integer>entry : FlavorMap.entrySet()){
                value += avaliableV.get(nameInfo.indexOf(entry.getKey()))*entry.getValue();
                weight += avaliableW.get(nameInfo.indexOf(entry.getKey()))*entry.getValue();
            }
            if(value<maxV && weight<maxW){
                while(true){
                    for(int j=0;j<changeName.size();j++){
                        if(value+changeValue.get(j)<=maxV && weight+changeWeight.get(j)<=maxW){
                            value += changeValue.get(j);
                            weight += changeWeight.get(j);
                            if(FlavorMap.containsKey(changeName.get(j))){
                                int count = FlavorMap.get(changeName.get(j))+1;
                                FlavorMap.remove(changeName.get(j));
                                FlavorMap.put(changeName.get(j),count);
                            }else{
                                FlavorMap.put(changeName.get(j),1);
                            }
                        }
                    }
                    if(value+changeValue.get(0)>maxV || weight+changeWeight.get(0)>maxW)
                        break;
                }
            }
            resultListInput.set(i,FlavorMap);
        }
        return resultListInput;
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
