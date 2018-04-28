package com.elasticcloudservice.arima;

import java.util.Random;

public class ARMAModel {
    private double[] data = {};
    private double[] arCoe = {};
    private double[] maCoe = {};
    private int p;        //AR阶数
    private int q;        //MA阶数

    public ARMAModel(double[] data, int p, int q) {
        this.data = data;
        this.p = p;
        this.q = q;
    }

    /**
     * 在ARMA模型中，首先根据原始数据求得AR模型的自回归系数(AR系数)
     * 利用AR系数与原始数据，求解的残差序列，根据残差序列的自协方差最终求得ARMA中MA系数
     *
     * @return ar, ma
     */
    public double[] solveCoeOfARMA() {

        //ARMA模型
        double[] armaCoe = new ARMAMethod().computeARMACoe(this.data, this.p, this.q);
        //AR系数
        arCoe = new double[this.p + 1];
        System.arraycopy(armaCoe, 0, arCoe, 0, arCoe.length);
        //MA系数
        maCoe = new double[this.q + 1];
        System.arraycopy(armaCoe, (this.p + 1), maCoe, 0, maCoe.length);

        return armaCoe;
    }

    public double[] getArCoe() {
        return arCoe;
    }

    public double[] getMaCoe() {
        return maCoe;
    }


    public double getModelAIC() {
        int n = data.length;
        double tmpAR = 0.0, tmpMA = 0.0;
        double sumErr = 0.0;
        Random random = new Random();

        double[] errData = new double[q+1];
        for (int i=0;i<q+1;i++) {
            errData[i] = 0;
        }

//        for (int i=0;i<arCoe.length;i++) {
//            LOG.info("arCoe[" + i + "]=" + arCoe[i]);
//        }
//        LOG.info("p=" + p);
//
//        for (int i=0;i<maCoe.length;i++) {
//            LOG.info("maCoe[" + i + "]=" + maCoe[i]);
//        }
//        LOG.info("q=" + q);

        for (int i = p ; i < n; ++i) {
            tmpAR = 0.0;
            for (int j = 0; j < p ; ++j) {
                tmpAR += arCoe[j] * data[i - j - 1];
            }
            tmpMA = 0.0;
            for (int j = 1; j <= q; ++j) {
                tmpMA += maCoe[j] * errData[j];
            }
            for (int j = q; j > 0; --j) {
                errData[j] = errData[j - 1];
            }
            errData[0] = random.nextGaussian() * Math.sqrt(maCoe[0]);

            double Err = (data[i] - tmpAR - tmpMA) * (data[i] - tmpAR - tmpMA);
//            LOG.info("data[" + i + "]=" + data[i] + " tmpMA=" + tmpMA + " tmpAr=" + tmpAR + " Err=" + Err);
            sumErr += Err;
        }

        return (n - (q + p - 1)) * Math.log(sumErr / (n - (q + p - 1))) + (p + q) * 2;

    }
}
