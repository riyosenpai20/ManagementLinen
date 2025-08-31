package com.android.management_linen.helpers;

import android.content.Context;
import android.os.Environment;
import android.widget.Toast;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class PDFHelper {
    public static void  createPDF(Context context, List<Map<String , Object>> data) {
        String pdfPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString();
        File file = new File(pdfPath, "report.pdf");
        try {
            FileOutputStream outputStream = new FileOutputStream(file);
            PdfWriter writer = new PdfWriter(outputStream);
            PdfDocument pdfDocument = new PdfDocument(writer);
            Document document = new Document(pdfDocument);

            // Add Table
            float[] columnWidth = {1,2,2};
            Table table = new Table(columnWidth);

            table.addCell(new Cell().add(new Paragraph("ID")));
            table.addCell(new Cell().add(new Paragraph("Nama")));

            for (Map<String, Object> item : data) {
                table.addCell(new Cell().add(new Paragraph(item.get("id").toString())));
                table.addCell(new Cell().add(new Paragraph(item.get("nama").toString())));
            }

            document.add(table);

            document.close();

            Toast.makeText(context, "PDF Created", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(context, "Error Creating PDF", Toast.LENGTH_SHORT).show();
        }
    }
}
