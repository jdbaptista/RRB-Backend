package com.jdbaptista.Office.materials;

import com.jdbaptista.Office.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

@RestController
@RequestMapping(path="api/v1/materials")
public class MaterialsController {

    private final MaterialsService service;

    @Autowired
    public MaterialsController(MaterialsService service) {
        this.service = service;
    }

    @GetMapping
    public String getMaterials() {
    return service.getMaterials();
    }
}
