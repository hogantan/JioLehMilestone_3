package com.example.jioleh.settings;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.jioleh.PostLoginPage;
import com.example.jioleh.R;
import com.example.jioleh.userprofile.UserProfile;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

public class EditProfilePage extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private TextInputLayout til_username;
    private TextInputLayout til_contact;
    private Spinner gender;
    private String spinner_input = null;
    private TextInputLayout til_age;
    private TextInputLayout til_bio;
    private TextInputLayout til_location;
    private TextInputLayout til_interests;
    private Button confirmEdit;
    private ImageView iv_ImageView;
    private ImageButton ic_camera;
    private Toolbar toolbar;

    private UserProfile oldUserProfile;
    private UserProfile newUserProfile;
    private FirebaseUser firebaseUser;

    //identifier for image upload
    private final int PICK_IMAGE_REQUEST = 1;

    private Uri mImageUri = null;

    //Firebase Storage
    private StorageReference storageReference;

    //boolean check
    private boolean wantToRemoveProfilePic = false;


    StorageTask uploadTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_time_user_page);
        initialise();
        initialiseToolbar();
        initialiseSpinners();
        getUserProfile();

        ic_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                profileImageDialog().show();
            }
        });

        confirmEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditProfile();
            }
        });
    }

    private Dialog profileImageDialog() {

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Edit Profile Image")
                    .setItems(new String[]{"Replace with another Image", "Remove Current Image"}, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // The 'which' argument contains the index position
                            // of the selected item

                            if (which == 0) {
                                openFileChooser();
                            } else if (which == 1) {
                                iv_ImageView.setImageResource(R.drawable.default_picture);
                                mImageUri = null;
                                wantToRemoveProfilePic = true;
                            }
                        }
                    });
            return builder.create();
        }


    private void initialise() {
        til_username = findViewById(R.id.username);
        til_contact = findViewById(R.id.contact);
        gender = findViewById(R.id.spGender);
        til_age = findViewById(R.id.age);
        til_bio = findViewById(R.id.bio);
        iv_ImageView = findViewById(R.id.image_view);
        ic_camera = findViewById(R.id.ic_camera);
        til_interests = findViewById(R.id.Interest);
        til_location = findViewById(R.id.Location);
        confirmEdit = findViewById(R.id.btnConfirmEdit);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        //the storage file for userProfileImage
        storageReference = FirebaseStorage.getInstance().getReference("userProfileImage");
    }

    private void initialiseSpinners() {
        ArrayAdapter<CharSequence> type_activity_adapter = ArrayAdapter.createFromResource(this,
                R.array.gender, android.R.layout.simple_spinner_item);
        type_activity_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        gender.setAdapter(type_activity_adapter);
        gender.setOnItemSelectedListener(this);
    }

    private void initialiseToolbar() {
        toolbar = findViewById(R.id.tbTopBar);
        toolbar.setTitle("");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void EditProfile() {
        String username = til_username.getEditText().getText().toString();
        String contact = til_contact.getEditText().getText().toString();
        String gender = spinner_input;
        String age = til_age.getEditText().getText().toString();
        String bio = til_bio.getEditText().getText().toString();
        String interests = til_interests.getEditText().getText().toString();
        String location = til_location.getEditText().getText().toString();

        if (!validateFields(til_username) | !validateFields(til_age) | !validateSpinner(spinner_input)) {
            alertDialog();
        } else {

            if (checkAge(til_age)) {
                Toast.makeText(this, "Please key in an appropriate age", Toast.LENGTH_SHORT).show();
                return;
            }

            newUserProfile = new UserProfile(username, contact, gender.toUpperCase().charAt(0) + gender.substring(1).toLowerCase()
                    , age, bio, interests, location);

            if (mImageUri == null && oldUserProfile.getImageUrl()!=null) {

                if (wantToRemoveProfilePic) {
                    newUserProfile.setImageUrl("");
                } else {
                    newUserProfile.setImageUrl(oldUserProfile.getImageUrl());
                }
                putInFirestore(newUserProfile);

            } else {
                uploadFile(newUserProfile, mImageUri);
            }
        }
    }

    //Benchmark set as 122 as oldest recorded person is 122 lol
    private boolean checkAge(TextInputLayout til) {
        if (Integer.parseInt(til.getEditText().getText().toString()) > 122) {
            return true;
        } else {
            return false;
        }
    }

    public void alertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(EditProfilePage.this);

        builder.setMessage("Please fill up the required fields")
                .setTitle("Setup Profile");
        builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            }
        });
        AlertDialog dialog = builder.create();

        dialog.show();
    }
    public boolean validateFields(TextInputLayout til) {
        if (til.getEditText().getText().toString().isEmpty()) {
            til.getEditText().setError("Field cannot be empty");
            return false;
        } else {
            til.getEditText().setError(null);
            return true;
        }
    }

    private boolean validateSpinner(String input) {
        if (input == null) {
            return false;
        } else {
            return true;
        }
    }

    //onClick for the ImageView
    public void addImage(View v) {
        openFileChooser();
    }

    //open file explorer in phone for user to choose image to upload into ImageView
    public void openFileChooser() {
        Intent intent = new Intent();
        //fix to those file that is image
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    public void getUserProfile() {
        String uid = firebaseUser.getUid();
        DocumentReference docRef = FirebaseFirestore
                .getInstance()
                .collection("users")
                .document(uid);

        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    UserProfile currUserProfile = task.getResult().toObject(UserProfile.class);
                    fill(currUserProfile);
                } else {
                    Toast.makeText(EditProfilePage.this,
                            "User details cannot be found please check with admin",
                            Toast.LENGTH_SHORT)
                            .show();
                }
            }
        });
    }

    public void fill(UserProfile profile){
        oldUserProfile = profile;
        til_username.getEditText().setText(profile.getUsername());
        til_age.getEditText().setText(profile.getAge());
        gender.setSelection(setSpinnerDetails(profile.getGender()));
        til_contact.getEditText().setText(profile.getContact());
        til_bio.getEditText().setText(profile.getBio());
        til_location.getEditText().setText(profile.getLocation());
        til_interests.getEditText().setText(profile.getInterests());

        if (!profile.getImageUrl().equals("") && profile.getImageUrl()!=null) {
            Picasso.get().load(profile.getImageUrl()).into(iv_ImageView);
        }
    }

    public void startNextActivity() {
        Intent intent = new Intent(EditProfilePage.this, PostLoginPage.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra("loadProfileFrag", "profilePage");
        startActivity(intent);
    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //ensure image not null and request matches our code (PICK_IMAGE_REQUEST)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {

            //get image uri
            mImageUri = data.getData();

            //load into imageView on app
            Picasso.get().load(mImageUri).into(iv_ImageView);
        }
    }

    public void uploadFile(final UserProfile userProf, Uri mImageUri) {

        //ensure that users have selected an image
        if (mImageUri != null) {
            String fileNameForCurrentUser = firebaseUser.getUid();

            //creating the file for current user based on uid
            final StorageReference fileRef = storageReference.child(fileNameForCurrentUser);

            //uploading to firebase storage
            uploadTask = fileRef.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Toast.makeText(EditProfilePage.this, "Upload success", Toast.LENGTH_LONG).show();

                    //get the download url so that we can store in the userProfile object and retrieve when needed
                    fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {

                            //this line below serves as a confirmation for testing
                            //Toast.makeText(FirstTimeUserPage.this, uri.toString(), Toast.LENGTH_LONG).show();

                            //put uri in the userProfile obj we created
                            userProf.setImageUrl(uri.toString());

                            //then put our obj into firestore
                            putInFirestore(userProf);
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(EditProfilePage.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            //if users decide not to upload image, then imageURL in userProfile will be empty string
            putInFirestore(userProf);
        }
    }

    public void putInFirestore(UserProfile user) {

        String uid = firebaseUser.getUid();
        if (user.getImageUrl() == null || user.getImageUrl().equals("")) {
            //delete profile pic from firebase storage if user decides to remove image.
            storageReference.child(uid).delete();
        }

        FirebaseFirestore.getInstance()
                .collection("users")
                .document(uid)
                .set(user, SetOptions.merge());
        //onBackPressed();
        startNextActivity();
    }

    //Used for spinner
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        if (parent.getItemAtPosition(position).equals("Select one")) {
            spinner_input = null;
        } else {
            //do something
            spinner_input = parent.getItemAtPosition(position).toString();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private int setSpinnerDetails(String input) {
        if (input.equals("Male")) {
            return 1;
        } else if (input.equals("Female")) {
            return 2;
        } else {
            return 0;
        }
    }
}
