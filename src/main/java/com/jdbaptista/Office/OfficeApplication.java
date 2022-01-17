package com.jdbaptista.Office;

import com.jdbaptista.Office.labor.ReportFileProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

@SpringBootApplication
public class OfficeApplication {
	public static void main(String[] args) {
		SpringApplication.run(OfficeApplication.class, args);
	}
}
