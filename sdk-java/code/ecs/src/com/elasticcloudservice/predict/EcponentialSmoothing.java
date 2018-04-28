package com.elasticcloudservice.predict;

/**
 * @Author: Skye
 * @Date: 10:24 2018/4/24
 * @Description:  指数平滑
 */
public class EcponentialSmoothing {

    /**
     *  一次平滑
     * @param trainData 输入数组
     * @param a   平滑参数
     * @return
     *
     */
    public static double[] OneSmooth(double[] trainData,double a){
        double[] arr = new double[trainData.length];
        for (int i = 0; i < trainData.length; i++) {
            double pre = 0.0;
            if (i != 0){
                pre = a * trainData[i] + (1-a)* arr[i-1];
            }else {
                // 初始值
                pre = (trainData[0] + trainData[1]+ trainData[2])/3;
            }
            arr[i] = pre;
        }
        return arr;
    }

    /**
     *   二次平滑预测 k 天
     * @param trainData 数据
     * @param k 需要预测的天数
     * @param a   平滑参数
     * @return   返回预测 k 天的值
     *
     *
     */
    public static double[] twoSmooth(double[] trainData,double a,int k){

        double[] smooth1 = OneSmooth(trainData,a);   // 一次平滑值
        double[] smooth2 = OneSmooth(smooth1,a);    // 二次平滑值
        int num = trainData.length-1;
        double at = 0.0,bt = 0.0;
        at = 2 * smooth1[num] - smooth2[num];
        bt = (a/(1-a)) * (smooth1[num] - smooth2[num]);

        double[] preValue = new double[k];
        for (int i = 0; i < k; i++) {
            preValue[i] = at + bt * (i+1);
        }

        return preValue;

    }

    /**
     *   三次平滑预测 k 天
     * @param trainData 数据
     * @param k 需要预测的天数
     * @param a   平滑参数
     * @return   返回预测 k 天的值
     *
     *
     */
    public static double[] threeSmooth(double[] trainData,double a,int k){

        double[] smooth1 = OneSmooth(trainData,a);   // 一次平滑值
        double[] smooth2 = OneSmooth(smooth1,a);    // 二次平滑值
        double[] smooth3 = OneSmooth(smooth2,a);    // 二次平滑值
        int num = trainData.length-1;
        double at = 0.0,bt = 0.0,ct = 0.0;
        at = 3 * smooth1[num] - 3 * smooth2[num] +  smooth3[num];
        bt = a / (2 * (1 - a) * (1 - a)) * ((6 - 5 * a) * smooth1[num] - (2 * (5 - 4 * a)) * smooth2[num] + (4 - 3 * a) * smooth3[num]);
        ct = a * a / (2 * (1 - a) * (1 - a)) * (smooth1[num] - 2 * smooth2[num] + smooth3[num]);

        double[] preValue = new double[k];
        for (int i = 0; i < k; i++) {
             double val = at + bt * (i+1) + ct * (i+1) * (i+1);
             if (val  < 0)
                 val = Math.abs(val);
             preValue[i] = val;
        }
        return preValue;
    }


}
