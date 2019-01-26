package com.projects.maheshbelnekar.event_networks;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private Button loginButton;
    private EditText userEmail, userPassword;
    private TextView needNewAccountLink;
    private FirebaseAuth mAuth;
    private ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        // Initializing all my elements
        needNewAccountLink = (TextView)findViewById(R.id.register_account_link);
        userEmail = (EditText)findViewById(R.id.login_email);
        userPassword = (EditText)findViewById(R.id.login_password);
        loginButton = (Button)findViewById(R.id.login_button);
        loadingBar = new ProgressDialog(this);


        // set a listener for login button
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUserToAccount();
            }
        });

        // set a listener for signup link
        needNewAccountLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Send him to register
                sendUserToRegisterActivity();
            }
        });

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


    private Boolean loginUserToAccount() {

        // first get the values from the screen
        String email = userEmail.getText().toString();
        String password = userPassword.getText().toString();
        // perform validations
        if(email.isEmpty()){
            Toast.makeText(this,"Please Enter Email",Toast.LENGTH_SHORT).show();
        }
        else if(password.isEmpty()){
            Toast.makeText(this,"Please Enter Password",Toast.LENGTH_SHORT).show();
        }
        else{

            loadingBar.setTitle("Login");
            loadingBar.setMessage("Please wait, while we log into your account");
            loadingBar.show();
            loadingBar.setCanceledOnTouchOutside(true);

            // Create user at firebase using email
            mAuth.signInWithEmailAndPassword(email,password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            loadingBar.dismiss();
                            if(task.isSuccessful()){
                                sendUserToMainActivity();
                                Toast.makeText(LoginActivity.this, "Login succesfully", Toast.LENGTH_SHORT).show();
                            }
                            else{
                                String errMessage = task.getException().getMessage();
                                Toast.makeText(LoginActivity.this, "Error in login: "+errMessage, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
        return true;
    }

    private Boolean sendUserToMainActivity() {
        Intent mainIntent = new Intent(LoginActivity.this, MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        return true;
    }

    private Boolean sendUserToRegisterActivity() {

        Intent registerIntent = new Intent(LoginActivity.this,RegisterActivity.class);
        startActivity(registerIntent);
        return true;
    }
}
