package com.android.management_linen.helpers;

import android.app.AlertDialog;
import android.content.Context;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.io.File;

public class PdfPreviewDialog {
    public static void showPdfDialog(Context context, File pdfFile) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
        WebView webView = new WebView(context);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageFinished(WebView view, String url) {

            }
        });
        webView.loadUrl("file:///" + pdfFile.getAbsolutePath());
        dialogBuilder.setView(webView);
        dialogBuilder.setPositiveButton("Print", (dialog, which) -> {

        });

        dialogBuilder.setNegativeButton("Cancel", (dialog, which) -> {});
        AlertDialog dialog = dialogBuilder.create();
        dialog.show();
    }
}
