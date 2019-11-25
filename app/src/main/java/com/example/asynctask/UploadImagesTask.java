package com.example.asynctask;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.Toast;

import com.example.activity.R;
import com.example.model.Image;
import com.example.utility.Constant;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.concurrent.TimeUnit;

/**
 * This class uploads the images to the firebase.
 */

public class UploadImagesTask extends AsyncTask <Uri,Void,Void>{

   // private StorageReference mStorageReference;
    private DatabaseReference mDatabaseImages;
    private String username;
    private EditText mImageNameEditText;
    private Context mContext;
    private ProgressDialog mProgressDialog;
    private static final String IMAGE_ID = "image_id_";

    public UploadImagesTask(Context context,String username, EditText imageNameEditText)
    {
        this.username = username;
        this.mContext = context;
        this.mImageNameEditText = imageNameEditText;
    }
    @Override
    protected void onPreExecute() {
        mProgressDialog = new ProgressDialog(mContext);
        mProgressDialog.setMessage(mContext.getString(R.string.uploading_progress_dialog));
        mProgressDialog.show();
    }

    @Override
    protected Void doInBackground(Uri... uri) {

        Uri mImageUri=uri[0];
        StorageReference mStorageReference = FirebaseStorage.getInstance().getReference(Constant.UPLOADS);
        mDatabaseImages = FirebaseDatabase.getInstance().getReference(Constant.IMAGES);

        if(mImageUri!=null)
        {
            StorageReference fileReference = mStorageReference.child(System.currentTimeMillis()+"."+getFileExtension(mImageUri));
            fileReference.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    Image image = new Image(username,mImageNameEditText.getText().toString().trim(),taskSnapshot.getDownloadUrl().toString());
                    String imageId = mDatabaseImages.push().getKey();
                    mDatabaseImages.child(IMAGE_ID+imageId).setValue(image);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(mContext,e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {

                }
            });
        }

        return null;
    }

    @Override
    protected void onPostExecute(Void result)
    {
        Toast.makeText(mContext, R.string.successful_upload,Toast.LENGTH_SHORT).show();
        mProgressDialog.dismiss();

    }
    private String getFileExtension(Uri uri)
    {
        ContentResolver contentResolver = mContext.getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(contentResolver.getType(uri));
    }
}
