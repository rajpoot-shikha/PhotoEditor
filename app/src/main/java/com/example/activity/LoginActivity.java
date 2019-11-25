package com.example.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.model.User;
import com.example.utility.ApplicationUtils;
import com.example.utility.Constant;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * This class provides the platform to login the user
 */
public class LoginActivity extends BaseActivity implements View.OnClickListener {
    private TextView mSignUpTextView;
    private Button mLoginButton;
    private EditText mUsernameEditText;
    private EditText mPasswordEditText;
    private DatabaseReference mDatabaseUsers;
    private Toolbar mToolbar;
    private String nextScreen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);
        initializeViews();
        setOnClickListener();
        configureToolbar(mToolbar);

        Intent intent = getIntent();
        nextScreen = intent.getStringExtra(Constant.NEXT_SCREEN);
        mDatabaseUsers = FirebaseDatabase.getInstance().getReference(Constant.USERS);
    }

    /**
     * This method configure the tool bar from base activity.
     *
     * @param mToolbar
     */
    protected void configureToolbar(Toolbar mToolbar) {
        super.configureToolbar(mToolbar);
    }

    /**
     * This method initializes the views.
     */
    private void initializeViews() {
        mToolbar = findViewById(R.id.app_bar);
        mSignUpTextView = findViewById(R.id.sign_up_textview);
        mLoginButton = findViewById(R.id.login_btn);
        mUsernameEditText = findViewById(R.id.login_username_edittext);
        mPasswordEditText = findViewById(R.id.login_password_edittext);
    }

    /**
     * This method register the views for click listener
     */
    private void setOnClickListener() {
        mSignUpTextView.setOnClickListener(this);
        mLoginButton.setOnClickListener(this);
    }

    /**
     * This method validates the user credentials
     *
     * @param username
     * @param password
     */
    private void validateCredentials(final String username, final String password) {
        mDatabaseUsers.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(username).exists()) {
                    if (!username.isEmpty()) {
                        User user = dataSnapshot.child(username).getValue(User.class);
                        if (ApplicationUtils.decrypt(user.getPassword()).equals(password)) {

                            if (nextScreen != null && nextScreen.equals(Constant.UPLOAD_SCREEN)) {
                                Intent returnIntent = new Intent(LoginActivity.this, UploadImageActivity.class);
                                returnIntent.putExtra(Constant.USERNAME, username);
                                setResult(Activity.RESULT_OK, returnIntent);
                                Toast.makeText(LoginActivity.this, R.string.login_success, Toast.LENGTH_SHORT).show();
                                finish();

                            } else {
                                startActivity(new Intent(LoginActivity.this, ImagesActivity.class).putExtra(Constant.USERNAME, username));
                            }
                        } else {
                            Toast.makeText(LoginActivity.this, R.string.incorrect_password, Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(LoginActivity.this, R.string.empty_fields, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(LoginActivity.this, R.string.user_not_registered, Toast.LENGTH_SHORT).show();
                }
                setTextEmpty();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    /**
     * This method handles the onclick events
     *
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sign_up_textview:
                setTextEmpty();
                startActivity(new Intent(this, RegisterActivity.class));
                break;

            case R.id.login_btn:
                validateCredentials(mUsernameEditText.getText().toString(), mPasswordEditText.getText().toString());

        }
    }

    @Override
    public void onBackPressed() {
        finish();
        startActivity(new Intent(this, WelcomeScreenActivity.class));
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void setTextEmpty() {
        mUsernameEditText.setText("");
        mPasswordEditText.setText("");
    }
}
