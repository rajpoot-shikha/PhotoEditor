package com.example.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

/**
 * This Activity shows the splash screen at the time of launch of application.
 */
public class SplashScreenActivity extends AppCompatActivity {

    private static int DURATION = 2000;
    private static String EMAIL_ID = "shikha.rajpoot1995@gmail.com";
    private static String PASSWORD = "Shikha$1307";
    private FirebaseAuth mFirebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        mFirebaseAuth = FirebaseAuth.getInstance();
        authenticateUser();
        init();
    }

    private void init() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(SplashScreenActivity.this, WelcomeScreenActivity.class));
                finish();
            }
        }, DURATION);

    }

    private void authenticateUser() {
        mFirebaseAuth.createUserWithEmailAndPassword(EMAIL_ID, PASSWORD).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(SplashScreenActivity.this, R.string.splash_authentication_success_message, Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}
