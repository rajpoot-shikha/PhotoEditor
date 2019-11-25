package com.example.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.activity.R;
import com.example.model.Image;
import com.example.viewholder.ViewHolder;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * This adapter class sets the data(images) into the recycler view.
 */
public class ImageAdapter extends RecyclerView.Adapter {

    private Context mContext;
    private List<Image> mImages;

    public ImageAdapter(Context context,List<Image> images) {
        this.mContext = context;
        this.mImages = images;
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.single_image_item,parent, false);

        if(view!=null)
            return new ViewHolder(view);
        else
            return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        ViewHolder hold = (ViewHolder) holder;
        Image image = mImages.get(position);
        if(hold!=null && image!=null) {
            hold.mImageTitleTextView.setText(image.getImage_name().toUpperCase());
            Picasso.with(mContext)
                    .load(image.getImage_url())
                    .fit()
                    .centerCrop()
                    .into(hold.mImageIconImageView);
        }
    }

    @Override
    public int getItemCount() {
        if(mImages!=null)
        {
            return mImages.size();
        }
        else
        {
            return 0;
        }

    }
}
