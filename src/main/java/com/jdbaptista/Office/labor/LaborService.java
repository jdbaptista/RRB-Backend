package com.jdbaptista.Office.labor;

import com.jdbaptista.Office.AppConfig;
import com.jdbaptista.Office.*;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.ContextStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;

@Service
public class LaborService {

    private static ApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
    private final Path fileStorageLocation;

    private ArrayList<LaborContainer> containers;
    private File inFile;

    @Autowired
    public LaborService(ReportFileProperties fileProperties) {
        this.fileStorageLocation = Paths.get(fileProperties.getUploadDir())
                .toAbsolutePath().normalize();

        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    @EventListener(ApplicationReadyEvent.class)
    public void init() {
        try {
            inFile = new File(context.getBean(Office.class).officeConfig.LABOR_PATH + "/containers.xlsx");
            containers = parseContainers();
        } catch (Exception e) {
            containers = null;
            inFile = null;
            System.out.println("Material containers failed to load because");
            System.out.println("the material containers.xlsx could not be opened.");
        }
    }

    public ArrayList<LaborContainer> parseContainers() {
        ArrayList<LaborContainer> ret = new ArrayList<>();
        int day;
        int month;
        int year;
        String name;
        String task;
        int time;
        int wcCode;

        // try to open the input file
        Workbook wb;
        Sheet sheet = null;
        try {
            wb = WorkbookFactory.create(inFile);
            sheet = wb.getSheetAt(0);
        } catch (IOException e) {
            System.out.println("The labor containers.xlsx file is formatted incorrectly.");
            System.out.println("Check the filepath " + inFile.getAbsolutePath());
        }

        // try to read the data
        for (Row row : sheet) {
            Iterator<Cell> cellIterator = row.cellIterator();
            Cell cell;
            try {
                cell = cellIterator.next();
                day = (int) Double.parseDouble(cell.toString());
                cell = cellIterator.next();
                month = (int) Double.parseDouble(cell.toString());
                cell = cellIterator.next();
                year = (int) Double.parseDouble(cell.toString());
                cell = cellIterator.next();
                name = cell.toString();
                cell = cellIterator.next();
                task = cell.toString();
                cell = cellIterator.next();
                time = (int) Double.parseDouble(cell.toString());
                cell = cellIterator.next();
                wcCode = (int) Double.parseDouble(cell.toString());
            } catch (Exception e) {
                System.out.println(e);
                System.out.println("Something went wrong reading data.");
                return ret;
            }
            LaborContainer c = new LaborContainer(day, name, task, time, wcCode);
            ret.add(c);
        }
        return ret;
    }

    public String addNewContainer(LaborContainer container) {
        if (containers.contains(container)) return "POST UNSUCCESSFUL \n CONTAINER ALREADY EXISTS";

        Workbook wb = null;
        Sheet sheet = null;
        try {
            FileInputStream myxlsx = new FileInputStream(inFile);
            wb = new XSSFWorkbook(myxlsx);
            sheet = wb.getSheetAt(0);
        } catch (IOException e) {
            System.out.println("The labor containers.xlsx file is formatted incorrectly.");
            System.out.println("Check the filepath " + inFile.getAbsolutePath());
        }
        Row newRow = sheet.createRow(sheet.getLastRowNum() + 1);
        try {
            int cellNum = 0;
            Cell cell = newRow.createCell(cellNum++);
            cell.setCellValue(container.getDay());
            cell = newRow.createCell(cellNum++);
            cell.setCellValue(1);
            cell = newRow.createCell(cellNum++);
            cell.setCellValue(1);
            cell = newRow.createCell(cellNum++);
            cell.setCellValue(container.getName());
            cell = newRow.createCell(cellNum++);
            cell.setCellValue(container.getTask());
            cell = newRow.createCell(cellNum++);
            cell.setCellValue(container.getTime());
            cell = newRow.createCell(cellNum++);
            cell.setCellValue(container.getWcCode());
        } catch (Exception e) {
            System.out.println(e);
            return "POST UNSUCCESSFUL\nERROR WRITING TO FILE";
        }

        try {
            OutputStream fileOut = new FileOutputStream(inFile);
            wb.write(fileOut);
            fileOut.close();
            wb.close();
        } catch (Exception e) {
            System.out.println(e);
        }

        containers.add(container);

        return "POST SUCCESSFUL";
    }

    public ArrayList<LaborContainer> getContainers() {
        return containers;
    }

    public Resource loadFileAsResource(String fileName) {
        try {
            Path filePath = this.fileStorageLocation.resolve(fileName).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists()) {
                return resource;
            } else {
                System.out.println("file not found");
            }
        } catch (Exception e) {
            System.out.println(e);
        }
        return null;
    }

}
