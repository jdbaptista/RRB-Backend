package com.jdbaptista.Office.materials;

public record Container(String address,
                        String vendor,
                        double amount) {

    @Override
    public String toString() {
        return ("Container[" + address + ":" + vendor + ":" + amount + "]");
    }
}
