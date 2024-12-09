package com.aref.jleader.cli;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

import static com.aref.jleader.cli.Color.*;

public class CliUtils {

    public static void printList(List<?> objects) throws IllegalAccessException {
        if (objects.isEmpty())
            return;
        Class<?> clazz = objects.get(0).getClass();
        Field[] declaredFields = clazz.getDeclaredFields();
        printTableHeader(declaredFields);
        for (Object object : objects) {
            printContainerRow(object);
        }
    }

    // Print the table header with column names
    private static void printTableHeader(Field[] declaredFields) {
        String[] headers = new String[declaredFields.length];
        StringBuilder length = new StringBuilder();
        int borderLength = 0;

        for (int i = 0; i < declaredFields.length; i++) {
            CliField declaredAnnotation = declaredFields[i].getAnnotation(CliField.class);
            String name = declaredAnnotation.name();
            headers[i] = name;
            length.append("| %-").append(name.length()).append("s ");
            borderLength += name.length();

        }
        length.append("|\n");

        System.out.println(RESET + "| " + "-".repeat(borderLength + 3) + " |");
        System.out.printf(length.toString(), headers);
        System.out.println(RESET + "| " + "-".repeat(borderLength + 3) + " |");
    }

    // Print the container data in a formatted row
    private static void printContainerRow(Object obj) throws IllegalAccessException {
        Field[] declaredFields = obj.getClass().getDeclaredFields();

        String[] values = new String[declaredFields.length];
        StringBuilder length = new StringBuilder();
        int borderLength = 0;

        for (int i = 0; i < declaredFields.length; i++) {
            Field field = declaredFields[i];
            CliField declaredAnnotation = declaredFields[i].getAnnotation(CliField.class);
            String name = declaredAnnotation.name();
            field.setAccessible(true);
            Object value = field.get(obj);
            values[i] = value.toString();
            length.append("| %-").append(name.length()).append("s ");
            borderLength += name.length();
        }
        length.append("|\n");
        System.out.printf(BLUE + length, values);
        System.out.println(RESET + "| " + "-".repeat(borderLength + 3) + " |");

    }
}
