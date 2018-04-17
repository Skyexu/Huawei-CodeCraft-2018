package com.elasticcloudservice.test;

import com.elasticcloudservice.predict.Predict;
import com.filetool.util.FileUtil;
import com.filetool.util.LogUtil;

/**
 * @Author: Skye
 * @Date: 0:46 2018/4/17
 * @Description:
 */
public class TestPrdict {
    public static void main(String[] args) {
        String path = "D:\\Works\\competition\\huawei\\second\\data\\testCase\\";
        String ecsDataPath = path+ "TrainData_2015.12.txt";
        String inputFilePath =  path + "input_3hosttypes_5flavors_1week.txt";
        String resultFilePath = path +  "out.txt";
        String testFilePath = path + "TestData_2016.1.8_2016.1.14.txt";
        LogUtil.printLog("Begin");

        // 读取输入文件
        String[] ecsContent = FileUtil.read(ecsDataPath, null);
        String[] inputContent = FileUtil.read(inputFilePath, null);

        // 功能实现入口
        String[] resultContents = Predict.predictVm(ecsContent, inputContent);
    }
}
