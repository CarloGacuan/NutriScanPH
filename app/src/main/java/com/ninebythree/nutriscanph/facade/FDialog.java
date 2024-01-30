package com.ninebythree.nutriscanph.facade;

import android.app.Dialog;
import android.content.Context;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.ninebythree.nutriscanph.R;

import org.tensorflow.lite.support.metadata.schema.Content;

public class FDialog {
    private Dialog processingDialog;

    public FDialog(Context activity) {
        processingDialog = new android.app.Dialog(activity);
        processingDialog.setContentView(R.layout.dialog_processing);
        processingDialog.setCancelable(false); // prevent the dialog from being dismissed
    }

    public FDialog(View activity) {
        processingDialog = new android.app.Dialog(activity.getContext());
        processingDialog.setContentView(R.layout.dialog_processing);
        processingDialog.setCancelable(false); // prevent the dialog from being dismissed
    }

    public void show() {
        if (processingDialog != null && !processingDialog.isShowing()) {
            processingDialog.show();
        }
    }

    public void hide() {
        if (processingDialog != null && processingDialog.isShowing()) {
            processingDialog.dismiss();
        }
    }

}
