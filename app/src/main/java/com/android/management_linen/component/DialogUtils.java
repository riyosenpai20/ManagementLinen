package com.android.management_linen.component;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

public class DialogUtils {
    public static void showAlertDialog(Context context, String title, String message, String positiveButtonlabel, DialogInterface.OnClickListener positiveAction, String negativeButtonLabel, DialogInterface.OnClickListener negativeAction) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title)
                .setMessage(message)
                .setPositiveButton(positiveButtonlabel, positiveAction);
        if(negativeButtonLabel != null && negativeAction != null) {
            builder.setNegativeButton(negativeButtonLabel, negativeAction);
        }
        builder.setCancelable(true)
                .show();
    }
}
