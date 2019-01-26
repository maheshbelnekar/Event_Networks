package com.projects.maheshbelnekar.event_networks;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import de.hdodenhof.circleimageview.CircleImageView;

public class SetupActivity extends AppCompatActivity {

    private EditText userName, fullName, profession;
    private DatePicker dob;
    private CircleImageView profileImage;
    private Button saveInformationButton;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        setContentView(R.layout.activity_setup);
        userName = (EditText)findViewById(R.id.setup_username);
        fullName = (EditText)findViewById(R.id.setup_fullname);
        profession = (EditText)findViewById(R.id.setup_fullname);
        saveInformationButton = (Button)findViewById(R.id.setup_save_information_button);
        profileImage = (CircleImageView)findViewById(R.id.setup_profile_image);

    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null)
        {
            // If the user is aready logged in, send him to main activity
            sendUserToMainActivity();
        }
    }


    private Boolean sendUserToMainActivity() {
        Intent mainIntent = new Intent(SetupActivity.this, MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        return true;
    }

}
