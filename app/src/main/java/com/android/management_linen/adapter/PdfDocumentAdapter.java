package com.android.management_linen.adapter;

//import android.content.Context;
//import android.graphics.pdf.PdfRenderer;
//import android.os.Bundle;
//import android.os.CancellationSignal;
//import android.os.ParcelFileDescriptor;
//import android.print.PageRange;
//import android.print.PrintAttributes;
//import android.print.PrintDocumentAdapter;
//import android.print.PrintDocumentInfo;
//import android.print.pdf.PrintedPdfDocument;
//
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.FileOutputStream;
//import java.io.IOException;
//
//public class PdfDocumentAdapter extends PrintDocumentAdapter {
//
//    private Context context;
//    private String path;
//    private PdfRenderer pdfRenderer;
//    private PrintedPdfDocument pdfDocument;
//    private PrintAttributes printAttributes;
//
//    public PdfDocumentAdapter(Context context, String path) {
//        this.context = context;
//        this.path = path;
//    }
//
//    @Override
//    public void onLayout(PrintAttributes oldAttributes, PrintAttributes newAttributes, CancellationSignal cancellationSignal, LayoutResultCallback callback, Bundle extras) {
//        this.printAttributes = newAttributes;
//        pdfDocument = new PrintedPdfDocument(context, newAttributes);
//
//        if (cancellationSignal.isCanceled()) {
//            callback.onLayoutCancelled();
//            return;
//        }
//
//        PrintDocumentInfo info = new PrintDocumentInfo.Builder("namaRuang.pdf")
//                .setContentType(PrintDocumentInfo.CONTENT_TYPE_DOCUMENT)
//                .setPageCount(PrintDocumentInfo.PAGE_COUNT_UNKNOWN)
//                .build();
//
//        callback.onLayoutFinished(info, true);
//    }
//
//    @Override
//    public void onWrite(PageRange[] pages, ParcelFileDescriptor destination, CancellationSignal cancellationSignal, WriteResultCallback callback) {
//        FileInputStream input = null;
//        FileOutputStream output = null;
//
//        try {
//            input = new FileInputStream(new File(path));
//            output = new FileOutputStream(destination.getFileDescriptor());
//
//            byte[] buf = new byte[1024];
//            int size;
//            while ((size = input.read(buf)) >= 0 && !cancellationSignal.isCanceled()) {
//                output.write(buf, 0, size);
//            }
//
//            if (cancellationSignal.isCanceled()) {
//                callback.onWriteCancelled();
//            } else {
//                callback.onWriteFinished(new PageRange[]{PageRange.ALL_PAGES});
//            }
//        } catch (Exception e) {
//            callback.onWriteFailed(e.toString());
//        } finally {
//            try {
//                if (input != null) input.close();
//                if (output != null) output.close();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//    }
//
//    @Override
//    public void onFinish() {
//        if (pdfDocument != null) {
//            pdfDocument.close();
//        }
//        if (pdfRenderer != null) {
//            pdfRenderer.close();
//        }
//    }
//}


import android.content.Context;
import android.graphics.pdf.PdfRenderer;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.os.ParcelFileDescriptor;
import android.print.PageRange;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.pdf.PrintedPdfDocument;
import android.graphics.pdf.PdfDocument;
import android.print.PrintDocumentInfo;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;


public class PdfDocumentAdapter extends PrintDocumentAdapter {
//    private Context context;
//    private View content;
//    private PrintedPdfDocument pdfDocument;
//    private int totalPages = 1;
//
//    public PdfDocumentAdapter(Context context, View content) {
//        this.context = context;
//        this.content = content;
//    }
//
//    @Override
//    public void onLayout(PrintAttributes oldAttributes, PrintAttributes newAttributes, CancellationSignal cancellationSignal, LayoutResultCallback callback, Bundle extras) {
//        int pages = computePageCount(newAttributes);
//        if(cancellationSignal.isCanceled()){
//            callback.onLayoutCancelled();
//            return;
//        }
//        PrintDocumentInfo info = new PrintDocumentInfo.Builder("report.pdf")
//                .setContentType(PrintDocumentInfo.CONTENT_TYPE_DOCUMENT)
//                .setPageCount(pages)
//                .build();
//        totalPages = pages;
//        callback.onLayoutFinished(info, true);
//    }
//
//    private int computePageCount(PrintAttributes printAttributes) {
//        int itemsPerPage = 4; // For simplicity, we assume 4 items per page
//        int pageCount = (int) Math.ceil(dataList.size() / (float) itemsPerPage);
//        return pageCount;
//    }
//
//    @Override
//    public void onWrite(PageRange[] pages, ParcelFileDescriptor destination, CancellationSignal cancellationSignal, WriteResultCallback callback) {
//        PrintedPdfDocument document = new PrintedPdfDocument(context, new PrintAttributes.Builder().build());
//        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(content.getWidth(), content.getHeight(), 1).create();
//        PdfDocument.Page page = document.startPage(pageInfo);
//
//        if(cancellationSignal.isCanceled()){
//            document.close();
//            callback.onWriteCancelled();
//            return;
//        }
//
//        Canvas canvas = page.getCanvas();
//        content.draw(canvas);
//        document.finishPage(page);
//
//        try {
//            document.writeTo(new FileOutputStream(destination.getFileDescriptor()));
//        } catch (IOException e) {
//            callback.onWriteFailed(e.toString());
//            return;
//        } finally {
//            document.close();
//        }
//
//        callback.onWriteFinished(new PageRange[]{PageRange.ALL_PAGES});
//
//    }

        private Context context;
    private String path;
    private PdfDocument pdfDocument;
    private PrintAttributes printAttributes;
    private PdfRenderer pdfRenderer;
    public PdfDocumentAdapter(Context context, String path) {
        this.context = context;
        this.path = path;
    }


    @Override
    public void onFinish() {
        if(pdfDocument != null) {
            pdfDocument.close();
        }
        if (pdfRenderer != null) {
            pdfRenderer.close();
        }
    }

    @Override
    public void onLayout(PrintAttributes oldAttributes, PrintAttributes newAttributes, CancellationSignal cancellationSignal, LayoutResultCallback callback, Bundle extras) {
        this.printAttributes = newAttributes;
        pdfDocument = new PrintedPdfDocument(context, newAttributes);

        if(cancellationSignal.isCanceled()) {
            callback.onLayoutCancelled();
            return;
        }

        PrintDocumentInfo info = new PrintDocumentInfo.Builder("report.pdf")
                .setContentType(PrintDocumentInfo.CONTENT_TYPE_DOCUMENT)
                .setPageCount(PrintDocumentInfo.PAGE_COUNT_UNKNOWN)
                .build();
        callback.onLayoutFinished(info, true);
    }

    @Override
    public void onWrite(PageRange[] pages, ParcelFileDescriptor destination, CancellationSignal cancellationSignal, WriteResultCallback callback) {
        FileInputStream input = null;
        OutputStream output = null;

        try {
            input = new FileInputStream(path);
            output = new FileOutputStream(destination.getFileDescriptor());

            byte[] buf = new byte[1024];
            int size;
            while ((size = input.read(buf)) >= 0 && !cancellationSignal.isCanceled()) {
                output.write(buf, 0, size);
            }
            if (cancellationSignal.isCanceled()) {
                callback.onWriteCancelled();
            } else {
                callback.onWriteFinished(new PageRange[]{PageRange.ALL_PAGES});
            }
        } catch (IOException e) {
            callback.onWriteFailed(e.toString());
        } finally {
            try {
                if(input != null) input.close();
                if(input != null) output.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
