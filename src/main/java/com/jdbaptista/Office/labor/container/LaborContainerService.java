package com.jdbaptista.Office.labor.container;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class LaborContainerService {
    private ArrayList<LaborContainer> containers;

    public LaborContainerService() {
        containers = new ArrayList<LaborContainer>();
    }

    public void addNewContainer(LaborContainer container) {
        containers.add(container);
    }

    public List<LaborContainer> getContainers() {
        return containers;
    }
}
