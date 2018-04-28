package com.elasticcloudservice.allocate;

/**
 * @Author: Skye
 * @Date: 9:53 2018/4/19
 * @Description:
 */
class Result {
    double vPgd;
    byte[][] pgd;

    // 构造函数
    public Result() {
        super();
    }

    public double getvPgd() {
        return vPgd;
    }

    public void setvPgd(double vPgd) {
        this.vPgd = vPgd;
    }

    public byte[][] getPgd() {
        return pgd;
    }

    public void setPgd(byte[][] pgd) {
        this.pgd = pgd;
    }
}