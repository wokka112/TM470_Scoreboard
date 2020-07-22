package com.floatingpanda.scoreboard.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;

import com.floatingpanda.scoreboard.interfaces.PopupConfirmDialogInterface;
import com.floatingpanda.scoreboard.views.activities.BoardGameActivity;

/**
 * Simple helper class that provides a method to create and display a popup dialog warning with a
 * message passed as a parameter.
 */
public class AlertDialogHelper {
    /**
     * Creates and displays a simple popup dialog warning and an 'okay' button to close the dialog.
     * @param message the message to be displayed in the dialog warning
     * @param activity the activity which will contain the dialog
     */
    public static void popupWarning(String message, Activity activity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setMessage(message);
        builder.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    public static void popupConfirmDialog(String title, String message, String positiveButtonText, String negativeButtonText, Activity activity, PopupConfirmDialogInterface popupConfirmDialogInterface) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(title)
                .setMessage(message)
                .setPositiveButton(positiveButtonText, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        popupConfirmDialogInterface.positiveAction();
                    }
                })
                .setNegativeButton(negativeButtonText, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        popupConfirmDialogInterface.negativeAction();
                    }
                })
                .create()
                .show();
    }
}
