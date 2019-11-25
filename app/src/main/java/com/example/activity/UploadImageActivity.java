package com.example.activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.asynctask.UploadImagesTask;
import com.example.utility.Constant;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;

/**
 * This
 */
public class UploadImageActivity extends BaseActivity implements View.OnClickListener {

    private ImageView mImageView;
    private android.support.v7.widget.Toolbar mToolbar;
    private Button mUploadButton;
    private Bitmap mBitmap = null;
    private Bundle mBundle;
    private Uri mImageUri;
    private EditText mImageNameEditText;
    private String mUsername;
    private final static String TITLE = "title";
    private final static int EDIT_RESULT_CODE = 0;
    private final static int LOGIN_RESULT_CODE = 5;
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_image);

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("Fetching Image....");
        mProgressDialog.show();

        initializeViews();
        configureToolbar(mToolbar);
        setOnClickListener();


        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();

        if (Intent.ACTION_SEND.equals(action) && type != null) {
            if (type.startsWith("image/")) {
                handleSendImage(intent); // Handle single image being sent
                mProgressDialog.dismiss();
                return;
            }
        }

        //getting the intent information from ImagesActivity
        mBundle = getIntent().getExtras();
        if (mBundle != null) {
            mUsername = mBundle.getString(Constant.USERNAME);
            int choice = mBundle.getInt(Constant.CHOICE_KEY);
            switch (choice) {
                case Constant.GALLERY:
                    showImageFromGallery();
                    break;

                case Constant.CAMERA:
                    showImageFromCamera();
                    break;

            }
        }
    }

    /**
     * This method configures the toolbar from base activity.
     * @param mToolbar
     */
    protected void configureToolbar(android.support.v7.widget.Toolbar mToolbar) {
        super.configureToolbar(mToolbar);
    }

    /**
     * This method handles the image shared form some other application
     * @param intent
     */
    private void handleSendImage(Intent intent) {
        mImageUri = intent.getParcelableExtra(Intent.EXTRA_STREAM);
        if (mImageUri != null) {
            Picasso.with(this).load(mImageUri).into(mImageView);
        }
    }

    /**
     * This method initializes the views.
     */
    private void initializeViews() {
        mToolbar = findViewById(R.id.app_bar);
        mImageView = findViewById(R.id.imageview);
        mUploadButton = findViewById(R.id.upload_btn);
        mImageNameEditText = findViewById(R.id.image_name_edittext);
    }

    /**
     * This method register the views for click listener
     */
    private void setOnClickListener() {
        mUploadButton.setOnClickListener(this);
        mImageView.setOnClickListener(this);
    }

    /**
     * This method shows the image chosed from gallery.
     */
    private void showImageFromGallery() {
        String str = mBundle.getString(Constant.IMAGE_FROM_GALLERY_KEY);
        mImageUri = Uri.parse(str);
        if(mImageUri!=null) {
            Picasso.with(this).load(mImageUri).into(mImageView);
        }

        mProgressDialog.dismiss();
    }

    /**
     * This method shows the image captured from camera.
     */
    private void showImageFromCamera() {
        mBitmap = getIntent().getExtras().getParcelable(Constant.IMAGE_FROM_CAMERA_KEY);
        mImageView.setImageBitmap(mBitmap);

        mProgressDialog.dismiss();

        //converting bitmap to uri
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(this.getContentResolver(), mBitmap, TITLE, null);
        mImageUri = Uri.parse(path);

    }

    /**
     * This method uploads the image to the firebase storage in relation with the username.
     */
    private void uploadImageFile() {
        if (mUsername != null) {
            UploadImagesTask uploadTask = new UploadImagesTask(this, mUsername,mImageNameEditText);
            uploadTask.execute(mImageUri);
        } else {
            //In case the user is not logged in
            showLoginDialog();
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        switch (requestCode) {
            case EDIT_RESULT_CODE: //  Receives the result from SaveScreenActivity
                if (resultCode == RESULT_OK) {
                    String str = intent.getStringExtra(Constant.EDITED_URI);
                    mImageUri = Uri.parse(str);
                    Picasso.with(this).load(mImageUri).into(mImageView);
            }
                break;
            case LOGIN_RESULT_CODE: // Receives the result from LoginActivity
                if (resultCode == RESULT_OK) {
                    mUsername = intent.getStringExtra(Constant.USERNAME);
                }
                break;

        }
    }

    private void showLoginDialog() {
        AlertDialog.Builder showLoginDialog = new AlertDialog.Builder(this);

        showLoginDialog.setPositiveButton(R.string.sign_in, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Intent loginIntent = new Intent(UploadImageActivity.this, LoginActivity.class);
                loginIntent.putExtra(Constant.NEXT_SCREEN, Constant.UPLOAD_SCREEN);
                startActivityForResult(loginIntent,LOGIN_RESULT_CODE);
            }
        });
        showLoginDialog.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                finish();
            }
        });

        showLoginDialog.setTitle(R.string.login_alert);
        showLoginDialog.setMessage(R.string.user_not_signed_in);

        AlertDialog dialog = showLoginDialog.create();
        dialog.show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.upload_btn:
                uploadImageFile();
                break;
            case R.id.imageview:
                startActivityForResult(new Intent(this, EditImageActivity.class).putExtra(Constant.TO_BE_EDITED_URI, mImageUri.toString()),EDIT_RESULT_CODE);
        }
    }

    @Override
    public void onBackPressed() {

        finish();
        startActivity(new Intent(this, ImagesActivity.class).putExtra(Constant.USERNAME, mUsername));

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

}
