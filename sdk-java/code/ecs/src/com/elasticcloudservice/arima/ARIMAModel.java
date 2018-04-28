package com.elasticcloudservice.arima;


import java.util.Random;

public class ARIMAModel {


    double[] originalData = {};

    double[] arCoe = {};
    double[] maCoe = {};

    public ARIMAModel() {

    }

    public ARIMAModel(double[] originalData) {
        this.originalData = originalData;
    }


    public double[] preSeasonDiff(double[] preData, int period) {
//        for (int i=0; i<preData.length; i++) {
//            LOG.info(i + ":" + preData[i]);
//        }
        double[] tmpData = new double[preData.length - period];
        for (int i = 0; i < preData.length - period; ++i) {
            tmpData[i] = preData[i + period] - preData[i];
//            LOG.info(i + ":" + tmpData[i] + " " + preData[i + 7] + " " + preData[i]);
        }
        return tmpData;
    }

    public double[] preDealDiff(int period) {
        if (period >= originalData.length - 1) {
            period = 0;
        }
        switch (period) {
            case 0:
                return originalData;
            default:
                return preSeasonDiff(originalData, period);
        }
    }


    public int aftDeal(int predictValue, int period) {
        if (period >= originalData.length) {
            period = 0;
        }

        switch (period) {
            case 0:
                return (int) predictValue;
            case 1:
                return (int) (predictValue + originalData[originalData.length - 1]);
            default:
                return (int) (predictValue + originalData[originalData.length - period]);
        }
    }

    public int[] getARIMAModel(int period) {
        double[] data = preDealDiff(period);
//        for (int i=0; i<data.length;i++) {
//            LOG.info("data[" + i + "]=" + data[i]);
//        }

        double minAIC = Double.MAX_VALUE;
        int[] bestModel = new int[3];
        int type = 0;
        double[] coe = {};

        int len = data.length;
        if (len > 7) {
            len = 7;
        }
        int size = (len + 1) * (len + 1) - 1;
        int[][] model = new int[size][2];
        int cnt = 0;
        for (int i = 0; i <= len; ++i) {
            for (int j = 0; j <= len; ++j) {
                if (i == 0 && j == 0)
                    continue;
                model[cnt][0] = i;
                model[cnt][1] = j;
                cnt++;
            }
        }

        for (int i = 0; i < model.length; ++i) {

            double aic = 0;
            double[] _arCoe = {};
            double[] _maCoe = {};
            if (model[i][0] == 0) {
                MAModel ma = new MAModel(data, model[i][1]);
                _maCoe = ma.solveCoeOfMA();
                aic = ma.getModelAIC();
                type = 1;
            } else if (model[i][1] == 0) {
                ARModel ar = new ARModel(data, model[i][0]);
                _arCoe = ar.solveCoeOfAR();
                aic = ar.getModelAIC();
                type = 2;
            } else {
                ARMAModel arma = new ARMAModel(data, model[i][0], model[i][1]);
                arma.solveCoeOfARMA();
                _arCoe = arma.getArCoe();
                _maCoe = arma.getMaCoe();
                aic = arma.getModelAIC();
                type = 3;
            }

//            LOG.info("p=" + model[i][0] + " q=" + model[i][1] + " aic=" +  aic);

            // 在求解过程中如果阶数选取过长，可能会出现NAN或者无穷大的情况
            if (!Double.isNaN(aic) && aic < minAIC) {
                minAIC = aic;
                bestModel[0] = model[i][0];
                bestModel[1] = model[i][1];
                bestModel[2] = (int) Math.round(minAIC);
                arCoe = _arCoe;
                maCoe = _maCoe;

//                for (int k = 0; k < arCoe.length; k++) {
//                    LOG.info("arCoe[" + k + "]=" + arCoe[k]);
//                }
//                for (int k = 0; k < maCoe.length; k++) {
//                    LOG.info("maCoe[" + k + "]=" + maCoe[k]);
//                }
              //  LOG.info("best model p=" + model[i][0] + " q=" + model[i][1] + " aic=" + aic);

            }
        }
        return bestModel;
    }


    public int predictValue(int p, int q, int period) {
        double[] data = this.preDealDiff(period);
        int n = data.length;
        int predict = 0;
        double tmpAR = 0.0, tmpMA = 0.0;
        double[] errData = new double[q + 1];
        for (int i = 0; i <= q; i++) {
            errData[i] = 0;
        }

        Random random = new Random();

        if (p == 0) {

//            for (int i = 0; i < maCoe.length; i++) {
//                LOG.info("maCoe[" + i + "]=" + maCoe[i]);
//            }
//            LOG.info("q=" + q);

            for (int k = q; k < n; ++k) {
                tmpMA = 0;
                for (int i = 1; i <= q; ++i) {
                    tmpMA += maCoe[i] * errData[i];
                }
                for (int j = q; j > 0; --j) {
                    errData[j] = errData[j - 1];
                }
                errData[0] = random.nextGaussian() * Math.sqrt(maCoe[0]);
            }
            predict = (int) (tmpMA);
        } else if (q == 0) {

//            for (int i = 0; i < arCoe.length; i++) {
//                LOG.info("arCoe[" + i + "]=" + arCoe[i]);
//            }
//            LOG.info("p=" + p);

            for (int k = p; k < n; ++k) {
                tmpAR = 0;
                for (int i = 0; i < p; ++i) {
                    tmpAR += arCoe[i] * data[k - i - 1];
                }
            }
            predict = (int) (tmpAR);
        } else {

//            for (int i = 0; i < arCoe.length; i++) {
//                LOG.info("arCoe[" + i + "]=" + arCoe[i]);
//            }
//            LOG.info("p=" + p);
//
//            for (int i = 0; i < maCoe.length; i++) {
//                LOG.info("maCoe[" + i + "]=" + maCoe[i]);
//            }
//            LOG.info("q=" + q);

            for (int k = p; k < n; ++k) {
                tmpAR = 0;
                tmpMA = 0;
                for (int i = 0; i < p; ++i) {
                    tmpAR += arCoe[i] * data[k - i - 1];
                }
                for (int i = 1; i <= q; ++i) {
                    tmpMA += maCoe[i] * errData[i];
                }
                for (int j = q; j > 0; --j) {
                    errData[j] = errData[j - 1];
                }
                errData[0] = random.nextGaussian() * Math.sqrt(maCoe[0]);
            }

            predict = (int) (tmpAR + tmpMA);
        }

        return predict;
    }
}
