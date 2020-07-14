package com.example.jioleh.userprofile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jioleh.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class ReportUserPage extends AppCompatActivity {

    private Button btn_submit_report;
    private EditText report_words;
    private TextView tv_reportedUsername;
    private Toolbar tb_report_user_top_bar;

    private ProgressDialog progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_user);
        initialise();
        initTb();

        Intent intent = getIntent();

        final String reportedUserId = intent.getStringExtra("user_id");
        String reportedUserName = intent.getStringExtra("username");
        final String currentUid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        tv_reportedUsername.setText(reportedUserName);

        btn_submit_report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String words = report_words.getText().toString();
                confirmationDialog(currentUid,reportedUserId, words, reportedUserName);
            }
        });


    }

    public void initialise() {
        progressBar = new ProgressDialog(ReportUserPage.this);
        btn_submit_report = findViewById(R.id.report_submit);
        report_words = findViewById(R.id.report_words);
        tv_reportedUsername = findViewById(R.id.report_username);
    }

    public void initTb() {
        tb_report_user_top_bar = findViewById(R.id.report_user_top_bar);
        tb_report_user_top_bar.setTitle("");
        tb_report_user_top_bar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }



    public void uploadToReportToFirestore(String currenUid, String reportedUid, String words) {

        FirebaseFirestore.getInstance().collection("Reports")
                .document(currenUid)
                .set(new Report(currenUid, reportedUid, words, true))
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            progressBar.dismiss();
                            Toast.makeText(ReportUserPage.this, "Submitted successfully", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            progressBar.dismiss();
                            Toast.makeText(ReportUserPage.this, "Please try again", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private void confirmationDialog(String currenUid, String reportedUid, String words, String reportedUsername) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ReportUserPage.this);

        builder.setMessage("Are you done with your report of " + reportedUsername + "?")
                .setTitle("Confirm Report");

        builder.setNegativeButton("no", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            }
        });

        builder.setPositiveButton("yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                //Progress bar settings
                progressBar.setTitle("Submit Report");
                progressBar.setMessage("Please wait while we submit your report");
                progressBar.setCanceledOnTouchOutside(false);
                progressBar.show();

                uploadToReportToFirestore(currenUid, reportedUid, words);
            }
        });
        AlertDialog dialog = builder.create();

        dialog.show();
    }

}
