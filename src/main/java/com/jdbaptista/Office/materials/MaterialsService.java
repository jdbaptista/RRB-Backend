package com.jdbaptista.Office.materials;

import com.jdbaptista.Office.AppConfig;
import com.jdbaptista.Office.Office;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.Scanner;

@Service
public class MaterialsService {
    private static ApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);

    public String getMaterials() {
        try {
            File textFile = new File(context.getBean(Office.class).officeConfig.MATERIALS_PATH + "/input.txt");
            Scanner sc = new Scanner(textFile);
            return sc.nextLine();
        } catch (Exception e) {
            return e.toString();
        }
    }

}
