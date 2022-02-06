package com.jdbaptista.Office.labor;

import com.jdbaptista.Office.AppConfig;
import com.jdbaptista.Office.*;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

@Service
public class LaborService {

    private static ApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
    private final Path fileStorageLocation;

    private ArrayList<Container> containers;
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
            containers = new ArrayList<>();
            inFile = new File(context.getBean(Office.class).officeConfig.LABOR_PATH + "/containers.xlsx");
            System.out.println("Material containers failed to load because");
            System.out.println("the material containers.xlsx could not be opened.");
        }
    }

    public ArrayList<Container> parseContainers() {
        ArrayList<Container> ret = new ArrayList<>();
        String name;
        String address;
        String task;
        Date date;
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
                name = cell.toString();
                cell = cellIterator.next();
                address = cell.toString();
                cell = cellIterator.next();
                date = cell.getDateCellValue();
                System.out.println(date);
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
            Container c = new Container(name, address, date, task, time, wcCode, 0.0);
            ret.add(c);
        }
        return ret;
    }

    public String addNewContainer(Container container) {
        if (containers.contains(container)) return "POST UNSUCCESSFUL \n CONTAINER ALREADY EXISTS";

        Workbook wb = null;
        Sheet sheet = null;
        Row newRow = null;
        try {
            // input stream is needed bc .create will erase all previous data
            FileInputStream myxlsx = new FileInputStream(inFile);
            wb = new XSSFWorkbook(myxlsx);
            sheet = wb.getSheetAt(0);
            newRow = sheet.createRow(sheet.getLastRowNum() + 1);
        } catch (Exception e) {
            System.out.println("The labor containers.xlsx file is empty, generating new workbook");
            wb = new XSSFWorkbook();
            sheet = wb.createSheet();
            newRow = sheet.createRow(0);
        }
        try {
            int cellNum = 0;
            Cell cell = newRow.createCell(cellNum++);
            cell.setCellValue(container.name());
            cell = newRow.createCell(cellNum++);
            cell.setCellValue(container.address());
            cell = newRow.createCell(cellNum++);
            cell.setCellValue(container.date());
            CellStyle dateStyle = wb.createCellStyle();
            dateStyle.setDataFormat((short) 14);
            cell.setCellStyle(dateStyle);
            cell = newRow.createCell(cellNum++);
            cell.setCellValue(container.task());
            cell = newRow.createCell(cellNum++);
            cell.setCellValue(container.time());
            cell = newRow.createCell(cellNum++);
            cell.setCellValue(container.wcCode());
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

    public ArrayList<Container> getContainers() {
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

    public String[] listReportNames() {
        String[] files;
        try {
            File reportDir = new File(context.getBean(Office.class).officeConfig.LABOR_PATH + "/reports");
            files = reportDir.list();
        } catch(Exception e) {
            throw e;
        }
        return files;
    }

    public String generateReports() {
        File configFile = new File(context.getBean(Office.class).officeConfig.LABOR_PATH + "/WCPercentages.xlsx");
        File salaryFile = new File(context.getBean(Office.class).officeConfig.LABOR_PATH + "/Salaries.xlsx");
        String outFolder = context.getBean(Office.class).officeConfig.LABOR_PATH + "\\reports";
        LaborGenerator generator = new LaborGenerator(configFile, salaryFile, outFolder);
        return generator.run(containers);
    }
}
