package com.elasticcloudservice.predict;

import com.filetool.util.FileUtil;

import java.io.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;

/**
 * @Author: Skye
 * @Date: 19:18 2018/3/12
 * @Description:
 */
public class Test {
    public static int[] physicInfo = new int[3];
    public static void main(String[] args) {
        String time1 = "2018-04-03 17:34:57";
        String time2 = "2015-01-30 18:30:57";
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime localDateTime1 = LocalDateTime.parse(time1,dateTimeFormatter);
        LocalDateTime localDateTime2 = LocalDateTime.parse(time2,dateTimeFormatter);
        System.out.println(localDateTime1);
        System.out.println(localDateTime2);

        long hoursDiff = ChronoUnit.HOURS.between(localDateTime1,localDateTime2);
        long daysDiff = ChronoUnit.DAYS.between(localDateTime1,localDateTime2);
        System.out.println(hoursDiff);
        System.out.println(daysDiff);

        LocalDate localDate1 = LocalDate.from(localDateTime1);
        LocalDate localDate2 = LocalDate.from(localDateTime2);

        long daysDiffC = ChronoUnit.DAYS.between(localDate1,localDate2);

        System.out.println(localDate1);
        System.out.println(localDate2);
        LocalDate localDate3 = localDate1.plusDays(3);
        System.out.println(localDate3);
        System.out.println(localDate3.getDayOfWeek().getValue());
        Period period = Period.between(localDate2,localDate1);

        int daysDiffP = period.getDays();
        System.out.println("period: " + daysDiffP);
        System.out.println("ChronoUnit: " + daysDiffC);
        int index = 0;
        while (index++ >= 0){
            System.out.println(index);
            if (index > 5)
                break;
        }
        int[][] flavorInfo = {{0,0},{1,1024},{1,2048},{1,4096},{2,2048},{2,4096},{2,8192},{4,4096},{4,8192},{4,16384},{8,8192},{8,16384},
                {8,32768},{16,16384},{16,32768},{16,65536}};
        System.out.println("a: "+ flavorInfo[1][1]);

        physicInfo[0] = 1;
        System.out.println("physicInfo[0]: "+ physicInfo[0]);
        physicInfo[0] = 2;
        System.out.println("physicInfo[0]: "+ physicInfo[0]);

        String[] input = FileUtil.read("D:\\Works\\competition\\huawei\\资料\\练习数据\\初赛文档\\用例示例\\input_5flavors_cpu_7days.txt",null);
        System.out.println("input[0]: "+ input[0]);
        System.out.println("input[0]: "+ input[1]);
        System.out.println("input[0]: "+ input[2]);

        List<String> resultList = new LinkedList<>();
        List<String> list1 = new LinkedList<>();
        List<String> list2 = new LinkedList<>();
        list1.add("222");
        list1.add("231");
        list2.add("6757");
        resultList.addAll(list1);
        resultList.addAll(list2);

        System.out.println(resultList.get(0));
        System.out.println(resultList.get(1));
        System.out.println(resultList.get(2));

        String str = 6+"";
        int a = Integer.parseInt(str);
        System.out.println(a);

        int[][] train = new int[3][3];
        System.out.print(System.getProperty("line.separator"));
        int[][] testArr = new int[3][3];
        testArr[1][0] = 1;
        testArr[1][1] = testArr[2][0] + 1;
        System.out.println(testArr[1][1]);
        System.out.println(3/5);

        List<String> list = new ArrayList<>();
        list.add("sdg");
        list.add("aaa");
        List<String> list3 = list;
        List<String> list4 = new ArrayList<>();
        list4.addAll(list);
        list4.set(0,"bbb");
        System.out.println(list);
        System.out.println(list4);
//        Collections.sort(list);
//
//        System.out.println(list);
//        System.out.println(list3);
//
//        Employee employee = new Employee();
//        System.out.println(employee.getId());

        LocalDate date =  LocalDate.now();

    }

}
