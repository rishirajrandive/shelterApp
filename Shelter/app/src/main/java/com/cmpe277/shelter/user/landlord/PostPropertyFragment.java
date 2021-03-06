package com.cmpe277.shelter.user.landlord;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.cmpe277.shelter.DownloadImageCallback;
import com.cmpe277.shelter.FragmentCallback;
import com.cmpe277.shelter.HomeActivity;
import com.cmpe277.shelter.R;
import com.cmpe277.shelter.helper.PropertyImage;
import com.cmpe277.shelter.helper.PropertyImageAdapter;
import com.cmpe277.shelter.property.Property;
import com.cmpe277.shelter.property.PropertyLab;
import com.cmpe277.shelter.user.UserSessionManager;
import com.cmpe277.shelter.util.GetBitmapTask;
import com.cmpe277.shelter.util.ImagePicker;
import com.cmpe277.shelter.util.PostPropertyTask;
import com.cmpe277.shelter.util.SendEmail;
import com.cmpe277.shelter.util.ShelterConstants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Fragment for handling operations for posting new property.
 * TODO Sending data to DB
 *
 */
public class PostPropertyFragment extends Fragment {

    private static final String TAG = "PostPropertyFragment";
    private static final int REQUEST_ADD_IMAGE = 1;
    private RelativeLayout mPhotoLayout;
    private RecyclerView mImageRecyclerView;
    private List<PropertyImage> mImageList = new ArrayList<>();
    private Uri mCapturedImageURI;

    private EditText mPropertyName;
    private EditText mStreet;
    private EditText mCity;
    private EditText mState;
    private EditText mZipcode;

    private EditText mTotalRooms;
    private EditText mMonthlyRent;
    private EditText mBath;
    private EditText mFloorArea;

    private RadioButton mHouseType;
    private RadioButton mTownhouseType;
    private RadioButton mApartmentType;

    private EditText mContactEmail;
    private EditText mContactPhone;

    private EditText mPropertyDescription;
    private Button mFinishButton;

    private Property mPropertyToPost;
    private Button mSelectPictureButton;

    private ProgressDialog mProgressDialog;
    private boolean isNewProperty;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        setHasOptionsMenu(true);
        Log.d(TAG, "On create called");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "On create view");

        UUID propertyId = (UUID) getActivity().getIntent().getSerializableExtra(PostPropertyActivity.EXTRA_PROPERTY_ID);
        mPropertyToPost = PropertyLab.get(getContext()).getProperty(propertyId);

        View view = inflater.inflate(R.layout.post_property_fragment, container, false);

        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Post new property");


        // TODO Populate complete data in model and send it to DB

        mPropertyName = (EditText) view.findViewById(R.id.post_property_name);

        mStreet = (EditText) view.findViewById(R.id.street_name);
        mCity = (EditText) view.findViewById(R.id.city_name);
        mState = (EditText) view.findViewById(R.id.state_name);
        mZipcode = (EditText) view.findViewById(R.id.zip);

        mTotalRooms = (EditText) view.findViewById(R.id.number_of_rooms);
        mMonthlyRent = (EditText) view.findViewById(R.id.monthly_rent);
        mBath = (EditText) view.findViewById(R.id.bath);
        mFloorArea = (EditText)  view.findViewById(R.id.floor_area);

        mHouseType = (RadioButton) view.findViewById(R.id.house_type);
        mTownhouseType = (RadioButton) view.findViewById(R.id.townhouse_type);
        mApartmentType = (RadioButton) view.findViewById(R.id.apartment_type);

        mContactEmail = (EditText) view.findViewById(R.id.email);
        mContactPhone = (EditText) view.findViewById(R.id.phone_number);

        mPropertyDescription = (EditText) view.findViewById(R.id.description);

        RadioGroup propertyTypes = (RadioGroup) view.findViewById(R.id.property_type);
        propertyTypes.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                onPropertyTypeClicked(checkedId);
            }
        });

        mImageRecyclerView = (RecyclerView) view.findViewById(R.id.property_images_recycler_view);
        StaggeredGridLayoutManager gridLayoutManager =
                new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
        mImageRecyclerView.setLayoutManager(gridLayoutManager);

        mSelectPictureButton = (Button) view.findViewById(R.id.select_picture);
        mSelectPictureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent selectedIntent = ImagePicker.getGalleryImagePickIntent(getActivity());
                //startActivityForResult(selectedIntent, REQUEST_ADD_IMAGE);
                startActivityForResult(Intent.createChooser(selectedIntent, "Select picture"), REQUEST_ADD_IMAGE);

            }
        });

        mPhotoLayout = (RelativeLayout) view.findViewById(R.id.pictures_layout);

        mFinishButton = (Button) view.findViewById(R.id.finish_button);
        mFinishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO Add code to send data to DB and move to next activity My Postings
                try{
                    if(isFormValid()){
                        populatePropertyToPost();
                        postPropertyData();
                    }
                } catch (JSONException ex){
                    Log.e(TAG, ex.getStackTrace().toString());
                    Toast.makeText(getActivity(), "Property was NOT posted", Toast.LENGTH_LONG).show();

                } catch (Exception ex){
                    Log.e(TAG, ex.getStackTrace().toString());
                }
            }
        });

        if(mPropertyToPost == null){
            isNewProperty = true;
            mPropertyToPost = new Property();
        }else{
            isNewProperty = false;
            mFinishButton.setText("Update");
            populateFields();
        }


        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if ( mProgressDialog != null && mProgressDialog.isShowing() ){
            mProgressDialog.dismiss();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "On activity result called");
        if (resultCode == getActivity().RESULT_OK){
            switch(requestCode) {
                case REQUEST_ADD_IMAGE:
                    if (null != data) {
                        Log.d(TAG, "Image selected ");
                        mImageList.add(ImagePicker.get(getContext()).getPropertyImage(getContext(), data));
                        setupAdapter();
                        if(mImageList.size() == 4){
                            mSelectPictureButton.setClickable(false);
                        }
                    }
                    break;
                default:
                    super.onActivityResult(requestCode, resultCode, data);
                    break;
            }
        }
    }

    /**
     * Set list in the image adapter
     */
    private void setupAdapter() {
        if (isAdded()) {
            Log.d(TAG, "Setting adapter for view");
            PropertyLab.get(getContext()).updateImageList(mImageList);
            mImageRecyclerView.setAdapter(new PropertyImageAdapter(mImageList, getActivity(), getActivity().getSupportFragmentManager()));
        }
    }

    /**
     * Method to compose email and call {@link SendEmail} to send the email to user in Asynctask
     */
    public void composeEmail() {
        String email = UserSessionManager.get(getContext()).getUserEmail();
        String userName = UserSessionManager.get(getContext()).getUserName();
        String subject = "";
        String message = "";
        if(isNewProperty){
            subject = "Thank you!";
            message = "Hello "+ userName + "<br>" +
                    "You have posted a new property, we will keep you updated.";
        }else {
            subject = "Property updated";
            message = "Hello "+ userName + "<br>" +
                    "You property "+ mPropertyToPost.getName() + " is updated.";
        }

        //Creating SendMail object and executing to send email
        if(email != null && userName != null){
            SendEmail sm = new SendEmail(getContext(), email, subject, message);
            sm.execute();
        }else{
            Log.d(TAG, "Email was not sent to user - "+ userName + " email - "+ email);
        }

    }

    public void onPropertyTypeClicked(int id){
        // Check which radio button was clicked
        switch(id) {
            case R.id.house_type:
                Log.d(TAG, "Its house type");
                mPropertyToPost.setType("House");
                break;
            case R.id.townhouse_type:
                Log.d(TAG, "Its townhouse type");
                mPropertyToPost.setType("Townhouse");
                break;
            case R.id.apartment_type:
                Log.d(TAG, "Its apartment type");
                mPropertyToPost.setType("Apartment");
                break;
        }

    }

    private void populatePropertyToPost(){
        mPropertyToPost.setName(mPropertyName.getText().toString());
        mPropertyToPost.setStreet(mStreet.getText().toString());
        mPropertyToPost.setCity(mCity.getText().toString());
        mPropertyToPost.setState(mState.getText().toString());
        mPropertyToPost.setZipcode(mZipcode.getText().toString());

        mPropertyToPost.setRooms(mTotalRooms.getText().toString());
        mPropertyToPost.setRent(mMonthlyRent.getText().toString());
        mPropertyToPost.setBath(mBath.getText().toString());
        mPropertyToPost.setFloorArea(mFloorArea.getText().toString());

        mPropertyToPost.setEmail(mContactEmail.getText().toString());
        mPropertyToPost.setPhoneNumber(mContactPhone.getText().toString());

        mPropertyToPost.setDescription(mPropertyDescription.getText().toString());

        mPropertyToPost.setPropertyImages(mImageList);
    }

    private void populateFields(){
        mPropertyName.setText(mPropertyToPost.getName());

        mStreet.setText(mPropertyToPost.getStreet());
        mCity.setText(mPropertyToPost.getCity());
        mState.setText(mPropertyToPost.getState());
        mZipcode.setText(mPropertyToPost.getZipcode());

        mTotalRooms.setText(mPropertyToPost.getRooms());
        mMonthlyRent.setText(mPropertyToPost.getRent());
        mBath.setText(mPropertyToPost.getBath());
        mFloorArea.setText(mPropertyToPost.getFloorArea());

        if(mPropertyToPost.getType().equalsIgnoreCase("House")){
            mHouseType.setChecked(true);
        } else if(mPropertyToPost.getType().equalsIgnoreCase("Townhouse")){
            mTownhouseType.setChecked(true);
        } else if(mPropertyToPost.getType().equalsIgnoreCase("Apartment")){
            mApartmentType.setChecked(true);
        }

        mContactEmail.setText(mPropertyToPost.getEmail());
        mContactPhone.setText(mPropertyToPost.getPhoneNumber());

        mPropertyDescription.setText(mPropertyToPost.getDescription());

        //mImageList = mPropertyToPost.getPropertyImages();
        for(PropertyImage image : mPropertyToPost.getPropertyImages()){
            final PropertyImage propertyImage = image;
            if(propertyImage.getImageResourceId() == 0){
                new GetBitmapTask(new DownloadImageCallback() {
                    @Override
                    public void onTaskDone(Bitmap bitmap) {
                        propertyImage.setImageBitMap(bitmap);
                        mImageList.add(propertyImage);
                        setupAdapter();
                    }
                }).execute(propertyImage.getImagePath());
            }
        }
        setupAdapter();

        mSelectPictureButton.setText("Update");
    }

    private void showMyPostings(){
        Log.d(TAG, "Finishing the activity");
        Intent intent = new Intent();
        intent.putExtra(HomeActivity.EXTRA_FRAGMENT_ID, HomeActivity.MY_POSTING_FRAGMENT_ID);
        intent.putExtra(PostedPropertyFragment.EXTRA_IS_UPDATED, true);
        getActivity().setResult(Activity.RESULT_OK, intent);
        getActivity().finish();
    }

    private boolean isFormValid(){
        boolean isValid = true;
        if(TextUtils.isEmpty(mPropertyName.getText())){
            mPropertyName.setError(getString(R.string.blank_error_msg));
            isValid = false;
        }
        if(TextUtils.isEmpty(mStreet.getText())){
            mStreet.setError(getString(R.string.blank_error_msg));
            isValid = false;
        }
        if(TextUtils.isEmpty(mCity.getText())){
            mCity.setError(getString(R.string.blank_error_msg));
            isValid = false;
        }
        if(TextUtils.isEmpty(mState.getText())){
            mState.setError(getString(R.string.blank_error_msg));
            isValid = false;
        }
        if(TextUtils.isEmpty(mZipcode.getText())){
            mZipcode.setError(getString(R.string.blank_error_msg));
            isValid = false;
        }
        if(TextUtils.isEmpty(mTotalRooms.getText())){
            mTotalRooms.setError(getString(R.string.blank_error_msg));
            isValid = false;
        }
        if(TextUtils.isEmpty(mMonthlyRent.getText())){
            mMonthlyRent.setError(getString(R.string.blank_error_msg));
            isValid = false;
        }
        if(TextUtils.isEmpty(mBath.getText())){
            mBath.setError(getString(R.string.blank_error_msg));
            isValid = false;
        }
        if(TextUtils.isEmpty(mFloorArea.getText())){
            mFloorArea.setError(getString(R.string.blank_error_msg));
            isValid = false;
        }
        if(!mHouseType.isChecked() && !mTownhouseType.isChecked() && !mApartmentType.isChecked()){
            mApartmentType.setError(getString(R.string.type_not_selected_error_msg));
            isValid = false;
        }
        if(TextUtils.isEmpty(mContactPhone.getText())){
            mContactPhone.setError(getString(R.string.blank_error_msg));
            isValid = false;
        }
        if(TextUtils.isEmpty(mContactEmail.getText())){
            mContactEmail.setError(getString(R.string.blank_error_msg));
            isValid = false;
        }

        if(!isValid){
            Toast.makeText(getContext(), "Something is wrong, please fill details properly", Toast.LENGTH_LONG).show();
        }
        return isValid;
    }

    private void postPropertyData() throws JSONException{

        JSONObject jsonObject = new JSONObject();

        jsonObject.put(ShelterConstants.PROPERTY_ID, mPropertyToPost.getId());
        Log.d(TAG, "Property ID for "+ mPropertyToPost.getId());
        String ownerId = PreferenceManager.getDefaultSharedPreferences(getActivity()).getString(ShelterConstants.SHARED_PREFERENCE_OWNER_ID, ShelterConstants.DEFAULT_INT_STRING);
        jsonObject.put(ShelterConstants.OWNER_ID, ownerId);

        jsonObject.put(ShelterConstants.PROPERTY_NAME, mPropertyToPost.getName());
        JSONArray address = new JSONArray();
        JSONObject addressObject = new JSONObject();
        addressObject.put(ShelterConstants.STREET, mPropertyToPost.getStreet());
        addressObject.put(ShelterConstants.CITY, mPropertyToPost.getCity());
        addressObject.put(ShelterConstants.STATE, mPropertyToPost.getState());
        addressObject.put(ShelterConstants.ZIPCODE, mPropertyToPost.getZipcode());
        address.put(addressObject);
        jsonObject.put(ShelterConstants.ADDRESS, address);

        jsonObject.put(ShelterConstants.PROPERTY_TYPE, mPropertyToPost.getType());

        JSONArray details = new JSONArray();
        JSONObject detailsObject = new JSONObject();
        detailsObject.put(ShelterConstants.ROOMS, mPropertyToPost.getRooms());
        detailsObject.put(ShelterConstants.BATH, mPropertyToPost.getBath());
        detailsObject.put(ShelterConstants.FLOOR_AREA, mPropertyToPost.getFloorArea());
        details.put(detailsObject);
        jsonObject.put(ShelterConstants.DETAILS, details);

        JSONArray rentDetails = new JSONArray();
        JSONObject rentObject = new JSONObject();
        rentObject.put(ShelterConstants.RENT, mPropertyToPost.getRent());
        rentDetails.put(rentObject);
        jsonObject.put(ShelterConstants.RENT_DETAILS, rentDetails);

        JSONArray ownerInfo = new JSONArray();
        JSONObject ownerObject = new JSONObject();
        ownerObject.put(ShelterConstants.PHONE_NUMBER, mPropertyToPost.getPhoneNumber());
        ownerObject.put(ShelterConstants.DISPLAY_PHONE, mPropertyToPost.getPhoneNumber());
        ownerObject.put(ShelterConstants.EMAIL, mPropertyToPost.getEmail());
        ownerInfo.put(ownerObject);
        jsonObject.put(ShelterConstants.OWNER_CONTACT_INFO, ownerInfo);

        jsonObject.put(ShelterConstants.IS_FAVORITE, mPropertyToPost.isFavorite());
        jsonObject.put(ShelterConstants.IS_RENTED_OR_CANCEL, mPropertyToPost.isRentedOrCancel());
        jsonObject.put(ShelterConstants.DESCRIPTION, mPropertyToPost.getDescription());

        JSONArray moreInfo = new JSONArray();
        JSONObject moreObject = new JSONObject();
        moreObject.put(ShelterConstants.LEASE_TYPE, "Percentage");
        moreObject.put(ShelterConstants.DEPOSIT, "2000");
        moreInfo.put(moreObject);
        jsonObject.put(ShelterConstants.MORE, moreInfo);

        jsonObject.put(ShelterConstants.PROPERTY_IMAGES, new ArrayList<String>());
        Log.d(TAG, jsonObject.toString());

        showProgressDialog();
        new PostPropertyTask(getContext(), "postings/", true, jsonObject, new FragmentCallback(){
            @Override
            public void onTaskDone() {
                Log.d(TAG, "Property posting done.....");
                hideProgressDialog();
                composeEmail();
                showMyPostings();
            }
        }).execute();
    }

    private void showProgressDialog(){
        if(mProgressDialog == null){
            mProgressDialog = new ProgressDialog(getContext());
            mProgressDialog.setMessage("Saving property");
            mProgressDialog.setIndeterminate(true);
        }
        mProgressDialog.show();
    }

    private void hideProgressDialog(){
        if(mProgressDialog != null){
            mProgressDialog.hide();
        }
    }
}

