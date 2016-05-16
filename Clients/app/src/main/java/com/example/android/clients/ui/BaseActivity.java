package com.example.android.clients.ui;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import com.example.android.clients.util.GooglePlusAuthHelper;
import com.example.android.clients.util.PrefUtils;

import static com.example.android.clients.util.LogUtils.*;

public abstract class BaseActivity extends ActionBarActivity implements
        GooglePlusAuthHelper.Callbacks {

    private static final String TAG = makeLogTag(BaseActivity.class);

    private GooglePlusAuthHelper mGooglePlusAuthHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Restore the saved state if exists
        super.onCreate(savedInstanceState);

        // Check if the EULA has been accepted; if not, show it.
        if (!PrefUtils.isTosAccepted(this)) {
            Intent intent = new Intent(this, WelcomeActivity.class);
            startActivity(intent);
            finish();
        }
    }

    private void startLoginProcess() {
        LOGD(TAG, "Starting Google+ authentication process ... ");
        if (mGooglePlusAuthHelper == null) {
            mGooglePlusAuthHelper = new GooglePlusAuthHelper(this, this);
        }

        mGooglePlusAuthHelper.start();
        LOGD(TAG, "Starting Google+ authentication process ... done.");
    }

    @Override
    public void onStart() {
        LOGD(TAG, "Starting BaseActivity ...");
        super.onStart();
        if(PrefUtils.isGooglePlusAuthEnabled(this)) {
            startLoginProcess();
        }
        LOGD(TAG, "Starting BaseActivity ... done.");
    }

    @Override
    public void onStop() {
        LOGD(TAG, "Stoping BaseActivity ...");
        super.onStop();
        if (mGooglePlusAuthHelper != null) {
            mGooglePlusAuthHelper.stop();
        }
        LOGD(TAG, "Stoping BaseActivity ... done.");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        LOGD(TAG, "onActivityResult invoked.");
        if (mGooglePlusAuthHelper == null ||
                !mGooglePlusAuthHelper.onActivityResult(requestCode, resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onPlusInfoLoaded() {
        LOGD(TAG, "Google+ user information is loaded.");
        //populateNavDrawer();
    }

    @Override
    /**
     * Called when authentication succeeds. This may either happen because the user just
     * authenticated for the first time (and went through the sign in flow), or because it's
     * a returning user.
     * @param accountName name of the account that just authenticated successfully.
     * @param newlyAuthenticated If true, this user just authenticated for the first time.
     * If false, it's a returning user.
     */
    public void onAuthSuccess(boolean newlyAuthenticated) {
        LOGD(TAG, "Google+ authentication successful.");
    }

    @Override
    public void onAuthFailure() {
        LOGD(TAG, "Google+ authentication failed.  Exiting application ...");
        finish();
    }

    public void signOut() {
        if(mGooglePlusAuthHelper != null)
            mGooglePlusAuthHelper.signOut();
    }
}
