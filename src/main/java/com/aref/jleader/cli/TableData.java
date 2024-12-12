package com.aref.jleader.cli;

import java.lang.reflect.Field;
import java.util.List;


public class TableData<T> {

    private final List<T> data;
    private final Field[] fields;

    public TableData(List<T> data) {
        if (data == null || data.isEmpty()) {
            throw new IllegalArgumentException("Data list cannot be null or empty.");
        }
        this.data = data;
        this.fields = getAllFields(data.get(0).getClass());
        for (Field field : fields) {
            field.setAccessible(true);
        }
    }

    public List<T> getData() {
        return data;
    }

    public Field[] getFields() {
        return fields;
    }

    public int[] calculateColumnWidths() {
        int[] columnWidths = new int[fields.length];
        for (int i = 0; i < fields.length; i++) {
            columnWidths[i] = fields[i].getName().length();
        }
        for (T obj : data) {
            for (int i = 0; i < fields.length; i++) {
                try {
                    Object value = fields[i].get(obj);
                    columnWidths[i] = Math.max(columnWidths[i], value != null ? value.toString().length() : 4);
                } catch (IllegalAccessException e) {
                    columnWidths[i] = Math.max(columnWidths[i], 5); // "ERROR"
                }
            }
        }
        return columnWidths;
    }

    public Field[] getAllFields() {
        Class<?> clazz = data.get(0).getClass();
        Field[] fields = clazz.getDeclaredFields();
        Class<?> superclass = clazz.getSuperclass();
        if (superclass != null) {
            Field[] superclassFields = getAllFields(superclass);
            Field[] allFields = new Field[fields.length + superclassFields.length];
            System.arraycopy(superclassFields, 0, allFields, 0, superclassFields.length);
            System.arraycopy(fields, 0, allFields, superclassFields.length, fields.length);
            return allFields;
        }
        return fields;
    }

    // Recursively retrieves all fields from a class (including nested classes)
    private Field[] getAllFields(Class<?> clazz) {
        Field[] fields = clazz.getDeclaredFields();
        Class<?> superclass = clazz.getSuperclass();
        if (superclass != null) {
            Field[] superclassFields = getAllFields(superclass); // Recursively add superclass fields
            Field[] allFields = new Field[fields.length + superclassFields.length];
            System.arraycopy(superclassFields, 0, allFields, 0, superclassFields.length);
            System.arraycopy(fields, 0, allFields, superclassFields.length, fields.length);
            return allFields;
        }
        return fields;
    }
}
