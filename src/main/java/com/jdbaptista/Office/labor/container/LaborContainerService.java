package com.jdbaptista.Office.labor.container;

import com.jdbaptista.Office.AppConfig;
import com.jdbaptista.Office.*;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class LaborContainerService {

    private static ApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);

    private ArrayList<LaborContainer> containers;

    public LaborContainerService() {
        containers = new ArrayList<LaborContainer>();
    }

    public String addNewContainer(LaborContainer container) {
        containers.add(container);
        return context.getBean(Office.class).officeConfig.MATERIALS_PATH;
    }

    public List<LaborContainer> getContainers() {
        return containers;
    }
}
