package com.jdbaptista.Office;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Office {
    public OfficeConfig officeConfig;

    @Autowired
    public Office(OfficeConfig officeConfig) {
        this.officeConfig = officeConfig;
    }

}
