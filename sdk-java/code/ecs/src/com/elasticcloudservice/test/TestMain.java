package com.elasticcloudservice.test;

import com.elasticcloudservice.predict.Predict;
import com.filetool.util.FileUtil;
import com.filetool.util.LogUtil;

/**
 * @Author: Skye
 * @Date: 0:24 2018/3/13
 * @Description:
 */
public class TestMain {
    public static void main(String[] args) {

//        if (args.length != 3) {
//            System.err
//                    .println("please input args: ecsDataPath, inputFilePath, resultFilePath");
//            return;
//        }
        //D:\\Works\\competition\\huawei\\资料\\练习数据\\初赛文档\\用例示例\\TrainData_2015.1.1_2015.2.19.txt
        // D:\\Works\\competition\\huawei\\资料\\练习数据\\初赛文档\\用例示例\\input_5flavors_cpu_7days.txt
        // D:\\Works\\competition\\huawei\\资料\\练习数据\\初赛文档\\用例示例\\out.txt

        String ecsDataPath = "D:\\Works\\competition\\huawei\\second\\data\\testCase\\TrainData_2015.12.txt";
        String inputFilePath = "D:\\Works\\competition\\huawei\\second\\data\\testCase\\input_3hosttypes_5flavors_1week.txt";
        String resultFilePath = "D:\\Works\\competition\\huawei\\second\\data\\testCase\\out.txt";
        LogUtil.printLog("Begin");

        // 读取输入文件
        String[] ecsContent = FileUtil.read(ecsDataPath, null);
        String[] inputContent = FileUtil.read(inputFilePath, null);

        // 功能实现入口
        String[] resultContents = Predict.predictVm(ecsContent, inputContent);

        // 写入输出文件
        if (hasResults(resultContents)) {
            FileUtil.write(resultFilePath, resultContents, false);
        } else {
            FileUtil.write(resultFilePath, new String[] { "NA" }, false);
        }
        LogUtil.printLog("End");
    }

    private static boolean hasResults(String[] resultContents) {
        if (resultContents == null) {
            return false;
        }
        for (String contents : resultContents) {
            if (contents != null && !contents.trim().isEmpty()) {
                return true;
            }
        }
        return false;
    }
}
