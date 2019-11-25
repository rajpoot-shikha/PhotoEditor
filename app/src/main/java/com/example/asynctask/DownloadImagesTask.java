package com.example.asynctask;

import android.os.AsyncTask;

import com.example.interfaces.IOnDownloadCompleted;
import com.example.model.Image;
import com.example.utility.Constant;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * This class downloads the images from the firebase.
 */
public class DownloadImagesTask extends AsyncTask<Void, Void, ArrayList<Image>> {

    private ArrayList<Image> mImages = new ArrayList<>();
    private DatabaseReference mDatabaseImages;
    private String mUsername;
    private IOnDownloadCompleted downloadCompleted;


    public DownloadImagesTask(String username, IOnDownloadCompleted context) {

        this.mUsername = username;
        this.downloadCompleted = context;
    }

    @Override
    protected ArrayList<Image> doInBackground(Void... params) {
        mDatabaseImages = FirebaseDatabase.getInstance().getReference(Constant.IMAGES);
        mDatabaseImages.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    Image image = child.getValue(Image.class);
                    if (mUsername.equals(image.getUser_name())) {
                        mImages.add(image);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        return mImages;
    }

    @Override
    protected void onPostExecute(ArrayList<Image> images) {

        try {
            downloadCompleted.onDownloadCompleted(images);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}