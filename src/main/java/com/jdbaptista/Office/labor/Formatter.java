package com.jdbaptista.Office.labor;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Formats data input by an instance of {@link LaborGenerator} for one {@link Workbook} and {@link Job}.
 * Creates a .xlsx file of the format [address].xlsx with a tab for each {@link Month} in the {@link Job}.
 */
public class Formatter {
    final private Workbook wb;
    final private String outFolder;
    final public HashMap<String, CellStyle> styles;
    int rowNum;
    int cellNum;

    public Formatter(Workbook wb, String outFolder) {
        this.wb = wb;
        this.outFolder = outFolder;
        styles = new HashMap<>();
        loadStyles();
    }

    /**
     * Writes the data for the job on the output file with a tab for each month.
     * @param job The job to be written.
     * @throws IOException Something went wrong.
     */
    public void writeJob(Job job) throws IOException {
        for (Month month : job.getMonths()) {
            Sheet sheet = wb.createSheet(month.strMonth + " " + month.getYear());
            // give to apache to format
            rowNum = 0;
            cellNum = 0;
            writeTitle(job.getAddress(), month.strMonth, month.getYear(), sheet);
            writeDays(month, sheet);
            writeMonthTotal(month, sheet);
            writeTasks(month, sheet);
            sheet.setColumnWidth(1,256*20);
            sheet.setColumnWidth(2, 256*4);
            sheet.setColumnWidth(3, 256*10);
            sheet.setColumnWidth(7,256*4);
            sheet.setColumnWidth(8,256*10);
        }
        OutputStream fileOut = new FileOutputStream( outFolder + "/" + job.getAddress() + ".xlsx");
        wb.write(fileOut);
        fileOut.close();
        wb.close();
    }

    private void writeTitle(String address, String month, int year, Sheet sheet) {
        Row row = sheet.createRow(rowNum++);
        Cell cell = row.createCell(cellNum++);
        cell.setCellValue(address);
        cell.setCellStyle(styles.get("TITLE"));
        sheet.addMergedRegion(new CellRangeAddress(rowNum-1, rowNum, cellNum-1, cellNum+1));
        row.createCell(cellNum++);
        row.createCell(cellNum++);
        cell = row.createCell(cellNum++);
        cell.setCellValue(month + " " + year);
        cell.setCellStyle(styles.get("TITLE"));
        sheet.addMergedRegion(new CellRangeAddress(rowNum-1, rowNum, cellNum-1, cellNum+1));
        row.createCell(cellNum++);
        row.createCell(cellNum++);
        sheet.createRow(rowNum++);
    }

    private void writeDays(Month month, Sheet sheet) {
        ArrayList<Container> containers = month.getContainers();
        if (containers.size() == 0)
            return;
        int day = containers.get(0).day;
        writeDayLabels(sheet);
        for (Container container : containers) {
            if (container.day != day) {
                writeDayTotal(month, day, sheet);
                day = container.day;
            }
            writeDay(container, sheet);
        }
        writeDayTotal(month, day, sheet);
    }

    private void writeDayLabels(Sheet sheet) {
        cellNum = 0;
        Row row = sheet.createRow(rowNum++);
        Cell cell = row.createCell(cellNum++);
        cell.setCellValue("Name");
        cell.setCellStyle(styles.get("LABEL"));
        cell = row.createCell(cellNum++);
        cell.setCellValue("Task");
        cell.setCellStyle(styles.get("LABEL"));
        cell = row.createCell(cellNum++);
        cell.setCellValue("Hr");
        cell.setCellStyle(styles.get("LABEL"));
        cell = row.createCell(cellNum++);
        cell.setCellValue("Amount");
        cell.setCellStyle(styles.get("LABEL"));
        cell = row.createCell(cellNum++);
        cell.setCellValue("WC");
        cell.setCellStyle(styles.get("LABEL"));
        cell = row.createCell(cellNum++);
        cell.setCellValue("Tax");
        cell.setCellStyle(styles.get("LABEL"));
    }

    private void writeDay(Container container, Sheet sheet) {
        cellNum = 0;
        Row row = sheet.createRow(rowNum++);
        Cell cell = row.createCell(cellNum++);
        cell.setCellValue(container.name);
        cell.setCellStyle(styles.get("DAYNAME"));
        cell = row.createCell(cellNum++);
        cell.setCellValue(container.task);
        cell.setCellStyle(styles.get("TASKDESCRIPTION"));
        cell = row.createCell(cellNum++);
        cell.setCellValue(container.time);
        cell.setCellStyle(styles.get("TIME"));
        cell = row.createCell(cellNum++);
        cell.setCellValue(container.amount);
        cell.setCellStyle(styles.get("CURRENCY"));
        cell = row.createCell(cellNum++);
        cell.setCellValue(container.wc);
        cell.setCellStyle(styles.get("CURRENCY"));
        cell = row.createCell(cellNum++);
        cell.setCellValue(container.tax);
        cell.setCellStyle(styles.get("CURRENCY"));
    }

    private void writeDayTotal(Month month, int day, Sheet sheet) {
        cellNum = 0;
        Row row = sheet.createRow(rowNum++);
        Cell cell = row.createCell(cellNum++);
        cell.setCellValue(month.getMonth() + "/" + day + " Total");
        cell.setCellStyle(styles.get("DAYTOTAL"));
        sheet.addMergedRegion(new CellRangeAddress(rowNum-1, rowNum-1, cellNum-1, cellNum));
        cell = row.createCell(cellNum++);
        cell.setCellStyle(styles.get("DAYTIME"));
        cell = row.createCell(cellNum++);
        cell.setCellStyle(styles.get("DAYTIME"));
        cell.setCellValue(month.dailyTotals.get(day)[1]);
        cell = row.createCell(cellNum++);
        cell.setCellStyle(styles.get("DAYAMOUNT"));
        cell.setCellValue(month.dailyTotals.get(day)[0]);
        cell = row.createCell(cellNum++);
        cell.setCellStyle(styles.get("DAYAMOUNT"));
        cell.setCellValue(month.dailyTotals.get(day)[2]);
        cell = row.createCell(cellNum++);
        cell.setCellStyle(styles.get("DAYAMOUNT"));
        cell.setCellValue(month.dailyTotals.get(day)[3]);
    }

    private void writeMonthTotal(Month month, Sheet sheet) {
        cellNum = 1;
        Row row = sheet.createRow(rowNum++);
        Cell cell = row.createCell(cellNum++);
        cell.setCellValue(month.strMonth + " Total");
        cell.setCellStyle(styles.get("MONTHTOTAL"));
        cell = row.createCell(cellNum++);
        cell.setCellValue(month.dailyTotals.get(0)[1]);
        cell.setCellStyle(styles.get("MONTHTIME"));
        cell = row.createCell(cellNum++);
        cell.setCellValue(month.dailyTotals.get(0)[0]);
        cell.setCellStyle(styles.get("MONTHAMOUNT"));
        cell = row.createCell(cellNum++);
        cell.setCellValue(month.dailyTotals.get(0)[2]);
        cell.setCellStyle(styles.get("MONTHAMOUNT"));
        cell = row.createCell(cellNum++);
        cell.setCellValue(month.dailyTotals.get(0)[3]);
        cell.setCellStyle(styles.get("MONTHAMOUNT"));
    }

    private void writeTasks(Month month, Sheet sheet) {
        Iterator<Row> rowIterator = sheet.rowIterator();
        rowIterator.next();
        rowIterator.next();
        for (String task : month.taskTotals.keySet()) {
            writeTask(month, task, sheet, rowIterator);
        }
    }

    private void writeTask(Month month, String task, Sheet sheet, Iterator<Row> rowIterator) {
        HashMap<String, double[]> taskContainers = month.taskTotals.get(task);
        Row usingRow;
        cellNum = 6;
        if (rowIterator.hasNext()) {
            usingRow = rowIterator.next();
        } else {
            usingRow = sheet.createRow(rowNum++);
        }
        writeTaskName(task, usingRow, sheet);
        // write task containers
        for (String name : taskContainers.keySet()) {
            if (!name.equals("Total")) {
                writeTaskContainer(taskContainers, name, rowIterator, sheet);
            }
        }
        // write task total
        writeTaskTotal(taskContainers, rowIterator, sheet);
    }

    private void writeTaskName(String task, Row usingRow, Sheet sheet) {
        Cell cell = usingRow.createCell(cellNum++);
        cell.setCellValue(task);
        cell.setCellStyle(styles.get("TASKNAME"));
        sheet.addMergedRegion(new CellRangeAddress(usingRow.getRowNum(), usingRow.getRowNum(), cellNum-1, cellNum+1));
        cell = usingRow.createCell(cellNum++);
        cell.setCellStyle(styles.get("TASKNAME"));
        cell = usingRow.createCell(cellNum++);
        cell.setCellStyle(styles.get("TASKNAME"));
    }

    private void writeTaskContainer(HashMap<String, double[]> taskContainers, String name, Iterator<Row> rowIterator, Sheet sheet) {
        double[] data = taskContainers.get(name);
        cellNum = 6;
        Row usingRow;
        if (rowIterator.hasNext()) {
            usingRow = rowIterator.next();
        } else {
            usingRow = sheet.createRow(rowNum++);
        }
        Cell cell = usingRow.createCell(cellNum++);
        cell.setCellValue(name);
        cell.setCellStyle(styles.get("TASKCONTAINER"));
        cell = usingRow.createCell(cellNum++);
        cell.setCellValue(data[0]);
        cell.setCellStyle(styles.get("TIME"));
        cell = usingRow.createCell(cellNum++);
        cell.setCellValue(data[1]);
        cell.setCellStyle(styles.get("TASKCURRENCY"));
    }

    private void writeTaskTotal(HashMap<String, double[]> taskContainers, Iterator<Row> rowIterator, Sheet sheet) {
        double[] data = taskContainers.get("Total");
        cellNum = 6;
        Row usingRow;
        if (rowIterator.hasNext()) {
            usingRow = rowIterator.next();
        } else {
            usingRow = sheet.createRow(rowNum++);
        }
        Cell cell = usingRow.createCell(cellNum++);
        cell.setCellValue("Total");
        cell.setCellStyle(styles.get("TASKTOTAL"));
        cell = usingRow.createCell(cellNum++);
        cell.setCellValue(data[0]);
        cell.setCellStyle(styles.get("TASKTIME"));
        cell = usingRow.createCell(cellNum++);
        cell.setCellValue(data[1]);
        cell.setCellStyle(styles.get("TASKAMOUNT"));
    }

    /**
     * Loads all styles.
     * @since 1.1
     */
    private void loadStyles() {
        styles.put("TITLE", createTITLEFont(wb));
        styles.put("LABEL", createLABELFont(wb));
        styles.put("DAYNAME", createDAYNAMEFont(wb));
        styles.put("DAYTOTAL", createDAYTOTALFont(wb));
        styles.put("DAYTIME", createDAYTIMEFont(wb));
        styles.put("DAYAMOUNT", createDAYAMOUNTFont(wb));
        styles.put("TIME", createTIMEFont(wb));
        styles.put("TASKDESCRIPTION", createTASKDESCRIPTIONFont(wb));
        styles.put("MONTHTOTAL", createMONTHTOTALFont(wb));
        styles.put("MONTHTIME", createMONTHTIMEFont(wb));
        styles.put("MONTHAMOUNT", createMONTHAMOUNTFont(wb));
        styles.put("CURRENCY", createCURRENCYFont(wb));
        styles.put("TASKNAME", createTASKNAMEFont(wb));
        styles.put("TASKAMOUNT", createTASKAMOUNTFont(wb));
        styles.put("TASKCURRENCY", createTASKCURRENCYFont(wb));
        styles.put("TASKCONTAINER", createTASKCONTAINERFont(wb));
        styles.put("TASKTOTAL", createTASKTOTALFont(wb));
        styles.put("TASKTIME", createTASKTIMEFont(wb));
    }

    /**
     * Creates the title style.
     * @param wb Workbook the style is created in
     * @return The {@link CellStyle} corresponding to the Title.
     */
    private CellStyle createTITLEFont(Workbook wb) {
        // set up the style and font and link together
        CellStyle TITLE = wb.createCellStyle();
        Font TITLEFONT = wb.createFont();
        TITLE.setFont(TITLEFONT);
        // modify the style
        TITLE.setAlignment(HorizontalAlignment.CENTER);
        TITLE.setVerticalAlignment(VerticalAlignment.CENTER);
        // modify the font
        TITLEFONT.setBold(true);
        TITLEFONT.setFontHeightInPoints((short) 15);
        return TITLE;
    }

    private CellStyle createLABELFont(Workbook wb) {
        CellStyle LABEL = wb.createCellStyle();
        Font LABELFONT = wb.createFont();
        LABEL.setFont(LABELFONT);
        // modify the style
        LABEL.setAlignment(HorizontalAlignment.CENTER);
        LABEL.setVerticalAlignment(VerticalAlignment.CENTER);
        LABEL.setBorderRight(BorderStyle.THIN);
        LABEL.setBorderTop(BorderStyle.THIN);
        LABEL.setBorderBottom(BorderStyle.THIN);
        // modify the font
        LABELFONT.setBold(true);
        return LABEL;
    }

    /**
     * Creates the day-name style.
     * @param wb Workbook the style is created in
     * @return The {@link CellStyle} corresponding to day-name cells.
     */
    private CellStyle createDAYNAMEFont(Workbook wb) {
        // create and modify the style
        CellStyle DAYNAME = wb.createCellStyle();
        DAYNAME.setBorderLeft(BorderStyle.MEDIUM);
        DAYNAME.setBorderRight(BorderStyle.THIN);
        DAYNAME.setBorderTop(BorderStyle.THIN);
        DAYNAME.setBorderBottom(BorderStyle.THIN);
        return DAYNAME;
    }

    /**
     * Creates the day-total style.
     * @param wb Workbook the style is created in
     * @return The {@link CellStyle} corresponding to day-total cells.
     */
    private CellStyle createDAYTOTALFont(Workbook wb) {
        // set up the style and font and link together
        CellStyle DAYTOTAL = wb.createCellStyle();
        Font DAYTOTALFONT = wb.createFont();
        DAYTOTAL.setFont(DAYTOTALFONT);
        // modify the style
        DAYTOTAL.setBorderLeft(BorderStyle.MEDIUM);
        DAYTOTAL.setBorderBottom(BorderStyle.THIN);
        DAYTOTAL.setAlignment(HorizontalAlignment.CENTER);
        // modify the font
        DAYTOTALFONT.setBold(true);
        return DAYTOTAL;
    }

    /**
     * Creates the day-time style.
     * @param wb Workbook the style is created in
     * @return The {@link CellStyle} corresponding to day-time cells.
     */
    private CellStyle createDAYTIMEFont(Workbook wb) {
        // set up the style and font and link together
        CellStyle DAYTIME = wb.createCellStyle();
        Font DAYTIMEFONT = wb.createFont();
        DAYTIME.setFont(DAYTIMEFONT);
        // modify the style
        DAYTIME.setBorderBottom(BorderStyle.THIN);
        DAYTIME.setBorderRight(BorderStyle.THIN);
        DAYTIME.setBorderLeft(BorderStyle.THIN);
        DAYTIME.setAlignment(HorizontalAlignment.CENTER);
        // modify the font
        DAYTIMEFONT.setBold(true);
        return DAYTIME;
    }

    /**
     * Creates the day-amount style.
     * @param wb Workbook the style is created in
     * @return The {@link CellStyle} corresponding to day-amount cells.
     */
    private CellStyle createDAYAMOUNTFont(Workbook wb) {
        // set up the style and font and link together
        CellStyle DAYAMOUNT = wb.createCellStyle();
        Font DAYAMOUNTFONT = wb.createFont();
        DAYAMOUNT.setFont(DAYAMOUNTFONT);
        // modify the style
        DAYAMOUNT.setBorderBottom(BorderStyle.THIN);
        DAYAMOUNT.setBorderTop(BorderStyle.THIN);
        DAYAMOUNT.setBorderRight(BorderStyle.THIN);
        DAYAMOUNT.setBorderLeft(BorderStyle.THIN);
        DAYAMOUNT.setDataFormat((short) 7); // currency format
        // modify the font
        DAYAMOUNTFONT.setBold(true);
        return DAYAMOUNT;
    }

    /**
     * Creates the time style.
     * @param wb Workbook the style is created in
     * @return The {@link CellStyle} corresponding to time cells.
     */
    private CellStyle createTIMEFont(Workbook wb) {
        // create and modify the style
        CellStyle TIME = wb.createCellStyle();
        TIME.setAlignment(HorizontalAlignment.CENTER);
        TIME.setBorderLeft(BorderStyle.THIN);
        TIME.setBorderRight(BorderStyle.THIN);
        TIME.setBorderTop(BorderStyle.THIN);
        TIME.setBorderBottom(BorderStyle.THIN);
        return TIME;
    }

    /**
     * Creates the task-description style.
     * @param wb Workbook the style is created in
     * @return The {@link CellStyle} corresponding to task-description cells.
     */
    private CellStyle createTASKDESCRIPTIONFont(Workbook wb) {
        // create the style and font and link together
        CellStyle TASKDESCRIPTION = wb.createCellStyle();
        Font TASKDESCRIPTIONFONT = wb.createFont();
        TASKDESCRIPTION.setFont(TASKDESCRIPTIONFONT);
        // modify the style
        TASKDESCRIPTION.setBorderLeft(BorderStyle.THIN);
        TASKDESCRIPTION.setBorderRight(BorderStyle.THIN);
        TASKDESCRIPTION.setBorderTop(BorderStyle.THIN);
        TASKDESCRIPTION.setBorderBottom(BorderStyle.THIN);
        // modify the font
        // TODO: TASKDESCRIPTION text wrapping doesn't work as intended.
        TASKDESCRIPTION.setWrapText(true);
        return TASKDESCRIPTION;
    }

    /**
     * Creates the month total style.
     * @param wb Workbook the style is created in
     * @return The {@link CellStyle} corresponding to the month total.
     */
    private CellStyle createMONTHTOTALFont(Workbook wb) {
        // create the style and font and link together
        CellStyle MONTHTOTAL = wb.createCellStyle();
        Font MONTHTOTALFONT = wb.createFont();
        MONTHTOTAL.setFont(MONTHTOTALFONT);
        // modify the style
        MONTHTOTAL.setBorderLeft(BorderStyle.MEDIUM);
        MONTHTOTAL.setBorderBottom(BorderStyle.MEDIUM);
        MONTHTOTAL.setBorderRight(BorderStyle.THIN);
        MONTHTOTAL.setAlignment(HorizontalAlignment.CENTER);
        // modify the font
        //TODO: create one bold font instead of multiple.
        MONTHTOTALFONT.setBold(true);
        return MONTHTOTAL;
    }

    private CellStyle createMONTHTIMEFont(Workbook wb) {
        // create the style and font and link together
        CellStyle MONTHTIME = wb.createCellStyle();
        Font MONTHTIMEFONT = wb.createFont();
        MONTHTIME.setFont(MONTHTIMEFONT);
        // modify the style
        MONTHTIME.setBorderLeft(BorderStyle.THIN);
        MONTHTIME.setBorderBottom(BorderStyle.MEDIUM);
        MONTHTIME.setBorderRight(BorderStyle.THIN);
        MONTHTIME.setAlignment(HorizontalAlignment.CENTER);
        // modify the font
        //TODO: create one bold font instead of multiple.
        MONTHTIMEFONT.setBold(true);
        return MONTHTIME;
    }

    /**
     * Creates the month-amount style.
     * @param wb Workbook the style is created in
     * @return The {@link CellStyle} corresponding to month-amount cells.
     */
    private CellStyle createMONTHAMOUNTFont(Workbook wb) {
        // create the style and font and link together
        CellStyle MONTHAMOUNT = wb.createCellStyle();
        Font MONTHAMOUNTFONT = wb.createFont();
        MONTHAMOUNT.setFont(MONTHAMOUNTFONT);
        // modify the style
        MONTHAMOUNT.setBorderRight(BorderStyle.THIN);
        MONTHAMOUNT.setBorderBottom(BorderStyle.MEDIUM);
        MONTHAMOUNT.setBorderLeft(BorderStyle.THIN);
        MONTHAMOUNT.setBorderTop(BorderStyle.THIN);
        MONTHAMOUNT.setDataFormat((short) 7); // currency format
        // modify the font
        MONTHAMOUNTFONT.setBold(true);
        return MONTHAMOUNT;
    }

    /**
     * Creates the currency style.
     * @param wb Workbook the style is created in
     * @return The {@link CellStyle} corresponding to currency cells.
     */
    private CellStyle createCURRENCYFont(Workbook wb) {
        // create the style
        CellStyle CURRENCY = wb.createCellStyle();
        // modify the style
        CURRENCY.setBorderLeft(BorderStyle.THIN);
        CURRENCY.setBorderRight(BorderStyle.THIN);
        CURRENCY.setBorderTop(BorderStyle.THIN);
        CURRENCY.setBorderBottom(BorderStyle.THIN);
        CURRENCY.setDataFormat((short) 7); // currency format
        return CURRENCY;
    }

    /**
     * Creates the task-name style.
     * @param wb Workbook the style is created in
     * @return The {@link CellStyle} corresponding to task-name cells.
     */
    private CellStyle createTASKNAMEFont(Workbook wb) {
        // create the style and font and link together
        CellStyle TASKNAME = wb.createCellStyle();
        Font TASKNAMEFONT = wb.createFont();
        TASKNAME.setFont(TASKNAMEFONT);
        // modify the style
        TASKNAME.setBorderTop(BorderStyle.MEDIUM);
        TASKNAME.setBorderLeft(BorderStyle.MEDIUM);
        TASKNAME.setBorderBottom(BorderStyle.DOUBLE);
        TASKNAME.setBorderRight(BorderStyle.MEDIUM);
        TASKNAME.setAlignment(HorizontalAlignment.CENTER);
        TASKNAME.setWrapText(true);
        // modify the font
        TASKNAMEFONT.setBold(true);
        return TASKNAME;
    }

    /**
     * Creates the task-amount style.
     * @param wb Workbook the style is created in
     * @return The {@link CellStyle} corresponding to task-amount cells.
     */
    private CellStyle createTASKAMOUNTFont(Workbook wb) {
        // create the style and font and link together
        CellStyle TASKAMOUNT = wb.createCellStyle();
        Font TASKAMOUNTFONT = wb.createFont();
        TASKAMOUNT.setFont(TASKAMOUNTFONT);
        // modify the style
        TASKAMOUNT.setBorderLeft(BorderStyle.THIN);
        TASKAMOUNT.setBorderRight(BorderStyle.MEDIUM);
        TASKAMOUNT.setBorderTop(BorderStyle.DOUBLE);
        TASKAMOUNT.setBorderBottom(BorderStyle.MEDIUM);
        TASKAMOUNT.setDataFormat((short) 7); // currency format
        // modify the font
        TASKAMOUNTFONT.setBold(true);
        return TASKAMOUNT;
    }

    /**
     * Creates the task-currency style.
     * @param wb Workbook the style is created in
     * @return The {@link CellStyle} corresponding to task-currency cells.
     */
    private CellStyle createTASKCURRENCYFont(Workbook wb) {
        // create the style
        CellStyle TASKCURRENCY = wb.createCellStyle();
        // modify the style
        TASKCURRENCY.setBorderLeft(BorderStyle.THIN);
        TASKCURRENCY.setBorderRight(BorderStyle.MEDIUM);
        TASKCURRENCY.setBorderBottom(BorderStyle.THIN);
        TASKCURRENCY.setDataFormat((short) 7); // currency format
        return TASKCURRENCY;
    }

    /**
     * Creates the task-container style.
     * @param wb Workbook the style is created in
     * @return The {@link CellStyle} corresponding to task-container cells.
     */
    private CellStyle createTASKCONTAINERFont(Workbook wb) {
        // create the style
        CellStyle TASKCONTAINER = wb.createCellStyle();
        // modify the style
        TASKCONTAINER.setBorderLeft(BorderStyle.MEDIUM);
        TASKCONTAINER.setBorderLeft(BorderStyle.MEDIUM);
        TASKCONTAINER.setBorderTop(BorderStyle.THIN);
        TASKCONTAINER.setBorderBottom(BorderStyle.THIN);
        return TASKCONTAINER;
    }

    /**
     * Creates the task-total style.
     * @param wb Workbook the style is created in
     * @return The {@link CellStyle} corresponding to task-total cells.
     */
    private CellStyle createTASKTOTALFont(Workbook wb) {
        // create the style and font and link together
        CellStyle TASKTOTAL = wb.createCellStyle();
        Font TASKTOTALFONT = wb.createFont();
        TASKTOTAL.setFont(TASKTOTALFONT);
        // modify the style
        TASKTOTAL.setBorderLeft(BorderStyle.MEDIUM);
        TASKTOTAL.setBorderRight(BorderStyle.THIN);
        TASKTOTAL.setBorderTop(BorderStyle.DOUBLE);
        TASKTOTAL.setBorderBottom(BorderStyle.MEDIUM);
        // modify the font
        TASKTOTALFONT.setBold(true);
        return TASKTOTAL;
    }

    /**
     * Creates the task-time style.
     * @param wb Workbook the style is created in
     * @return The {@link CellStyle} corresponding to task-time cells.
     */
    private CellStyle createTASKTIMEFont(Workbook wb) {
        // create the style and font and link together
        CellStyle TASKTIME = wb.createCellStyle();
        Font TASKTIMEFONT = wb.createFont();
        TASKTIME.setFont(TASKTIMEFONT);
        // modify the style
        TASKTIME.setBorderTop(BorderStyle.DOUBLE);
        TASKTIME.setBorderBottom(BorderStyle.MEDIUM);
        TASKTIME.setBorderRight(BorderStyle.THIN);
        TASKTIME.setBorderLeft(BorderStyle.THIN);
        TASKTIME.setAlignment(HorizontalAlignment.CENTER);
        // modify the font
        TASKTIMEFONT.setBold(true);
        return TASKTIME;
    }
}
