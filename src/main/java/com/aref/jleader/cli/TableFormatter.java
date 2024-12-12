package com.aref.jleader.cli;

import java.lang.reflect.Field;
import java.util.List;

import static com.aref.jleader.cli.Color.*;


public class TableFormatter<T> {

    private static final String HEADER_COLOR = BOLD + BLUE;
    private static final String BORDER_COLOR = WHITE;
    private static final String DATA_COLOR = GREEN;
    private static final String ALTERNATE_DATA_COLOR = CYAN;


    private final TableData<T> tableData;

    public TableFormatter(TableData<T> tableData) {
        this.tableData = tableData;
    }

    public void printTable() {
        Field[] fields = tableData.getFields();
        List<T> data = tableData.getData();
        int[] columnWidths = tableData.calculateColumnWidths();

        StringBuilder border = new StringBuilder(BORDER_COLOR + "+");
        for (int width : columnWidths) {
            border.append("-".repeat(width + 2)).append("+");
        }
        border.append(RESET);

        System.out.println(border);
        System.out.print("|");
        for (int i = 0; i < fields.length; i++) {
            System.out.printf(HEADER_COLOR + " %-" + columnWidths[i] + "s " + RESET + BORDER_COLOR + "|" + RESET, fields[i].getName());
        }
        System.out.println();
        System.out.println(border);

        boolean useAlternateColor = false;
        for (T obj : data) {
            System.out.print("|");
            for (int i = 0; i < fields.length; i++) {
                try {
                    Object value = fields[i].get(obj);
                    String color = useAlternateColor ? ALTERNATE_DATA_COLOR : DATA_COLOR;
                    System.out.printf(color + " %-" + columnWidths[i] + "s " + RESET + BORDER_COLOR + "|" + RESET, value != null ? value.toString().replaceAll("\n", "") : "null");
                } catch (IllegalAccessException e) {
                    System.out.printf(RED + " %-" + columnWidths[i] + "s " + RESET + BORDER_COLOR + "|" + RESET, "ERROR");
                }
            }
            System.out.println();
            useAlternateColor = !useAlternateColor;
        }
        System.out.println(border);
    }

}
