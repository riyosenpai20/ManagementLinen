package com.android.management_linen.utils;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class PrinterUtils extends AppCompatActivity {
    public static void printRow(OutputStream outputStream, String[] columns, int[] columnWidths, int[] alignments, boolean isHeader, boolean wrapText) throws IOException {
        List<String[]> wrappedRows = new ArrayList<>();

        for (int i = 0; i < columns.length; i++) {
            if (wrapText) {
                wrappedRows.add(wrapText(columns[i], columnWidths[i]));
            } else {
                wrappedRows.add(new String[]{columns[i]});
            }
        }

        int maxLines = 0;
        for (String[] wrappedRow : wrappedRows) {
            if (wrappedRow.length > maxLines) {
                maxLines = wrappedRow.length;
            }
        }

        for (int line = 0; line < maxLines; line++) {
            StringBuilder rowBuilder = new StringBuilder();
            for (int i = 0; i < columns.length; i++) {
                String text = (line < wrappedRows.get(i).length) ? wrappedRows.get(i)[line] : "";
                rowBuilder.append(padRight(alignText(text, columnWidths[i], alignments[i]), columnWidths[i]));
                if (i < columns.length - 1) {
                    rowBuilder.append(" "); // Add space between columns
                }
            }
            rowBuilder.append("\n"); // New line at the end of the row
            outputStream.write(rowBuilder.toString().getBytes("UTF-8"));
        }
    }

    public static String[] wrapText(String text, int columnWidth) {
        List<String> lines = new ArrayList<>();
        int start = 0;
        while (start < text.length()) {
            int end = Math.min(start + columnWidth, text.length());
            lines.add(text.substring(start, end));
            start = end;
        }
        return lines.toArray(new String[0]);
    }

    public static String padRight(String text, int length) {
        if (text.length() > length) {
            return text.substring(0, length);
        }
        StringBuilder sb = new StringBuilder(text);
        while (sb.length() < length) {
            sb.append(' ');
        }
        return sb.toString();
    }


    public static String padLeft(String text, int length) {
        if (text.length() > length) {
            return text.substring(0, length);
        }
        StringBuilder sb = new StringBuilder();
        while (sb.length() < length - text.length()) {
            sb.append(' ');
        }
        sb.append(text);
        return sb.toString();
    }


    public static String alignText(String text, int length, int alignment) {
        if (alignment == 1) { // Center
            return centerText(text, length);
        } else if (alignment == 2) { // Right
            return padLeft(text, length);
        } else { // Left (default)
            return padRight(text, length);
        }
    }

    public static String centerText(String text, int length) {
        if (text.length() >= length) {
            return text.substring(0, length);
        }
        int padding = (length - text.length()) / 2;
        StringBuilder sb = new StringBuilder();
        while (sb.length() < padding) {
            sb.append(' ');
        }
        sb.append(text);
        while (sb.length() < length) {
            sb.append(' ');
        }
        return sb.toString();
    }

    public static void printSeparatorLine(OutputStream outputStream, int[] columnWidths) throws IOException {
        StringBuilder separatorLine = new StringBuilder();
        for (int width : columnWidths) {
            for (int i = 0; i < width; i++) {
                separatorLine.append("-");
            }
        }
        separatorLine.append("\n");
        outputStream.write(separatorLine.toString().getBytes("UTF-8"));
    }

    public static void printCustomSeparatorLine(OutputStream outputStream, int[] columnWidths, String symbol) throws IOException {
        StringBuilder lineBuilder = new StringBuilder();
        for (int width : columnWidths) {
            for (int i = 0; i < width; i++) {
                lineBuilder.append(symbol);
            }
            lineBuilder.append(""); // Add space between columns
        }
        lineBuilder.append("\n");
        outputStream.write(lineBuilder.toString().getBytes("UTF-8"));
    }

    public static void printCustomText(OutputStream outputStream, String text, int alignment, int textSize) throws IOException {
        String alignedText = alignText(text, 32, alignment); // Assuming a fixed width of 32 characters
        outputStream.write(alignedText.getBytes("UTF-8"));
        outputStream.write("\n".getBytes());
    }
}
