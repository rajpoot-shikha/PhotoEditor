package com.example.activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.example.manager.EditImageManager;
import com.example.utility.ApplicationUtils;
import com.example.utility.Constant;
import com.fenchtose.nocropper.CropperView;

import java.io.IOException;

/**
 * This Activity performs the editing of image
 */
public class EditImageActivity extends BaseActivity implements View.OnClickListener {

    private android.support.v7.widget.Toolbar mToolbar;
    private Uri mEditedImageUri;
    private Uri mOriginalUri;
    private CropperView mCropperView;
    private Button mSaveButton;
    private ImageButton mRotateImageBtn;
    private ImageButton mCropImageBtn;
    private ImageButton mGrayScaleImageBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_image);
        initializeViews();
        setOnClickListener();
        configureToolbar(mToolbar);

        String str = getIntent().getStringExtra(Constant.TO_BE_EDITED_URI);
        mEditedImageUri = Uri.parse(str);
        mOriginalUri = mEditedImageUri;
        try {
            setImageToCropperView(mEditedImageUri);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * This method configures the toolbar from base activity
     *
     * @param mToolbar
     */
    protected void configureToolbar(android.support.v7.widget.Toolbar mToolbar) {
        super.configureToolbar(mToolbar);
    }

    /**
     * This method sets the image to cropper view
     *
     * @param uri
     * @throws IOException
     */
    private void setImageToCropperView(Uri uri) throws IOException {

        mCropperView.setImageBitmap(ApplicationUtils.convertUriToBitmap(uri, this));
        mEditedImageUri = uri;
    }

    /**
     * This method initializes the views
     */
    private void initializeViews() {
        mCropperView = findViewById(R.id.edit_cropview);
        mSaveButton = findViewById(R.id.save_btn);
        mCropImageBtn = findViewById(R.id.crop);
        mGrayScaleImageBtn = findViewById(R.id.gray_scale);
        mRotateImageBtn = findViewById(R.id.rotate);
        mToolbar = findViewById(R.id.app_bar);
    }

    /**
     * This method registers the views for click listener
     */
    private void setOnClickListener() {
        mSaveButton.setOnClickListener(this);
        mRotateImageBtn.setOnClickListener(this);
        mCropImageBtn.setOnClickListener(this);
        mGrayScaleImageBtn.setOnClickListener(this);

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.save_btn:
                Intent returnIntent = new Intent();
                returnIntent.putExtra(Constant.EDITED_URI, mEditedImageUri.toString());
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
                break;
            case R.id.rotate:
                try {
                    setImageToCropperView(EditImageManager.getInstance().rotateImage(mEditedImageUri, this));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.crop:
                try {
                    setImageToCropperView(EditImageManager.getInstance().cropImage(mEditedImageUri, this, mCropperView));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.gray_scale:
                try {
                    setImageToCropperView(EditImageManager.getInstance().grayScaleImage(mEditedImageUri, this));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if (mOriginalUri != mEditedImageUri) {
            showSaveAlertDialog();
        } else {
            sendTheEditedImageBack();
        }
    }

    /**
     * This method shows the alert dialog to alert the user to save the changes made.
     */
    private void showSaveAlertDialog() {
        AlertDialog.Builder saveAlertDialog = new AlertDialog.Builder(this);

        saveAlertDialog.setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                sendTheEditedImageBack();

            }
        });

        saveAlertDialog.setNegativeButton(R.string.dont_save, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                finish();
            }
        });

        saveAlertDialog.setTitle(R.string.save_alert_title);
        saveAlertDialog.setMessage(R.string.save_alert_message);

        AlertDialog dialog = saveAlertDialog.create();
        dialog.show();
    }

    private void sendTheEditedImageBack() {
        Intent returnIntent = new Intent();
        returnIntent.putExtra(Constant.EDITED_URI, mEditedImageUri.toString());
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }

}
