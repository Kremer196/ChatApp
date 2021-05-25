package com.example.chatapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class NewAccountActivity extends AppCompatActivity {

    private EditText registerFirstName, registerLastName, registerEmail, registerPassword, registerConfirmPassword;
    private Button registerBtn;

    private FirebaseAuth fAuth;
    DatabaseReference rootRef;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.newaccount_activity);


        registerFirstName = findViewById(R.id.RegisterFirstName);
        registerLastName = findViewById(R.id.RegisterLastName);
        registerEmail = findViewById(R.id.RegisterEmail);
        registerPassword = findViewById(R.id.RegisterPassword);
        registerConfirmPassword = findViewById(R.id.RegisterConfirmPassword);

        registerBtn = findViewById(R.id.RegisterBtn);

        fAuth = FirebaseAuth.getInstance();

        rootRef = FirebaseDatabase.getInstance().getReference();


        registerBtn.setOnClickListener((View v) -> {
            if(registerEmail.getText().toString().isEmpty() && registerPassword.getText().toString().isEmpty()) {
                registerEmail.setError("Email field is empty.");
                registerPassword.setError("Password field is empty.");
            } else if(registerEmail.getText().toString().isEmpty()) {
                registerEmail.setError("Email field is empty.");
            } else if(registerPassword.getText().toString().isEmpty()) {
                registerPassword.setError("Password field is empty.");
            } else if(registerPassword.getText().toString().length() < 8) {
                registerPassword.setError("Password has to have minimal 8 characters.");
            } else if (!registerPassword.getText().toString().equals(registerConfirmPassword.getText().toString())) {
                registerPassword.setError("The passwords do not match.");
            } else {
                fAuth.createUserWithEmailAndPassword(registerEmail.getText().toString(), registerPassword.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()) {
                            String currentUserID = fAuth.getCurrentUser().getUid();
                            rootRef.child("Users").child(currentUserID).setValue("");
                            sendVerificationEmail();
                        } else {
                            Toast.makeText(NewAccountActivity.this, "Error: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });


    }


    private void sendVerificationEmail() {
        FirebaseUser user = fAuth.getCurrentUser();
        user.reload();
        String name = registerFirstName.getText().toString() + " " + registerLastName.getText().toString();
        UserProfileChangeRequest updateUser = new UserProfileChangeRequest.Builder().setDisplayName(name).build();
        user.updateProfile(updateUser);
        user.sendEmailVerification().addOnCompleteListener(this, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()) {
                    Toast.makeText(NewAccountActivity.this, "Verification email sent to " + user.getEmail(), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(NewAccountActivity.this, "Error: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });


     }
}
