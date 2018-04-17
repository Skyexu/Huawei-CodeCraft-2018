package com.elasticcloudservice.predict;

import com.elasticcloudservice.model.Physical;
import com.filetool.util.FileUtil;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
//                            _ooOoo_
//                           o8888888o
//                           88" . "88
//                           (| -_- |)
//                            O\ = /O
//                        ____/`---'\____
//                      .   ' \\| |// `.
//                       / \\||| : |||// \
//                     / _||||| -:- |||||- \
//                       | | \\\ - /// | |
//                     | \_| ''\---/'' | |
//                      \ .-\__ `-` ___/-. /
//                   ___`. .' /--.--\ `. . __
//                ."" '< `.___\_<|>_/___.' >'"".
//               | | : `- \`.;`\ _ /`;.`/ - ` : | |
//                 \ \ `-. \_ __\ /__ _/ .-` / /
//         ======`-.____`-.___\_____/___.-`____.-'======
//                            `=---='
//
//         .............................................
//                  佛祖保佑             永无BUG
public class Predict {

    public static int trainData[][] = new int[10000][100];
    public static  LocalDateTime trainStartDateTime;
    public static DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    public static int[][] flavorInfo = {{0,0},{1,1024},{1,2048},{1,4096},{2,2048},{2,4096},{2,8192},{4,4096},{4,8192},{4,16384},{8,8192},{8,16384},
            {8,32768},{16,16384},{16,32768},{16,65536},{32,32768},{32,65536},{32,131072}};


    /**
     * 
     * @param ecsContent
     * @param inputContent
     * @return
     */
	public static String[] predictVm(String[] ecsContent, String[] inputContent) {

		/** =========do your work here========== **/

		String[] results = new String[ecsContent.length];
        int flavorNum; // 需要预测的虚拟机种类个数
        int physicalNum; // 物理机种类数
        List<Integer> preFlavorList = new ArrayList<>();   // 虚拟机类型
        int preStartTime; //预测开始时间索引
        int preEndTime; //预测结束时间索引
        int trainStartTime = 0;
        int trainEndTime = 0;
        int preDay ;   // 需要预测的天数
        List<Physical> physicalList = new ArrayList<>();   // 物理机列表

		List<String> history = new ArrayList<String>();
        // 解析训练文件
		for (int i = 0; i < ecsContent.length; i++) {
			if (ecsContent[i].contains("\t")
					&& ecsContent[i].split("\t").length == 3) {

				String[] array = ecsContent[i].split("\t");
				String uuid = array[0];
				String flavorName = array[1];
				String createTime = array[2];
                if (i == 0){
                    trainStartDateTime = LocalDateTime.parse(createTime,dateTimeFormatter);
                }
                int timeIndex = getTimeIndex(createTime,0);

                int flavor = Integer.parseInt(flavorName.substring(6));
                // 只需预测 15 种虚拟机
                if (flavor < 19){
                    trainData[timeIndex][flavor]++;
                }
                if (i == ecsContent.length -1 || i== ecsContent.length -2)
                    trainEndTime = timeIndex;
				//history.add(uuid + " " + flavorName + " " + createTime);
			}
		}

		// 解析 input 文件
        // 保存物理机信息
        physicalNum = Integer.parseInt(inputContent[0]);
        for (int i = 1; i < 1 + physicalNum; i++) {
            String[] physicInfoString = inputContent[i].split(" ");
            Physical physical = new Physical();
            physical.setName(physicInfoString[0]);
            physical.setCpu(Integer.parseInt(physicInfoString[1]));
            physical.setMemory(Integer.parseInt(physicInfoString[2]));
            physicalList.add(physical);
        }

        // 保存虚拟机信息
        flavorNum = Integer.parseInt(inputContent[2 + physicalNum]);
        for (int i = 3 + physicalNum; i < 3 + physicalNum + flavorNum; i++) {
            String[] flavorString = inputContent[i].split(" ");
            preFlavorList.add(Integer.parseInt(flavorString[0].substring(6)));
        }



        String preStartString = inputContent[4 + physicalNum + flavorNum];
        String preEndString = inputContent[5 + physicalNum + flavorNum];

        preStartTime = getTimeIndex(preStartString,0);
        preEndTime = getTimeIndex(preEndString,0);
        preDay = preEndTime - preStartTime ;

        System.out.println("trainStartTime: " + trainStartDateTime);
        System.out.println("trainEndIndex: " + trainEndTime);
        System.out.println("preStartTime: " + preStartTime);
        System.out.println("preEndTime: " + preEndTime);
        System.out.println("preDay: " + preDay);



        // 数据预处理
        DataUtil.refineData2(trainData,trainEndTime,preFlavorList);

        Map<Integer,Integer> flavorResultMap1 = predictModel(preStartTime,preDay,preFlavorList);   // 77.854     不去异常：78.781
 //       Map<Integer,Integer> flavorResultMap2 = predictModelPeriodRule(preStartTime,preDay,preFlavorList);    // 74.391   不去异常：75.531
   //     Map<Integer,Integer> flavorResultMap3  = predictModelConstantRegressionTwo(preStartTime,preDay,preFlavorList); //71.821    不去异常 ：77.385
        Map<Integer,Integer> flavorResultMap4 = predictModelConstantRegressionOne(preStartTime,preDay,preFlavorList); // 79.452       不去异常 ：   79.558
 //      Map<Integer,Integer> flavorResultMap5 = predictModelLinear(preStartTime,preDay,preFlavorList);   // 76.465       不去异常 ：  79.558
         double a = 0.28;     //平滑参数
  //      Map<Integer,Integer> flavorResultMap6 = predictModelTwoSmooth(preStartTime,preDay,preFlavorList,a);   // 59.107   不去异常 ：  61.872
  //      Map<Integer,Integer> flavorResultMap7= predictModelThreeSmooth(preStartTime,preDay,preFlavorList,a);  //54.459    不去异常 ：  58.288

        Map<Integer,Integer> flavorResultMap8= predictModelAverage(preStartTime,preDay,preFlavorList);  // 77.93         不去异常 ：   76.302


        // 加权
        Map<Integer,Integer> flavorResultMap = new HashMap<>();
        for (int flavorNumber : preFlavorList){
            //double preNumber = flavorResultMap1.get(flavorNumber) * 0.1 + flavorResultMap2.get(flavorNumber) * 0.3 + flavorResultMap4.get(flavorNumber) * 0.7;
            //double preNumber = flavorResultMap1.get(flavorNumber) * 0.5 + flavorResultMap4.get(flavorNumber) * 0.5;
            double preNumber = flavorResultMap1.get(flavorNumber) * 0.63 + flavorResultMap4.get(flavorNumber) * 0.5;  // 90.719
            //double preNumber = flavorResultMap1.get(flavorNumber);
            //double preNumber = flavorResultMap2.get(flavorNumber) * 0.2 + flavorResultMap4.get(flavorNumber) * 0.8;
            //double preNumber = flavorResultMap4.get(flavorNumber);
            //double preNumber = flavorResultMap6.get(flavorNumber) * 0.3 + flavorResultMap8.get(flavorNumber) * 0.4 + flavorResultMap4.get(flavorNumber) * 0.5;
            //double preNumber =flavorResultMap6.get(flavorNumber)*1.2;
            //double preNumber = flavorResultMap8.get(flavorNumber);  //82.965
            //double preNumber = flavorResultMap4.get(flavorNumber) * 0.44 + flavorResultMap8.get(flavorNumber) * 0.63;
            //double preNumber = flavorResultMap5.get(flavorNumber) * 0.8 + flavorResultMap4.get(flavorNumber) * 0.4;
            //double preNumber = flavorResultMap7.get(flavorNumber)*0.25 + flavorResultMap1.get(flavorNumber) * 0.8;
            flavorResultMap.put(flavorNumber,(int)Math.round(preNumber));
        }


/*
        //分配方案 1 背包
        List<Integer> cpu = new ArrayList<>();
        List<Integer> memory = new ArrayList<>();
        List<String> name = new ArrayList<>();
        // 初始
        cpu.add(0);
        memory.add(0);
        name.add("null");

       // int flavorTotal = 0; //预测的虚拟机总数
      //  List<String> flavorTypeNum = new ArrayList<>(); // 虚拟机规格名称1 虚拟机个数
        for (Map.Entry<Integer,Integer> map: flavorResultMap.entrySet()){
            int fNumber = map.getKey();
            int fTotal = map.getValue();
            for (int i = 0; i < fTotal; i++) {
                if (fTotal > 0){
                    name.add("flavor" + fNumber );
                    cpu.add(flavorInfo[fNumber][0]);
                    memory.add(flavorInfo[fNumber][1]/1024);
                }

            }
          //  flavorTotal += fTotal;
          //  flavorTypeNum.add("flavor"+ fNumber+ " " + fTotal);
        }

        //List<Map<String,Integer>> assignmentResult = Assignment.startAssignment(keyword,physicInfo[0],physicInfo[1],cpu,memory,name);
       List<Map<String,Integer>> assignmentResult = Assignment3.startAssignment(keyword,physicInfo[0],physicInfo[1],cpu,memory,name,preFlavorList);

*/
/*
        //分配方案2 粒子群
        int packFlavorNum = 0;
        for (Map.Entry<Integer, Integer> map : flavorResultMap.entrySet()) {
            int fTotal = map.getValue();
            packFlavorNum += fTotal;
        }
        int[][] packFlavorInfo = new int[packFlavorNum][2];
        String[] names = new String[packFlavorNum];
        int index = 0;
        for (Map.Entry<Integer, Integer> map : flavorResultMap.entrySet()) {
            int fNumber = map.getKey();
            int fTotal = map.getValue();
            for (int i = 0; i < fTotal; i++) {
                packFlavorInfo[index][0] = flavorInfo[fNumber][0];
                packFlavorInfo[index][1] = flavorInfo[fNumber][1] / 1024;
                names[index] = "flavor" + fNumber;
                index++;
            }
        }
        List<Map<String,Integer>> assignmentResult = GetAns.startAssignment(packFlavorNum,physicInfo[0],physicInfo[1],keyword,packFlavorInfo,names,preFlavorList);

        Map<String,Integer> flavorMap = new HashMap<>();   // 存储分配后的结果
        List<String> physicResult = new ArrayList<>();     // 存储分配的物理机输出信息
        for (int i = 0; i < assignmentResult.size() ; i++) {
            Map<String,Integer> tempMap = assignmentResult.get(i);
            StringBuilder stringBuilder  = new StringBuilder();
            stringBuilder.append(i+1 +" ");
            for (Map.Entry<String,Integer> entry:
                 tempMap.entrySet()) {
                stringBuilder.append(entry.getKey()+" "+entry.getValue()+" ");
                if (flavorMap.containsKey(entry.getKey())){
                    flavorMap.put(entry.getKey(),flavorMap.get(entry.getKey()) + entry.getValue());
                }else {
                    flavorMap.put(entry.getKey(),entry.getValue());
                }
            }
            physicResult.add(stringBuilder.toString());
         //  System.out.println(stringBuilder.toString());
        }

        // 以分配结果作为最后的虚拟机数量结果
        int flavorTotal = 0; //预测的虚拟机总数
        List<String> flavorTypeNum = new ArrayList<>(); // 虚拟机规格名称1 虚拟机个数
        for (int flavor: preFlavorList){
            String flavorStr = "flavor" + flavor;
            if (flavorMap.containsKey(flavorStr)){
                flavorTypeNum.add(flavorStr +" " +flavorMap.get(flavorStr));
                flavorTotal += flavorMap.get(flavorStr);
            }else {
                flavorTypeNum.add(flavorStr + " " +0);
            }

        }

//        if (assignmentResult.isEmpty()){
//            assignmentResult.add("0");
//        }
        history.add(flavorTotal+"");
        history.addAll(flavorTypeNum);
        history.add(System.getProperty("line.separator"));
        history.add(assignmentResult.size()+"");
        history.addAll(physicResult);

		for (int i = 0; i < history.size(); i++) {
			results[i] = history.get(i);
            //System.out.println(results[i]);
        }

        // 测试输出
*/
       //String testFilePath = "D:\\Works\\competition\\huawei\\资料\\练习数据\\初赛文档\\用例示例\\TestData_2015.2.20_2015.2.27.txt";
       //testResult(flavorResultMap,flavorMap,keyword,physicInfo[0],physicInfo[1],assignmentResult.size(),testFilePath,preFlavorList);
		return results;
	}

	public static void testResult( Map<Integer,Integer> firstResultMap,Map<String,Integer> lastFlavorMap,String keyword,int maxCpu,int maxMemory,int physicSize,String testFilePath,List<Integer> preFlavorList){
	    // 读取测试数据
        String[] testContent = FileUtil.read(testFilePath,null);
        Map<Integer,Integer> testMap = new HashMap<>(); // 测试集中每个虚拟机对应的个数
        for (String test: testContent) {
            String[] testStrs = test.split("\t");
            int flavorNum = Integer.parseInt(testStrs[1].substring(6));
            if (preFlavorList.contains(flavorNum)){
                if (testMap.containsKey(flavorNum)){
                    testMap.put(flavorNum,testMap.get(flavorNum)+1);
                }else {
                    testMap.put(flavorNum,1);
                }
            }
        }

        // 计算预测结果分数，打印输出
        int size = preFlavorList.size();
        double fenzi = 0.0;
        double fenmuLeft = 0.0;
        double fenmuRight = 0.0;
        double score = 0.0;
        for (Integer flavor: preFlavorList) {
            int pre = firstResultMap.get(flavor);

            int real;
            if (testMap.containsKey(flavor))
                real = testMap.get(flavor);
            else
                real = 0;

            System.out.println("初始预测 flavor" + flavor +": " + pre  + "  实际值：" + real);
            fenzi += Math.pow(real - pre,2);
            fenmuLeft += Math.pow(real,2);
            fenmuRight += Math.pow(pre,2);
        }
        score = 1- Math.sqrt(fenzi/size) / (Math.sqrt(fenmuLeft/size)+Math.sqrt(fenmuRight/size));
        System.out.println("初始预测分数：" + score);
        System.out.println();
        fenzi = 0.0;
        fenmuLeft = 0.0;
        fenmuRight = 0.0;
        score = 0.0;
        for (Integer flavor: preFlavorList) {
            int pre ;
            if (lastFlavorMap.containsKey("flavor"+flavor))
                pre = lastFlavorMap.get("flavor"+flavor);
            else
                pre = 0;
            int real;
            if (testMap.containsKey(flavor))
                real = testMap.get(flavor);
            else
                real = 0;
            System.out.println("分配后预测 flavor" + flavor +": " + pre+ "  实际值：" + real);
            fenzi += Math.pow(real - pre,2);
            fenmuLeft += Math.pow(real,2);
            fenmuRight += Math.pow(pre,2);
        }
        score = 1- Math.sqrt(fenzi/size) / (Math.sqrt(fenmuLeft/size)+Math.sqrt(fenmuRight/size));
        System.out.println("分配后预测分数：" + score);

        // 计算分配分数
        double rFlavor = 0.0;
        double rPhysic = 0.0;
        for (Integer flavor: preFlavorList) {
            int pre ;
            if (lastFlavorMap.containsKey("flavor"+flavor))
                pre = lastFlavorMap.get("flavor"+flavor);
            else
                pre = 0;
            if ("CPU".equals(keyword)){
                rFlavor += flavorInfo[flavor][0] * pre;
            }else {
                rFlavor += flavorInfo[flavor][1] * pre;
            }
        }
        if ("CPU".equals(keyword)){
            rPhysic = maxCpu * physicSize;
        }else {
            rPhysic = maxMemory * physicSize;
        }
        System.out.println();
        System.out.println("物理机资源利用率： " + rFlavor/rPhysic);

        System.out.println("评测函数值： " + score * (rFlavor/rPhysic));
    }

    /**
     *  获取时间索引，从 0 开始
     *
     * @param timeString 时间字符串
     * @param timeType  时间类型 0 为天，1 为小时
     * @return 时间的索引，从 0 开始
     * @TODO: 2018/3/12 暂时只实现时间类型为天的
     */
	public static int getTimeIndex(String timeString,int timeType){

        LocalDateTime localDateTime = LocalDateTime.parse(timeString,dateTimeFormatter);
        LocalDate localDate1 = LocalDate.from(trainStartDateTime);
        LocalDate localDate2 = LocalDate.from(localDateTime);

        long daysDiff = ChronoUnit.DAYS.between(localDate1,localDate2);

        return (int)daysDiff;
    }

    /**
     *
     * @param preStartTime 预测的开始时间
     * @param preDay 需要预测的天数
     * @return
     * @// TODO: 2018/3/12 现在直接使用前预测天数的数据量，后面加入预测模型,先实现常数回归，再实现高级模型
     *
     */
    public static Map<Integer,Integer> predictModel(int preStartTime,int preDay,List<Integer> preFlavorList){
        Map<Integer,Integer> returnMap = new HashMap<>();
        int[] flavor = new int[100];
        int dayPeriod = 1;

        //System.out.println(preDay);
        for (int i = preDay * dayPeriod; i > 0; i-- ){
            for (Integer preFlavor : preFlavorList) {
                flavor[preFlavor] += trainData[preStartTime - i][preFlavor];
            }

        }
        for (Integer preFlavor : preFlavorList){
            flavor[preFlavor] = (int)Math.rint( flavor[preFlavor] * 1.0 /(preDay * dayPeriod) * preDay);
            returnMap.put(preFlavor,flavor[preFlavor]);
            //System.out.println("flavor"+preFlavor +" : "+ flavor[preFlavor]);
        }
        return returnMap;
    }

    /**
     *   线性人工权重
     * @param preStartTime 预测的开始时间
     * @param preDay 需要预测的天数
     * @return
     *
     *
     */
    public static Map<Integer,Integer> predictModelLinear(int preStartTime,int preDay,List<Integer> preFlavorList){
        Map<Integer,Integer> returnMap = new HashMap<>();
        //int period = (preStartTime -1) / preDay ;
        int period = 4 ;
        int[] flavor = new int[100];
        int dayPeriod = 1;
        int flavorSize = preFlavorList.size();
        double[][] trainMatrixOne = new double[flavorSize][period];  // 历史每 K 天的总虚拟机数

        for (int i = 0; i < flavorSize; i++) {
            // 存储每个 flavor 信息
            for (int j = 0; j < period; j++) {
                double total = 0;
                for (int k = 0; k < preDay; k++) {
                    total += trainData[preStartTime - (j + 1) * preDay + k][preFlavorList.get(i)];
                }
                trainMatrixOne[i][j] = total;
              //  System.out.println("flavor:" + i +"  period:" + j);
              //  System.out.println("trainMatrixOne: " + trainMatrixOne[i][j]);

            }
        }

        for (int i = 0; i < flavorSize; i++) {
            double number = 0;

            number = trainMatrixOne[i][0] * 0.55 + trainMatrixOne[i][1] * 0.38;

            returnMap.put(preFlavorList.get(i),(int)Math.floor(number));
        }
        return returnMap;
    }

    public static double lossFuction(double y, double[] train,double distance) {
        double loss = 0;
        double numerator = 0; // 分子
        double denominator = 0; // 分母
        double denominatorLeft = 0;
        double denominatorRight = 0;
        if (distance > 1) {
            // 以周期形式
            for (int i = 0; i < train.length; i++) {
                loss += (y - train[i]) / (y + train[i]) * (1.0 / ((i + 1) * distance));
            }
        } else {
            // 以每日形式
            for (int i = 0; i < train.length; i++) {
                loss += (y - train[i]) / (y + train[i]) * (1.0 / (train.length - i));
            }
        }
        return Math.abs(loss);
    }

    public static double lossFuction2(double y, double[] train,double distance){
        double loss = 0;
        double weight;
        if (distance > 1){
            // 以周期形式
            for (int i = 0; i < train.length; i++) {
                weight = 1.0 / ((i+1)*distance);
                loss += (y - train[i])/(y + train[i]) * weight;
            }
        }else {
            // 以每日形式
            for (int i = 0; i < train.length; i++) {
                weight = 1.0 /(train.length - i );
                loss += (y - train[i])/(y + train[i]) * weight;
            }
        }
        return Math.abs(loss);
    }

    public static double getMin(double[] array){
        double minValue = array[0];
        for (int i = 0; i<array.length;i++){
            if (array[i]<minValue)
                minValue = array[i];
        }
        return minValue;

    }
    public static double getMax(double[] array){
        double maxValue = array[0];
        for (int i = 0; i<array.length;i++){
            if (array[i]>maxValue)
                maxValue = array[i];
        }
        return maxValue;

    }

    public static Map<Integer,Integer> predictModelConstantRegression(int preStartTime,List<Integer> preFlavorList){
        Map<Integer,Integer> returnMap = new HashMap<>();
        int[] flavor = new int[100];
        int dayPeriod = 1;
        int flavorSize = preFlavorList.size();
        double[][] trainMatrixTwo = new double[flavorSize][preStartTime];  // 每个虚拟机型号每天的数量 * preDay

        //System.out.println(preDay);
        for (int i = 0; i < preStartTime; i++ ){
            for (int j = 0; j < flavorSize; j++) {
                //System.out.println(i+" "+ j + " " + trainData[i][preFlavorList.get(j)]);
                trainMatrixTwo[j][i] = trainData[i][preFlavorList.get(j)]  * 1.0 ;
                //System.out.println(trainMatrixTwo[j][i]);
            }
        }
        for (int i = 0; i < flavorSize; i++) {
            List<Double> lossList = new ArrayList<>();
            List valueList = Arrays.asList(trainMatrixTwo[i]);
            int min = (int)getMin(trainMatrixTwo[i]);
            int max = (int)getMax(trainMatrixTwo[i]);
           // System.out.println(min +" "+ max);
            for (int j = min; j <= max; j++) {
                lossList.add(lossFuction(j,trainMatrixTwo[i],1));
            }
            double value = Collections.min(lossList);
           // System.out.println(value);
            returnMap.put(preFlavorList.get(i),lossList.indexOf(value) + min);
        }
        return returnMap;
    }
    /**
     *  常数回归模型
     *
     *  样本，设预测天数为 K ，
     *  1. 历史每 K 天的总虚拟机数
     *  2. 历史每天虚拟机数 ,预测 K 天    ##
     *  3. 历史每两天虚拟机数 * K/2
     *
     * @param preStartTime
     * @param preFlavorList
     * @return
     */
    public static Map<Integer,Integer> predictModelConstantRegressionTwo(int preStartTime,int preDay,List<Integer> preFlavorList){
        Map<Integer,Integer> returnMap = new HashMap<>();
        for (int i = 0; i < preDay; i++) {
            Map<Integer,Integer> map  = predictModelConstantRegression(preStartTime+i,preFlavorList);
            for (Map.Entry<Integer,Integer> entry: map.entrySet()) {
                int flavor = entry.getKey();
                int num = entry.getValue();
                if (returnMap.containsKey(flavor)){
                    returnMap.put(flavor,returnMap.get(flavor)+num);
                }else {
                    returnMap.put(flavor,num);
                }
            }
        }
        return returnMap;
    }
    /**
     *  常数回归模型
     *
     *  样本，设预测天数为 K ，
     *  1. 历史每 K 天的总虚拟机数
     *  2. 历史每天虚拟机数 * K    ##
     *  3. 历史每 p 天虚拟机数 * K/p
     *
     * @param preStartTime
     * @param preDay
     * @param preFlavorList
     * @return
     */
    public static Map<Integer,Integer> predictModelConstantRegressionThree(int p,int preStartTime,int preDay,List<Integer> preFlavorList){
        Map<Integer,Integer> returnMap = new HashMap<>();
        int period = (preStartTime -1) / p ;

        int[] flavor = new int[100];
        int dayPeriod = 1;
        int flavorSize = preFlavorList.size();
        double[][] trainMatrixTwo = new double[flavorSize][preStartTime];  // 每个虚拟机型号每天的数量 * preDay

        //System.out.println(preDay);
        for (int i = 0; i < preStartTime; i++ ){
            for (int j = 0; j < flavorSize; j++) {
                //System.out.println(i+" "+ j + " " + trainData[i][preFlavorList.get(j)]);
                trainMatrixTwo[j][i] = trainData[i][preFlavorList.get(j)] * preDay * 1.0 ;
                //System.out.println(trainMatrixTwo[j][i]);
            }
        }
        for (int i = 0; i < flavorSize; i++) {
            List<Double> lossList = new ArrayList<>();
            List valueList = Arrays.asList(trainMatrixTwo[i]);
            int min = (int)getMin(trainMatrixTwo[i]);
            int max = (int)getMax(trainMatrixTwo[i]);
            // System.out.println(min +" "+ max);
            for (int j = min; j <= max; j++) {
                lossList.add(lossFuction(j,trainMatrixTwo[i],1));
            }
            double value = Collections.min(lossList);
            // System.out.println(value);
            returnMap.put(preFlavorList.get(i),lossList.indexOf(value) + min);
        }
        return returnMap;
    }
    public static boolean isWeekDay(int dayIndex){

        LocalDate localDate1 = LocalDate.from(trainStartDateTime);
        LocalDate localDate2 = localDate1.plusDays(dayIndex);
        int week = localDate2.getDayOfWeek().getValue();
        //long daysDiff = ChronoUnit.DAYS.between(localDate1,localDate2);
        if (week == 6 || week==7)
            return true;
        else
            return false;
    }
    /**
     *  常数回归模型 One
     *
     *  样本，设预测天数为 K ，
     *  1. 历史每 K 天的总虚拟机数   ##
     *  2. 历史每天虚拟机数 * K
     *  3. 历史每两天虚拟机数 * K/2
     *
     * @param preStartTime
     * @param preDay
     * @param preFlavorList
     * @return
     */
    public static Map<Integer,Integer> predictModelConstantRegressionOne(int preStartTime,int preDay,List<Integer> preFlavorList){
        Map<Integer,Integer> returnMap = new HashMap<>();
        //int period = (preStartTime -1) / preDay ;
        int period = 2 ;
        int[] flavor = new int[100];
        int dayPeriod = 1;
        int flavorSize = preFlavorList.size();
        double[][] trainMatrixOne = new double[flavorSize][period];  // 历史每 K 天的总虚拟机数

        for (int i = 0; i < flavorSize; i++) {
            // 存储每个 flavor 信息
            for (int j = 0; j < period; j++) {
                double total = 0;
                double weekDayCount = 0;
                for (int k = 0; k < preDay; k++) {
                    total += trainData[preStartTime - (j + 1) * preDay + k][preFlavorList.get(i)];
                }
                trainMatrixOne[i][j] = total;
               // System.out.println("flavor:" + i +"  period:" + j);
               // System.out.println("trainMatrixOne: " + trainMatrixOne[i][j]);

            }
        }

        for (int i = 0; i < flavorSize; i++) {
            List<Double> lossList = new ArrayList<>();
            int min = (int)getMin(trainMatrixOne[i]);
            int max = (int)getMax(trainMatrixOne[i]);
           // System.out.println(min +" "+ max);
            for (int j = min; j <= max; j++) {
                lossList.add(lossFuction(j,trainMatrixOne[i],preDay));
            }
            double value = Collections.min(lossList);
           // System.out.println(value);
            returnMap.put(preFlavorList.get(i),lossList.indexOf(value) + min);
        }
        return returnMap;
    }
    /**
     * 周期因子规则
     * @param preStartTime
     * @param preDay
     * @param preFlavorList
     * 数据太稀疏，周期不敏感，结果不好
     * @return
     */
    public static Map<Integer,Integer> predictModelPeriodRule(int preStartTime,int preDay,List<Integer> preFlavorList){
        Map<Integer,Integer> returnMap = new HashMap<>();
        //int period = (preStartTime -1) / preDay ;    //平移周期
        int period = 2 ;
        int flavorSize = preFlavorList.size();
        // 转换训练矩阵为周期矩阵
        double[][][] periodMatrix = new double[flavorSize][period][preDay];
        double[][][] periodFactorMatrix = new double[flavorSize][period][preDay]; // periodMatrix 除以 周期均值
        double[][] periodFactor = new double[flavorSize][preDay];    // 每个虚拟机的周因子
        double[][] periodMean = new double[flavorSize][period];  // 每个虚拟机的周期均值
        int[][] predictDayValue = new int[flavorSize][preDay];  // 最后一周预测值
        double[] flavorBase = new double[flavorSize];
        int baseDay = preDay;
        for (int i = 0; i < flavorSize; i++) {
            // 存储每个 flavor 信息
            for (int j = 0; j < period; j++) {
                double total = 0;
                for (int k = 0; k < preDay; k++) {
                    periodMatrix[i][j][k]= trainData[preStartTime - preDay * (j + 1) + k][preFlavorList.get(i)] * 1.0;
                    total += periodMatrix[i][j][k];
                    if (j == period-1 && k> period-baseDay)
                        flavorBase[i] += periodMatrix[i][j][k];
                }
              //  System.out.println("flavor:" + i +"  period:" + j);
              //  System.out.println("total: " + total);

                periodMean[i][j] = total / preDay;
               // System.out.println("periodMean: " + periodMean[i][j]);
                // 存储周期因子矩阵
                for (int k = 0; k < preDay; k++) {
                    if (periodMean[i][j] == 0)
                        periodFactorMatrix[i][j][k] = 0;
                    else
                        periodFactorMatrix[i][j][k] = periodMatrix[i][j][k] / periodMean[i][j];

                }
            }
            flavorBase[i] = flavorBase[i]/baseDay;
        }
        // 遍历周期因子矩阵 计算每个虚拟机的周因子 * base 最后一周平均
        for (int i = 0; i < flavorSize; i++) {
            int flavorNum = 0;
            for (int j = 0; j < preDay; j++) {
                List<Double> factorArr = new ArrayList<>();
                for (int k = 0; k < period; k++) {
                      factorArr.add(periodFactorMatrix[i][k][j]);
                }
                Collections.sort(factorArr);
                //中位数
                double mid = 0;
                int len = factorArr.size();
                if(len%2==0)
                    mid = (factorArr.get((len-1)/2)+factorArr.get(len/2))/2;
                else
                    mid = factorArr.get(len/2);
                periodFactor[i][j] = mid;
                double sum = 0;
                for (double d : factorArr) {
                    sum +=d;
                }
                double average = sum / factorArr.size();

                //System.out.println("factor: "+i+" average: "+ average+ " mid: " + mid);
               // predictDayValue[i][j] = (int)Math.floor(average * periodMean[i][period-1]);  // 每天的周因子乘以最后一周的周平均
                predictDayValue[i][j] = (int)Math.floor(average * flavorBase[i]);
                flavorNum += predictDayValue[i][j];
            }
            returnMap.put(preFlavorList.get(i),flavorNum);
        }

        return returnMap;
    }

    /**
     *   一次平滑
     * @param trainMatrixOne 输入矩阵
     * @param period 需要预测周期
     * @param a   平滑参数
     * @return
     *
     *
     */
    public static double[][] predictModelOneSmooth(double[][] trainMatrixOne,int period,int flavorSize,double a){
        Map<Integer,Integer> returnMap = new HashMap<>();

        double[][] smoothMatrix = new double[flavorSize][period];   // 平滑值矩阵

        for (int i = 0; i < flavorSize; i++) {
            for (int j = 0; j < period; j++) {
                double pre = 0.0;
                if (j != 0){
                    pre = a * trainMatrixOne[i][j] + (1-a)* smoothMatrix[i][j-1];
                }else {
                    pre = (trainMatrixOne[i][0] + trainMatrixOne[i][1]+ trainMatrixOne[i][2])/3;
                }
                smoothMatrix[i][j] = pre;
            }
        }

        return smoothMatrix;
    }

    /**
     *   二次平滑
     * @param preStartTime 预测的开始时间
     * @param preDay 需要预测的天数
     * @param a   平滑参数
     * @return
     *
     *
     */
    public static Map<Integer,Integer> predictModelTwoSmooth(int preStartTime,int preDay,List<Integer> preFlavorList,double a){
        Map<Integer,Integer> returnMap = new HashMap<>();
        //int period = (preStartTime -1) / preDay ;
        int period = 3 ;
        int[] flavor = new int[100];

        int flavorSize = preFlavorList.size();
        double[][] trainMatrixOne = new double[flavorSize][period];  // 历史每 K 天的总虚拟机数


        for (int i = 0; i < flavorSize; i++) {
            // 存储每个 flavor 信息
            for (int j = 0; j < period; j++) {
                double total = 0;
                for (int k = 0; k < preDay; k++) {
                    total += trainData[preStartTime - (j + 1) * preDay + k][preFlavorList.get(i)];
                }
                trainMatrixOne[i][j] = total;
                //  System.out.println("flavor:" + i +"  period:" + j);
                //  System.out.println("trainMatrixOne: " + trainMatrixOne[i][j]);
            }
        }
        double[][] smoothMatrix1 = predictModelOneSmooth(trainMatrixOne,period, flavorSize,a);   // 一次平滑值矩阵
        double[][] smoothMatrix2 = predictModelOneSmooth(smoothMatrix1,period, flavorSize,a);    // 二次平滑值矩阵

        for (int i = 0; i < flavorSize; i++) {

            double at = 0.0,bt = 0.0;
            at = 2 * smoothMatrix1[i][period - 1] - smoothMatrix2[i][period - 1];
            bt = (a/(1-a)) * (smoothMatrix1[i][period - 1] - smoothMatrix2[i][period - 1]);
            returnMap.put(preFlavorList.get(i),(int)Math.floor(at + bt * 1));
        }
        return returnMap;

    }
    public static Map<Integer,Integer> predictModelTwoSmooth2(int preStartTime,int preDay,List<Integer> preFlavorList,double a){
        Map<Integer,Integer> returnMap = new HashMap<>();
        int period = (preStartTime -1) / preDay ;
        //int period = 4 ;
        int[] flavor = new int[100];

        int flavorSize = preFlavorList.size();
        double[][] trainMatrixOne = new double[flavorSize][period];  // 历史每 K 天的总虚拟机数


        for (int i = 0; i < flavorSize; i++) {
            // 存储每个 flavor 信息
            for (int j = 0; j < period; j++) {
                double total = 0;
                for (int k = 0; k < preDay; k++) {
                    total += trainData[preStartTime - (j + 1) * preDay + k][preFlavorList.get(i)];
                }
                trainMatrixOne[i][j] = total;
                //  System.out.println("flavor:" + i +"  period:" + j);
                //  System.out.println("trainMatrixOne: " + trainMatrixOne[i][j]);
            }
        }
        for (int i = 0; i < flavorSize; i++) {
            double value = getExpect(trainMatrixOne[i],1,a);

            returnMap.put(preFlavorList.get(i),(int)Math.floor(value));
        }
        return returnMap;
    }
    private static Double getExpect(double[] dataArr, int year, Double modulus ) {


        Double modulusLeft = 1 - modulus;

        Double lastIndex = dataArr[0];
        Double lastSecIndex = dataArr[0];

        for (Double data :dataArr) {
            lastIndex = modulus * data + modulusLeft * lastIndex;
            lastSecIndex = modulus * lastIndex + modulusLeft * lastSecIndex;
        }

        Double a = 2 * lastIndex - lastSecIndex;
        Double b = (modulus / modulusLeft) * (lastIndex - lastSecIndex);

        return a + b * year;
    }
    /**
     *   三次平滑
     * @param preStartTime 预测的开始时间
     * @param preDay 需要预测的天数
     * @param a   平滑参数
     * @return
     *
     *
     */
    public static Map<Integer,Integer> predictModelThreeSmooth(int preStartTime,int preDay,List<Integer> preFlavorList,double a){
        Map<Integer,Integer> returnMap = new HashMap<>();
        int period = (preStartTime -1) / preDay ;
        //int period = 4 ;
        int[] flavor = new int[100];

        int flavorSize = preFlavorList.size();
        double[][] trainMatrixOne = new double[flavorSize][period];  // 历史每 K 天的总虚拟机数


        for (int i = 0; i < flavorSize; i++) {
            // 存储每个 flavor 信息
            for (int j = 0; j < period; j++) {
                double total = 0;
                for (int k = 0; k < preDay; k++) {
                    total += trainData[preStartTime - (j + 1) * preDay + k][preFlavorList.get(i)];
                }
                trainMatrixOne[i][j] = total;
                //  System.out.println("flavor:" + i +"  period:" + j);
                //  System.out.println("trainMatrixOne: " + trainMatrixOne[i][j]);
            }
        }
        double[][] smoothMatrix1 = predictModelOneSmooth(trainMatrixOne,period, flavorSize,a);   // 一次平滑值矩阵
        double[][] smoothMatrix2 = predictModelOneSmooth(smoothMatrix1,period, flavorSize,a);    // 二次平滑值矩阵
        double[][] smoothMatrix3 = predictModelOneSmooth(smoothMatrix2,period, flavorSize,a);

        for (int i = 0; i < flavorSize; i++) {

            double at = 0.0,bt = 0.0,ct = 0.0;
            at = 3 * smoothMatrix1[i][period - 1] - 3*smoothMatrix2[i][period - 1] +smoothMatrix3[i][period - 1] ;
            bt = a / (2 * (1 - a) * (1 - a)) * ((6 - 5 * a) * smoothMatrix1[i][period - 1] - (2 * (5 - 4 * a)) * smoothMatrix2[i][period - 1] + (4 - 3 * a) * smoothMatrix3[i][period - 1]);
            ct = a * a / (2 * (1 - a) * (1 - a)) * (smoothMatrix1[i][period - 1] - 2 * smoothMatrix2[i][period - 1] + smoothMatrix3[i][period - 1]);
            returnMap.put(preFlavorList.get(i),(int)Math.floor(Math.abs(at + bt * 1 + ct *1*1)));
        }
        return returnMap;

    }

    /**
     *  前 preday 天的平均值作为当天预测值
     * @param preStartTime
     * @param preDay
     * @param preFlavorList
     * @return
     */
    public static Map<Integer,Integer> predictModelAverage(int preStartTime,int preDay,List<Integer> preFlavorList){
        Map<Integer,Integer> returnMap = new HashMap<>();
        int flavorSize = preFlavorList.size();
        double[][] trainMatrix = new double[flavorSize][preDay * 2];

        for (int i = 0; i < flavorSize; i++) {
            for (int j = 0; j < preDay; j++) {
                if (j==0){
                    double total = 0;
                    for (int k = 0; k < preDay; k++) {
                        trainMatrix[i][k] = trainData[preStartTime - preDay + k][preFlavorList.get(i)];
                        total += trainMatrix[i][k] ;
                    }
                    trainMatrix[i][j+preDay] = total / preDay;
                }else {
                    double total = 0;
                    for (int k = j; k < j + preDay; k++) {
                        total += trainMatrix[i][k] ;
                    }
                    trainMatrix[i][j + preDay] = total / preDay;
                }
            }

            }
        for (int i = 0; i < flavorSize; i++) {
            double total = 0;
            for (int k = preDay ; k < preDay*2; k++) {
                total += trainMatrix[i][k];
            }
            returnMap.put(preFlavorList.get(i),(int)Math.ceil(total));
        }
        return returnMap;
    }


        /**
         *  构造样本并使用回归预测
         * @param preStartTime
         * @param preDay
         * @param preFlavorList
         * @return
         */
        /*
    public static Map<Integer,Integer> makeRegression(int preStartTime,int preDay,List<Integer> preFlavorList){
        int sampleNum = 3;  // 样本数量
        int featureNum = 6;
        double a = 0.25;     //平滑参数
        int size = preFlavorList.size();
        double[][][] trainMatrix = new double[size][sampleNum][featureNum];
        double[][] Y  = new double[size][sampleNum];    //实际值
        double[][] lastData = new double[size][featureNum];   // 最近一次待预测样本
        Map<Integer,Integer> returnMap = new HashMap<>();

        // 构造样本
        for (int i = 1; i <= sampleNum; i++) {
            Map<Integer,Integer> flavorResultMap1 = predictModel(preStartTime - preDay*i,preDay,preFlavorList);
           // Map<Integer,Integer> flavorResultMap2 = predictModelPeriodRule(preStartTime - preDay*i,preDay,preFlavorList);
            Map<Integer,Integer> flavorResultMap3 = predictModelConstantRegressionTwo(preStartTime - preDay*i,preDay,preFlavorList);
            Map<Integer,Integer> flavorResultMap4 = predictModelConstantRegressionOne(preStartTime - preDay*i,preDay,preFlavorList);
            Map<Integer,Integer> flavorResultMap5 = predictModelLinear(preStartTime - preDay*i,preDay,preFlavorList);
            Map<Integer,Integer> flavorResultMap6 = predictModelTwoSmooth(preStartTime - preDay*i,preDay,preFlavorList,a);
            Map<Integer,Integer> flavorResultMap7 = predictModelThreeSmooth(preStartTime - preDay*i,preDay,preFlavorList,a);

            for (int j = 0; j < size; j++) {
                trainMatrix[j][i-1][0] = flavorResultMap1.get(preFlavorList.get(j));

                trainMatrix[j][i-1][1] = flavorResultMap3.get(preFlavorList.get(j));
                trainMatrix[j][i-1][2] = flavorResultMap4.get(preFlavorList.get(j));
                trainMatrix[j][i-1][3] = flavorResultMap5.get(preFlavorList.get(j));
                trainMatrix[j][i-1][4] = flavorResultMap6.get(preFlavorList.get(j));
                trainMatrix[j][i-1][5] = flavorResultMap7.get(preFlavorList.get(j));
                //trainMatrix[j][i-1][6] = flavorResultMap2.get(preFlavorList.get(j));
            }
        }
        // 实际值
        for (int i = 1; i <= sampleNum; i++) {

            for (int j = 0; j < size; j++) {
                double total = 0;
                for (int k = 0; k < preDay; k++) {
                    total += trainData[preStartTime - i * preDay + k][preFlavorList.get(j)];
                }
                Y[j][i-1] = total;
            }
        }

        Map<Integer,Integer> flavorResultMap1 = predictModel(preStartTime ,preDay,preFlavorList);
       /// Map<Integer,Integer> flavorResultMap2 = predictModelPeriodRule(preStartTime,preDay,preFlavorList);
        Map<Integer,Integer> flavorResultMap3  = predictModelConstantRegressionTwo(preStartTime ,preDay,preFlavorList);
        Map<Integer,Integer> flavorResultMap4 = predictModelConstantRegressionOne(preStartTime ,preDay,preFlavorList);
        Map<Integer,Integer> flavorResultMap5 = predictModelLinear(preStartTime,preDay,preFlavorList);
        Map<Integer,Integer> flavorResultMap6 = predictModelTwoSmooth(preStartTime,preDay,preFlavorList,a);
        Map<Integer,Integer> flavorResultMap7= predictModelThreeSmooth(preStartTime ,preDay,preFlavorList,a);

        for (int j = 0; j < size; j++) {
            lastData[j][0] = flavorResultMap1.get(preFlavorList.get(j));

            lastData[j][1] = flavorResultMap3.get(preFlavorList.get(j));
            lastData[j][2] = flavorResultMap4.get(preFlavorList.get(j));
            lastData[j][3] = flavorResultMap5.get(preFlavorList.get(j));
            lastData[j][4] = flavorResultMap6.get(preFlavorList.get(j));
            lastData[j][5] = flavorResultMap7.get(preFlavorList.get(j));
            //lastData[j][6] = flavorResultMap2.get(preFlavorList.get(j));
        }

        for (int i = 0; i < size; i++) {
            double[] K = new double[featureNum + 1];
            double result =  Regression.LineRegression(trainMatrix[i],Y[i],K,featureNum,sampleNum);

            if (result == 0){
                returnMap.put(preFlavorList.get(i),flavorResultMap4.get(preFlavorList.get(i)));
            }else {
                double preNum = K[0];
                for (int k = 1; k < K.length; k++) {
                    preNum += K[k] * lastData[i][k-1];
                }
                returnMap.put(preFlavorList.get(i),(int)Math.ceil(preNum));
            }

        }
        return returnMap;
    }
    */
}
