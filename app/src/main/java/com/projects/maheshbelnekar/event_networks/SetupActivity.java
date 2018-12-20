package com.projects.maheshbelnekar.event_networks;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

import de.hdodenhof.circleimageview.CircleImageView;

public class SetupActivity extends AppCompatActivity {

    private EditText userName, fullName;
    private DatePicker dob;
    private CircleImageView profileImage;
    private Button saveInformationButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);
        userName = (EditText)findViewById(R.id.setup_username);
        fullName = (EditText)findViewById(R.id.setup_fullname);
        dob = (DatePicker)findViewById(R.id.setup_dob);
        saveInformationButton = (Button)findViewById(R.id.setup_save_information_button);
        profileImage = (CircleImageView)findViewById(R.id.setup_profile_image);

    }
}
