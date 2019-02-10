package com.projects.maheshbelnekar.event_networks;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class SetupActivity extends AppCompatActivity {

    private EditText UserName, FullName, Profession;
    private DatePicker dob;
    private CircleImageView profileImage;
    private Button saveInformationButton;
    private ProgressDialog loadingBar;

    private FirebaseAuth mAuth;
    private DatabaseReference usersRef;
    private StorageReference userProfileImageRef;

    String currentUserId;
    final static int GALLERY_PICK = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();
        usersRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserId);
        userProfileImageRef = FirebaseStorage.getInstance().getReference().child("Profile_Images");

        setContentView(R.layout.activity_setup);
        UserName = (EditText)findViewById(R.id.setup_username);
        FullName = (EditText)findViewById(R.id.setup_fullname);
        Profession = (EditText)findViewById(R.id.setup_profession);
        saveInformationButton = (Button)findViewById(R.id.setup_save_information_button);
        profileImage = (CircleImageView)findViewById(R.id.setup_profile_image);

        loadingBar = new ProgressDialog(this);

        saveInformationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveAccountSetUpInformation();
            }
        });

        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Select a picture from the gallery
                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent,GALLERY_PICK);
            }
        });

        usersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){

                    if (dataSnapshot.hasChild("profileImage")){
                        String profileImagePath = dataSnapshot.child("profileImage").getValue().toString();
                        Picasso.get().load(profileImagePath).placeholder(R.drawable.profile).into(profileImage);
                    }
                    else {
                        Toast.makeText(SetupActivity.this,"Please Select a Profile Image", Toast.LENGTH_SHORT);
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if ( requestCode == GALLERY_PICK && resultCode == RESULT_OK && data != null ){
            Uri imageUri = data.getData();

            // send to crop image
            CropImage.activity().setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1,1).start(this);
        }

        // Check if this request result is from crop image activity
        if (requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK){
                // Get the uri of the new cropped image
                final Uri resultUri = result.getUri();

                // Store(upload) this image on our realtime database with user id as the key
                final StorageReference filePath = userProfileImageRef.child(currentUserId + ".jpg");

                filePath.putFile(resultUri).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if (!task.isSuccessful()) {
                            throw task.getException();
                        }

                        // Continue with the task to get the download URL
                        return filePath.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        Toast.makeText(SetupActivity.this, "profile image stored", Toast.LENGTH_SHORT).show();

                        //final String downloadUrl = task.getResult().getMetadata().getReference().getDownloadUrl();
                        final String downloadUrl = task.getResult().toString();
                        Log.i("Mahesh", "Download URL ," + downloadUrl);
                        usersRef.child("profileImage").setValue(downloadUrl).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(SetupActivity.this, "Profile iamge saved to databse"
                                            , Toast.LENGTH_SHORT).show();

                                    Intent selfIntent = new Intent(SetupActivity.this, SetupActivity.class);
                                    startActivity(selfIntent);

                                } else {
                                    Toast.makeText(SetupActivity.this, "Error Occured: " + task.getException().getMessage()
                                            , Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                    });
            }
            else {
                Toast.makeText(SetupActivity.this,"Error in cropping image",Toast.LENGTH_SHORT).show();
            }
        }
    }

    private Boolean saveAccountSetUpInformation() {
        String username = UserName.getText().toString();
        String fullname = FullName.getText().toString();
        String profession = Profession.getText().toString();

        if(username.isEmpty()){
            Toast.makeText(this,"Please Enter Username",Toast.LENGTH_SHORT).show();
        }
        else if(fullname.isEmpty()){
            Toast.makeText(this,"Please Enter Full Name",Toast.LENGTH_SHORT).show();
        }
        else if(profession.isEmpty()){
            Toast.makeText(this,"Please Enter Profession",Toast.LENGTH_SHORT).show();
        }
        else {
            loadingBar.setTitle("Creating new account");
            loadingBar.setMessage("Please wait, while we are creating your new account");
            loadingBar.show();
            loadingBar.setCanceledOnTouchOutside(true);

            // Save data to realtime database
            HashMap userMap = new HashMap();
            userMap.put("username",username);
            userMap.put("fullname",fullname);
            userMap.put("profession",profession);
            usersRef.updateChildren(userMap).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    loadingBar.dismiss();
                    if(task.isSuccessful()){
                        sendUserToMainActivity();
                        Toast.makeText(SetupActivity.this,"your account is created successfully",Toast.LENGTH_SHORT).show();
                    }
                    else{
                        String errMsg = task.getException().getMessage();
                        Toast.makeText(SetupActivity.this,errMsg,Toast.LENGTH_LONG).show();
                    }
                }
            });
        }


        return true;
    }

    private Boolean sendUserToMainActivity() {
        Intent mainIntent = new Intent(SetupActivity.this, MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        return true;
    }

}
