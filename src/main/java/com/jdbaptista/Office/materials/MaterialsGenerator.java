package com.jdbaptista.Office.materials;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;

public class MaterialsGenerator {
    final private String inFolder;
    final private File[] inFiles;
    final private String outFile;
    public String log;
    public ArrayList<Container> containers;
    private Formatter formatter;
    private HashMap<Integer, XSSFWorkbook> workbooks;

    public MaterialsGenerator(String inFolder, String outFile) {
        this.inFolder = inFolder;
        File folder = new File(inFolder);
        inFiles = folder.listFiles();
        this.outFile = outFile;
        this.containers = new ArrayList<>();
        this.workbooks = new HashMap<>();
        this.log = "";
    }

    public String run() {
        try {
            XSSFWorkbook wb = new XSSFWorkbook();
            formatter = new Formatter(wb);
            OutputStream fileOut = new FileOutputStream(outFile + ".xlsx");
            for (int i = 0; i < inFiles.length; i++) {
                ArrayList<Container> containers = parseData(inFiles[i]);
                String[] partials = inFiles[i].getName().split(" ");
                String[] ending = partials[2].split("_");
                ending[2] = ending[2].split("\\.")[0];
                Sheet sheet = wb.createSheet(ending[0] + "_" + ending[1] + "_" + ending[2]);
                writeWeek(sheet, containers, "Week Ending " + ending[0] + "/" + ending[1] + "/" + ending[2]);
            }
            wb.write(fileOut);
            fileOut.close();
            wb.close();

        } catch (Exception e) {
            e.printStackTrace();
            log += "Failed to write material reports.\n";
            return log;
        }
        log += "Generated material reports successfully.\n";
        return log;
    }

    private ArrayList<Container> parseData(File inFile) throws IOException {
        String addressCell;
        String vendorCell;
        double amountCell;
        ArrayList<Container> ret = new ArrayList<>();

        // try to open the input file.
        Workbook wb;
        Sheet sheet = null;
        try {
            wb = WorkbookFactory.create(inFile);
            sheet = wb.getSheetAt(0);
        } catch (IOException e) {
            log += "Something went wrong reading the input.\n";
            throw e;
        }

        // try to read the data
        if (sheet == null) {
            log += "Input file contains no data.\n";
            throw new IOException("sheet is null in file " + inFile);
        }
        for (Row row : sheet) {
            // get the row
            Iterator<Cell> cellIterator = row.cellIterator();
            try {
                addressCell = cellIterator.next().toString();
                vendorCell = cellIterator.next().toString();
                amountCell = Double.parseDouble(cellIterator.next().toString());

                // validate row
            } catch (Exception e) {
                log += "Something went wrong reading the receipts at row " + (row.getRowNum() + 1) + ". Skipped this row.\n";
                e.printStackTrace();
                continue;
            }
            if (addressCell == null || vendorCell == null) {
                log += "Something went wrong reading the receipts at row " + (row.getRowNum() + 1) + ". Skipped this row.\n";
                continue;
            }

            // input the row
            ret.add(new Container(addressCell, vendorCell, amountCell));
        }

        return ret;
    }

    private void writeWeek(Sheet sheet, ArrayList<Container> containers, String header) throws IOException {
        // split the containers up into packets of similar year/month
        // order packets by the following specification for the formatter
        Container[] sorted = containers.stream()
                                        .sorted(Comparator.comparing(Container::address)
                                                .thenComparing(Container::vendor)
                                                .thenComparing(Container::amount))
                                        .toArray(Container[]::new);
        formatter.writeWeek(sheet, sorted, header);
    }
}
