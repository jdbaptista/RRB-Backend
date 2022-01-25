package com.jdbaptista.Office.labor;

import org.apache.poi.ss.usermodel.Sheet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class Job {
    final private String address;
    final private HashMap<Month, Sheet> months;

    public Job(String address) {
        this.address = address;
        months = new HashMap<>();
    }

    public void addMonth(Month month) {
        months.put(month, null);
    }

    public ArrayList<Month> getMonths() {
        return new ArrayList<>(months.keySet());
    }

    public Month getMonthRef(Month query) {
        if (!months.containsKey(query)) return null;
        for (Month month : months.keySet()) {
            if (month.equals(query)) {
                return month;
            }
        }
        return null;
    }

    public String getAddress() {
        return address;
    }

    @Override
    public String toString() {
        return address;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Job job = (Job) o;
        return job.getAddress().equals(address);
    }

    @Override
    public int hashCode() {
        return Objects.hash(address);
    }
}
