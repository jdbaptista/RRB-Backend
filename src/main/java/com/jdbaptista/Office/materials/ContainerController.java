package com.jdbaptista.Office.materials;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;

@RestController
@RequestMapping(path="api/v1/materials")
public class ContainerController {

    private final MaterialsService service;

    @Autowired
    public ContainerController(MaterialsService service) {
        this.service = service;
    }

    @GetMapping
    public File getMaterials() {
        System.out.println("Get request incoming.");
        return service.getMaterials();
    }
}
