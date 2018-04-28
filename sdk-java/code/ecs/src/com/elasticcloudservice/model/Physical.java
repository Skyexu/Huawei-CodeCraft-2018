package com.elasticcloudservice.model;

/**
 * @Author: Skye
 * @Date: 0:23 2018/4/17
 * @Description:
 */
public class Physical implements Comparable<Physical>{
    private String name;
    private int cpu;
    private int memory;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCpu() {
        return cpu;
    }

    public void setCpu(int cpu) {
        this.cpu = cpu;
    }

    public int getMemory() {
        return memory;
    }

    public void setMemory(int memory) {
        this.memory = memory;
    }

    @Override
    public int compareTo(Physical o) {
        return this.cpu - o.cpu;
    }

    @Override
    public String toString() {
        return "Physical{" +
                "name='" + name + '\'' +
                ", cpu=" + cpu +
                ", memory=" + memory +
                '}';
    }
}
