package com.elasticcloudservice.test;

import com.elasticcloudservice.model.Physical;
import com.filetool.util.FileUtil;
import com.filetool.util.LogUtil;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;

/**
 * @Author: Skye
 * @Date: 22:07 2018/4/17
 * @Description: 将文件处理计算输出，供数据分析
 */
public class GetDataToFile {
    public static String[] outputDataFile(String[] ecsContent) {
        int trainData[][] = new int[10000][100];
        LocalDateTime trainStartDateTime = null;
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        int[][] flavorInfo = {{0, 0}, {1, 1024}, {1, 2048}, {1, 4096}, {2, 2048}, {2, 4096}, {2, 8192}, {4, 4096}, {4, 8192}, {4, 16384}, {8, 8192}, {8, 16384},
                {8, 32768}, {16, 16384}, {16, 32768}, {16, 65536}, {32, 32768}, {32, 65536}, {32, 131072}};
        String[] results = new String[ecsContent.length];
        int flavorNum; // 需要预测的虚拟机种类个数
        int physicalNum; // 物理机种类数
        List<Integer> preFlavorList = new ArrayList<>();   // 虚拟机类型
        int preStartTime; //预测开始时间索引
        int preEndTime; //预测结束时间索引
        int trainStartTime = 0;
        int trainEndTime = 0;
        int preDay;   // 需要预测的天数
        List<Physical> physicalList = new ArrayList<>();   // 物理机列表

        List<String> history = new ArrayList<String>();
        Set<Integer> flavorSet = new LinkedHashSet<>();
        int maxTimeIndex = 0;
        // 解析训练文件
        for (int i = 0; i < ecsContent.length; i++) {
            if (ecsContent[i].contains("\t")
                    && ecsContent[i].split("\t").length == 3) {

                String[] array = ecsContent[i].split("\t");
                String uuid = array[0];
                String flavorName = array[1];
                String createTime = array[2];
                if (i == 0) {
                    trainStartDateTime = LocalDateTime.parse(createTime, dateTimeFormatter);
                }
                int timeIndex = getTimeIndex(createTime, dateTimeFormatter, trainStartDateTime);
                if (timeIndex > maxTimeIndex)
                    maxTimeIndex = timeIndex;
                int flavor = Integer.parseInt(flavorName.substring(6));
                flavorSet.add(flavor);

                trainData[timeIndex][flavor]++;
                if (i == ecsContent.length - 1 || i == ecsContent.length - 2)
                    trainEndTime = timeIndex;
            }
        }

        System.out.println("trainStartTime: " + trainStartDateTime);
        System.out.println("trainEndIndex: " + trainEndTime);

        List<Integer> flavorList = new ArrayList<>();
        flavorList.addAll(flavorSet);
        Collections.sort(flavorList);
        List<String> resultList = new ArrayList<>();

        for (Integer flaNum: flavorList) {
            for (int i = 0; i <= maxTimeIndex; i++) {
                StringBuilder str = new StringBuilder();
                str.append(flaNum).append(",").append(i).append(",").append(trainData[i][flaNum]);
                resultList.add(str.toString());
            }
        }

        return resultList.toArray(new String[resultList.size()]);
    }

    /**
     * 获取时间索引，从 0 开始
     *
     * @param timeString 时间字符串
     * @return 时间的索引，从 0 开始
     * @TODO: 2018/3/12 暂时只实现时间类型为天的
     */
    public static int getTimeIndex(String timeString, DateTimeFormatter dateTimeFormatter, LocalDateTime trainStartDateTime) {

        LocalDateTime localDateTime = LocalDateTime.parse(timeString, dateTimeFormatter);
        LocalDate localDate1 = LocalDate.from(trainStartDateTime);
        LocalDate localDate2 = LocalDate.from(localDateTime);

        long daysDiff = ChronoUnit.DAYS.between(localDate1, localDate2);

        return (int) daysDiff;
    }

    public static void main(String[] args) {

        String ecsDataPath = "D:\\Works\\competition\\huawei\\second\\data\\makedata\\data_2015_12_2016_1.txt";
        String resultFilePath = "D:\\Works\\competition\\huawei\\second\\data\\makedata\\out\\train_2015_12_2016_1.txt";
        LogUtil.printLog("Begin");

        // 读取输入文件
        String[] ecsContent = FileUtil.read(ecsDataPath, null);

        // 功能实现入口
        String[] resultContents = outputDataFile(ecsContent);

        // 写入输出文件
        if (hasResults(resultContents)) {
            FileUtil.write(resultFilePath, resultContents, false);
        } else {
            FileUtil.write(resultFilePath, new String[]{"NA"}, false);
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
