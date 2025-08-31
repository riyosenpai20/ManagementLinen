package com.android.management_linen;
import android.graphics.Bitmap;
import java.io.ByteArrayOutputStream;
public class EscPosCommandGenerator {
    public static byte[] generate(Bitmap bmp) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            int width = bmp.getWidth();
            int height = bmp.getHeight();
            int[] pixels = new int[width * height];
            bmp.getPixels(pixels, 0, width, 0, 0, width, height);

            // Initialize printer
            baos.write(new byte[]{0x1B, 0x40}); // ESC @

            // Set line spacing to 24 dots
            baos.write(new byte[]{0x1B, 0x33, 24});

            for (int y = 0; y < height; y += 24) {
                baos.write(new byte[]{0x1B, 0x2A, 33, (byte)(width % 256), (byte)(width / 256)});

                for (int x = 0; x < width; x++) {
                    for (int k = 0; k < 3; k++) {
                        byte slice = 0;
                        for (int b = 0; b < 8; b++) {
                            int yOffset = y + (k * 8) + b;
                            if (yOffset < height) {
                                int pixel = pixels[x + (yOffset * width)];
                                int luminance = ((pixel >> 16) & 0xff) + ((pixel >> 8) & 0xff) + (pixel & 0xff);
                                if (luminance < 382) { // threshold of 50% grey
                                    slice |= (1 << (7 - b));
                                }
                            }
                        }
                        baos.write(slice);
                    }
                }
                baos.write(new byte[]{0x0A}); // New line
            }

            // Reset line spacing
            baos.write(new byte[]{0x1B, 0x32});
        } catch (Exception e) {
            e.printStackTrace();
        }

        return baos.toByteArray();
    }
}
