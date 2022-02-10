package com.jdbaptista.Office.labor;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

public record Container(
        String name,
        String address,
        @JsonFormat(pattern="yyyy-MM-dd")
        Date date,
        String task,
        double time,
        int wcCode,
        double multiplier
) {
}
