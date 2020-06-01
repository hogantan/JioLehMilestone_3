package com.example.jioleh;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

public class FirstTimeUserPage extends AppCompatActivity {


    private TextInputLayout til_username;
    private TextInputLayout til_contact;
    private TextInputLayout til_gender;
    private TextInputLayout til_age;
    private TextInputLayout til_bio;
    private Button btn_CreateProfile;
    private ImageView iv_ImageView;
    private TextView tv_addProfileImage;


    private UserProfile userProfile;
    private FirebaseUser firebaseUser;

    //identifier for image upload
    private final int PICK_IMAGE_REQUEST = 1;

    private Uri mImageUri = null;

    //Firebase Storage
    private StorageReference storageReference;


    StorageTask uploadTask;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_time_user_page);
        initialise();

        btn_CreateProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //prevent users from accidentally clicking button and upload duplicate image
                if (uploadTask != null && uploadTask.isInProgress()) {
                    Toast.makeText(FirstTimeUserPage.this, "upload in progress", Toast.LENGTH_SHORT).show();
                } else {
                    createProfile();
                }
            }
        });

    }

    private void initialise() {
        til_username = findViewById(R.id.username);
        til_contact = findViewById(R.id.contact);
        til_gender = findViewById(R.id.gender);
        til_age = findViewById(R.id.age);
        til_bio = findViewById(R.id.bio);
        btn_CreateProfile = findViewById(R.id.btn_createProfile);
        iv_ImageView = findViewById(R.id.image_view);
        tv_addProfileImage = findViewById(R.id.textView);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        //the storage file for userProfileImage
        storageReference = FirebaseStorage.getInstance().getReference("userProfileImage");
    }

    public void createProfile() {
        String username = til_username.getEditText().getText().toString();
        String contact = til_contact.getEditText().getText().toString();
        String gender = til_gender.getEditText().getText().toString();
        String age = til_age.getEditText().getText().toString();
        String bio = til_bio.getEditText().getText().toString();

        if (!validateUsername(username) | !validateContact(contact)) {
            alertDialog();
        } else {
            userProfile = new UserProfile(username, contact, gender, age, bio);

            uploadFile();
        }
    }

    public boolean validateUsername(String username) {
        if (username.isEmpty()) {
            til_username.getEditText().setError("Field can't be empty");
            return false;
        } else {
            til_username.getEditText().setError(null);
            return true;
        }
    }

    public boolean validateContact(String contact) {
        if (contact.isEmpty()) {
            til_contact.getEditText().setError("Field can't be empty");
            return false;
        } else {
            til_contact.getEditText().setError(null);
            return true;
        }
    }

    public void alertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(FirstTimeUserPage.this);

        builder.setMessage("Please fill up the required fields")
                .setTitle("Setup Profile");
        builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            }
        });
        AlertDialog dialog = builder.create();

        dialog.show();
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

    //remove default background image on userProfile imageView
    public void removeDefaultProfilePic() {
        iv_ImageView.setBackgroundResource(android.R.color.transparent);
        tv_addProfileImage.setVisibility(View.GONE);
    }

    //getting data from the file choosen
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //ensure image not null and request matches our code (PICK_IMAGE_REQUEST)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {

            //get image uri
            mImageUri = data.getData();

            //remove default pic
            removeDefaultProfilePic();

            //load into imageView on app
            Picasso.get().load(mImageUri).into(iv_ImageView);
        }
    }

    //get file extension for image i.e. the file type (.jpn, .jpeg etc)
    private String getFileExtension(Uri uri) {
        ContentResolver cr = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cr.getType(uri));
    }

    public void putInFirestore(UserProfile user) {
        String uid = firebaseUser.getUid();
        FirebaseFirestore.getInstance().collection("users").document(uid).set(user, SetOptions.merge());
        startNextActivity();
    }

    public void startNextActivity() {
        startActivity(new Intent(FirstTimeUserPage.this, PostLoginPage.class));
    }

    public void uploadFile() {

        //ensure that users have selected an image
        if (mImageUri != null) {
            String fileNameForCurrentUser = firebaseUser.getUid();

            //creating the file for current user based on uid
            final StorageReference fileRef = storageReference.child(fileNameForCurrentUser);

            //uploading to firebase storage
            uploadTask = fileRef.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Toast.makeText(FirstTimeUserPage.this, "Upload success", Toast.LENGTH_LONG).show();

                    //get the download url so that we can store in the userProfile object and retrieve when needed
                    fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {

                            //this line below serves as a confirmation for testing
                            //Toast.makeText(FirstTimeUserPage.this, uri.toString(), Toast.LENGTH_LONG).show();

                            //put uri in the userProfile obj we created
                            userProfile.setImageUrl(uri.toString());

                            //then put our obj into firestore
                            putInFirestore(userProfile);
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(FirstTimeUserPage.this, e.getMessage(),Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            //if users decide not to upload image, then imageURL in userProfile will be empty string
            putInFirestore(userProfile);
        }
    }


    }
