package com.android.shelter.helper;

import android.app.Activity;
import android.content.Intent;
import android.media.ThumbnailUtils;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.android.shelter.user.landlord.ImagePagerActivity;
import com.android.shelter.R;


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
        mPropertyImageView.setImageBitmap(ThumbnailUtils.extractThumbnail(mPropertyImage.getImageBitMap(), 180, 100));
    }

    @Override
    public void onClick(View v) {
        Intent intent = ImagePagerActivity.newIntent(mActivity, mPropertyImage.getId());
        mActivity.startActivity(intent);
    }


}