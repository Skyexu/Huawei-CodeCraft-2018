package com.elasticcloudservice.predict;

import java.io.*;
import java.util.*;

public class GetAns {
    /**
     *
     * @param flavorNum 虚拟机个数
     * @param maxCpu //物理机最大cpu
     * @param maxMem//物理机最大mem
     * @param keyWord//"CPU" or" MEM"
     * @param flavorInfo//二维数组，虚拟机的属性 [falavorNum][2]  ,[falavorNum][0] = cpu [falavorNum][1] = mem mem为（1，2，4，8，16，32，64）即已除以1024;
     * @param name//一维数组，依次对应上述的虚拟机属性
     * @return 返回的是结果List<String> answer,如果answer.get(0).equals("change"),则需要装填，否则直接返回结果即可。
     *
     * 可以调试的参数为temp1、temp2、temp3，大于temp1代表继续粒子群算法，大于temp2代表需要装填，如果利用率为100%，但是虚拟机丢弃率大于temp3,则取下一个粒子群的结果并装填。
     */
    public static  List<Map<String,Integer>>  getAnswer(int flavorNum, int maxCpu, int maxMem, String keyWord, int[][] flavorInfo, String[] name){
        List<Map<String,Integer>> resultList = new LinkedList<>();

        List<int[][]> list1 = new ArrayList<>();
        List<Double> list2 = new ArrayList<>();
        List<Integer> list3 = new ArrayList<>();
        int[][] ans={};
        int machineNum =0;
        double temp1 = 0.96;
        double temp2 = 0.9;
        double temp3 = 0.20;
        int temp = 0;
        int i,j,k;
        List<String> answer = new ArrayList<>();
        int num = 0;
        int num1 = 0;
        int num2 = 0;
        for(i = 0;i<flavorNum;i++){

            if(keyWord.toUpperCase().equals("CPU")){
                num += flavorInfo[i][0];
            }
            else {
                num += flavorInfo[i][1];
            }
            num1 +=flavorInfo[i][0];
            num2 +=flavorInfo[i][1];
        }
        num1 = num1/maxCpu;
        num2 = num2/maxMem;
        int maxNum = 0;
        int minNum = 0;
//        if(keyWord.toUpperCase().equals("CPU")){
//            num = num/maxCpu + 1;
//            maxNum = num + 1;
//            minNum = num - 1;
//        }else{
//            num = num/maxMem + 1;
//            maxNum = num + 1;
//            minNum = num - 1;
//        }
        Result result = new Result();
        if(num1 >num2){
            minNum = num1+1;
        }else{
            minNum = num2+1;
        }
        minNum = minNum -1;
        if(minNum<=0){
            minNum = 1;
        }
        for (i = minNum;;i++) {
            PSO pso = new PSO(flavorNum, 30, 200, 0.5f, i, maxCpu, maxMem, keyWord, flavorInfo, name);
            result = pso.solve();
            pso = null;
            System.gc();
            machineNum = i;
            ans = result.getPgd();
            temp = 0;
            for(j=0;j<flavorNum;j++) {
                for (k = 0; k< machineNum; k++) {
                    if (ans[k][j] == 1)
                        break;
                }
                if(k == machineNum)
                    temp++;
            }
            double key = temp*1.0/flavorNum;
            if(key == 0 ){
                list1.add(ans);
                list2.add(result.getvPgd()*(1-key));
                list3.add(i);
                break;
            }
            if(result.getvPgd()*(1-key) <0.7 && flavorNum>240)
                i++;
//            if(key <= 0.08 ){
//                machineNum = i;
//                ans = result.getPgd();
//                break;
//            }

            if(key<=temp3){   //如果被删除的虚拟机数量大于20%，则忽略。
                list1.add(ans);
                list2.add(result.getvPgd()*(1-key));
                list3.add(i);
            }
        }
        if(list1.size() == 0){
            ans = result.getPgd();
            machineNum = i-1;
        }else{
            double max = 0;
            int index = 0;
            for(i = 0;i<list2.size();i++){
                if(max <list2.get(i)){
                    index = i;
                    max = list2.get(i);
                }
            }
            ans = list1.get(index);
            machineNum = list3.get(index);
        }

        for (i = 0; i < machineNum; i++) {
            Map<String, Integer> map = new HashMap<>();
            //StringBuilder flavorLine = new StringBuilder();
            //flavorLine.append(i + 1 + " ");
            //System.out.print(i + 1 + " ");
            for (j = 0; j < flavorNum; j++) {
                if (ans[i][j] == 1) {
                    if (map.containsKey(name[j])) {
                        int count = map.get(name[j]) + 1;
                        map.remove(name[j]);
                        map.put(name[j], count);
                    } else {
                        map.put(name[j], 1);
                    }
                }
            }
            resultList.add(map);
//            System.out.print(i+" ");
//            for (Map.Entry<String, Integer> entry : map.entrySet()) {
//                System.out.print(entry.getKey() + " " + entry.getValue() + " ");
//                //  flavorLine.append(entry.getKey() + " " + entry.getValue() + "\t");
//            }
//            System.out.println();
//            System.out.println();
//            map.clear();
//            System.out.println(flavorLine.toString());
//            answer.add(flavorLine.toString());
        }
        // return answer;
        return resultList;
    }


    public static List<Map<String,Integer>> changeAnswer(List<String> answer){
        List<Map<String,Integer>> resultList = new LinkedList<>();
        for (String answerStr: answer) {
            Map<String,Integer> map = new HashMap<>();
            String[] flavorStrs = answerStr.split("\t");
            for (String fStr: flavorStrs) {
                String[] fStrs = fStr.split(" ");
                map.put(fStrs[0],Integer.parseInt(fStrs[1]));
            }
            resultList.add(map);
        }

        return resultList;
    }

    public static List<Map<String,Integer>> startAssignment(int flavorNum, int maxCpu, int maxMem, String keyWord, int[][] flavorInfo, String[] name,List<Integer> preFlavorList){
        List<Map<String, Integer>> resultList ;
        //List<String> answer  = getAnswer(flavorNum,maxCpu, maxMem, keyWord, flavorInfo, name);
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
        resultList = getAnswer(flavorNum,maxCpu, maxMem, keyWord, flavorInfo, name);
        resultList = Assignment3.refine2(resultList,keyWord,maxCpu,maxMem,preFlavorList);
        return resultList;
/*


        //resultList = Assignment3.refine7(resultList,keyWord,maxCpu,maxMem,avaliableV,avaliableW,nameInfo);
        // return resultList;

        // resultList = changeAnswer(answer);
        //resultList = Assignment3.refine2(resultList,keyWord,maxCpu,maxMem,preFlavorList);

        List<List<Map<String,Integer>> > allResult = new ArrayList<>();
        //allResult.add(refine(resultList,keyword,maxCpu,maxMemory,preFlavorList)); // 效果不好




        allResult.add(Assignment3.refine7(resultList,keyWord,maxCpu,maxMem,avaliableV,avaliableW,nameInfo));
        allResult.add(Assignment3.refine2(resultList,keyWord,maxCpu,maxMem,preFlavorList));
        allResult.add(Assignment3.refine8(resultList,keyWord,maxCpu,maxMem,avaliableV,avaliableW,nameInfo));
        allResult.add(Assignment3.refine3(resultList,keyWord,maxCpu,maxMem,preFlavorList));

        double[] sores = new double[7];
        double maxScore = 0;
        int index = 0;
        for (int i = 0; i < allResult.size(); i++) {
            sores[i] = Assignment3.getScore(allResult.get(i),keyWord,maxCpu,maxMem);
            System.out.println(i+": " +sores[i]);
            if (sores[i] > maxScore){
                maxScore = sores[i];
                index = i;
            }
        }

        System.out.println("choose method index: " + index);
        return allResult.get(index);
*/
    }
    public static void main(String[] args) throws IOException {
        long startTime=System.currentTimeMillis();   //获取开始时间
        List<Map<String,Integer>> answer = new ArrayList<>();
        String strbuff;
        int flavorNum = 100;
        int maxCpu = 56;
        int maxMem = 128;
        String keyWord = "MEM";
        String filename = "E:\\huawei\\data1.txt";
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
        answer = getAnswer(flavorNum,maxCpu, maxMem, keyWord, flavorInfo, name);
        long endTime=System.currentTimeMillis(); //获取结束时间

        System.out.println("程序运行时间： "+(endTime-startTime)+"ms");
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
