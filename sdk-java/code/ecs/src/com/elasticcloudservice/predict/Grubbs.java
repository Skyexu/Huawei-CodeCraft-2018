package com.elasticcloudservice.predict;

import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Grubbs {
    private ArrayList<Double> dataArrayList;
    private final double alpha = 0.01;

    //传入一组数据，剔除最大或最小的异常值
    public Grubbs(ArrayList<Double> arrayList) {
        this.dataArrayList = arrayList;
    }

    public ArrayList<Double> calc() {
        //因为格拉布斯准则只能对大于等于3个数据进行判断，所以数据量小于3时，直接返回
        while(true){
            if (dataArrayList.size() < 3) {
                return dataArrayList;
            }
            //首先对数据进行排序
            dataArrayList = bubbleSort(dataArrayList, dataArrayList.size());
            //求出数据平均值和标准差
            double average = calcAverage(dataArrayList);
            double standard = calcStandard(dataArrayList, dataArrayList.size(), average);
            //求助最小值和最大值G1，Gn
            double dubMin = average - dataArrayList.get(0);
            double dubMax = dataArrayList.get(dataArrayList.size() - 1) - average;
            double G1 = dubMin / standard;
            double Gn = dubMax / standard;
            //做比较，是否剔除,判断哪个是最大偏离值
            if(dubMin<dubMax){
                if (Gn > calcG(alpha, dataArrayList.size())) {
                    dataArrayList.remove(dataArrayList.size() - 1);
                }else {
                    break;
                }
            }else{
                if(G1 > calcG(alpha, dataArrayList.size())){
                    dataArrayList.remove(0);
                }else{
                    break;
                }
            }
//            if(Gn < calcG(alpha, dataArrayList.size()))
//                break;
//            if (G1 > calcG(alpha, dataArrayList.size())) {
//                dataArrayList.remove(0);
//                if (Gn > calcG(alpha, length)) {
//                    dataArrayList.remove(dataArrayList.size() - 2);
//                }
//            } else if (Gn > calcG(alpha, dataArrayList.size())) {
//                dataArrayList.remove(dataArrayList.size() - 1);
//            }
        }

        return dataArrayList;

    }

    //冒泡排序
    private ArrayList<Double> bubbleSort(ArrayList<Double> arr, int n) {
        // TODO Auto-generated method stub
        double temp = 0;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n - i - 1; j++) {
                if (arr.get(j) > arr.get(j + 1)) {
                    temp = arr.get(j);
                    arr.set(j, arr.get(j + 1));
                    arr.set(j + 1, temp);
                }
            }
        }
        return arr;
    }

    //求平均
    public double calcAverage(ArrayList<Double> sample) {
        // TODO Auto-generated method stub
        double sum = 0;
        int cnt = 0;
        for (int i = 0; i < sample.size(); i++) {
            sum += sample.get(i);
            cnt++;
        }

        return  sum*1.0 / cnt;
    }

    //求标准差
    private double calcStandard(ArrayList<Double> array, int n, double average) {
        // TODO Auto-generated method stub
        double sum = 0;
        for (int i = 0; i < n; i++) {
            sum += ((double) array.get(i) - average)
                    * ((double) array.get(i) - average);
        }
        return  Math.sqrt((sum*1.0 / (n)));
    }

    //算临界值的表，这里alpha为0.01
    private double calcG(double alpha, int n) {
        double[] N = {
                1.154637225,
                1.4925,
                1.748856802,
                1.944245158,
                2.097303944,
                2.220833452,
                2.323148425,
                2.409724587,
                2.484279034,
                2.549417088,
                2.607019535,
                2.65848008,
                2.704855374,
                2.746962683,
                2.785445274,
                2.820817304,
                2.853495371,
                2.883821136,
                2.912077855,
                2.938502659,
                2.963295822,
                2.986627844,
                3.008644937,
                3.029473325,
                3.049222641,
                3.06798865,
                3.085855434,
                3.102897179,
                3.119179632,
                3.134761303,
                3.149694471,
                3.16402601,
                3.177798099,
                3.19104881,
                3.203812617,
                3.216120819,
                3.228001916,
                3.239481919,
                3.250584631,
                3.26133188,
                3.271743729,
                3.281838657,
                3.291633716,
                3.301144673,
                3.310386131,
                3.31937164,
                3.328113792,
                3.336624311,
                3.344914123,
                3.35299343,
                3.36087177,
                3.368558072,
                3.376060705,
                3.383387523,
                3.390545906,
                3.397542799,
                3.404384737,
                3.411077886,
                3.417628062,
                3.424040761,
                3.430321177,
                3.436474229,
                3.442504576,
                3.448416633,
                3.454214593,
                3.459902436,
                3.465483945,
                3.470962718,
                3.476342181,
                3.481625598,
                3.486816079,
                3.49191659,
                3.496929966,
                3.50185891,
                3.506706009,
                3.511473736,
                3.516164456,
                3.520780437,
                3.525323849,
                3.529796772,
                3.534201203,
                3.538539055,
                3.542812168,
                3.547022308,
                3.551171172,
                3.55526039,
                3.559291534,
                3.563266114,
                3.567185583,
                3.571051344,
                3.574864744,
                3.578627087,
                3.582339626,
                3.586003573,
                3.589620097,
                3.593190324,
                3.596715346,
                3.600196216,
                3.60363395,
                3.607029534,
                3.610383919,
                3.613698027,
                3.616972748,
                3.620208947,
                3.623407459,
                3.626569094,
                3.629694637,
                3.632784849,
                3.635840469,
                3.638862211,
                3.641850769,
                3.644806819,
                3.647731015,
                3.65062399,
                3.653486364,
                3.656318735,
                3.659121685,
                3.661895782,
                3.664641575,
                3.667359601,
                3.670050379,
                3.672714418,
                3.67535221,
                3.677964235,
                3.680550961,
                3.683112842,
                3.685650323,
                3.688163835,
                3.6906538,
                3.693120626,
                3.695564716,
                3.697986458,
                3.700386233,
                3.702764411,
                3.705121355,
                3.707457419,
                3.709772945,
                3.712068272,
                3.714343728,
                3.716599633,
                3.7188363,
                3.721054037,
                3.723253141,
                3.725433905,
                3.727596614,
                3.729741548,
                3.73186898,
                3.733979176,
                3.736072397,
                3.738148898,
                3.74020893,
                3.742252736,
                3.744280555,
                3.746292622,
                3.748289165,
                3.750270409,
                3.752236572,
                3.75418787,
                3.756124514,
                3.758046709,
                3.759954657,
                3.761848557,
                3.763728601,
                3.765594981,
                3.767447882,
                3.769287487,
                3.771113975,
                3.772927521,
                3.774728298,
                3.776516473,
                3.778292214,
                3.780055681,
                3.781807034,
                3.78354643,
                3.785274022,
                3.78698996,
                3.788694392,
                3.790387463,
                3.792069316,
                3.793740089,
                3.795399921,
                3.797048947,
                3.798687297,
                3.800315104,
                3.801932493,
                3.803539591,
                3.805136521,
                3.806723404,
                3.808300358,
                3.809867502,
                3.81142495,
                3.812972814,
                3.814511207,
                3.816040237,
                3.817560011,
                3.819070635,
                3.820572214,
                3.822064849,
                3.82354864,
                3.825023686,
                3.826490086,
                3.827947933,
                3.829397323,
                3.830838348,
                3.832271099,
                3.833695665,
                3.835112136,
                3.836520597,
                3.837921135,
                3.839313833,
                3.840698774,
                3.842076041,
                3.843445713,
                3.844807869,
                3.846162588,
                3.847509946,
                3.848850018,
                3.85018288,
                3.851508605,
                3.852827264,
                3.854138929,
                3.855443671,
                3.856741558,
                3.858032659,
                3.85931704,
                3.860594768,
                3.861865908,
                3.863130524,
                3.86438868,
                3.865640438,
                3.86688586,
                3.868125006,
                3.869357937,
                3.870584711,
                3.871805387,
                3.873020022,
                3.874228673,
                3.875431395,
                3.876628244,
                3.877819274,
                3.879004539,
                3.880184091,
                3.881357984,
                3.882526267,
                3.883688993,
                3.884846211,
                3.88599797,
                3.887144321,
                3.88828531,
                3.889420985,
                3.890551394,
                3.891676582,
                3.892796596,
                3.89391148,
                3.895021279,
                3.896126038,
                3.897225799,
                3.898320606,
                3.899410501,
                3.900495525,
                3.901575721,
                3.902651129,
                3.903721789,
                3.904787741,
                3.905849024,
                3.906905678,
                3.907957741,
                3.90900525,
                3.910048243,
                3.911086757,
                3.912120829,
                3.913150494,
                3.914175789,
                3.915196749,
                3.916213408,
                3.917225802,
                3.918233963,
                3.919237926,
                3.920237724,
                3.92123339,
                3.922224956,
                3.923212455,
                3.924195918,
                3.925175377,
                3.926150862,
                3.927122404,
                3.928090035,
                3.929053783,
                3.930013678,
                3.93096975,
                3.931922027,
                3.932870539,
                3.933815313,
                3.934756378,
                3.935693762,
                3.936627491,
                3.937557594,
                3.938484096,
                3.939407025,
                3.940326407,
                3.941242267,
                3.942154632,
                3.943063526,
                3.943968975,
                3.944871005,
                3.945769638,
                3.946664901,
                3.947556816,
                3.948445409,
                3.949330701,
                3.950212718,
                3.951091481,
                3.951967015,
                3.952839341,
                3.953708481,
                3.954574459,
                3.955437296,
                3.956297013,
                3.957153633,
                3.958007176,
                3.958857664,
                3.959705118,
                3.960549557,
                3.961391004,
                3.962229477,
                3.963064997,
                3.963897584,
                3.964727258,
                3.965554037,
                3.966377942,
                3.967198991,
                3.968017203,
                3.968832597,
                3.969645192,
                3.970455005,
                3.971262056,
                3.972066361,
                3.97286794,
                3.973666809,
                3.974462986,
                3.975256488,
                3.976047333,
                3.976835537,
                3.977621118,
                3.978404091,
                3.979184475,
                3.979962284,
                3.980737536,
                3.981510246,
                3.98228043,
                3.983048104,
                3.983813284,
                3.984575985,
                3.985336222,
                3.986094012,
                3.986849368,
                3.987602305,
                3.98835284,
                3.989100985,
                3.989846757,
                3.990590168,
                3.991331234,
                3.992069969,
                3.992806386,
                3.9935405,
                3.994272325,
                3.995001873,
                3.995729159,
                3.996454196,
                3.997176997,
                3.997897576,
                3.998615945,
                3.999332118,
                4.000046107,
                4.000757926,
                4.001467586,
                4.002175101,
                4.002880482,
                4.003583743,
                4.004284894,
                4.004983949,
                4.00568092,
                4.006375818,
                4.007068655,
                4.007759442,
                4.008448193,
        };

        return N[n - 3];

    }
    public static double[] grubsUse(double[] data){
        ArrayList<Double> list = new ArrayList<>();
        ArrayList<Double> original = new ArrayList<>();
        for(int i=0;i<data.length;i++){
            list.add(data[i]);
            original.add(data[i]);
        }

        Grubbs grubbs = new Grubbs(list);
        ArrayList<Double> answer = new ArrayList<>();
        answer = grubbs.calc();
       // System.out.println(answer.size());
        Set<Double> set = new HashSet<>();
        for(double s:answer){
            //System.out.println(s);
            set.add(s);
        }

        for(int i = 0;i<original.size();i++){
            if(set.contains(original.get(i)))
                continue;
            else{
                original.remove(i);
                i--;
            }
        }
        double [] out = new double [original.size()];
        for(int i=0;i<original.size();i++){
            out[i] = original.get(i);
        }
        return out;
    }
    public static void main(String[] args) {
        ArrayList<Double> list = new ArrayList<>();
        ArrayList<Double> original = new ArrayList<>();
        String fileName = "E:\\huawei\\flavor2.txt";
        File file = new File(fileName);
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            String tmp = "";
            while ((tmp = reader.readLine())!=null){
                list.add(Double.valueOf(tmp));
                original.add(Double.valueOf(tmp));
                System.out.println(tmp);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            try {
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        System.out.println(list.size());
//        Grubbs grubbs = new Grubbs(list);
//        ArrayList<Double> answer = new ArrayList<>();
//        answer = grubbs.calc();
//        System.out.println(answer.size());
//        Set<Double> set = new HashSet<>();
//        for(double s:answer){
//            System.out.println(s);
//            set.add(s);
//        }
//
//        for(int i = 0;i<original.size();i++){
//            if(set.contains(original.get(i)))
//                continue;
//            else{
//                original.remove(i);
//                i--;
//            }
//        }
        double [] data = new double[list.size()];
        for(int i=0;i<list.size();i++)
            data[i] = list.get(i);
        double [] ans ;
        ans = grubsUse(data);
        System.out.println(ans.length);
        for(double s:ans)
            System.out.println(s);
    }
}

