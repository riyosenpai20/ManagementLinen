package com.android.management_linen;

import android.graphics.Bitmap;
import android.graphics.Color;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class BitmapUtils {
    public static byte[] bitmapToPrinterBytes(Bitmap bitmap) throws IOException {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        for(int y=0; y < height; y+= 8) {
            for(int x=0; x < width; x++) {
                byte[] row = new byte[width / 8];
                for (int bit = 0; bit < 8; bit++) {
                    int pixel = bitmap.getPixel(x, y + bit);
                    int grayscale = (Color.red(pixel) + Color.green(pixel) + Color.blue(pixel)) / 3;
                    if (grayscale < 128) {
                        row[x / 8] |= 1 << (7 - (x % 8));
                    }
                }
                outputStream.write(row);
            }
        }
        return outputStream.toByteArray();
    }
}
