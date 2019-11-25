package com.example.activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

/**
 * This class provides the options for user login and sign up.
 */
public class WelcomeScreenActivity extends AppCompatActivity implements View.OnClickListener {

    private Button mLoginButton;
    private Button mRegisterButton;
    private AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_screen);
        initializeViews();
        setOnClickListener();
        registerAdView();
    }

    /**
     * This method initializes the views.
     */
    private void initializeViews() {
        mLoginButton = findViewById(R.id.login_btn);
        mRegisterButton = findViewById(R.id.register_btn);
    }

    /**
     * This method register the views for click listener
     */
    private void setOnClickListener() {
        mLoginButton.setOnClickListener(this);
        mRegisterButton.setOnClickListener(this);
    }

    /**
     * This method register the adview to the activity
     */
    private void registerAdView() {
        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
    }

    /**
     * This method directs the flow of execution as per button clicks.
     *
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login_btn:
                startActivity(new Intent(this, LoginActivity.class));
                break;

            case R.id.register_btn:
                startActivity(new Intent(this, RegisterActivity.class));
                break;
        }
    }


    @Override
    public void onBackPressed() {
        finish();
    }
}
