package com.cmpe277.shelter.user.landlord;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import com.cmpe277.shelter.R;
import com.cmpe277.shelter.helper.PropertyImage;
import com.cmpe277.shelter.property.Property;
import com.cmpe277.shelter.property.PropertyLab;
import com.cmpe277.shelter.util.DownloadImageTask;
import com.cmpe277.shelter.util.RentedOrCancelTask;
import com.cmpe277.shelter.util.ShelterConstants;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.UUID;

/**
 * Shows detail view of posted property which are posted by landlord
 */
public class PostedPropertyFragment extends Fragment {
    private static final String TAG = "PostedPropertyFragment";
    private static final String ARG_PROPERTY_ID = "property_id";
    public static final int REQUEST_EDIT = 1;
    public static final String EXTRA_IS_UPDATED = "extra_is_updated";

    private Property mProperty;
    private ImageView mImage;
    private TextView mPropertyName;
    private TextView mAddress;
    private TextView mPropertyRooms;
    private TextView mPropertyType;
    private TextView mRent;
    private TextView mBath;
    private TextView mFloorArea;
    private TextView mContactPhone;
    private TextView mContactEmail;
    private TextView mDesc;
    private TextView mPageViews;
    private CheckBox mRentedOrCancel;

    private PropertyImage mPropertyImage;

    public static PostedPropertyFragment newInstance(UUID propertyId) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_PROPERTY_ID, propertyId);

        PostedPropertyFragment fragment = new PostedPropertyFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        setHasOptionsMenu(true);

        UUID id = (UUID) getArguments().getSerializable(ARG_PROPERTY_ID);
        mProperty = PropertyLab.get(getContext()).getProperty(id);
        if(mProperty.getPropertyImages().size() > 0){
            mPropertyImage = mProperty.getPropertyImages().get(0);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_posted_property_detail, container, false);



        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);

        mImage = (ImageView) v.findViewById(R.id.posted_property_image);
        if(mPropertyImage.getImageResourceId() == 0){
            new DownloadImageTask(mImage).execute(mPropertyImage.getImagePath());
        } else {
            mImage.setBackgroundResource(mPropertyImage.getImageResourceId());
        }

        mPropertyName = (TextView) v.findViewById(R.id.posted_property_name);
        mPropertyName.setText(mProperty.getName());

        mAddress = (TextView) v.findViewById(R.id.posted_property_address);
        mAddress.setText(mProperty.getAddress());

        mPropertyRooms = (TextView) v.findViewById(R.id.posted_property_rooms);
        mPropertyRooms.append(mProperty.getDisplayRoom());
        mPropertyType = (TextView) v.findViewById(R.id.posted_property_type);
        mPropertyType.append(mProperty.getType());
        mBath = (TextView) v.findViewById(R.id.posted_property_bath);
        mBath.append(mProperty.getDisplayBath());
        mFloorArea = (TextView) v.findViewById(R.id.posted_property_floor_area);
        mFloorArea.append(mProperty.getDisplayFloorArea());
        mPageViews = (TextView) v.findViewById(R.id.page_reviews);
        mPageViews.setText(mProperty.getDisplayPageViews());

        mContactPhone = (TextView) v.findViewById(R.id.posted_property_contact_phone);
        mContactPhone.setText(mProperty.getPhoneNumber());
        mContactEmail = (TextView) v.findViewById(R.id.posted_property_contact_email);
        mContactEmail.setText(mProperty.getEmail());

        mRent = (TextView) v.findViewById(R.id.posted_property_rent);
        mRent.setText(mProperty.getDisplayRent());

        mDesc = (TextView) v.findViewById(R.id.posted_property_desc);
        mDesc.setText(mProperty.getDescription());

        mRentedOrCancel = (CheckBox) v.findViewById(R.id.rented_or_cancel);
        mRentedOrCancel.setChecked(mProperty.isRentedOrCancel());

        mRentedOrCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());

                    JSONObject putData = new JSONObject();
                    putData.put("isRented", mRentedOrCancel.isChecked());
                    putData.put(ShelterConstants.OWNER_ID, preferences.getString(ShelterConstants.SHARED_PREFERENCE_OWNER_ID, ShelterConstants.DEFAULT_INT_STRING));
                    putData.put(ShelterConstants.PROPERTY_ID, mProperty.getId());

                    new RentedOrCancelTask(getContext(), "/isrented/", putData).execute();
                }catch (JSONException ex){
                    Log.d(TAG, "Exception in fetching data");
                }
            }
        });

        mImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PropertyLab.get(getContext()).updateImageList(mProperty.getPropertyImages());
                Intent intent = ImagePagerActivity.newIntent(getActivity(), mPropertyImage.getId());
                getActivity().startActivity(intent);
            }
        });
        return v;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_posted_property, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.edit_property:
                Log.d(TAG, "Edit clicked restart the PostPropertyActivity");
                Intent postPropertyActivity = PostPropertyActivity.newIntent(getContext(), mProperty.getId());
                startActivityForResult(postPropertyActivity, REQUEST_EDIT);
                //getActivity().finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == getActivity().RESULT_OK){
            if(requestCode == REQUEST_EDIT && data.hasExtra(EXTRA_IS_UPDATED) && data.getBooleanExtra(EXTRA_IS_UPDATED, false)){
                getActivity().finish();
            }
        }
    }
}


