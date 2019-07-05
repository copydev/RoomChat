package com.example.roomchat;

import android.content.Intent;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


import java.util.Arrays;
import java.util.List;

public class Splash extends AppCompatActivity {

    private static final int RC_SIGN_IN = 123;

    Button signin;
    ProgressBar progressBar;

    //FirebaseUser user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        signin = findViewById(R.id.signin);
        progressBar = findViewById(R.id.progressBar);

        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createSignInIntent();
            }
        });

    }

    public void createSignInIntent() {
        // [START auth_fui_create_intent]
        // Choose authentication providers
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build()
//                new AuthUI.IdpConfig.PhoneBuilder().build(),
//                new AuthUI.IdpConfig.GoogleBuilder().build(),
//                new AuthUI.IdpConfig.FacebookBuilder().build(),
//                new AuthUI.IdpConfig.TwitterBuilder().build()
        );

        // Create and launch sign-in intent
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .build(),
                RC_SIGN_IN);
        // [END auth_fui_create_intent]
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            if (resultCode == RESULT_OK) {
                // Successfully signed in
                //FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                Toast.makeText(this,"Successful",Toast.LENGTH_LONG).show();
                goToMainPage();

                // ...
            } else {
                // Sign in failed. If response is null the user canceled the
                // sign-in flow using the back button. Otherwise check
                // response.getError().getErrorCode() and handle the error.
                // ...
                Toast.makeText(this,"Some Error Occured",Toast.LENGTH_LONG).show();
                signin.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if(user == null){
            signin.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);

            //createSignInIntent();
            //Commented for login button
        }
        else{
            goToMainPage();
            Toast.makeText(this,user.getDisplayName(),Toast.LENGTH_LONG).show();
        }
    }


    //Only starts when user is successfully signup-ed
    public void goToMainPage(){
        progressBar.setVisibility(View.VISIBLE);
        signin.setVisibility(View.GONE);

//        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

//        FirebaseDatabase database = FirebaseDatabase.getInstance();
//        DatabaseReference myRef = database.getReference().child(user.getUid());
//
//        myRef.setValue("Hello, World!");


        Intent intent = new Intent(Splash.this, MainActivity.class);
        startActivity(intent);
        //Toast.makeText(this,user.getDisplayName(),Toast.LENGTH_LONG).show();
    }




}
