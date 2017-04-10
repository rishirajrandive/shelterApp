package com.cmpe277.shelter.helper;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.cmpe277.shelter.user.landlord.ImagePagerActivity;
import com.cmpe277.shelter.R;
import com.cmpe277.shelter.util.DownloadImageTask;
import com.cmpe277.shelter.util.ImagePicker;


/**
 * Property image holder, handles the display of thumbnails in horizontal list.
 */
public class PropertyImageHolder extends RecyclerView.ViewHolder
        implements View.OnClickListener {

    private ImageView mPropertyImageView;

    private PropertyImage mPropertyImage;

    private Activity mActivity;

    private FragmentManager mFragmentManager;

    /**
     * Constructor for holder
     * @param itemView
     * @param context
     * @param fragmentManager
     */
    public PropertyImageHolder(View itemView, Activity context, FragmentManager fragmentManager) {
        super(itemView);
        itemView.setOnClickListener(this);

        mActivity = context;
        mFragmentManager = fragmentManager;
        mPropertyImageView = (ImageView) itemView.findViewById(R.id.property_image);
    }

    /**
     * Binds the image for the property
     * @param aImage
     */
    public void bindImage(PropertyImage aImage) {
        mPropertyImage = aImage;
        if(mPropertyImage.getImageBitMap() == null){
            if(mPropertyImage.getImageResourceId() == 0){
                new DownloadImageTask(mPropertyImageView).execute(mPropertyImage.getImagePath());
            }else {
                mPropertyImageView.setBackgroundResource(mPropertyImage.getImageResourceId());
            }
        } else {
            mPropertyImageView.setImageBitmap(ThumbnailUtils.extractThumbnail(mPropertyImage.getImageBitMap(), 180, 100));
        }
    }

    @Override
    public void onClick(View v) {
        Intent intent = ImagePagerActivity.newIntent(mActivity, mPropertyImage.getId());
        mActivity.startActivity(intent);
    }


}