package com.jdbaptista.Office.labor.container;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path="api/v1/laborContainers")
public class LaborContainerController {

    private final LaborContainerService laborContainerService;

    @Autowired
    public LaborContainerController(LaborContainerService laborContainerService) {
        this.laborContainerService = laborContainerService;
    }

    @PostMapping
    public void registerLaborContainer(@RequestBody LaborContainer container) {
        laborContainerService.addNewContainer(container);
    }

    @GetMapping
    public List<LaborContainer> getLaborContainers() {
        return laborContainerService.getContainers();
    }

}
