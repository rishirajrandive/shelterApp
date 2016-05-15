package com.android.shelter.user;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.shelter.HomeActivity;
import com.android.shelter.R;
import com.android.shelter.util.ShelterConstants;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.api.Status;

/**
 * Created by rishi on 5/12/16.
 */
public class UserProfileActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener{

    private static final String TAG = "UserProfileActivity";
    private GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("User Profile");

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        TextView username = (TextView) findViewById(R.id.profile_user_name);
        username.setText(preferences.getString(ShelterConstants.SHARED_PREFERENCE_USER_NAME, ShelterConstants.DEFAULT_STRING));
        TextView email = (TextView) findViewById(R.id.profile_email);
        email.setText(preferences.getString(ShelterConstants.SHARED_PREFERENCE_EMAIL, ShelterConstants.DEFAULT_STRING));

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestScopes(new Scope(Scopes.PLUS_LOGIN))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, (GoogleApiClient.OnConnectionFailedListener) this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        Button logoutButton = (Button) findViewById(R.id.profile_logout);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());


            }
        });
    }

    private void finishActivity(){
        Intent intent = new Intent();
        intent.putExtra(HomeActivity.EXTRA_IS_LOGGED_OUT, true);
        setResult(Activity.RESULT_OK, intent);
        finish();
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                Log.d(TAG, "Home clicked");
                if (NavUtils.getParentActivityIntent(this) != null) {
                    NavUtils.navigateUpFromSameTask(this);
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d(TAG, "Connection failed");
    }
}