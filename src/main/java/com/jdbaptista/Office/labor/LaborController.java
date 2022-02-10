package com.jdbaptista.Office.labor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileUrlResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(path="api/v1/labor")
public class LaborController {

    private final LaborService service;

    @Autowired
    public LaborController(LaborService service) {
        this.service = service;
    }

    @PostMapping
    public String registerLaborContainer(@RequestBody Container container) {
        System.out.println("Post request incoming.");
        return service.addNewContainer(container);
    }

    @GetMapping
    public ArrayList<Container> getContainer() {
        System.out.println("Get request incoming.");
        return service.getContainers();
    }

}
