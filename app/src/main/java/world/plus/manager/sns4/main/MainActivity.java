package world.plus.manager.sns4.main;

import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.crashlytics.android.Crashlytics;
import com.facebook.Session;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;

import world.plus.manager.sns4.R;
import world.plus.manager.sns4.util.PopupDialog;
import world.plus.manager.sns4.util.ProgressDialogManager;
import world.plus.manager.sns4.write.WriteFragment;

public class MainActivity extends SherlockFragmentActivity implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = "MainActivity";

    private static final int REQUEST_CODE_RESOLVE_ERR = 9000;
    private GoogleApiClient mPlusClient;
    private boolean mResolvingError;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Crashlytics.start(this);
        setContentView(R.layout.activity_main2);


        initGooglePlus();

        getSupportFragmentManager().beginTransaction()
                .add(R.id.content_frame, new WriteFragment(), SMConstants.TAG_WRITE_FRAGMENT).commit();

    }


    /**
     * Log in Google Plus
     */
    public void logInGooglePlus() {
        showLoginDialog();
        mPlusClient.connect();
    }

    private void showLoginDialog() {
        ProgressDialogManager.showProgessDialog(this,"Signing in Google Plus");
    }


    /**
     * Log out Google Plus
     */
    public void logOutGooglePlus() {
        if (mPlusClient.isConnected()) {
            mPlusClient.disconnect();
        }
    }

    /**
     * Called when connection to Google Plus failed
     */
    @Override
    public void onConnectionFailed(ConnectionResult result) {
        Log.i(TAG, "GoogleApiClient connection failed: " + result.toString());
        if (mResolvingError) {
            // Already attempting to resolve an error.
            ProgressDialogManager.removeProgressDialog();
            return;
        }else {
            if (!result.hasResolution()) {
                mResolvingError = true;
                // show the localized error dialog.
                GooglePlayServicesUtil.getErrorDialog(result.getErrorCode(), this, 0).show();
                return;
            }
            try {
                mResolvingError = true;
                result.startResolutionForResult(this, REQUEST_CODE_RESOLVE_ERR);
            } catch (IntentSender.SendIntentException e) {
                Log.e(TAG, "Exception while starting resolution activity", e);
            }
        }

    }

    /**
     * Called when connected to Google Plus
     */
    @Override
    public void onConnected(Bundle connectionHint) {

        ProgressDialogManager.removeProgressDialog();

        android.support.v4.app.Fragment fragment = getSupportFragmentManager().findFragmentByTag(SMConstants.TAG_WRITE_FRAGMENT);
        ((WriteFragment)fragment).onLoginSuccess(SMConstants.GOOGLE_PLUS);

    }

    @Override
    public void onConnectionSuspended(int i) {

    }


    /**
     * Show exit dialog when back button was pressed
     */
    @Override
    public void onBackPressed() {

        showExitDialog();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_RESOLVE_ERR) {
            mResolvingError = false;
            if (resultCode == RESULT_OK) {
                // Make sure the app is not already connected or attempting to connect
                if (!mPlusClient.isConnecting() &&
                        !mPlusClient.isConnected()) {
                    mPlusClient.connect();
                }
            }
        }
        // Facebook Oauth
        // When user is not logged in Facebook, null pointer error happens
        // So check if active session is null
        if (Session.getActiveSession() != null)
            Session.getActiveSession().onActivityResult(this, requestCode,
                    resultCode, data);

//        if (resultCode != RESULT_OK)
//            return;

        switch (requestCode) {

            case SMConstants.GOOGLE_PLUS_POST:
                WriteFragment.getInstance().runHttpRequest();
                return;

//            // Google Plus OAuth
//            case REQUEST_CODE_RESOLVE_ERR:
//
//                mPlusClient.connect();
//                break;

        }

    }

    /**
     * Initialize Google Plus
     */
    private void initGooglePlus() {
        mPlusClient = new GoogleApiClient.Builder(this)
                .addApi(Plus.API)
                .addScope(Plus.SCOPE_PLUS_LOGIN)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
    }

    /**
     * Make exit dialog
     */
    private void showExitDialog() {
        final PopupDialog popup = new PopupDialog(this, R.layout.popup_exit);
        popup.setTitle(R.string.exit_popup_title);
        popup.setFirstMenuText(R.string.no);
        popup.setSecondMenuText(R.string.yes);
        popup.setFirstMenuListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                popup.dismiss();

            }
        });

        popup.setSecondMenuListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                popup.dismiss();
                finish();

            }
        });

        popup.show();
    }


}
