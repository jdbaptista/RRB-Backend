package com.jdbaptista.Office.labor;

public class LaborContainer {
    private int day;
    private String name;
    private String task;
    private double time;
    private int wcCode;
    private double multiplier;
    private double amount;
    private double wc;
    private double tax;

    public LaborContainer(int day, String name, String task, double time, int wcCode) {
        this.day = day;
        this.name = name;
        this.task = task;
        this.time = time;
        this.wcCode = wcCode;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public double getWc() {
        return wc;
    }

    public void setWc(double wc) {
        this.wc = wc;
    }

    public double getTax() {
        return tax;
    }

    public void setTax(double tax) {
        this.tax = tax;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTask() {
        return task;
    }

    public void setTask(String task) {
        this.task = task;
    }

    public double getTime() {
        return time;
    }

    public void setTime(double time) {
        this.time = time;
    }

    public int getWcCode() {
        return wcCode;
    }

    public void setWcCode(int wcCode) {
        this.wcCode = wcCode;
    }

    public double getMultiplier() {
        return multiplier;
    }

    public void setMultiplier(double multiplier) {
        this.multiplier = multiplier;
    }
}
