package com.example.jioleh;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatDialogFragment;

// Pop-up window when user successfully registers an account
// Returns to login page when ok is pressed
public class VerificationEmailSentDialog extends AppCompatDialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Email has been sent")
                .setMessage("Please check email to verify your account")
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent nextActivity = new Intent(getContext(), MainActivity.class);
                        startActivity(nextActivity);
                    }
                });
        return builder.create();
    }
}
