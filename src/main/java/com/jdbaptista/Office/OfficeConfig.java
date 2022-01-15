package com.jdbaptista.Office;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class OfficeConfig {
    public final String MATERIALS_PATH;
    public final String LABOR_PATH;

    public OfficeConfig(String MATERIALS_PATH, String LABOR_PATH) {
        this.MATERIALS_PATH = MATERIALS_PATH;
        this.LABOR_PATH = LABOR_PATH;
    }
}
