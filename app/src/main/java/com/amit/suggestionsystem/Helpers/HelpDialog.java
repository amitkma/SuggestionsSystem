package com.amit.suggestionsystem.Helpers;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;

import com.amit.suggestionsystem.R;

/**
 * Created by Amit on 04-05-2016.
 */
public class HelpDialog extends DialogFragment {

    private String message;
    private String title;
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        Bundle bundle = getArguments();
        message = bundle.getString("message_key");
        title = bundle.getString("title_key");

        // Build the dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(message);
        builder.setTitle(title);
        builder.setCancelable(true);
        return builder.create();
    }
}
