package com.example.android.clients.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.plus.People;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;
import com.google.android.gms.plus.model.people.PersonBuffer;
import java.lang.ref.WeakReference;

import static com.example.android.clients.util.LogUtils.*;

/**
 * This helper handles the UI flow for signing in and authenticating an account. It handles
 * connecting to the Google+ API to fetch profile data (name, cover photo, etc). The life
 * of this object is tied to an Activity. Do not attempt to share it across Activities, as
 * unhappiness will result.
 */
public class GooglePlusAuthHelper implements GoogleApiClient.ConnectionCallbacks,
            GoogleApiClient.OnConnectionFailedListener,
            ResultCallback<People.LoadPeopleResult> {

    private static final String TAG = makeLogTag(GooglePlusAuthHelper.class);

    private static final int REQUEST_AUTHENTICATE = 100;
    private static final int REQUEST_RECOVER_FROM_AUTH_ERROR = 101;

    //
    Context mAppContext;

    // The Activity this object is bound to (we use a weak ref to avoid context leaks)
    WeakReference<Activity> mActivityRef;

    public interface Callbacks {
        void onPlusInfoLoaded();

        void onAuthSuccess(boolean newlyAuthenticated);

        void onAuthFailure();
    }

    // Callbacks interface we invoke to notify the user of this class of useful events
    WeakReference<Callbacks> mCallbacksRef;

    /* Client used to interact with Google APIs. */
    private GoogleApiClient mGoogleApiClient = null;

    // Are we in the started state? Started state is between onStart and onStop.
    boolean mStarted = false;

    /* Track whether the sign-in requested */
    private boolean mSignInRequestPending = false;

    /* A flag indicating that a PendingIntent is in progress and prevents
     * us from starting further intents.
     */
    private boolean mResolvingIntent = false;

    //
    private boolean mIsSignedIn = false;

    /* Store the connection result from onConnectionFailed callbacks so that we can
     * resolve them when the user clicks sign-in.
     */
    private ConnectionResult mConnectionResult;

    /* Request code used to invoke sign in user interactions. */
    public static final int RC_SIGN_IN = 0;

    public GooglePlusAuthHelper(Activity callerActivity, Callbacks callbacks) {
        LOGD(TAG, "Creating Google+ Authentication Helper ... ");
        mActivityRef = new WeakReference<Activity>(callerActivity);
        mCallbacksRef = new WeakReference<Callbacks>(callbacks);
        mAppContext = callerActivity.getApplicationContext();
        LOGD(TAG, "Creating Google+ Authentication Helper ... done.");
    }

    private Activity getActivity(String methodName) {
        Activity activity = mActivityRef.get();
        if (activity == null) {
            LOGD(TAG, "Helper lost Activity reference, ignoring (" + methodName + ")");
        }

        return activity;
    }

    /**
     * Starts the helper. Call this from your Activity's onStart().
     */
    public void start() {
        Activity activity = getActivity("start()");
        if (activity == null) {
            return;
        }

        if (mStarted) {
            LOGW(TAG, "Helper already started. Ignoring redundant call.");
            return;
        }

        mStarted = true;
        if (mResolvingIntent) {
            // if resolving, don't reconnect the plus client
            LOGD(TAG, "Helper ignoring signal to start because we're resolving a failure.");
            return;
        }

        mSignInRequestPending = true;
        LOGD(TAG, "Google+ authentication helper starting ... ");
        if (mGoogleApiClient == null) {
            LOGD(TAG, "Creating Google API client ... ");
            mGoogleApiClient = new GoogleApiClient.Builder(mAppContext)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(Plus.API)
                    .addScope(Plus.SCOPE_PLUS_LOGIN)
                    .build();
            LOGD(TAG, "Creating Google API client ... done.");
        }
        LOGD(TAG, "Connecting Google API client ... ");
        mGoogleApiClient.connect();
        LOGD(TAG, "Connecting Google API client ... done.");
        LOGD(TAG, "Google+ authentication helper starting ... done.");
    }

    /**
     * Stop the helper. Call this from your Activity's onStop().
     */
    public void stop() {
        if (!mStarted) {
            LOGW(TAG, "Google+ authentication helper already stopped. Ignoring redundant call.");
            return;
        }

        LOGD(TAG, "Google+ authentication helper stopping.");
        mStarted = false;
        if (mGoogleApiClient.isConnected()) {
            LOGD(TAG, "Google API client disconnecting ...");
            mGoogleApiClient.disconnect();
        }

        mResolvingIntent = false;
    }

    @Override
    // Implements the GoogleApiClient.ConnectionCallbacks interface method.
    public void onConnected(Bundle bundle) {
        mIsSignedIn = true;
        mSignInRequestPending = false;

        Activity activity = getActivity("onConnected()");
        if (activity == null) {
            return;
        }

        PendingResult<People.LoadPeopleResult> result = Plus.PeopleApi.load(mGoogleApiClient, "me");
        result.setResultCallback(this);

        LOGD(TAG, "Google+ authentication helper connected.");
    }

    @Override
    // Implements the GoogleApiClient.ConnectionCallbacks interface method.
    public void onConnectionSuspended(int i) {
        LOGD(TAG, "Google API client connection suspended.");
        mIsSignedIn = false;
        mGoogleApiClient.connect();
    }

    @Override
    // Implements the GoogleApiClient.OnConnectionFailedListener method.
    // Called when there was an error connecting the client to the service.
    public void onConnectionFailed(ConnectionResult result) {
        LOGD(TAG, "Google API client connection failed.");

        Activity activity = getActivity("onConnectionFailed()");
        if (activity == null) {
            return;
        }

        mIsSignedIn = false;
        if (!mResolvingIntent) {
            // Store the ConnectionResult so that we can use it later when the user clicks
            // 'sign-in'.
            mConnectionResult = result;

            if (mSignInRequestPending) {
                // The user has already clicked 'sign-in' so we attempt to resolve all
                // errors until the user is signed in, or they cancel.
                resolveSignInError(activity);
            }
        }
    }

    // Called asynchronously -- result of loadPeople() call
    @Override
    public void onResult(People.LoadPeopleResult loadPeopleResult) {
        LOGD(TAG, "onPeopleLoaded, status=" + loadPeopleResult.getStatus().toString());
        if (loadPeopleResult.getStatus().isSuccess()) {
            PersonBuffer personBuffer = loadPeopleResult.getPersonBuffer();
            if (personBuffer != null && personBuffer.getCount() > 0) {
                LOGD(TAG, "Got plus profile for account ");
                Person currentUser = personBuffer.get(0);
                personBuffer.close();

                /*
                // Record profile ID, image URL and name
                LOGD(TAG, "Saving plus profile ID: " + currentUser.getId());
                AccountUtils.setPlusProfileId(mAppContext, mAccountName, currentUser.getId());
                String imageUrl = currentUser.getImage().getUrl();
                if (imageUrl != null) {
                    imageUrl = Uri.parse(imageUrl)
                            .buildUpon().appendQueryParameter("sz", "256").build().toString();
                }
                LOGD(TAG, "Saving plus image URL: " + imageUrl);
                AccountUtils.setPlusImageUrl(mAppContext, mAccountName, imageUrl);
                LOGD(TAG, "Saving plus display name: " + currentUser.getDisplayName());
                AccountUtils.setPlusName(mAppContext, mAccountName, currentUser.getDisplayName());
                Person.Cover cover = currentUser.getCover();
                if (cover != null) {
                    Person.Cover.CoverPhoto coverPhoto = cover.getCoverPhoto();
                    if (coverPhoto != null) {
                        LOGD(TAG, "Saving plus cover URL: " + coverPhoto.getUrl());
                        AccountUtils.setPlusCoverUrl(mAppContext, mAccountName, coverPhoto.getUrl());
                    }
                } else {
                    LOGD(TAG, "Profile has no cover.");
                }
                */

                Callbacks callbacks;
                if (null != (callbacks = mCallbacksRef.get())) {
                    callbacks.onPlusInfoLoaded();
                }
            } else {
                LOGE(TAG, "Plus response was empty! Failed to load profile.");
            }
        } else {
            LOGE(TAG, "Failed to load plus proflie, error " + loadPeopleResult.getStatus().getStatusCode());
        }
    }

    /* A helper method to resolve the current ConnectionResult error. */
    public void resolveSignInError(Activity activity) {
        if (mConnectionResult.hasResolution()) {
            LOGD(TAG, "Attempting to resolve sign in error ... ");
            try {
                mResolvingIntent = true;
                activity.startIntentSenderForResult(mConnectionResult.getResolution().
                        getIntentSender(), RC_SIGN_IN, null, 0, 0, 0);
            } catch (IntentSender.SendIntentException e) {
                LOGE(TAG, "SendIntentException occurred: " + e.getMessage());
                mResolvingIntent = false;
                mGoogleApiClient.connect();
            }
        }
    }

    /**
     * Handles an Activity result. Call this from your Activity's onActivityResult().
     */
    public boolean onActivityResult(int requestCode, int resultCode, Intent data) {
        Activity activity = getActivity("onActivityResult()");
        if (activity == null) {
            return false;
        }

        mResolvingIntent = false;
        if (requestCode == REQUEST_AUTHENTICATE || requestCode == REQUEST_RECOVER_FROM_AUTH_ERROR) {
            LOGD(TAG, "onActivityResult, req = " + requestCode + ", result = " + resultCode);

            if (resultCode == Activity.RESULT_OK) {
                if (mGoogleApiClient != null && !mGoogleApiClient.isConnecting()) {
                    LOGD(TAG, "Since activity result was RESULT_OK, reconnecting client.");
                    mGoogleApiClient.connect();
                } else {
                    LOGD(TAG, "Activity result was RESULT_OK, but we have no client to reconnect.");
                }
            } else {
                mSignInRequestPending = false;
                LOGW(TAG, "Failed to recover from a login/auth failure, resultCode=" + resultCode);
            }
            return true;
        }
        return false;
    }

    private void reportAuthSuccess(boolean newlyAuthenticated) {
        LOGD(TAG, "Authentication success, newlyAuthenticated=" + newlyAuthenticated);
        Callbacks callbacks;
        if (null != (callbacks = mCallbacksRef.get())) {
            callbacks.onAuthSuccess(newlyAuthenticated);
        }
    }

    private void reportAuthFailure() {
        LOGD(TAG, "Authentication failed");
        Callbacks callbacks;
        if (null != (callbacks = mCallbacksRef.get())) {
            callbacks.onAuthFailure();
        }
    }

    public void signOut() {
        if(!mIsSignedIn) {
            return;
        }

        Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
        mGoogleApiClient.disconnect();
        mGoogleApiClient.connect();
        mIsSignedIn = false;
        mSignInRequestPending = false;
    }

    public boolean isSignedIn() {
        return mIsSignedIn;
    }

    public UserInfo getUserInfo() {
        if(!mIsSignedIn)
            return null;

        Person user = Plus.PeopleApi.getCurrentPerson(mGoogleApiClient);
        String email = Plus.AccountApi.getAccountName(mGoogleApiClient);
        UserInfo userInfo = new UserInfo();
        userInfo.setFromGooglePlusProfile(user, email);
        return userInfo;
    }
}
