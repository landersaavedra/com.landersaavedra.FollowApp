package com.landersaavedra.FollowApp.account;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;
import com.landersaavedra.FollowApp.MainActivity;

import java.io.IOException;

/**
 * Created by Lander on 20/02/2018.
 */
public class GoogleOAuthActivity extends Activity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener  {

    private static final String TAG = GoogleOAuthActivity.class.getSimpleName();
    private GoogleApiClient mGoogleApiClient;

    private boolean mGoogleIntentInProgress;

    private boolean mGoogleLoginClicked;

    private ConnectionResult mGoogleConnectionResult;

    @Override
    protected void onCreate(Bundle savedInstanceState){

        super.onCreate(savedInstanceState);
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Plus.API)
                .addScope(Plus.SCOPE_PLUS_LOGIN)
                .build();
        mGoogleLoginClicked = true;

        if(!mGoogleApiClient.isConnected()){
            if(mGoogleConnectionResult != null){
                resolveSignInError();
            }else if(mGoogleApiClient.isConnected()){
                getGoogleOAuthTokenAndLogin();
            }else{
                Log.d(TAG, "conectando a Google Api");
            }
        }
    }

    private void resolveSignInError(){
        if(mGoogleConnectionResult.hasResolution()){
            try{
                mGoogleIntentInProgress = true;
                mGoogleConnectionResult.startResolutionForResult(this, MainActivity.RC_GOOGLE_LOGIN);
            }catch (IntentSender.SendIntentException e){
                mGoogleIntentInProgress= false;
                mGoogleApiClient.connect();
            }
        }
    }

    private void getGoogleOAuthTokenAndLogin(){

        AsyncTask<Void, Void, String>task = new AsyncTask<Void, Void, String>() {
            String errorMessage = null;
            @Override
            protected String doInBackground(Void... voids) {
                String token = null;
                try{
                    String scope = String.format("oauth2:%s", Scopes.PLUS_LOGIN);
                    token = GoogleAuthUtil.getToken(GoogleOAuthActivity.this, Plus.AccountApi.getAccountName(mGoogleApiClient), scope);
                }catch (IOException transientEx){
                    Log.e(TAG, "Error al autenticarse con Google: " + transientEx);
                    errorMessage = "Error de red: " + transientEx.getMessage();

                }catch (UserRecoverableAuthException e) {
                    Log.w(TAG, "Error recuperable de Google OAuth: " + e.toString());
                    if (!mGoogleIntentInProgress) {
                        mGoogleIntentInProgress = true;
                        Intent recover = e.getIntent();
                        startActivityForResult(recover, MainActivity.RC_GOOGLE_LOGIN);
                    }
                }catch (GoogleAuthException authEx) {
                    Log.e(TAG, "Error al autenticarse con Google: " + authEx.getMessage(), authEx);
                    errorMessage = "Error al autenticarse con Google: " + authEx.getMessage();
                }
                return token;
            }

            @Override
            protected void onPostExecute(String token) {
                mGoogleLoginClicked = false;
                Intent resultIntent = new Intent();
                if (token != null) {
                    resultIntent.putExtra("oauth_token", token);
                } else if (errorMessage != null) {
                    resultIntent.putExtra("error", errorMessage);
                }
                setResult(MainActivity.RC_GOOGLE_LOGIN, resultIntent);
                finish();
            }
        };
        task.execute();
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        getGoogleOAuthTokenAndLogin();
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        if (!mGoogleIntentInProgress) {
            mGoogleConnectionResult = connectionResult;
            if (mGoogleLoginClicked) {
                resolveSignInError();
            } else {
                Log.e(TAG, connectionResult.toString());
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK) {
            mGoogleLoginClicked = false;
        }
        mGoogleIntentInProgress = false;
        if (!mGoogleApiClient.isConnecting()) {
            mGoogleApiClient.connect();
        }
    }
}
