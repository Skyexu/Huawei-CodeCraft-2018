package com.elasticcloudservice.arima;


import java.util.Random;

public class MAModel {
    private double[] data;
    private double[] maCoe;
    private int q;

    public MAModel(double[] data, int q) {
        this.data = data;
        this.q = q;
    }

    public double[] solveCoeOfMA() {
        maCoe = new ARMAMethod().computeMACoe(data, q);
        return maCoe;
    }

    public double getModelAIC() {
        int n = data.length;
        double tmpAR = 0.0, tmpMA = 0.0;
        double sumErr = 0.0;
        Random random = new Random();

//        for (int i=0;i<maCoe.length;i++) {
//            LOG.info("maCoe[" + i + "]=" + maCoe[i]);
//        }
//        LOG.info("q=" + q);

        double[] errData = new double[q+1];
        for (int i=0;i<q+1;i++) {
            errData[i]=0;
        }
        for (int i = q ; i < n; ++i) {
            tmpMA = 0.0;
            for (int j = 1; j <= q; ++j) {
                tmpMA += maCoe[j] * errData[j];
            }

            for (int j = q; j > 0; --j) {
                errData[j] = errData[j - 1];
            }
            errData[0] = random.nextGaussian() * Math.sqrt(maCoe[0]);
            double Err = (data[i] - tmpMA) * (data[i] - tmpMA);
//            LOG.info("data[" + i + "]=" + data[i] + " tmpMA=" + tmpMA + " Err=" + Err);
            sumErr += Err;
        }
        return (n - (q - 1)) * Math.log(sumErr / (n - (q - 1))) + (q + 1) * 2;

    }
}