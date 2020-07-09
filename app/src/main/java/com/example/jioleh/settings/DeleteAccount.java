package com.example.jioleh.settings;

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
import android.widget.Toast;

import com.example.jioleh.R;
import com.example.jioleh.listings.JioActivity;
import com.example.jioleh.login.LoginPage;
import com.example.jioleh.login.MainActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.EventListener;
import java.util.HashMap;
import java.util.List;

public class DeleteAccount extends AppCompatActivity {

    private Toolbar toolbar;
    private TextInputLayout email;
    private TextInputLayout password;
    private Button delete;

    private FirebaseAuth database;
    private FirebaseFirestore datastore;
    private FirebaseUser currentUser;

    private ProgressDialog progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_account);

        initialise();
        initialiseToolbar();

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datastore.collection("users")
                        .document(currentUser.getUid())
                        .get()
                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                //Confirming deletion/Verifying deletion
                                if (checkInputs()) {
                                    alertDialog();
                                } else {
                                    Toast.makeText(DeleteAccount.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                }
        });
    }

    private void initialise() {
        progressBar = new ProgressDialog(DeleteAccount.this);
        email = findViewById(R.id.tilConfirmEmail);
        password = findViewById(R.id.tilConfirmPassword);
        delete = findViewById(R.id.btnConfirmCDelete);
        database = FirebaseAuth.getInstance();
        datastore = FirebaseFirestore.getInstance();
        currentUser = database.getCurrentUser();
    }

    private void initialiseToolbar() {
        toolbar = findViewById(R.id.tbTempTopBar);
        toolbar.setTitle("Settings");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    //Flow of deleting of data is described in method
    private void alertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to delete your account?")
                .setTitle("Delete Account");

        builder.setPositiveButton("yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                //Progress bar settings
                progressBar.setTitle("Deleting Account");
                progressBar.setMessage("Please wait while we delete your account");
                progressBar.setCanceledOnTouchOutside(false);
                progressBar.show();

                //Deleting from FirebaseAuth
                AuthCredential credential = EmailAuthProvider
                        .getCredential(email.getEditText().getText().toString(), password.getEditText().getText().toString());
                currentUser.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            currentUser.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        progressBar.dismiss();
                                        Intent nextActivity = new Intent(DeleteAccount.this, MainActivity.class);
                                        nextActivity.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(nextActivity);
                                        Toast.makeText(DeleteAccount.this, "Account has been deleted successfully", Toast.LENGTH_SHORT).show();
                                        deleteData();
                                    } else {
                                        progressBar.dismiss();
                                        Toast.makeText(DeleteAccount.this, "Failed to delete account" , Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        } else {
                            progressBar.dismiss();
                            Toast.makeText(DeleteAccount.this, "Incorrect email or password", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        builder.setNegativeButton("no", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

            }
        });

        AlertDialog dialog = builder.create();

        dialog.show();
    }

    //Update number of current participants as well as the list of participants in the activity
    private void updateParticipants(final String activity_id) {
        datastore.collection("activities")
                .document(activity_id)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        //Document might not exist if another user has deleted an activity, etc
                        if (documentSnapshot.exists()) {
                            int current_participants = documentSnapshot.toObject(JioActivity.class).getCurrent_participants();
                            ArrayList<String> list_of_participants = documentSnapshot.toObject(JioActivity.class).getParticipants();

                            current_participants = current_participants -1;
                            list_of_participants.remove(currentUser.getUid());

                            datastore.collection("activities")
                                    .document(activity_id)
                                    .update("current_participants", current_participants);

                            datastore.collection("activities")
                                    .document(activity_id)
                                    .update("participants", list_of_participants);
                        }
                    }
                });

    }

    private boolean checkInputs() {
        if (email.getEditText() == null || password.getEditText() == null) {
            return false;
        } else {
            return true;
        }
    }

    private void deleteData() {
        HashMap<String, Boolean> input = new HashMap<>();
        input.put("isDeleted", true);

        //Deleting from FirebaseFireStore
        //To flag to delete or not rather than delete the whole collection which might run out of memory
        datastore.collection("users")
                .document(currentUser.getUid())
                .set(input, SetOptions.merge());

        //Delete all of users listed activities
        datastore.collection("users")
                .document(currentUser.getUid())
                .collection("activities_listed")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List<DocumentSnapshot> documents = queryDocumentSnapshots.getDocuments();
                        for (DocumentSnapshot document : documents) {
                            datastore.collection("activities")
                                    .document(document.getId())
                                    .delete();
                        }
                    }
                });

        //Delete user from all his joined activity
        datastore.collection("users")
                .document(currentUser.getUid())
                .collection("joined")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List<DocumentSnapshot> documents = queryDocumentSnapshots.getDocuments();
                        for (DocumentSnapshot document : documents) {
                            updateParticipants(document.getId());
                        }
                    }
                });
    }
}