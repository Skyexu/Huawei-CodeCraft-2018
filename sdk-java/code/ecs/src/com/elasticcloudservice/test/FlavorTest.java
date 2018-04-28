package com.elasticcloudservice.test;

import com.elasticcloudservice.arima.ARIMAModel;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author: Skye
 * @Date: 15:20 2018/4/20
 * @Description:
 */
public class FlavorTest {
    public static void main(String[] args) {
        //读数据
        File file = new File(System.getProperty("user.dir") + "/data/flavor_train");
        BufferedReader reader = null;
        ArrayList<Double> dataList =new ArrayList<Double>();
        try {
            reader = new BufferedReader(new FileReader(file));
            String tempString = null;
            while ((tempString = reader.readLine()) != null) {
                dataList.add(Double.parseDouble(tempString));
            }
            reader.close();
        }catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                }
            }
        }

        // 模型预测
        double[] allData = new double[dataList.size()];
        double[] trainData = new double[50];
        double[] testData = new double[10];
        for (int i = 0; i < dataList.size(); i++) {
            allData[i] = dataList.get(i);
            if (i < 50){
                trainData[i] = dataList.get(i);
            }else {
                testData[i-50] = dataList.get(i);
            }
        }

        List<Integer> predictResult = new ArrayList<>();
        // 预测10次，预测值添加到训练数据中继续预测下一天
        for (int i = 0; i < 10; i++) {
            ARIMAModel arima = new ARIMAModel(trainData);
            int period = 1;
            int[] bestModel = arima.getARIMAModel(period);
            double predictValue = 0.0;
            if (bestModel.length == 0) {
                predictValue = trainData[trainData.length - period];
            } else {
                int predictDiff = arima.predictValue(bestModel[0], bestModel[1], period);
                predictValue = arima.aftDeal(predictDiff, period);
                System.out.println("BestModel is " + bestModel[0] + " " + bestModel[1]);
            }
            predictResult.add((int)Math.round(Math.abs(predictValue)));
            // 生成新的训练数据，删除最前一天，增加预测天
            makeNewData(trainData,Math.round(predictValue));
        }

        // 计算预测准确率
        double RMSE = 0;
        for (int i = 0; i < predictResult.size(); i++) {
            RMSE += Math.pow(predictResult.get(i) - testData[i],2);
        }
        System.out.println("RMSE: " + Math.pow(RMSE / predictResult.size(),0.5) );
        System.out.println(predictResult);
        for (Double test:testData) {
            System.out.print(test+", ");
        }

    }

    public static void makeNewData(double[] trainData,double preVal){
        for (int i = 0; i < trainData.length-1; i++) {
            trainData[i] = trainData[i+1];
        }
        trainData[trainData.length-1] = preVal;
    }
}
