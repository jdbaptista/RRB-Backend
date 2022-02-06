package com.jdbaptista.Office.labor;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;


/*
Generates the reports. Uses DumbContainer instead of Container because it was ported from a different,
WORSE program :( (all in the name of learning though)
 */
public class LaborGenerator {
    final private File configFile;
    final private File salaryFile;
    final private String outFolder;
    public String log;
    final private HashMap<Job, XSSFWorkbook> jobs;
    final private HashMap<LocalDate, HashMap<Integer, Double>> workComp;
    final private HashMap<LocalDate, HashMap<String, Double>> salaries;

    public LaborGenerator(File configFile, File salaryFile, String outFolder) {
        this.configFile = configFile;
        this.salaryFile = salaryFile;
        this.outFolder = outFolder;
        this.log = "";
        workComp = new HashMap<>();
        salaries = new HashMap<>();
        jobs = new HashMap<>();

    }

    public String run(ArrayList<Container> containers) {
        try {
            getWorkComp();
            getSalaries();
            parseData(containers);
            calculate();
            generateFile();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return this.log;
    }

    private void getWorkComp() throws Exception {
        XSSFWorkbook wb = new XSSFWorkbook(configFile);
        Sheet sheet = wb.getSheetAt(0);
        Iterator<Row> rowIterator = sheet.iterator();

        // confirm the config file is correct.
        Row row = rowIterator.next();
        Iterator<Cell> cellIterator = row.cellIterator();
        Cell cell = cellIterator.next();
        if (!cell.toString().equals("WC"))
            throw new IOException("WorkerComp file not formatted correctly.");

        ArrayList<Integer> nums = new ArrayList<>();
        ArrayList<String> values = new ArrayList<>();
        String date;
        while(cellIterator.hasNext()) {
            cell = cellIterator.next();
            nums.add((int) Double.parseDouble(cell.toString()));
        }
        // get values
        while (rowIterator.hasNext()) {
            values.clear();
            row = rowIterator.next();
            cellIterator = row.cellIterator();
            cell = cellIterator.next();
            date = cell.toString();
            if (date.equals("")) {
                break;
            }
            while (cellIterator.hasNext()) {
                cell = cellIterator.next();
                values.add(cell.toString());
                if (cell.toString() == null || cell.toString() == "") {
                    log += "Salary value is empty at row " + (row.getRowNum() + 1) + ".\n";
                    throw new Exception("");
                }
            }
            addWCDate(date, nums.toArray(new Integer[0]), values.toArray(new String[0]));
        }
    }

    private void getSalaries() throws Exception {
        Workbook wb = WorkbookFactory.create(salaryFile);
        Sheet sheet = wb.getSheetAt(0);
        Iterator<Row> rowIterator = sheet.iterator();

        // confirm the salaries file is correct.
        Row row = rowIterator.next();
        Iterator<Cell> cellIterator = row.cellIterator();
        Cell cell = cellIterator.next();
        if (!cell.toString().equals("Salaries"))
            throw new IOException("Salaries file not formatted correctly.");

        ArrayList<String> names = new ArrayList<>();
        ArrayList<String> values = new ArrayList<>();
        String date;
        while(cellIterator.hasNext()) {
            cell = cellIterator.next();
            names.add(cell.toString());
        }
        // get values
        while (rowIterator.hasNext()) {
            values.clear();
            row = rowIterator.next();
            cellIterator = row.cellIterator();
            cell = cellIterator.next();
            date = cell.toString();
            if (date.equals("")) {
                break;
            }
            while (cellIterator.hasNext()) {
                cell = cellIterator.next();
                values.add(cell.toString());
                if (cell.toString() == null || cell.toString() == "") {
                    log += "Salary value is empty at row " + (row.getRowNum() + 1) + ".\n";
                    throw new Exception("");
                }
            }
            addSalaryDate(date, names.toArray(new String[0]), values.toArray(new String[0]));
        }
    }

    private void generateFile() {
        try {
            for (Job job : jobs.keySet()) {
                XSSFWorkbook wb = new XSSFWorkbook();
                Formatter formatter = new Formatter(wb, outFolder);
                formatter.writeJob(job);
            }
        } catch(Exception e) {
            e.printStackTrace();
            log += "Something went wrong generating the reports.\nCheck the input files.";
        }
        log += "Reports generated successfully.";
    }

    private void parseData(ArrayList<Container> containers) throws Exception {
        for (Container c : containers) {
            inputContainer(c.name(), c.address(), c.date(), c.task(), c.time(), c.wcCode(), "0");
        }

    }

    private void inputContainer(String name, String address, Date date, String task, double time, int type, String multiplier) throws Exception {
        // parse the raw date into usable data.
        // raw date in form 01-Mar-2021.
        int day;
        int month;
        int year;
        LocalDate local = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        day = local.getDayOfMonth();
        month = local.getMonthValue() + 1;
        year = local.getYear();

        // add new job to jobs list.
        Job newJob = new Job(address);
        Job jobRef = getJobRef(newJob);
        // This doubles for checking if newJob is in jobs hashmap.
        if (jobRef == null) {
            jobRef = newJob;
            jobs.put(jobRef, null);
        }

        // add new month to the specified job's month list.
        Month newMonth = new Month(month, year);
        Month monthRef = jobRef.getMonthRef(newMonth);
        if (monthRef == null) {
            monthRef = newMonth;
            jobRef.addMonth(monthRef);
        }


        // add new container to the specified month's container list.
        double doubleMultiplier;
        if (multiplier.equals("")) {
            doubleMultiplier = 1.0;
        } else {
            doubleMultiplier = Double.parseDouble(multiplier);
        }
        DumbContainer newContainer = new DumbContainer(day, name, task, time, type, doubleMultiplier);
        DumbContainer containerRef = monthRef.getContainerRef(newContainer);
        if (containerRef == null) {
            containerRef = newContainer;
            monthRef.addContainer(containerRef);
        }
    }

    private Job getJobRef(Job query) {
        if (!jobs.containsKey(query)) return null;
        for (Job job : jobs.keySet()) {
            if (job.equals(query)) {
                return job;
            }
        }
        return null;
    }

    private void calculate() {
        for (Job job : jobs.keySet()) {
            for (Month month : job.getMonths()) {
                for (DumbContainer container : month.getContainers()) {
                    container.amount = calculatePay(container, month.getMonth(), container.day, month.getYear());
                    container.wc = calculateWC(container, month.getMonth(), container.day, month.getYear());
                    container.tax = calculateTax(container);
                }
                month.calculateDailyTotals();
                month.calculateTaskTotals();
            }
        }
    }

    private void addWCDate(String date, Integer[] nums, String[] values) throws Exception {
        HashMap<Integer, Double> types = new HashMap<>();

        int day;
        String month;
        int intMonth;
        int year;
        try {
            String[] splitDate = date.split("-");
            day = Integer.parseInt(splitDate[0]);
            month = splitDate[1];
            year = Integer.parseInt(splitDate[2]);
        } catch (Exception e) {
            throw new Exception("Date is not formatted correctly.");
        }
        intMonth = convertMonth(month);

        for (int i = 0; i < nums.length; i++) {
            types.put(nums[i], Double.parseDouble(values[i]));
        }
        workComp.put(LocalDate.of(year, intMonth, day), types);
    }

    private void addSalaryDate(String date, String[] names, String[] values) throws Exception {
        HashMap<String, Double> types = new HashMap<>();

        int day;
        String month;
        int intMonth;
        int year;
        try {
            String[] splitDate = date.split("-");
            day = Integer.parseInt(splitDate[0]);
            month = splitDate[1];
            year = Integer.parseInt(splitDate[2]);
        } catch (Exception e) {
            throw new Exception("Date is not formatted correctly.");
        }
        intMonth = convertMonth(month);

        for (int i = 0; i < names.length; i++) {
            if (values[i] == "") throw new Exception("");
            types.put(names[i], Double.parseDouble(values[i]));
        }
        salaries.put(LocalDate.of(year, intMonth, day), types);
    }

    private double calculateWC(DumbContainer container, int month, int day, int year) {
        LocalDate date = LocalDate.of(year, month, day);
        LocalDate holdDate = null;
        ArrayList<LocalDate> dateChanges = new ArrayList<>(workComp.keySet());
        Collections.sort(dateChanges);
        for (LocalDate changeDate : dateChanges) {
            if (date.isAfter(changeDate))
                holdDate = changeDate;
            else
                break;
        }
        return ((int) Math.round(container.amount * workComp.get(holdDate).get(container.type))) / 100d;
    }

    private double calculatePay(DumbContainer container, int month, int day, int year) {
        LocalDate date = LocalDate.of(year, month, day);
        LocalDate holdDate = null;
        ArrayList<LocalDate> dateChanges = new ArrayList<>(salaries.keySet());
        Collections.sort(dateChanges);
        for (LocalDate changeDate : dateChanges) {
            if (date.isAfter(changeDate))
                holdDate = changeDate;
            else
                break;
        }
        return ((int) Math.round(container.multiplier * container.time * salaries.get(holdDate).get(container.name) * 100)) / 100d;
    }

    private double calculateTax(DumbContainer container) {
        return ((int) Math.round(container.amount * 7.7)) / 100d;
    }

    public static int convertMonth(String month) {
        switch (month) {
            case "Jan" -> {
                return 1;
            }
            case "Feb" -> {
                return 2;
            }
            case "Mar" -> {
                return 3;
            }
            case "Apr" -> {
                return 4;
            }
            case "May" -> {
                return 5;
            }
            case "Jun" -> {
                return 6;
            }
            case "Jul" -> {
                return 7;
            }
            case "Aug" -> {
                return 8;
            }
            case "Sep" -> {
                return 9;
            }
            case "Oct" -> {
                return 10;
            }
            case "Nov" -> {
                return 11;
            }
            case "Dec" -> {
                return 12;
            }
            default -> {
                return -1;
            }
        }
    }

}
