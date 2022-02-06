package com.jdbaptista.Office.labor;

import java.util.Objects;


/*
used in the labor generator (I don't want to waste time refactoring this to use Date instead of cell.getCellValue() so....
Dumb Container it is.
 */
public class DumbContainer {
    final public int day;
    final public String name;
    final public String task;
    final public double time;
    final public int type;
    final public double multiplier;
    public double amount;
    public double wc;
    public double tax;


    public DumbContainer(int day, String name, String task, double time, int type, double multiplier) {
        this.day = day;
        this.name = name;
        this.task = task;
        this.time = time;
        this.type = type;
        this.multiplier = multiplier;
        this.amount = -1;
    }

    public double getTotal() {
        return ((int) (amount) * 100) / 100d;
    }

    @Override
    public String toString() {
        return name + ", " + day;
    }

    @Override
    public boolean equals(Object query) {
        if (this == query) return true;
        if (query == null || getClass() != query.getClass()) return false;
        DumbContainer container = (DumbContainer) query;
        return day == container.day && Double.compare(container.time, time) == 0 && type == container.type && name.equals(container.name) && task.equals(container.task);
    }

    @Override
    public int hashCode() {
        return Objects.hash(day, name, task, time, type);
    }
}
