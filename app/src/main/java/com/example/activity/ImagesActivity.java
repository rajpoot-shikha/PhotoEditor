package com.example.activity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.adapter.ImageAdapter;
import com.example.asynctask.DownloadImagesTask;
import com.example.interfaces.IOnDownloadCompleted;
import com.example.model.Image;
import com.example.utility.Constant;

import java.util.ArrayList;

/**
 * This activity displays the images of user stored in firebase storage.
 */

public class ImagesActivity extends BaseActivity implements IOnDownloadCompleted, SwipeRefreshLayout.OnRefreshListener {
    private RecyclerView mImageRecyclerView;
    private TextView mUsernameTextView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private String username;
    private ImageAdapter mImageAdapter;
    private Toolbar mToolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_images);
        initializeViews();
        configureToolbar(mToolbar);

        username = getIntent().getExtras().getString(Constant.USERNAME);
        if (username != null) {
            mUsernameTextView.setText(getString(R.string.welcome) + " " + username.toUpperCase());
            DownloadImagesTask task = new DownloadImagesTask(username, this);
            task.execute();
        }
    }

    /**
     * This method configures the toolbar from base activity
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
        mUsernameTextView = findViewById(R.id.user_name_textview);
        mImageRecyclerView = findViewById(R.id.images_view);
        mImageRecyclerView.setHasFixedSize(true);
        mImageRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        mSwipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mToolbar = findViewById(R.id.app_bar);
    }

    /**
     * This method displays the dialog containing image choosing options.
     */
    private void showPictureDialog() {
        AlertDialog.Builder pictureDialog = new AlertDialog.Builder(this);
        pictureDialog.setTitle(R.string.choose_image_title);
        pictureDialog.setItems(R.array.image_choosing_options,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case Constant.GALLERY:
                                choosePhotoFromGallary();
                                break;
                            case Constant.CAMERA:
                                takePhotoFromCamera();
                                break;
                        }
                    }
                });
        pictureDialog.show();
    }

    /**
     * This method invoked when choose from gallery option is selected.
     */
    private void choosePhotoFromGallary() {

        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, Constant.GALLERY);
    }

    /**
     * This method invoked when capture from camera option is selected.
     */
    private void takePhotoFromCamera() {
        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, Constant.CAMERA);
    }

    /**
     * This method accepts the result from what so ever option is selected and directs the image to the upload screen.
     * uri is forwarded in case image is chosen from gallery.
     * bitmap is forwarded in case image is captured by camera.
     * username of the logged in user is also forwarded to keep the track of user-image pair.
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Bundle bundle = new Bundle();
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == this.RESULT_CANCELED) {
            return;
        }
        if (requestCode == Constant.GALLERY) {
            if (data != null) {
                Uri imageUri = data.getData();
                Intent intent = new Intent(this, UploadImageActivity.class);
                bundle.putString(Constant.IMAGE_FROM_GALLERY_KEY, imageUri.toString());
                bundle.putInt(Constant.CHOICE_KEY, Constant.GALLERY);
                bundle.putString(Constant.USERNAME, username);
                startActivity(intent.putExtras(bundle));
            }
        } else if (requestCode == Constant.CAMERA) {
            Bitmap bitmap = (Bitmap) data.getExtras().get("data");
            Intent intent = new Intent(this, UploadImageActivity.class);
            bundle.putInt(Constant.CHOICE_KEY, Constant.CAMERA);
            bundle.putParcelable(Constant.IMAGE_FROM_CAMERA_KEY, bitmap);
            bundle.putString(Constant.USERNAME, username);
            startActivity(intent.putExtras(bundle));
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add_images:
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, 123);
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    /**
     * This method requests the permission to access the storage and camera at run time.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        showPictureDialog();
    }

    /**
     * This method shows the images of user stored in firebase.
     *
     * @param images
     */
    @Override
    public void onDownloadCompleted(ArrayList<Image> images) {

        mImageAdapter = new ImageAdapter(this, images);
        mImageRecyclerView.setAdapter(mImageAdapter);

    }

    @Override
    public void onBackPressed() {
        showLogoutAlertDialog();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onRefresh() {
        Intent intent = getIntent();
        finish();
        startActivity(intent);
    }

    /**
     * This method shows the alert logout dialog
     */
    private void showLogoutAlertDialog() {
        AlertDialog.Builder logoutAlertDialog = new AlertDialog.Builder(this);

        logoutAlertDialog.setPositiveButton(R.string.logout, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                finish();
                startActivity(new Intent(ImagesActivity.this, LoginActivity.class));

            }
        });
        logoutAlertDialog.setNegativeButton(R.string.cancel_logout, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

            }
        });

        logoutAlertDialog.setTitle(R.string.logout_alert);
        logoutAlertDialog.setMessage(R.string.logout_message);

        AlertDialog dialog = logoutAlertDialog.create();
        dialog.show();
    }

}
