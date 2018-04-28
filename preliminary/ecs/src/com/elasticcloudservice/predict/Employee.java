package com.elasticcloudservice.predict;

import java.text.NumberFormat;

/**
 * @Author: Skye
 * @Date: 9:33 2018/3/23
 * @Description:
 */
public class Employee {
    private final String name ;
    private int id ;
    private static int lastid=0;
    {
        id = 5;
    }
    public Employee(){
        lastid++;
        name = "";
        id = 6;
    }
    public int getId() {
        return id;
    }
}
