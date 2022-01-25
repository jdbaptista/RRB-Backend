package com.jdbaptista.Office.labor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class Month {
    final private int month;
    final private int year;
    final public String strMonth;
    final private ArrayList<Container> containers;
    public HashMap<Integer, double[]> dailyTotals;
    public HashMap<String, HashMap<String, double[]>> taskTotals;


    public Month(String month, int year) {
        this.strMonth = month;
        this.month = LaborGenerator.convertMonth(month);
        this.year = year;
        containers = new ArrayList<>();
        dailyTotals = new HashMap<>();
        taskTotals = new HashMap<>();
    }

    public void calculateDailyTotals() {
        for (Container container : containers) {
            if (!dailyTotals.containsKey(container.day)) {
                double[] containerTotals = {container.amount, container.time, container.wc, container.tax};
                dailyTotals.put(container.day, containerTotals);
            } else {
                double[] dayTotals = dailyTotals.get(container.day);
                dayTotals[0] += container.amount;
                dayTotals[1] += container.time;
                dayTotals[2] += container.wc;
                dayTotals[3] += container.tax;
            }
        }

        double totalDailyAmount = 0;
        double totalDailyTime = 0;
        double totalDailyWC = 0;
        double totalDailyTax = 0;
        for (int day : dailyTotals.keySet()) {
            double[] dayTotal = dailyTotals.get(day);
            totalDailyAmount += dayTotal[0];
            totalDailyTime += dayTotal[1];
            totalDailyWC += dayTotal[2];
            totalDailyTax += dayTotal[3];
        }
        double[] monthTotal = new double[] {(int) (totalDailyAmount * 100) / 100d, (int) (totalDailyTime * 100) / 100d, (int) (totalDailyWC * 100) / 100d, (int) (totalDailyTax * 100) / 100d};
        dailyTotals.put(0, monthTotal);
    }

    public void calculateTaskTotals() {
        for (Container container : containers) {
            if (!taskTotals.containsKey(container.task)) {
                HashMap<String, double[]> workerTotal = new HashMap<>();
                double[] cellFormatted = {container.time, container.getTotal()};
                workerTotal.put(container.name, cellFormatted);
                taskTotals.put(container.task, workerTotal);
            } else {
                if (!taskTotals.get(container.task).containsKey(container.name)) {
                    double[] cellFormatted = new double[] {container.time, container.getTotal()};
                    taskTotals.get(container.task).put(container.name, cellFormatted);
                } else {
                    double[] totals = taskTotals.get(container.task).get(container.name);
                    totals[0] += container.time;
                    totals[1] += container.getTotal();
                }
            }
        }

        for (String task : taskTotals.keySet()) {
            HashMap<String, double[]> using = taskTotals.get(task);
            double totalTime = 0;
            double totalAmount = 0;
            for (String container : using.keySet()) {
                double[] array = using.get(container);
                totalTime += array[0];
                totalAmount += array[1];
            }
            double[] retArray = new double[] {totalTime, totalAmount};
            using.put("Total", retArray);
        }
    }

    public void addContainer(Container container) {
        containers.add(container);
    }

    public ArrayList<Container> getContainers() {
        return containers;
    }

    public Container getContainerRef(Container query) {
        if (!containers.contains(query)) return null;
        for (Container container : containers) {
            if (container.equals(query)) {
                return container;
            }
        }
        return null;
    }

    public int getMonth() {
        return month;
    }

    public int getYear() {
        return year;
    }

    @Override
    public String toString() {
        return month + "/" + year;
    }

    @Override
    public boolean equals(Object query) {
        if (this == query) return true;
        if (query == null || getClass() != query.getClass()) return false;
        Month month = (Month) query;
        return month.getMonth() == this.month && month.getYear() == this.year;
    }

    @Override
    public int hashCode() {
        return Objects.hash(month, year);
    }
}
