package com.android.management_linen;

import android.graphics.Bitmap;
import android.graphics.pdf.PdfRenderer;
import android.os.ParcelFileDescriptor;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class PDFRenderer {
    public static Bitmap renderToBitmap(File file, int pageIndex) throws IOException {
        ParcelFileDescriptor fileDescriptor = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY);
        PdfRenderer pdfRenderer = new PdfRenderer(fileDescriptor);
        PdfRenderer.Page page = pdfRenderer.openPage(pageIndex);

        Bitmap bitmap = Bitmap.createBitmap(page.getWidth(), page.getHeight(), Bitmap.Config.ARGB_8888);
        page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_PRINT);
        page.close();
        pdfRenderer.close();
        fileDescriptor.close();
        return bitmap;
    }

    public static File bitmapToFile(Bitmap bitmap, File file) throws IOException {
        FileOutputStream fos = new FileOutputStream(file);
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
        fos.close();
        return file;
    }
}
