package com.elasticcloudservice.predict;

import java.util.*;

/**
 * @Author: Skye
 * @Date: 23:54 2018/4/8
 * @Description: 工具类
 */
public class DataUtil {
    private static final int OUTLIER = Integer.MIN_VALUE;
    /**
     * 获取百分位数
     *
     * @param data
     * @param p
     * @return
     */
    public static double percentile(List<Integer> data, double p) {
        int n = data.size();
        if (n == 0){
            return 0;
        }
        List<Integer> tempData = new ArrayList<>();
        tempData.addAll(data);
        Collections.sort(tempData);
        double px = p * (n - 1);
        int i = (int) java.lang.Math.floor(px);
        double g = px - i;
        if (g == 0) {
            return tempData.get(i);
        } else {
            return (1 - g) * tempData.get(i) + g * tempData.get(i + 1);
        }
    }

    /**
     * 获取百分位数
     *
     * @param data
     * @param p
     * @return
     */
    public static double percentile(double[] data, double p) {
        int n = data.length;
        if (n == 0) {
            return 0;
        }
        double[] tempData = new double[data.length];
        for (int i = 0; i < data.length; i++) {
            tempData[i] = data[i];
        }
        Arrays.sort(tempData);
        double px = p * (n - 1);
        int i = (int) java.lang.Math.floor(px);
        double g = px - i;
        if (g == 0) {
            return tempData[i];
        } else {
            return (1 - g) * tempData[i] + g * tempData[i + 1];
        }
    }
    /**
     * 求中位数
     */
    public static  int  getMedian(List<Integer> data) {

        double middle = 0;
        int size = data.size();
        if (size == 0){
            return 0;
        }
        Integer[] array = data.toArray(new Integer[size]);
        Arrays.sort(array);
        if (size % 2 == 0) {
            middle = (array[size / 2 - 1] + array[size / 2]) / 2.0;
        } else {
            int inx = size / 2;
            middle = array[inx];
        }
        return (int)Math.round(middle);
    }

    /**
     * 求均值
     */
    public static Double getMean(List<Integer> data) {
        double middle = 0;
        int size = data.size();
        if (size == 0){
            return 0.0;
        }
        double sum = 0;
        for (Integer value:
                data) {
            sum += value;
        }

        return sum/size;
    }

    /**
     * 求均值
     */
    public static double getMean(double[] data) {
        int size = data.length;
        if (size == 0) {
            return 0.0;
        }
        double sum = 0;
        for (double value :
                data) {
            sum += value;
        }

        return sum / size;
    }

    public static void fillZero(double[] data, double vlaue) {
        for (int i = 0; i < data.length; i++) {
            if (data[i] == 0)
                data[i] = vlaue;
        }
    }
    // 获取倒数几天
    public static double[] getPeriodData(double[] data, int period) {
        double[] temp = new double[period];
        int index = 0;
        for (int i = data.length - period; i < data.length; i++) {
            temp[index++] = data[i];
        }
        return temp;
    }
    public static double getMax(double[] data) {
        double[] tempData = new double[data.length];
        for (int i = 0; i < data.length; i++) {
            tempData[i] = data[i];
        }
        Arrays.sort(tempData);
        return tempData[tempData.length - 1];
    }
    /**
     * 求众数
     */
    public static int getModalNums(List<Integer> data) {
        int n = data.size();

        if (n == 1) {
            return data.get(0);
        }

        Map<Integer, Integer> freqMap = new HashMap<>();
        for (int i = 0; i < n; i++) { // 统计数组中每个数出现的频率
            Integer v = freqMap.get(data.get(i));
            // v == null 说明 freqMap 中还没有这个 arr[i] 这个键
            freqMap.put(data.get(i), v == null ? 1 : v + 1);
        }

        // 将 freqMap 中所有的键值对（键为数，值为数出现的频率）放入一个 ArrayList
        List<Map.Entry<Integer, Integer>> entries = new ArrayList<>(freqMap.entrySet());
        // 对 entries 按出现频率从大到小排序
        Collections.sort(entries, new Comparator<Map.Entry<Integer, Integer>>() {
            @Override
            public int compare(Map.Entry<Integer, Integer> e1, Map.Entry<Integer, Integer> e2) {
                return e2.getValue() - e1.getValue();
            }
        });


        int freNum = entries.get(0).getKey();// 排序后第一个 entry 的键肯定是一个众数
        int size = entries.size();
        for (int i = 1; i < size; i++) {
            // 选最大的数作为众数
            if (entries.get(i).getValue().equals(entries.get(0).getValue())) {
                if (entries.get(i).getKey() > freNum){
                    freNum = entries.get(i).getKey();
                }
            } else {
                break;
            }
        }

        return freNum;
    }

    /**
     * 处理异常值（百分位法），填补缺失值（中位数）
     * @param trainData
     * @param preStartTime
     * @param preFlavorList
     */
    public static void refineData(int trainData[][], int preStartTime, List<Integer> preFlavorList) {

        int flavorSize = preFlavorList.size();

        for (int i = 0; i < flavorSize; i++) {
            List<Integer> flavorData = new ArrayList<>();

            for (int j = 0; j < preStartTime; j++) {
                if (trainData[j][preFlavorList.get(i)] != 0) {
                    flavorData.add(trainData[j][preFlavorList.get(i)]);
                }
            }
/*
            // 获取 1% 和 99% 百分位
            double percentFirst = percentile(flavorData, 0.01);
            double percentLast = percentile(flavorData, 0.99);

            for (int j = 0; j < preStartTime; j++) {
                // 若数据不在 1% 和 99% 百分位 之间，则用 0 替换
                if (trainData[j][preFlavorList.get(i)] > percentLast || trainData[j][preFlavorList.get(i)] < percentFirst) {
                    trainData[j][preFlavorList.get(i)] = 0;
                }
            }
*/
            flavorData = new ArrayList<>();
            for (int j = 0; j < preStartTime; j++) {
                if (trainData[j][preFlavorList.get(i)] != 0) {
                    flavorData.add(trainData[j][preFlavorList.get(i)]);
                }
            }
            int midNum = getMedian(flavorData);
            //int freNum = getModalNums(flavorData);
            int mean = (int)Math.ceil(getMean(flavorData));
            for (int j = 0; j < preStartTime; j++) {
                // 填补缺失值
                if (trainData[j][preFlavorList.get(i)] == 0){
                    trainData[j][preFlavorList.get(i)] = mean;
                }
            }
        }
    }

    /**
     * 处理异常值（百分位法）
     * @param trainData
     * @param trainEndTime  训练集结束索引
     * @param preFlavorList
     */
    public static void refineData2(int trainData[][], int trainEndTime, List<Integer> preFlavorList) {
        int trainEndTimePlus = trainEndTime+1;
        int flavorSize = preFlavorList.size();

        for (int i = 0; i < flavorSize; i++) {
            List<Integer> flavorData = new ArrayList<>();

            for (int j = 0; j < trainEndTimePlus; j++) {
                if (trainData[j][preFlavorList.get(i)] != 0) {
                    flavorData.add(trainData[j][preFlavorList.get(i)]);
                }
            }

            // 获取 25% 和 75% 百分位
            double Q1 = percentile(flavorData, 0.25);
            double Q3  = percentile(flavorData, 0.75);
            double upperLimit = Q3 + 3 * (Q3 - Q1);
            for (int j = 0; j < trainEndTimePlus; j++) {
                // 若数据超过上界，标记异常
                if (trainData[j][preFlavorList.get(i)] > upperLimit) {
                    trainData[j][preFlavorList.get(i)] = OUTLIER;
                }
            }

            flavorData = new ArrayList<>();
            for (int j = 0; j < trainEndTimePlus; j++) {
                if (trainData[j][preFlavorList.get(i)] != OUTLIER && trainData[j][preFlavorList.get(i)] != 0 ) {
                    flavorData.add(trainData[j][preFlavorList.get(i)]);
                }
            }
            int midNum = getMedian(flavorData);
            //int freNum = getModalNums(flavorData);
            int mean = (int) Math.round(getMean(flavorData));
            for (int j = 0; j < trainEndTimePlus; j++) {
                if (trainData[j][preFlavorList.get(i)] == OUTLIER ) {
                    trainData[j][preFlavorList.get(i)] = (int)Math.round(Q3 + 1.5*(Q3 - Q1));
                    //trainData[j][preFlavorList.get(i)] = mean;
                }
            }
        }
    }

    /**
     * 删除异常值（百分位法）
     *
     * @param trainData
     */
    public static double[] removeOutlier(double trainData[]) {

        // 获取 25% 和 75% 百分位
        double Q1 = percentile(trainData, 0.25);
        double Q3 = percentile(trainData, 0.75);
        double upperLimit = Q3 + 3 * (Q3 - Q1);
        List<Double> list = new ArrayList<>();
        for (int i = 0; i < trainData.length; i++) {
            if (trainData[i] <= upperLimit) {
                list.add(trainData[i]);
            }
        }
        double[] data = new double[list.size()];
        for (int i = 0; i < list.size(); i++) {
            data[i] = list.get(i);
        }
        return data;
    }

    /**
     * @param data 所有数据
     * @param a    训练和测试数据集大小
     * @return double[0]  训练集 double[0] 测试集
     */
    public static double[][] getTrainTest(double[] data, double a) {
        int trainEnd = (int) Math.round(data.length * a);
        double[][] returnData = new double[2][];
        double[] train = new double[trainEnd];
        double[] test = new double[data.length - trainEnd];
        for (int i = 0; i < data.length; i++) {
            if (i < trainEnd)
                train[i] = data[i];
            else
                test[i - trainEnd] = data[i];
        }
        returnData[0] = train;
        returnData[1] = test;
        return returnData;
    }

    public static double getMSE(double[] pre, double[] test) {
        if (pre.length != test.length)
            throw new IllegalArgumentException("train and test must have same length.");
        double mse = 0.0;

        for (int i = 0; i < pre.length; i++) {
            mse += Math.pow(pre[i] - test[i], 2);
        }
        mse = Math.sqrt(mse / pre.length);
        return mse;
    }

    public static double[] changeDataToPeriod(double[] data, int peroid) {
        int canPeriod = data.length / peroid;
        int index = data.length - canPeriod * peroid;
        double[] newData = new double[canPeriod];
        int index2 = 0;
        for (int i = index; i < data.length; i += peroid) {
            double sum = 0.0;
            for (int j = i; j < i + peroid; j++) {
                sum += data[j];
            }
            newData[index2++] = sum;
        }
        return newData;
    }
    public static void main(String[] args) {
        List<Integer> data = Arrays.asList(new Integer[]{1, 4, 6, 2, 8});

        System.out.println(percentile(data, 0.99));
        System.out.println(data.get(2));
        System.out.println(getMedian(data));
        Integer[] arr = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 1, 1, 2, 2, 3, 4, 5};

        List<Integer> data2 = Arrays.asList(arr);
        int modalNums = getModalNums(data2);

        System.out.println(modalNums);
    }

}
