package com.elasticcloudservice.arima;



import java.util.Random;

public class ARModel {
    private double[] data;
    private double[] arCoe;
    private int p;

    public ARModel(double[] data, int p) {
        this.data = data;
        this.p = p;
    }

    public double[] solveCoeOfAR() {

        arCoe = new ARMAMethod().computeARCoe(data, p);
        return arCoe;
    }

    public double getModelAIC() {
        int n = data.length;
        double tmpAR = 0.0, tmpMA = 0.0;
        double sumErr = 0.0;
        Random random = new Random();

//        for (int i=0;i<arCoe.length;i++) {
//            LOG.info("arCoe[" + i + "]=" + arCoe[i]);
//        }
//        LOG.info("p=" + p);

        for (int i = p ; i < n; ++i) {
            tmpAR = 0.0;
            for (int j = 0; j < p; ++j) {
                tmpAR += arCoe[j] * data[i - j - 1];
            }
            double Err = (data[i] - tmpAR) * (data[i] - tmpAR);
//            LOG.info("data[" + i + "]=" + data[i] + " tmpAR=" + tmpAR + " Err=" + Err);
            sumErr += Err;
        }

        return (n - (p - 1)) * Math.log(sumErr / (n - (p - 1))) + (p + 1) * 2;
    }
}
