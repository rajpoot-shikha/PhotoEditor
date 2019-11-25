package com.example.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This activity provides platform to register the user
 */
public class RegisterActivity extends BaseActivity implements View.OnClickListener {
    private static final String ALPHANUMERIC_REGEX_PATTERN = "^[a-zA-Z0-9]*$";
    private static final String PASSWORD_PATTERN = "[\\\\/ ]";
    private static final int MINIMUM_LENGTH = 6;
    private static final int MAXIMUM_LENGTH = 16;
    private TextView mSignInTextView;
    private Button mRegisterButton;
    private EditText mUsernameEditText;
    private EditText mPasswordEditText;
    private EditText mConfirmPasswordEditText;
    private Toolbar mToolbar;
    private DatabaseReference mDatabaseUsers;
    private String mUsername;
    private String mPassword;
    private String mConfirmPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        initializeViews();
        setOnClickListener();
        configureToolbar(mToolbar);

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
        mSignInTextView = findViewById(R.id.sign_in_textview);
        mRegisterButton = findViewById(R.id.register_btn);
        mUsernameEditText = findViewById(R.id.register_username_edittext);
        mPasswordEditText = findViewById(R.id.register_password_edittext);
        mConfirmPasswordEditText = findViewById(R.id.register_confirm_password_edittext);
    }

    /**
     * This method register the views for click listener
     */
    private void setOnClickListener() {
        mSignInTextView.setOnClickListener(this);
        mRegisterButton.setOnClickListener(this);
    }

    /**
     * This method returns the result upon validation of user credentials
     *
     * @param username
     * @param password
     * @param confirmPassword
     * @return
     */
    private boolean validateCredentials(String username, String password, String confirmPassword) {
        boolean flag = false;
        //if username is valid
        if (validUserName(username)) {
            flag = true;
            if (flag) {
                //check for password
                if (validPassword(password))
                    flag = true;
                else {
                    Toast.makeText(this, R.string.invalid_pass, Toast.LENGTH_SHORT).show();
                    flag = false;
                }
            }
            if (flag) {
                //check for password and confirm password equality
                if (password.equals(confirmPassword))
                    flag = true;
                else {
                    Toast.makeText(this, R.string.password_confirm_password_mismatch, Toast.LENGTH_SHORT).show();
                    flag = false;
                }
            }
        } else
            Toast.makeText(this, R.string.invalid_username, Toast.LENGTH_SHORT).show();
        return flag;
    }


    /**
     * This method validates username which should be alphanumeric
     *
     * @param userName
     * @return
     */
    private boolean validUserName(String userName) {
        Pattern pattern = Pattern.compile(ALPHANUMERIC_REGEX_PATTERN);
        Matcher matcher = pattern.matcher(userName);
        if (matcher.matches()) {
            return true;
        } else {
            return false;
        }

    }

    /**
     * This method validates password which should not support space,/,\
     *
     * @param password
     * @return
     */
    private boolean validPassword(String password) {
        Pattern pattern = Pattern.compile(PASSWORD_PATTERN);
        Matcher matcher = pattern.matcher(password);
        if (matcher.find()) {
            return false;
        } else {
            return true;
        }

    }

    /**
     * This method performs user registration
     */
    private void registerUser() {
        boolean flag = true;
        mUsername = mUsernameEditText.getText().toString();
        mPassword = mPasswordEditText.getText().toString();
        mConfirmPassword = mConfirmPasswordEditText.getText().toString();

        if (!TextUtils.isEmpty(mUsername) && !TextUtils.isEmpty(mPassword) && !TextUtils.isEmpty(mConfirmPassword)) {

            if (mUsername.length() < MINIMUM_LENGTH || mPassword.length() < MINIMUM_LENGTH) {
                flag = false;
                Toast.makeText(RegisterActivity.this, R.string.minimum_length_check, Toast.LENGTH_SHORT).show();
                setTextEmpty();
            }
            if (mUsername.length() >MAXIMUM_LENGTH || mPassword.length() > MAXIMUM_LENGTH) {
                flag = false;
                Toast.makeText(RegisterActivity.this, R.string.maximum_length_check, Toast.LENGTH_SHORT).show();
                setTextEmpty();
            }

            if (flag) {
                if (validateCredentials(mUsername, mPassword, mConfirmPassword)) {
                    if (mDatabaseUsers != null) {
                        final User user = new User(mUsername, ApplicationUtils.encrypt(mPassword));
                        mDatabaseUsers.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (!dataSnapshot.child(mUsername).exists()) {
                                    mDatabaseUsers.child(mUsername).setValue(user);
                                    Toast.makeText(RegisterActivity.this, R.string.successful_user_registration, Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                                } else {
                                    Toast.makeText(RegisterActivity.this, R.string.user_already_exists, Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                        //setting edittext to blank again
                        setTextEmpty();
                    }
                }
            }

        } else {
            Toast.makeText(this, R.string.empty_username_password_fields, Toast.LENGTH_LONG).show();
        }
    }

    /**
     * This method handles the onclick events
     *
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sign_in_textview:
                setTextEmpty();
                startActivity(new Intent(this, LoginActivity.class));
                break;
            case R.id.register_btn:
                registerUser();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void setTextEmpty() {
        mUsernameEditText.setText("");
        mPasswordEditText.setText("");
        mConfirmPasswordEditText.setText("");
    }
}
