package com.example.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.activity.R;

/**
 * This class describes an image item view and metadata about its place within the RecyclerView.
 */

public class ViewHolder extends RecyclerView.ViewHolder {
    public TextView mImageTitleTextView;
    public ImageView mImageIconImageView;

    public ViewHolder(View v) {
        super(v);
        mImageTitleTextView = v.findViewById(R.id.image_title);
        mImageIconImageView = v.findViewById(R.id.single_image);
    }

}
