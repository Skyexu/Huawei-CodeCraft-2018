package com.elasticcloudservice.predict;

import com.elasticcloudservice.arima.ARIMAModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author: Skye
 * @Date: 17:14 2018/4/20
 * @Description:
 *
 * 预测模型执行类
 */
public class PreModel {


    /**
     * ARIMA 模型
     *
     * @param trainData  所有训练数据
     * @param trainEndIndex  训练数据结束时间索引
     * @param preStartIndex  预测开始时间索引
     * @param preDay           需要预测天数
     * @param preFlavorList     所要预测的虚拟机类型
     * @param period            用多少天的数据进行训练,trainEndIndex + 1 代表所有数据进行训练
     * @return
     */
     /*
    public static Map<Integer, Integer> arimaModel(int trainData[][], int trainEndIndex, int preStartIndex, int preDay, List<Integer> preFlavorList,int period){
        Map<Integer,Integer> resultMap = new HashMap<>();
        int diff = 2;
                                //0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18
        int[] diffArr = new int[]{2,7,2,2,2,7,2,2,2,2, 7, 1, 5, 2, 2, 2, 2, 2,2};
        for (Integer flavor :preFlavorList) {
            // 获取相应 flavor 训练数据
            double[] train = getDataFromTrain(trainEndIndex,trainData,period,flavor);
            double max = DataUtil.getMax(train);
            train = Grubbs.grubsUse(train);
            //train =  DataUtil.removeOutlier(train);
            diff = diffArr[flavor];
            int preValue = makeArima(preStartIndex-trainEndIndex,preDay,train,diff,max);
            System.out.println(flavor+" : "+preValue);
            resultMap.put(flavor,preValue);
        }
        return resultMap;
    }
*/
    /**
     * 指数平滑 模型
     *
     * @param trainData  所有训练数据
     * @param trainEndIndex  训练数据结束时间索引
     * @param preStartIndex  预测开始时间索引
     * @param preDay           需要预测天数
     * @param preFlavorList     所要预测的虚拟机类型
     * @param period            用多少天的数据进行训练,trainEndIndex + 1 代表所有数据进行训练
     * @return
     */
    public static Map<Integer, Integer> ecSmoothingModel(int trainData[][], int trainEndIndex, int preStartIndex, int preDay, List<Integer> preFlavorList,int period){
        Map<Integer,Integer> resultMap = new HashMap<>();
                             //0, 1, 2, 3, 4, 5, 6, 7, 8, 9,10,11,12,13,14,15,16,17,18
        //int[] plus = new int[]{0,30,20,30,30,35 ,35 ,30 ,10 ,35 ,35 ,35 ,35 ,35 ,35 ,35 ,35 ,35 ,5};
        int[] plus = new int[]{0, 13,13,15,13,13 ,13 ,13 ,13 ,13 ,13 ,13 ,13 ,13 ,13 ,13 ,13 ,13 ,13};
        //                          1,   2,   3,   4,   5,   6,   7,   8,   9,   10,  11,  12,  13,  14,  15,  16,  17,  18
        //double[] a = new double[]{0,0.15,0.15,0.4,0.25,0.15,0.15,0.15,0.15,0.15,0.15,0.15,0.15,0.15,0.15,0.15,0.15,0.15,0.15};
        double[] a = new double[]{0,0.09,0.10,0.10,0.10,0.09,0.10,0.05,0.09,0.3,0.09,0.09,0.09,0.09,0.09,0.09,0.09,0.09,0.09};
        for (Integer flavor :preFlavorList) {
            // 获取相应 flavor 训练数据
            double[] train = getDataFromTrain(trainEndIndex,trainData,period,flavor);
            train = Grubbs.grubsUse(train);
            //train =  DataUtil.removeOutlier(train);
            //int preValue = makeEcSmoothingDay(preStartIndex-trainEndIndex,preDay,train);
            double preValue = makeEcSmoothingPeriod(preDay,train,a[flavor]) * 1.2 + plus[flavor];
            int preResult = (int)Math.round(preValue);
            System.out.println(flavor+" : "+preResult);
            resultMap.put(flavor,preResult);
        }
        return resultMap;
    }

    /**
     * 所有虚拟机数据，比例分配 模型
     *
     * @param trainData  所有训练数据
     * @param trainEndIndex  训练数据结束时间索引
     * @param preStartIndex  预测开始时间索引
     * @param preDay           需要预测天数
     * @param preFlavorList     所要预测的虚拟机类型
     * @param period            用多少天的数据进行训练,trainEndIndex + 1 代表所有数据进行训练
     * @return
     */
    public static Map<Integer, Integer> allModel(int trainData[][], int trainEndIndex, int preStartIndex, int preDay, List<Integer> preFlavorList,int period){
        Map<Integer,Integer> resultMap = new HashMap<>();
        Map<Integer,Integer> tempMap = new HashMap<>();

        double[] allDataTrain = getAllPreFlavorData(trainEndIndex,trainData,period,preFlavorList);
        //allDataTrain = Grubbs.grubsUse(allDataTrain);
        double allPreValue = makeEcSmoothingDay(preStartIndex-trainEndIndex,preDay,allDataTrain);

//        allDataTrain = Grubbs.grubsUse(allDataTrain);
//        double max = DataUtil.getMax(allDataTrain);
//        int allPreValue = makeArima(preStartIndex-trainEndIndex,preDay,allDataTrain,1,max);
        double sum = 0;
        // 以平移模型作为比例
//        for (Integer flavor :preFlavorList) {
//            // 获取相应 flavor 训练数据
//            double[] train = getDataFromTrain(trainEndIndex,trainData,period,flavor);
//            train = Grubbs.grubsUse(train);
//            //train =  DataUtil.removeOutlier(train);
//            //int preValue = makeEcSmoothingDay(preStartIndex-trainEndIndex,preDay,train);
//            double preValue = makeEcSmoothingPeriod(preDay,train,0.15) ;
//            int preResult = (int)Math.round(preValue);
//            System.out.println(flavor+" : "+preResult);
//            sum += preResult;
//            tempMap.put(flavor,preResult);
//        }
        for (Integer flavor :preFlavorList) {
            // 获取相应 flavor 训练数据
            double[] train = getDataFromTrain(trainEndIndex,trainData,period,flavor);
            double mean = DataUtil.getMean(train);
            DataUtil.fillZero(train,mean);
            train = DataUtil.getPeriodData(train,preDay);
            double preValue = 0.0;
            for (int i = 0; i < preDay; i++) {
                preValue += train[i];
            }
            int preResult = (int)Math.round(preValue);
            sum += preResult;
            System.out.println(flavor+" : "+preResult);
            tempMap.put(flavor,preResult);
        }
        for (Map.Entry<Integer,Integer> entry: tempMap.entrySet()) {
            double val = (entry.getValue() * 1.0 / sum) * allPreValue;
            resultMap.put(entry.getKey(),(int)Math.round(val));
        }
        return resultMap;
    }

    /**
     * 平移模型
     * @param trainData
     * @param trainEndIndex
     * @param preStartIndex
     * @param preDay
     * @param preFlavorList
     * @param period
     * @return
     */
    public static Map<Integer, Integer> shiftModel(int trainData[][], int trainEndIndex, int preStartIndex, int preDay, List<Integer> preFlavorList,int period){
        Map<Integer,Integer> resultMap = new HashMap<>();
        for (Integer flavor :preFlavorList) {
            // 获取相应 flavor 训练数据
            double[] train = getDataFromTrain(trainEndIndex,trainData,period,flavor);
            double mean = DataUtil.getMean(train);
            DataUtil.fillZero(train,mean);
            train = DataUtil.getPeriodData(train,preDay);
            double preValue = 0.0;
            for (int i = 0; i < preDay; i++) {
                preValue += train[i];
            }
            int preResult = (int)Math.round(preValue);
            System.out.println(flavor+" : "+preResult);
            resultMap.put(flavor,preResult);
        }
        return resultMap;
    }


    /**
     * 平均值模型，使用前 preDay 天的平均值作为当天的预测
     * @param trainData
     * @param trainEndIndex
     * @param preStartIndex
     * @param preDay
     * @param preFlavorList
     * @param period
     * @return
     */
    public static Map<Integer, Integer> averageModel(int trainData[][], int trainEndIndex, int preStartIndex, int preDay, List<Integer> preFlavorList,int period){
        Map<Integer,Integer> resultMap = new HashMap<>();
        //                          1,   2,   3,   4,   5,   6,   7,   8,   9,   10,  11,  12,  13,  14,  15,  16,  17,  18
        double[] a = new double[]{0,1, 0.8,   1,   1,   1,   1,   1,   1,   1,   0.8,   1,  1.5,   1,   1,   1,   1,   1,  1};
        for (Integer flavor :preFlavorList) {
            // 获取相应 flavor 训练数据
            double[] train = getDataFromTrain(trainEndIndex,trainData,period,flavor);
            double mean = DataUtil.getMean(train);
            DataUtil.fillZero(train,mean);
            train = DataUtil.getPeriodData(train,preDay);
            double preValue = 0.0;
            for (int i = 0; i < preDay; i++) {
                double pre = DataUtil.getMean(train);
                preValue += pre;
                makeNewData(train,pre);
            }
            int preResult = (int)Math.round(preValue * a[flavor]);
            System.out.println(flavor+" : "+preResult);
            resultMap.put(flavor,preResult);
        }
        return resultMap;
    }

    /**
     * 对一个虚拟机进行指数平滑训练,每天一个样本
     * @param gap
     * @param preDay
     * @param train
     * @return
     */
    public static int makeEcSmoothingDay(int gap,int preDay,double[] train){
        // 需要连续预测的天数
        int needPreDay = gap+preDay-1;

        // 预测 needPreDay 天，遍历不同的 a 值  0.1 ~0.6,选取最小 mse 对应的 a 值
        double minMse = Double.MAX_VALUE;
        double a = 0.25;
        for (double i = 0.05; i < 0.8; i+=0.05) {
            double[][] trainTest = DataUtil.getTrainTest(train,0.8);
            //double[] preVals = EcponentialSmoothing.threeSmooth(trainTest[0],i,trainTest[1].length);
            double[] preVals = EcponentialSmoothing.twoSmooth(trainTest[0],i,trainTest[1].length);
            double mse = DataUtil.getMSE(preVals,trainTest[1]);
            if ( mse < minMse){
                minMse = mse;
                a = i;
            }

        }
        System.out.println("best a: " + a);
        double[] preVals = EcponentialSmoothing.twoSmooth(train,a,needPreDay);
        // 返回结果
        double  result = 0;
        for (int i = gap-1; i < needPreDay ; i++) {
            result += preVals[i];
        }
        return (int)Math.round(result);
    }

    /**
     * 对一个虚拟机进行指数平滑训练,7天一个样本
     * @param preDay
     * @param train
     * @return
     */
    public static int makeEcSmoothingPeriod(int preDay,double[] train,double a){
        int period = 7;
        // 需要连续预测的天数
        double needPrePeriod = preDay * 1.0 / period;
        // 将数据转化成周期和形式
        train = DataUtil.changeDataToPeriod(train,period);

        // 预测 needPrePeriod ，遍历不同的 a 值  0.1 ~0.6,选取最小 mse 对应的 a 值
  //      double minMse = Double.MAX_VALUE;
//        for (double i = 0.1; i < 0.8; i+=0.05) {
//            double[][] trainTest = DataUtil.getTrainTest(train,0.8);
//            double[] preVals = EcponentialSmoothing.twoSmooth(trainTest[0],i,trainTest[1].length);
//            double mse = DataUtil.getMSE(preVals,trainTest[1]);
//            if ( mse < minMse){
//                minMse = mse;
//                a = i;
//            }
//
//        }
        System.out.println("best a: " + a);
        double[] preVals = EcponentialSmoothing.twoSmooth(train,a,1);
        // 返回结果
        double  result = preVals[0] * needPrePeriod;
        return (int)Math.round(result);
    }
    /**
     * 对一个虚拟机进行 arima 训练
     * @param gap
     * @param preDay
     * @param train
     * @param diff 差分值
     * @return
     */
    /*
    public static int makeArima(int gap,int preDay,double[] train,int diff,double max){
        // 需要连续预测的天数
        int needPreDay = gap+preDay-1;
        // 模型预测
        List<Integer> predictResult = new ArrayList<>();
        // 预测 needPreDay 次，预测值添加到训练数据中继续预测下一天
        for (int i = 0; i < needPreDay; i++) {
            ARIMAModel arima = new ARIMAModel(train);
            int[] bestModel = arima.getARIMAModel(diff);
            double predictValue = 0.0;
            if (bestModel.length == 0) {
                predictValue = train[train.length - diff];
            } else {
                int predictDiff = arima.predictValue(bestModel[0], bestModel[1], diff);
                predictValue = arima.aftDeal(predictDiff, diff);
                //System.out.println("BestModel is " + bestModel[0] + " " + bestModel[1]);
            }
            if (predictValue < 0)
                predictValue = 0;
            predictResult.add((int)Math.round(predictValue));
            // 生成新的训练数据，增加预测天
            //makeNewData(train,predictValue);
            train = makeNewData2(train,predictValue);
        }
        // 返回结果
        int result = 0;
        max = (int)Math.round(max);
        for (int i = gap-1; i < needPreDay ; i++) {
            result+= predictResult.get(i) > max? max : predictResult.get(i);
        }
        return result;
    }
*/
    /**
     * 插入新的预测值，删除最前一天的数据
     * @param trainData
     * @param preVal
     */
    public static void makeNewData(double[] trainData,double preVal){
        for (int i = 0; i < trainData.length-1; i++) {
            trainData[i] = trainData[i+1];
        }
        trainData[trainData.length-1] = preVal;
    }
    // 不删除前一天
    public static double[] makeNewData2(double[] trainData,double preVal){
        double[] newData = new double[trainData.length+1];
        for (int i = 0; i < trainData.length; i++) {
            newData[i] = trainData[i];
        }
        newData[newData.length-1] = preVal;
        return newData;
    }
    /**
     * 从训练集中获取flavor对应的数据
     * @param trainEndIndex
     * @param trainData
     * @param period   训练日期数
     * @return
     */
    public static double[] getDataFromTrain(int trainEndIndex,int trainData[][],int period,int flavor){
        double[] data = new double[period];
        int index = 0;
        for (int i = trainEndIndex - period + 1; i <= trainEndIndex; i++) {
            data[index++] = trainData[i][flavor];
        }
        return data;
    }

    /**
     * 从训练集中获取所要预测的 flavor 对应的数据
     * @param trainEndIndex
     * @param trainData
     * @param period   训练日期数
     * @return
     */
    public static double[] getAllPreFlavorData(int trainEndIndex,int trainData[][],int period,List<Integer> preFlavorList){
        double[] data = new double[period];
        int index = 0;
        for (int i = trainEndIndex - period + 1; i <= trainEndIndex; i++) {
            for (Integer flavor: preFlavorList) {
                data[index] += trainData[i][flavor];
            }
            index++;
        }
        return data;
    }
}
