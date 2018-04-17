package com.elasticcloudservice.predict;



import java.util.*;

/**
 * @author Team-"自然醒" 解决思路为背包问题的思想 input:
 *         step1输入至step2的应该包含：三个List,分别对应虚拟机的cpu核数，虚拟机的内存，虚拟机的名字，以及keyword，以及物理机的最大cpu核数和最大内存。
 *         keyword为"CPU"或者"MEM";
 */
public class Assignment2 {

    public static StringBuilder myPackage(List<Integer> value, List<Integer> weight, List<String> name, int[] res,
                                          int[][] dp, int max, int maxV, int num, String keyword, int[] v,int maxFlavor) {

        Map<String, Integer> map = new HashMap<>();
        StringBuilder flavorLine = new StringBuilder();
        int count;
        int size = value.size();
        int t = 0;// 以便于删除list的正确位置，list删除一个值，后面会往前进一
        int z = 0;
        for (int i = 1; i < size; i++) {
            for (int j = 1; j <= max; j++) {
                if (j > weight.get(i)) {
                    // if(dp[i-1][j-weight.get(i)]+value.get(i)<=maxV)
                    dp[i][j] = dp[i - 1][j - weight.get(i)] + value.get(i);
                    // else
                    // dp[i][j] = dp[i-1][j];
                } else
                    dp[i][j] = dp[i - 1][j];
            }
        }

        int w = max;
        if (dp[size - 1][max] > maxV) {
            for (w = w - 1; w > 0; w--) {
                for (int i = 1; i < size; i++) {
                    for (int j = 1; j <= w; j++) {
                        if (j > weight.get(i)) {
                            dp[i][j] = dp[i - 1][j - weight.get(i)] + value.get(i);
                        } else
                            dp[i][j] = dp[i - 1][j];
                    }
                }
                System.out.println(dp[size - 1][w]);
                if (dp[size - 1][w] <= maxV)
                    break;
            }
        }
        System.out.println(dp[size - 1][max]);

        int temp = w;
        String change = "change";
        for (int i = size - 1; i > 0; i--) {
            if (temp - weight.get(i) > 0 && dp[i][temp] == dp[i - 1][temp - weight.get(i)] + value.get(i)) {
                res[i] = 1;
                // System.out.println(i);
                if (map.containsKey(name.get(i))) {
                    count = map.get(name.get(i));
                    count = count + 1;
                    map.put(name.get(i), count);
                } else {
                    map.put(name.get(i), 1);
                }
                temp = temp - weight.get(i);
            }
        }
        if (dp[size - 1][max] < maxV * 2 / 7 && num > 1) {
            System.out.println("start....");
            List<Integer> valueBackup = new ArrayList<>();
            List<Integer> weightBackup = new ArrayList<>();
            List<String> nameBackup = new ArrayList<>();

            if (keyword != null && keyword.equals("CPU")) {
                System.out.println(size);
                for (int i = 1; i < size; i++) {
                    if (res[i] == 1) {
                        if (value.get(i) == 16) {
                            continue;
                            // value.remove(i);
                            // weight.remove(i);
                            // name.remove(i);
                        } else {
                            valueBackup.add(value.get(i));
                            // value.remove(i);
                            weightBackup.add(weight.get(i));
                            // weight.remove(i);
                            nameBackup.add(name.get(i));
                            // name.remove(i);
                        }
                    }
                }
            } else {
                for (int i = 1; i < size; i++) {
                    if (res[i] == 1) {
                        if (value.get(i) == 64) {
                            continue;
                            // value.remove(i);
                            // weight.remove(i);
                            // name.remove(i);
                        } else {
                            valueBackup.add(value.get(i));
                            // value.remove(i);
                            weightBackup.add(weight.get(i));
                            // weight.remove(i);
                            nameBackup.add(name.get(i));
                            // name.remove(i);
                        }
                    }
                }
            }
            if (valueBackup.size() == 1)
                return null;
            for (int i = 0; i < valueBackup.size(); i++) {
                for (int j = 1; j < valueBackup.size() - i; j++) {
                    if (valueBackup.get(j - 1) > valueBackup.get(j)) {
                        int swap1 = valueBackup.get(j - 1);
                        valueBackup.set(j - 1, valueBackup.get(j));
                        valueBackup.set(j, swap1);
                        int swap2 = weightBackup.get(j - 1);
                        weightBackup.set(j - 1, weightBackup.get(j));
                        weightBackup.set(j, swap2);
                        String swap3 = nameBackup.get(j - 1);
                        nameBackup.set(j - 1, nameBackup.get(j));
                        nameBackup.set(j, swap3);
                    }
                    if (valueBackup.get(j - 1) == valueBackup.get(j)) {
                        if (weightBackup.get(j - 1) > weightBackup.get(j)) {
                            int swap1 = valueBackup.get(j - 1);
                            valueBackup.set(j - 1, valueBackup.get(j));
                            valueBackup.set(j, swap1);
                            int swap2 = weightBackup.get(j - 1);
                            weightBackup.set(j - 1, weightBackup.get(j));
                            weightBackup.set(j, swap2);
                            String swap3 = nameBackup.get(j - 1);
                            nameBackup.set(j - 1, nameBackup.get(j));
                            nameBackup.set(j, swap3);
                        }
                    }
                }
            }
            // int[] v = {16,8,4,2,1};
            // int[] v = { 64, 32, 16, 8, 4, 2, 1 };
            List<Integer> label = new ArrayList<>();
            int sum = 0;
            int h = 0;// 记录下进行增加的起始值下标
            int wSum = 0;
            int vSum = 0;
            int wSumBackup = 0;
            //int maxFlavor = 64;
            for (int i = 0; i < v.length; i++) {
                sum = 0;
                if (h >= valueBackup.size() - 2)
                    break;
                for (int j = h; j < valueBackup.size(); j++) {
                    if (j == valueBackup.size() - 1 && sum == 0)
                        break;
                    sum += valueBackup.get(j);
                    wSumBackup += weightBackup.get(j);
                    if (sum == v[i]) {
                        change = change + ":";
                        System.out.println(change);
                        for (int k = h; k <= j; k++) {
                            label.add(k);
                            if (k == j) {
                                wSum += weightBackup.get(k);
                                vSum += valueBackup.get(k);
                                if (wSum > maxFlavor) {
                                    change = change.substring(0, change.length() - 1);
                                    change = change + ";" + (vSum - valueBackup.get(k)) + " "
                                            + (wSum - weightBackup.get(k));
                                    label.remove(label.size() - 1);
                                    h = k;
                                    j = h - 1;
                                    sum = 0;
                                    wSum = 0;
                                    vSum = 0;
                                    wSumBackup = 0;
                                    break;
                                } else {
                                    change = change.substring(0, change.length() - 1);
                                    change = change + nameBackup.get(k) + " " + 1 + ";" + (vSum) + " " + wSum;
                                    sum = 0;
                                    wSum = 0;
                                    vSum = 0;
                                    wSumBackup = 0;
                                }
                            } else {
                                wSum += weightBackup.get(k);
                                vSum += valueBackup.get(k);
                                if (wSum > maxFlavor) {
                                    change = change.substring(0, change.length() - 1);
                                    change = change + ";" + (vSum - valueBackup.get(k)) + " "
                                            + (wSum - weightBackup.get(k)) + ":";
                                    label.remove(label.size() - 1);
                                    h = k;
                                    j = h - 1;
                                    sum = 0;
                                    wSum = 0;
                                    vSum = 0;
                                    wSumBackup = 0;
                                    break;
                                }
                                change = change + nameBackup.get(k) + " " + 1 + " ";
                            }

                        }
                        h = j + 1;
                        sum = 0;
                        wSum = 0;
                        vSum = 0;
                        wSumBackup = 0;
                    }
                    if (sum > v[i]) {
                        change = change + ":";
                        for (int k = h; k < j; k++) {
                            label.add(k);
                            if (k == j - 1) {
                                wSum += weightBackup.get(k);
                                vSum += valueBackup.get(k);
                                if (wSum > maxFlavor) {
                                    change = change.substring(0, change.length() - 1);
                                    change = change + ";" + (vSum - valueBackup.get(k)) + " "
                                            + (wSum - weightBackup.get(k));
                                    label.remove(label.size() - 1);
                                    h = k;
                                    j = h - 1;
                                    sum = 0;
                                    wSum = 0;
                                    vSum = 0;
                                    wSumBackup = 0;
                                    break;
                                } else {
                                    change = change.substring(0, change.length() - 1);
                                    change = change + nameBackup.get(k) + " " + 1 + ";" + (vSum) + " " + wSum;
                                    sum = 0;
                                    wSum = 0;
                                    vSum = 0;
                                    wSumBackup = 0;
                                }
                            } else {
                                wSum += weightBackup.get(k);
                                vSum += valueBackup.get(k);
                                if (wSum > maxFlavor) {
                                    change = change.substring(0, change.length() - 1);
                                    change = change + ";" + (vSum - valueBackup.get(k)) + " "
                                            + (wSum - weightBackup.get(k));
                                    label.remove(label.size() - 1);
                                    h = k;
                                    j = h - 1;
                                    sum = 0;
                                    wSum = 0;
                                    vSum = 0;
                                    wSumBackup = 0;
                                    break;
                                }
                                change = change + nameBackup.get(k) + " " + 1 + " ";
                            }
                        }
                        j--;
                        h = j + 1;
                        sum = 0;
                        wSum = 0;
                        vSum = 0;
                        wSumBackup = 0;
                    }
                    if (j == valueBackup.size() - 1) {
                        if (sum < v[i] && wSumBackup <= maxFlavor) {
                            for (int k = h; k <= j; k++) {
                                label.add(k);
                                wSum += weightBackup.get(k);
                                vSum += valueBackup.get(k);
                                if (k == j) {
                                    change = change + nameBackup.get(k) + " " + 1 + ";" + (vSum) + " " + wSum;
                                } else {
                                    change = change + nameBackup.get(k) + " " + 1 + " ";
                                }
                            }
                            h = valueBackup.size();
                            j = h;
                        } else {
                            sum = 0;
                            wSum = 0;
                            vSum = 0;
                            wSumBackup = 0;
                            continue;
                        }

                    }
                }
            }
            System.out.println(change);
            flavorLine.append(change);
            value.clear();
            return flavorLine;
            //
            // return ;// 最后一个物理服务器待改进
        }

        System.out.print(num + " ");

        flavorLine.append(num + " ");
        int index = 0;

        for (Map.Entry<String, Integer> entry : map.entrySet()) {
            System.out.print(entry.getKey() + " " + entry.getValue() + " ");
            index++;
            if (index == map.size()) {
                flavorLine.append(entry.getKey() + " " + entry.getValue());
            } else {
                flavorLine.append(entry.getKey() + " " + entry.getValue() + " ");
            }

        }
        System.out.println();

        for (int i = 1; i < size; i++) {
            if (res[i] == 1) {
                value.remove(i - t);
                weight.remove(i - t);
                name.remove(i - t);
                t++;
            }
        }
        return flavorLine;
    }

    public static List<String> startAssignment(String keyword, int maxCpu, int maxMemory, List<Integer> cpu,
                                               List<Integer> memory, List<String> name) {
        List<String> resultList = new LinkedList<>();
        int maxV;
        int maxW;
        int maxFlavor=0;
        List<Integer> value = new ArrayList<>();
        List<Integer> weight = new ArrayList<>();
        List<Integer> valueInfo = new ArrayList<>();
        List<Integer> weightInfo = new ArrayList<>();
        List<String> nameInfo = new ArrayList<>();
        List<String> answer = new ArrayList<>();
        List<Integer> avaliableV = new ArrayList<>();
        for(int i=0;i<name.size();i++){
            if(nameInfo.contains(name.get(i)))
                continue;
            else{
                if(keyword != null && keyword.equals("CPU")){
                    if(!avaliableV.contains(cpu.get(i)))
                        avaliableV.add(cpu.get(i));
                    if(maxFlavor<memory.get(i))
                        maxFlavor = memory.get(i);
                    valueInfo.add(cpu.get(i));
                    weightInfo.add(memory.get(i));
                    nameInfo.add(name.get(i));
                }else{
                    if(!avaliableV.contains(memory.get(i)))
                        avaliableV.add(memory.get(i));
                    if(maxFlavor<cpu.get(i))
                        maxFlavor = cpu.get(i);
                    weightInfo.add(cpu.get(i));
                    valueInfo.add(memory.get(i));
                    nameInfo.add(name.get(i));
                }
            }
        }
        Collections.sort(avaliableV);
        Collections.reverse(avaliableV);
        int[] v = new int[avaliableV.size()];
        for(int i=0;i<avaliableV.size();i++)
            v[i] = avaliableV.get(i);
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

        int num = 0;

        while (value.size() > 1) {
            num = num + 1;
            int[][] dp = new int[value.size()][maxW + 1];
            int[] res = new int[value.size()];
            StringBuilder flavorLine = myPackage(value, weight, name, res, dp, maxW, maxV, num, keyword, v,maxFlavor);
            answer.add(flavorLine.toString());
            resultList.add(flavorLine.toString());
        }
        if (answer.get(answer.size() - 1) != null && answer.get(answer.size() - 1).contains("change")) {
            String last = answer.get(answer.size() - 1);
            String[] change = last.split(":");
            List<String> changeName = new ArrayList<>(); // 记录可以更换的虚拟机名字
            List<Integer> changeCount = new ArrayList<>();// 记录可以更换的虚拟机
            for (int i = 1; i < change.length; i++) {
                String[] buffer = change[i].split(";");
                int valueNum = Integer.parseInt(buffer[1].split(" ")[0]);
                int weightNum = Integer.parseInt(buffer[1].split(" ")[1]);
                for (int k = 0; k < valueInfo.size(); k++) {
                    if (valueNum > valueInfo.get(k) || weightInfo.get(k) < weightNum)
                        continue;
                    changeName.add(nameInfo.get(k));
                    changeCount.add(valueInfo.get(k) - valueNum);
                }
                for (int k = 0; k < changeCount.size(); k++) {
                    for (int j = 1; j < changeCount.size() - i; j++) {
                        if (changeCount.get(j + 1) < changeCount.get(j)) {
                            int q = changeCount.get(j + 1);
                            changeCount.set(j + 1, changeCount.get(j));
                            changeCount.set(j, q);
                            String q1 = changeName.get(j + 1);
                            changeName.set(j + 1, changeName.get(j));
                            changeName.set(j, q1);
                        }
                    }
                }
                int key = 0;
                for (int n = 0; n < changeName.size(); n++) {
                    for (int m = 0; m < answer.size() - 1; m++) {
                        if (answer.get(m) != null && answer.get(m).contains(changeName.get(n))) {
                            System.out.println(answer.get(m));
                            String[] getChange = answer.get(m).split(" ");
                            List<String> getChangeList = new ArrayList<>();
                            for (String s : getChange) {
                                getChangeList.add(s);
                            }
                            for (int z = 0; z < getChangeList.size(); z++) {
                                if (getChangeList.get(z) != null && getChangeList.get(z).equals(changeName.get(n))) {
                                    if (Integer.parseInt(getChangeList.get(z + 1)) > 1) {
                                        getChangeList.set(z + 1,
                                                String.valueOf((Integer.parseInt(getChange[z + 1]) - 1)));
                                        getChangeList.add(buffer[0]);
                                    } else {
                                        getChangeList.remove(z);
                                        getChangeList.set(z, buffer[0]);
                                    }
                                    String result = "";
                                    for (int h = 0; h < getChangeList.size(); h++) {
                                        if (h != getChangeList.size() - 1)
                                            result = result + getChangeList.get(h) + " ";
                                        else
                                            result = result + getChangeList.get(h);
                                    }
                                    System.out.println(result);
                                    answer.set(m, result);
                                    key = 1;
                                    break;
                                }
                            }
                        }
                        if (key == 1)
                            break;
                    }
                    if (key == 1)
                        break;
                }
            }
            answer.remove(answer.get(answer.size() - 1));
        }
        return answer;
    }

    public static void main(String[] args) {
        String keyword = "CPU";
        int maxCpu = 56;
        int maxMemory = 128;
        int maxV;
        int maxW;
        List<Integer> value = new ArrayList<>();
        List<Integer> weight = new ArrayList<>();

        List<Integer> cpu = new ArrayList<>();
        List<Integer> memory = new ArrayList<>();
        List<String> name = new ArrayList<>();

        // 例子
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
        memory.add(64);
        name.add("flavor15");
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
        memory.add(64);
        name.add("flavor15");
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
        memory.add(64);
        name.add("flavor15");
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
        memory.add(64);
        name.add("flavor15");
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
        memory.add(64);
        name.add("flavor15");
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
        memory.add(64);
        name.add("flavor15");
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
        // cpu.add(8);
        // memory.add(8);
        // name.add("flavor10");
        // cpu.add(8);
        // memory.add(16);
        // name.add("flavor11");
        // cpu.add(8);
        // memory.add(32);
        // name.add("flavor12");
        // cpu.add(2);
        // memory.add(8);
        // name.add("flavor6");
        // cpu.add(4);
        // memory.add(4);
        // name.add("flavor7");
        // cpu.add(4);
        // memory.add(8);
        // name.add("flavor8");
        // cpu.add(4);
        // memory.add(16);
        // name.add("flavor9");
        //
        // cpu.add(16);
        // memory.add(16);
        // name.add("flavor13");
        // cpu.add(16);
        // memory.add(32);
        // name.add("flavor14");
        // cpu.add(16);
        // memory.add(64);
        // name.add("flavor15");
        // cpu.add(1);
        // memory.add(1);
        // name.add("flavor1");
        // cpu.add(1);
        // memory.add(2);
        // name.add("flavor2");
        // cpu.add(1);
        // memory.add(4);
        // name.add("flavor3");
        // cpu.add(2);
        // memory.add(2);
        // name.add("flavor4");
        // cpu.add(2);
        // memory.add(4);
        // name.add("flavor5");
        // cpu.add(8);
        // memory.add(8);
        // name.add("flavor10");
        // cpu.add(8);
        // memory.add(16);
        // name.add("flavor11");
        // cpu.add(8);
        // memory.add(32);
        // name.add("flavor12");
        // cpu.add(2);
        // memory.add(8);
        // name.add("flavor6");
        // cpu.add(4);
        // memory.add(4);
        // name.add("flavor7");
        // cpu.add(4);
        // memory.add(8);
        // name.add("flavor8");
        // cpu.add(4);
        // memory.add(16);
        // name.add("flavor9");
        //
        // cpu.add(16);
        // memory.add(16);
        // name.add("flavor13");
        // cpu.add(16);
        // memory.add(32);
        // name.add("flavor14");
        // cpu.add(16);
        // memory.add(64);
        // name.add("flavor15");
        //
        // cpu.add(1);
        // memory.add(1);
        // name.add("flavor1");
        // cpu.add(1);
        // memory.add(2);
        // name.add("flavor2");
        // cpu.add(1);
        // memory.add(4);
        // name.add("flavor3");
        // cpu.add(2);
        // memory.add(2);
        // name.add("flavor4");
        // cpu.add(2);
        // memory.add(4);
        // name.add("flavor5");

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
        int[] v = { 16, 8, 4, 2, 1 };
        // int[] v = { 64, 32, 16, 8, 4, 2, 1 };
        int num = 0;
        List<String> answer = new ArrayList<>();
        int maxFlavor=64;
        while (value.size() > 1) {
            num = num + 1;
            int[][] dp = new int[value.size()][maxW + 1];
            int[] res = new int[value.size()];
            StringBuilder flavorLine = myPackage(value, weight, name, res, dp, maxW, maxV, num, keyword, v,maxFlavor);
            answer.add(flavorLine.toString());
            System.out.println(flavorLine);
        }
        List<Integer> valueInfo = new ArrayList<>();
        List<Integer> weightInfo = new ArrayList<>();
        List<String> nameInfo = new ArrayList<>();
        // weightInfo.add(1);
        // valueInfo.add(1);
        // nameInfo.add("flavor1");
        // weightInfo.add(1);
        // valueInfo.add(2);
        // nameInfo.add("flavor2");
        // weightInfo.add(1);
        // valueInfo.add(4);
        // nameInfo.add("flavor3");
        // weightInfo.add(2);
        // valueInfo.add(2);
        // nameInfo.add("flavor4");
        // weightInfo.add(2);
        // valueInfo.add(4);
        // nameInfo.add("flavor5");
        // weightInfo.add(2);
        // valueInfo.add(8);
        // nameInfo.add("flavor6");
        // weightInfo.add(4);
        // valueInfo.add(4);
        // nameInfo.add("flavor7");
        // weightInfo.add(4);
        // valueInfo.add(8);
        // nameInfo.add("flavor8");
        // weightInfo.add(4);
        // valueInfo.add(16);
        // nameInfo.add("flavor9");
        // weightInfo.add(8);
        // valueInfo.add(8);
        // nameInfo.add("flavor10");
        // weightInfo.add(8);
        // valueInfo.add(16);
        // nameInfo.add("flavor11");
        // weightInfo.add(8);
        // valueInfo.add(32);
        // nameInfo.add("flavor12");
        // weightInfo.add(16);
        // valueInfo.add(16);
        // nameInfo.add("flavor13");
        // weightInfo.add(16);
        // valueInfo.add(32);
        // nameInfo.add("flavor14");
        // weightInfo.add(16);
        // valueInfo.add(64);
        // nameInfo.add("flavor15");

        valueInfo.add(1);
        weightInfo.add(1);
        nameInfo.add("flavor1");
        valueInfo.add(1);
        weightInfo.add(2);
        nameInfo.add("flavor2");
        valueInfo.add(1);
        weightInfo.add(4);
        nameInfo.add("flavor3");
        valueInfo.add(2);
        weightInfo.add(2);
        nameInfo.add("flavor4");
        valueInfo.add(2);
        weightInfo.add(4);
        nameInfo.add("flavor5");
        valueInfo.add(2);
        weightInfo.add(8);
        nameInfo.add("flavor6");
        valueInfo.add(4);
        weightInfo.add(4);
        nameInfo.add("flavor7");
        valueInfo.add(4);
        weightInfo.add(8);
        nameInfo.add("flavor8");
        valueInfo.add(4);
        weightInfo.add(16);
        nameInfo.add("flavor9");
        valueInfo.add(8);
        weightInfo.add(8);
        nameInfo.add("flavor10");
        valueInfo.add(8);
        weightInfo.add(16);
        nameInfo.add("flavor11");
        valueInfo.add(8);
        weightInfo.add(32);
        nameInfo.add("flavor12");
        valueInfo.add(16);
        weightInfo.add(16);
        nameInfo.add("flavor13");
        valueInfo.add(16);
        weightInfo.add(32);
        nameInfo.add("flavor14");
        valueInfo.add(16);
        weightInfo.add(65);
        nameInfo.add("flavor15");
        if (answer.get(answer.size() - 1) != null && answer.get(answer.size() - 1).contains("change")) {
            String last = answer.get(answer.size() - 1);
            String[] change = last.split(":");
            List<String> changeName = new ArrayList<>(); // 记录可以更换的虚拟机名字
            List<Integer> changeCount = new ArrayList<>();// 记录可以更换的虚拟机
            for (int i = 1; i < change.length; i++) {
                String[] buffer = change[i].split(";");
                int valueNum = Integer.parseInt(buffer[1].split(" ")[0]);
                int weightNum = Integer.parseInt(buffer[1].split(" ")[1]);
                for (int k = 0; k < valueInfo.size(); k++) {
                    if (valueNum > valueInfo.get(k) || weightInfo.get(k) < weightNum)
                        continue;
                    changeName.add(nameInfo.get(k));
                    changeCount.add(valueInfo.get(k) - valueNum);
                }
                for (int k = 0; k < changeCount.size(); k++) {
                    for (int j = 1; j < changeCount.size() - i; j++) {
                        if (changeCount.get(j + 1) < changeCount.get(j)) {
                            int q = changeCount.get(j + 1);
                            changeCount.set(j + 1, changeCount.get(j));
                            changeCount.set(j, q);
                            String q1 = changeName.get(j + 1);
                            changeName.set(j + 1, changeName.get(j));
                            changeName.set(j, q1);
                        }
                    }
                }
                int key = 0;
                for (int n = 0; n < changeName.size(); n++) {
                    for (int m = 0; m < answer.size() - 1; m++) {
                        if (answer.get(m) != null && answer.get(m).contains(changeName.get(n))) {
                            System.out.println(answer.get(m));
                            String[] getChange = answer.get(m).split(" ");
                            List<String> getChangeList = new ArrayList<>();
                            for (String s : getChange) {
                                getChangeList.add(s);
                            }
                            for (int z = 0; z < getChangeList.size(); z++) {
                                if (getChangeList.get(z) != null && getChangeList.get(z).equals(changeName.get(n))) {
                                    if (Integer.parseInt(getChangeList.get(z + 1)) > 1) {
                                        getChangeList.set(z + 1,
                                                String.valueOf((Integer.parseInt(getChange[z + 1]) - 1)));
                                        getChangeList.add(buffer[0]);
                                    } else {
                                        getChangeList.remove(z);
                                        getChangeList.set(z, buffer[0]);
                                    }
                                    String result = "";
                                    for (int h = 0; h < getChangeList.size(); h++) {
                                        if (h != getChangeList.size() - 1)
                                            result = result + getChangeList.get(h) + " ";
                                        else
                                            result = result + getChangeList.get(h);
                                    }
                                    System.out.println(result);
                                    answer.set(m, result);
                                    key = 1;
                                    break;
                                }
                            }
                        }
                        if (key == 1)
                            break;
                    }
                    if (key == 1)
                        break;
                }
            }
            answer.remove(answer.get(answer.size() - 1));
        }
        for (int i = 0; i < answer.size(); i++)
            System.out.println(answer.get(i));
    }
}
