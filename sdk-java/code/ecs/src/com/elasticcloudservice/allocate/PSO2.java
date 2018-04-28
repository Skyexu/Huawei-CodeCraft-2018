package com.elasticcloudservice.allocate;


import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;


public class PSO2 {
    private int bestNum;
    private double w;
    private int c1 = 2;
    private int c2 = 2;
    private int MAX_GEN;// 迭代次数
    private int scale;// 种群规模

    private int flavorNum; // 虚拟机数量，编码长度
    private int maxMachineNum;//最大物理机数量
    private int medMachineNum;//中间物理机数量
    private int minMachineNum;//最小物理机数量
    private int machineNum;//最小物理机数量
    private int t;// 当前代数

    private int[][] flavorInfo; // 虚拟机信息

    private byte[][][] oPopulation;// 粒子群
    private float[][][] flavorV;// 初始速度

    private byte[][][] Pd;// 一颗粒子历代中出现最好的解，
    private double[] vPd;// 解的评价值

    private byte[][] Pgd;// 整个粒子群经历过的的最好的解，每个粒子都能记住自己搜索到的最好解
    private double vPgd;// 最好的解的评价值
    private int bestT;// 最佳出现代数
    private String[] name;
    private double[] fitness;// 种群适应度，表示种群中各个个体的适应度
    private int maxV = 5; //最大速度
    private int minV = -5; //最小速度
    private Random random;

    private int maxCpu; //最大物理机cpu
    private int maxMem; //最大物理机mem
    private int medCpu; //最大物理机cpu
    private int medMem; //最大物理机mem
    private int minCpu; //最大物理机cpu
    private int minMem; //最大物理机mem

    private String keyWord;

    public PSO2() {

    }

    /**
     * constructor of GA
     *
     * @param n 虚拟机数量
     * @param g 运行代数
     * @param w 权重
     **/
    public PSO2(int n, int g, int s, float w, int min, int med, int max, int j, int k, int l, int m, int o, int p, int[][] info, String[] name) {
        this.flavorNum = n;
        this.MAX_GEN = g;
        this.scale = s;
        this.w = w;
        this.maxMachineNum = max;
        this.medMachineNum = med;
        this.minMachineNum = min;
        this.machineNum = max + med + min;
        this.minCpu = j;
        this.minMem = k;
        this.medCpu = l;
        this.medMem = m;
        this.maxCpu = o;
        this.maxMem = p;
        this.flavorInfo = info;
        this.name = name;
    }

    /**
     * 初始化PSO算法类
     *
     * @throws IOException
     */

    private void init() {

//        int []aaa= new int[7000000];
//
//        int []bbb= new int[flavorNum];

        oPopulation = new byte[scale][machineNum][flavorNum];
//        int a1 = 1;
//        while (a1==1){
//
//        }
        flavorV = new float[scale][machineNum][flavorNum];

        fitness = new double[scale];
//        int a1 = 1;
//        while (a1==1){
//
//        }
        Pd = new byte[scale][machineNum][flavorNum];
        vPd = new double[scale];

        /*
         * for(int i=0;i<scale;i++) { vPd[i]=Integer.MAX_VALUE; }
         */

        Pgd = new byte[machineNum][flavorNum];
        vPgd = Integer.MIN_VALUE;

        // nPopulation = new int[scale][cityNum];

        bestT = 0;
        t = 0;

        random = new Random(System.currentTimeMillis());
        /*
         * for(int i=0;i<cityNum;i++) { for(int j=0;j<cityNum;j++) {
         * System.out.print(distance[i][j]+","); } System.out.println(); }
         */

    }

    // 初始化种群，多种随机生成办法
    void initGroup() {
        int i, j, k;
        for (k = 0; k < scale; k++)// 种群数
        {
            for (i = 0; i < flavorNum; i++) {
                j = Math.abs(random.nextInt()) % machineNum;
                oPopulation[k][j][i] = 1;
            }
        }

//        for (i = 0; i < machineNum; i++) {
//            for (j = 0; j < flavorNum; j++)
//                System.out.print(oPopulation[0][i][j]);
//            System.out.println();
//        }

        /*
         * for(i=0;i<scale;i++) { for(j=0;j<cityNum;j++) {
         * System.out.print(oldPopulation[i][j]+","); } System.out.println(); }
         */
    }

    void change() {
        int i, j, k, cpu, mem;
        List<Integer> machineIndex = new ArrayList<>();
        List<Integer> flavorIndex = new ArrayList<>();
        List<Integer> list = new ArrayList<>();
        int[] mCpu = new int[machineNum];
        int[] mMem = new int[machineNum];
        int temp = 0;
        for (i = 0; i < scale; i++) {
            int c=0;
            for(j=0;j<flavorNum;j++) {
                for (k = 0; k < machineNum; k++) {
                    if (oPopulation[i][k][j] == 1)
                        c++;
                }
                if(c >1)
                    System.out.println("wrong~~~~~~~~~~~~~~~~~~~~");
                c = 0;
            }
            for (j = 0; j < minMachineNum; j++) {
                cpu = 0;
                mem = 0;
                for (k = 0; k < flavorNum; k++) {
                    if (oPopulation[i][j][k] == 1) {
                        cpu += flavorInfo[k][0];
                        mem += flavorInfo[k][1];
                    }
                }
                if (cpu > minCpu || mem > minMem) {
                    for (k = 0; k < flavorNum; k++) {
                        if (oPopulation[i][j][k] == 1) {
                            if ((cpu - flavorInfo[k][0]) < minCpu && (mem - flavorInfo[k][1]) < minMem) {
                                oPopulation[i][j][k] = 0;
                                flavorIndex.add(k);
                                break;
                            }
                            oPopulation[i][j][k] = 0;
                            flavorIndex.add(k);
                            cpu -= flavorInfo[k][0];
                            mem -= flavorInfo[k][1];
                        }
                    }
                } else {
                    machineIndex.add(j);
                    mCpu[temp] = cpu;
                    mMem[temp] = mem;
                    temp++;
                }
            }
            for (j = minMachineNum; j < (minMachineNum + medMachineNum); j++) {
                cpu = 0;
                mem = 0;
                for (k = 0; k < flavorNum; k++) {
                    if (oPopulation[i][j][k] == 1) {
                        cpu += flavorInfo[k][0];
                        mem += flavorInfo[k][1];
                    }
                }
                if (cpu > medCpu || mem > medMem) {
                    for (k = 0; k < flavorNum; k++) {
                        if (oPopulation[i][j][k] == 1) {
                            if ((cpu - flavorInfo[k][0]) < medCpu && (mem - flavorInfo[k][1]) < medMem) {
                                oPopulation[i][j][k] = 0;
                                flavorIndex.add(k);
                                break;
                            }
                            oPopulation[i][j][k] = 0;
                            flavorIndex.add(k);
                            cpu -= flavorInfo[k][0];
                            mem -= flavorInfo[k][1];
                        }
                    }
                } else {
                    machineIndex.add(j);
                    mCpu[temp] = cpu;
                    mMem[temp] = mem;
                    temp++;
                }
            }
            for (j = (minMachineNum + medMachineNum); j < machineNum; j++) {
                cpu = 0;
                mem = 0;
                for (k = 0; k < flavorNum; k++) {
                    if (oPopulation[i][j][k] == 1) {
                        cpu += flavorInfo[k][0];
                        mem += flavorInfo[k][1];
                    }
                }
                if (cpu > maxCpu || mem > maxMem) {
                    for (k = 0; k < flavorNum; k++) {
                        if (oPopulation[i][j][k] == 1) {
                            if ((cpu - flavorInfo[k][0]) < maxCpu && (mem - flavorInfo[k][1]) < maxMem) {
                                oPopulation[i][j][k] = 0;
                                flavorIndex.add(k);
                                break;
                            }
                            oPopulation[i][j][k] = 0;
                            flavorIndex.add(k);
                            cpu -= flavorInfo[k][0];
                            mem -= flavorInfo[k][1];
                        }
                    }
                } else {
                    machineIndex.add(j);
                    mCpu[temp] = cpu;
                    mMem[temp] = mem;
                    temp++;
                }
            }
//            if(keyWord.equals("CPU")){
//                for (int m = 0; m < flavorIndex.size(); m++)
//                    for (int n = 0; n < flavorIndex.size() - 1 - m; n++) {
//                        if(flavorInfo[flavorIndex.get(n)][0]<flavorInfo[flavorIndex.get(n+1)][0] || (flavorInfo[flavorIndex.get(n)][0]==flavorInfo[flavorIndex.get(n+1)][0] && flavorInfo[flavorIndex.get(n)][1]<flavorInfo[flavorIndex.get(n+1)][1])){
//                            int t = flavorIndex.get(n);
//                            flavorIndex.set(n,flavorIndex.get(n+1));
//                            flavorIndex.set(n+1,t);
//                        }
//                    }
//            }else {
//                for (int m = 0; m < flavorIndex.size(); m++)
//                    for (int n = 0; n < flavorIndex.size() - 1 - m; n++) {
//                        if(flavorInfo[flavorIndex.get(n)][1]<flavorInfo[flavorIndex.get(n+1)][1] || (flavorInfo[flavorIndex.get(n)][1]==flavorInfo[flavorIndex.get(n+1)][1] && flavorInfo[flavorIndex.get(n)][0]<flavorInfo[flavorIndex.get(n+1)][0])){
//                            int t = flavorIndex.get(n);
//                            flavorIndex.set(n,flavorIndex.get(n+1));
//                            flavorIndex.set(n+1,t);
//                        }
//                    }
//            }

            for (int m = 0; m < machineIndex.size(); m++) {
                //list = judege(machineIndex.get(m));
                int jCpu = 0;
                int jMem = 0;
                if(machineIndex.get(m)<minMachineNum ){
                    jCpu = minCpu;
                    jMem = minMem;
                }else if(machineIndex.get(m)<(minMachineNum+medMachineNum)){
                    jCpu = medCpu;
                    jMem = medMem;
                }else {
                    jCpu = maxCpu;
                    jMem = maxMem;
                }
                for (int n = flavorIndex.size() - 1; n >= 0; n--) {
                    if (flavorIndex.get(n) == -1)
                        continue;
                    else if ((mCpu[m] + flavorInfo[flavorIndex.get(n)][0]) >= jCpu || (mMem[m] + flavorInfo[flavorIndex.get(n)][1]) >= jMem)
                        continue;
                    else {
                        mCpu[m] += flavorInfo[flavorIndex.get(n)][0];
                        mMem[m] += flavorInfo[flavorIndex.get(n)][1];
                        oPopulation[i][machineIndex.get(m)][flavorIndex.get(n)] = 1;
                        flavorIndex.set(n, -1);
                    }
                }
            }
            for (j = 0; j < minMachineNum; j++) {
                cpu = 0;
                mem = 0;
                for (k = 0; k < flavorNum; k++) {
                    if (oPopulation[i][j][k] == 1) {
                        cpu += flavorInfo[k][0];
                        mem += flavorInfo[k][1];
                    }
                }
                if(cpu>minCpu || mem >minMem)
                    System.out.println("wrong"+"sssssssssss");
            }
            for (j = minMachineNum; j < (minMachineNum+medMachineNum); j++) {
                cpu = 0;
                mem = 0;
                for (k = 0; k < flavorNum; k++) {
                    if (oPopulation[i][j][k] == 1) {
                        cpu += flavorInfo[k][0];
                        mem += flavorInfo[k][1];
                    }
                }
                if(cpu>medCpu || mem >medMem)
                    System.out.println("wrong"+"ddddddddddddddddd");
            }
            for (j = (minMachineNum+medMachineNum); j < machineNum; j++) {
                cpu = 0;
                mem = 0;
                for (k = 0; k < flavorNum; k++) {
                    if (oPopulation[i][j][k] == 1) {
                        cpu += flavorInfo[k][0];
                        mem += flavorInfo[k][1];
                    }
                }
                if(cpu>maxCpu || mem >maxMem)
                    System.out.println("wrong"+"gggggggggggggggggg");
            }
            temp = 0;
            machineIndex.clear();
            flavorIndex.clear();
        }
    }

    void initListV() {

        for (int i = 0; i < scale; i++) {
            for (int j = 0; j < machineNum; j++) {
                for (int k = 0; k < flavorNum; k++) {
                    flavorV[i][j][k] = (float)(minV + Math.random() * (maxV - minV));
                }
            }
        }
    }

    public List<Integer> judege(int i) {
        List<Integer> list = new ArrayList<>();
        if (i < minMachineNum) {
            list.add(minCpu);
            list.add(minMem);
        } else if (i >= minMachineNum && i < (minMachineNum+medMachineNum)) {
            list.add(medCpu);
            list.add(medMem);
        } else {
            list.add(maxCpu);
            list.add(maxMem);
        }
        return list;
    }

    public double evaluate(byte[][] chr) {
        // 0123
        double answer = 0.0;
        int fenZi1;
        int fenMu1;
        int fenZi2;
        int fenMu2;

        fenZi1 = 0;
        fenMu1 = maxCpu * maxMachineNum + medCpu * medMachineNum + minCpu * minMachineNum;
        fenZi2 = 0;
        fenMu2 = maxMem * maxMachineNum + medMem * medMachineNum + minMem * minMachineNum;

        for (int i = 0; i < machineNum; i++)
            for (int j = 0; j < flavorNum; j++) {
                if (chr[i][j] == 1){
                    fenZi1 += flavorInfo[j][0];
                    fenZi2 += flavorInfo[j][1];
                }
            }

        return ((fenZi1 * 1.0 / fenMu1) + (fenZi2 * 1.0 / fenMu2))/2;
    }

    // 二维数组拷贝
    public void copyarrayNum(byte[][] from, byte[][] to) {
        for (int i = 0; i < machineNum; i++) {
            for (int j = 0; j < flavorNum; j++) {
                to[i][j] = from[i][j];
            }
        }
    }

    // 三维数组拷贝
    public void copyarray(byte[][][] from, byte[][][] to) {
        for (int i = 0; i < scale; i++) {
            for (int j = 0; j < machineNum; j++) {
                for (int k = 0; k < flavorNum; k++)
                    to[i][j][k] = from[i][j][k];
            }
        }
    }

    public void evolution() {
        int i, j, k;
        int len = 0;
        float ra = 0f;


        // 迭代一次
        for (t = 0; t < MAX_GEN; t++) {
            // 对于每颗粒子
            for (i = 0; i < scale; i++) {
                if (i == bestNum) continue;

                //System.out.println("------------------------------");
                // 更新速度
                // Vii=wVi+ra(Pid-Xid)+rb(Pgd-Xid)
                w = 0.4+(MAX_GEN-t)*0.1/MAX_GEN;
                for (j = 0; j < machineNum; j++) {
                    for (k = 0; k < flavorNum; k++) {
                        flavorV[i][j][k] = (float)( w * flavorV[i][j][k] + c1 * Math.random() * (Pd[i][j][k] - oPopulation[i][j][k]) + c2 * Math.random() * (Pgd[j][k] - oPopulation[i][j][k]));
                    }
                }
                for (j = 0; j < machineNum; j++)
                    for (k = 0; k < flavorNum; k++)
                        oPopulation[i][j][k] = 0;
                for (j = 0; j < flavorNum; j++) {
                    for (k = 0; k < machineNum; k++) {
                        if ((1.0 / (1 + Math.exp(-flavorV[i][k][j]))) >= 0.5) {
                            oPopulation[i][k][j] = 1;
                            break;
                        }
                    }
                    if (k == machineNum) {
                        int temp = Math.abs(random.nextInt()) % machineNum;
                        oPopulation[i][temp][j] = 1;
                    }
                }
                int c=0;
                for(j=0;j<flavorNum;j++) {
                    for (k = 0; k < machineNum; k++) {
                        if (oPopulation[i][k][j] == 1)
                            c++;
                    }
                    if(c >1)
                        System.out.println("wrong~~~~~~~~~~~~~~~~~~~~");
                    c = 0;
                }
            }
            change();
//            for(j=0;j<flavorNum;j++)
//                if(oPopulation[i][0][j] == oPopulation[i][1][j])
//                    System.out.println("sssssssssssssssssss");

            // 计算新粒子群适应度，Fitness[max],选出最好的解
            for (k = 0; k < scale; k++) {
                fitness[k] = evaluate(oPopulation[k]);
                if (vPd[k] < fitness[k]) {
                    vPd[k] = fitness[k];
                    copyarrayNum(oPopulation[k], Pd[k]);
                    bestNum = k;
                }
                if (vPgd < vPd[k]) {
                    bestT = t;
                    //System.out.println("最佳使用率" + vPgd + " 代数：" + bestT);
                    vPgd = vPd[k];
                    copyarrayNum(Pd[k], Pgd);
                }
            }
        }
    }

    public Result solve() {
        int i;
        int k;
        int j;

        init();

        initGroup();

        change();

        initListV();

        // 每颗粒子记住自己最好的解
        copyarray(oPopulation, Pd);

        // 计算初始化种群适应度，Fitness[max],选出最好的解
        for (k = 0; k < scale; k++) {
            fitness[k] = evaluate(oPopulation[k]);
            vPd[k] = fitness[k];
            if (vPgd < vPd[k]) {
                vPgd = vPd[k];
                copyarrayNum(Pd[k], Pgd);
                bestNum = k;
            }
        }

//        // 打印
//        System.out.println("初始粒子群...");
//        for (k = 0; k < scale; k++) {
//            for (i = 0; i < machineNum; i++) {
//                for (j = 0; j < flavorNum; j++) {
//                    System.out.print(oPopulation[k][i][j] + ",");
//                }
//                System.out.println();
//            }
//            System.out.println();
//            System.out.println("----" + fitness[k]);
//
//            /*
//            ArrayList<SO> li = listV.get(k);
//            int l = li.size();
//            for (i = 0; i < l; i++) {
//                li.get(i).print();
//            }
//
//            System.out.println("----");
//            */
//        }

        // 进化
        evolution();

//        // 打印
//        System.out.println("最后粒子群...");
//        for (k = 0; k < scale; k++) {
//            for (i = 0; i < machineNum; i++) {
//                for (j = 0; j < flavorNum; j++) {
//                    System.out.print(oPopulation[k][i][j] + ",");
//                }
//                System.out.println();
//            }
//            System.out.println();
//            System.out.println("----" + fitness[k]);
//
//            /*
//            ArrayList<SO> li = listV.get(k);
//            int l = li.size();
//            for (i = 0; i < l; i++) {
//                li.get(i).print();
//            }
//
//            System.out.println("----");
//            */
//        }

        System.out.println("最佳使用率出现代数：");
        System.out.println(bestT);
        System.out.println("最佳使用率");
        System.out.println(vPgd);
//        System.out.println("最佳路径：");
//        for (i = 0; i < machineNum; i++) {
//            for (j = 0; j < flavorNum; j++)
//                System.out.print(Pgd[i][j]);
//            System.out.println();
//        }
        int temp = 0;
        for (i = 0; i < flavorNum; i++) {
            for (j = 0; j < machineNum; j++) {
                if (Pgd[j][i] == 1)
                    break;
            }
            if (j == machineNum)
                temp++;
        }
        System.out.println("虚拟机使用率为:" + (flavorNum - temp) * 1.0 / flavorNum);
        Result result = new Result();
        result.setvPgd(vPgd);
        result.setPgd(Pgd);
        return result;
//        Map<String ,Integer> map = new HashMap<>();
//        for (i = 0; i < machineNum; i++) {
//            System.out.print(i+1+" ");
//            for (j = 0; j < flavorNum; j++){
//                if(Pgd[i][j] == 1){
//                    if(map.containsKey(name[j])){
//                        int count = map.get(name[j])+1;
//                        map.remove(name[j]);
//                        map.put(name[j],count);
//                    }else{
//                        map.put(name[j],1);
//                    }
//                }
//            }
//            for(Map.Entry<String,Integer> entry:map.entrySet()){
//                System.out.print(entry.getKey() + " " + entry.getValue() + " ");
//            }
//            System.out.println();
//            map.clear();
//        }

    }

//    public static void main(String[] args) throws IOException {
//        System.out.println("Start....");
//        String strbuff;
//        int flavorNum = 24;
//        int maxCpu = 56;
//        int maxMem = 128;
//        int i, j;
//        String keyWord = "MEM";
//        String filename = "D:\\huawei\\data1.txt";
//        int[][] flavorInfo = new int[flavorNum][2];
//        String[] name = new String[flavorNum];
//        BufferedReader data = new BufferedReader(new InputStreamReader(
//                new FileInputStream(filename)));
//        flavorInfo = new int[flavorNum][2];
//        name = new String[flavorNum];
//        for (i = 0; i < flavorNum; i++) {
//            // 读取一行数据，数据格式flacor1 1 1
//            strbuff = data.readLine();
//            // 字符分割
//            String[] strcol = strbuff.split(" ");
//            flavorInfo[i][0] = Integer.valueOf(strcol[1]);
//            flavorInfo[i][1] = Integer.valueOf(strcol[2]);
//            name[i] = strcol[0];
//        }
//        List<int[][]> list1 = new ArrayList<>();
//        List<Double> list2 = new ArrayList<>();
//        List<Integer> list3 = new ArrayList<>();
//        int[][] ans;
//        int machineNum;
//        int temp = 0;
//        List<String> answer = new ArrayList<>();
//        for (i = 2; i < 3; i++) {
//            Result result = new Result();
//            PSO2 pso = new PSO2(flavorNum, 10, 10, 0.5f, i, maxCpu, maxMem, keyWord, flavorInfo, name);
//            result = pso.solve();
//            if (result.getvPgd() > 0.96) {
//                list1.add(result.getPgd());
//                list2.add(result.getvPgd());
//                list3.add(i);
//            } else {
//                list1.add(result.getPgd());
//                list2.add(result.getvPgd());
//                list3.add(i);
//                break;
//            }
//        }
//        if (list1.size() == 1) {
//            machineNum = list3.get(0);
//            ans = list1.get(0);
//            answer.add("change");
//        } else {
//            if (list2.get(list2.size() - 1) > 0.9) {
//                machineNum = list3.get(list3.size() - 1);
//                ans = list1.get(list1.size() - 1);
//                answer.add("change");
//            } else {
//                machineNum = list3.get(list3.size() - 2);
//                ans = list1.get(list1.size() - 2);
//                for (i = 0; i < flavorNum; i++) {
//                    for (j = 0; j < machineNum; j++) {
//                        if (ans[j][i] == 1)
//                            break;
//                    }
//                    if (j == machineNum)
//                        temp++;
//                }
//                if (temp * 1.0 / flavorNum >= 0.2) {
//                    machineNum = list3.get(list3.size() - 1);
//                    ans = list1.get(list1.size() - 1);
//                    answer.add("change");
//                }
//            }
//        }
//        for (i = 0; i < machineNum; i++) {
//            Map<String, Integer> map = new HashMap<>();
//            StringBuilder flavorLine = new StringBuilder();
//            flavorLine.append(i + 1 + " ");
//            System.out.print(i + 1 + " ");
//            for (j = 0; j < flavorNum; j++) {
//                if (ans[i][j] == 1) {
//                    if (map.containsKey(name[j])) {
//                        int count = map.get(name[j]) + 1;
//                        map.remove(name[j]);
//                        map.put(name[j], count);
//                    } else {
//                        map.put(name[j], 1);
//                    }
//                }
//            }
//            for (Map.Entry<String, Integer> entry : map.entrySet()) {
//                System.out.print(entry.getKey() + " " + entry.getValue() + " ");
//                flavorLine.append(entry.getKey() + " " + entry.getValue() + " ");
//            }
//            System.out.println();
//            map.clear();
//            System.out.println(flavorLine.toString());
//            answer.add(flavorLine.toString());
//        }
//
////        pso.init();
////        pso.solve();
//    }


}

