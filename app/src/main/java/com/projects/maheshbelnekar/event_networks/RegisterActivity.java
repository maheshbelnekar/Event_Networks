package com.projects.maheshbelnekar.event_networks;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class RegisterActivity extends AppCompatActivity {

    private Button createAccountButton;
    private EditText userEmail, userPassword, userConfirmPassword;
    private FirebaseAuth mAuth;
    private ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
        userEmail = (EditText)findViewById(R.id.register_email);
        userPassword = (EditText)findViewById(R.id.register_password);
        userConfirmPassword = (EditText)findViewById(R.id.register_confirm_password);
        loadingBar = new ProgressDialog(this);
        createAccountButton = (Button)findViewById(R.id.register_create_account);

        createAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createNewAccount();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null)
        {
            // If the user is already logged in, send him to main activity
            sendUserToMainActivity();
        }
    }

    private Boolean createNewAccount() {
        // first get the values from the screen
        String email = userEmail.getText().toString();
        String password = userPassword.getText().toString();
        String confirmPassword = userConfirmPassword.getText().toString();
        // perform validations
        if(email.isEmpty()){
            Toast.makeText(this,"Please Enter Email",Toast.LENGTH_SHORT).show();
        }
        else if(password.isEmpty()){
            Toast.makeText(this,"Please Enter Password",Toast.LENGTH_SHORT).show();
        }
        else if(confirmPassword.isEmpty()) {
            Toast.makeText(this, "Please Re-Enter Password", Toast.LENGTH_SHORT).show();
        }
        else if(!confirmPassword.equals(password)){
            Toast.makeText(this,"Passwords DO NOT match",Toast.LENGTH_SHORT).show();
        }
        else{

            loadingBar.setTitle("Saving Details");
            loadingBar.setMessage("Please wait, while we save your information");
            loadingBar.show();
            loadingBar.setCanceledOnTouchOutside(true);

            // Create user at firebase using email
            mAuth.createUserWithEmailAndPassword(email,password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            loadingBar.dismiss();
                            if(task.isSuccessful()){
                                sendUserToSetupActivity();
                                Toast.makeText(RegisterActivity.this, "User created succesfully", Toast.LENGTH_SHORT).show();
                            }
                            else{
                                String errMessage = task.getException().getMessage();
                                Toast.makeText(RegisterActivity.this, "Error in creating account: "+errMessage, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }

        return true;
    }

    private Boolean sendUserToSetupActivity() {
        Intent setupIntent = new Intent(RegisterActivity.this,SetupActivity.class);
        setupIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(setupIntent);
        finish();
        return true;
    }

    private Boolean sendUserToMainActivity() {
        Intent mainIntent = new Intent(RegisterActivity.this, MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        return true;
    }
}
