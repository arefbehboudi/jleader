package com.aref.jleader.cli;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

import static com.aref.jleader.cli.Color.*;

public class CliUtils {

    public static void printList(List<?> objects) {
        if (objects.isEmpty())
            return;
        Class<?> clazz = objects.get(0).getClass();
        Field[] declaredFields = clazz.getDeclaredFields();
        printTableHeader(declaredFields);
    }

    // Print the table header with column names
    private static void printTableHeader(Field[] declaredFields) {
        String[] headers = new String[declaredFields.length];
        StringBuilder length = new StringBuilder();
        for (int i = 0; i < declaredFields.length; i++) {
            CliField declaredAnnotation = declaredFields[i].getAnnotation(CliField.class);
            String name = declaredAnnotation.name();
            headers[i] = name;
            length.append("| %-").append(name.length()).append("s ");
        }
        length.append("|\n");

        System.out.println(BLUE + "-".repeat() + RESET);
        System.out.printf(length.toString(), headers);
        System.out.println(BLUE + "------------------------------------------------------------" + RESET);
    }

    // Print the container data in a formatted row
    private static void printContainerRow(String[] container) {
        String containerId = container[0];
        String image = container[1];
        String status = container[2];
        String name = container[3];

        // Determine row color based on the container's status (e.g., green for "Running", yellow for "Exited")
        String rowColor = CYAN;
        if ("Running".equals(status)) {
            rowColor = GREEN;
        } else if ("Exited".equals(status)) {
            rowColor = YELLOW;
        }

        // Print each row with aligned columns and colored status
        System.out.printf(rowColor + "| %-15s | %-20s | %-10s | %-20s |\n", containerId, image, status, name);
        System.out.println(RESET + "|------------------------------------------------------------|");
    }
}
