package com.example.chatapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApi;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    private EditText email;
    private EditText password;
    private SignInButton googleSignInButton;
    private Button createAnAccount;
    private int RC_SIGN_IN = 1;
    private Button loginBtn;

    private static final String TAG = "MainActivity";


    private FirebaseAuth fauth;
    DatabaseReference rootRef;

    private GoogleSignInClient mGoogleSignInClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rootRef = FirebaseDatabase.getInstance().getReference();

        //logging in
        loginBtn = findViewById(R.id.LoginBtn);
        email = findViewById(R.id.Email);
        password = findViewById(R.id.Password);

        loginBtn.setOnClickListener((View v) -> {
            String emailString = email.getText().toString();
            String passwordString = password.getText().toString();

            if(emailString.isEmpty() && passwordString.isEmpty()) {
                email.setError("Email field is empty.");
                password.setError("Password field is empty.");
            } else if(emailString.isEmpty()) {
                email.setError("Email field is empty.");
            } else if (passwordString.isEmpty()) {
                password.setError("Password field is empty.");
            } else {
                fauth.signInWithEmailAndPassword(emailString, passwordString).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        FirebaseUser user = fauth.getCurrentUser();
                        if(task.isSuccessful()) {
                            if(user.isEmailVerified()) {
                                Toast.makeText(MainActivity.this, "Successful login.", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(MainActivity.this, ChatActivity.class));
                            } else {
                                Toast.makeText(MainActivity.this, "Email is not verified.", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(MainActivity.this, "Error: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });





        createAnAccount = findViewById(R.id.CreateAnAccBtn);

        googleSignInButton = findViewById(R.id.GoogleSignInBtn);
        fauth = FirebaseAuth.getInstance();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
               .requestIdToken(getString(R.string.default_web_client_id))
               .requestEmail()
               .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        googleSignInButton.setOnClickListener((View v) -> {

            signIn();
        });

        createAnAccount.setOnClickListener((View v) -> {
            startActivity(new Intent(MainActivity.this, NewAccountActivity.class));
        });


    }

    private void signIn() {
        Intent signInClient = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInClient, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }


    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount acc = completedTask.getResult(ApiException.class);
            Toast.makeText(MainActivity.this, "Signed in successfully.", Toast.LENGTH_SHORT).show();
            FirebaseGoogleAuth(acc);
            startActivity(new Intent(MainActivity.this, ChatActivity.class));
        } catch (ApiException e) {
            Toast.makeText(MainActivity.this, "Sign in failed" + e.getMessage(), Toast.LENGTH_SHORT).show();
            FirebaseGoogleAuth(null);
        }
    }


    private void FirebaseGoogleAuth (GoogleSignInAccount acct) {
        AuthCredential authCredential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        fauth.signInWithCredential(authCredential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()) {
                    String currentUserID = fauth.getCurrentUser().getUid();
                    rootRef.child("Users").child(currentUserID).setValue("");
                    Toast.makeText(MainActivity.this, "Successful", Toast.LENGTH_SHORT).show();
                    FirebaseUser user = fauth.getCurrentUser();
                    updateUI(user);
                } else {
                    Toast.makeText(MainActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                    updateUI(null);
                }
            }
        });
    }


    private void updateUI(FirebaseUser fUser) {
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(getApplicationContext());
        if(account != null) {
            String name = account.getDisplayName();
            String givenName = account.getGivenName();
            String familyName = account.getFamilyName();
            String personEmail = account.getEmail();
            String personId = account.getId();
            Uri photo = account.getPhotoUrl();
        }
    }


}