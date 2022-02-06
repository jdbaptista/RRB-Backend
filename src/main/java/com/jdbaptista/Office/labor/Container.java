package com.jdbaptista.Office.labor;

import java.util.Date;

public record Container(
        String name,
        String address,
        Date date,
        String task,
        double time,
        int wcCode,
        double multiplier
) {
}
