package com.landersaavedra.FollowApp;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.accountkit.AccountKit;
import com.facebook.accountkit.AccountKitLoginResult;
import com.facebook.accountkit.PhoneNumber;
import com.facebook.accountkit.ui.AccountKitActivity;
import com.facebook.accountkit.ui.AccountKitConfiguration;
import com.facebook.accountkit.ui.LoginType;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.common.SignInButton;
import com.landersaavedra.FollowApp.account.GoogleSignInActivity;

import java.util.Locale;

/**
 * Created by Lander on 09/02/2018.
 */

public class LoginActivity extends BaseActivity  {

    public static int APP_REQUEST_CODE = 1;
    EditText loginText;
    LoginButton loginButton;
    Button accountkitButton;
    CallbackManager callbackManager;
    AppEventsLogger logger;
    SignInButton SingInGoogle;

    private GoogleSignInActivity SignIn;
     private static  int Registro = 0;
   
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        FontHelper.setCustomTypeface(findViewById(R.id.view_root));
        logger = AppEventsLogger.newLogger(this);
        loginText = (EditText) findViewById(R.id.login_text);
        accountkitButton = (Button) findViewById(R.id.accountkit_button);
        accountkitButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String login = loginText.getText().toString();
                if (login.contains("@")) {
                    logger.logEvent("emailLogin");
                    onAccountKitLogin(login, LoginType.EMAIL);
                } else {
                    logger.logEvent("phoneLogin");
                    onAccountKitLogin(login, LoginType.PHONE);
                }
            }
        });

        SingInGoogle = (SignInButton)findViewById(R.id.btn_sign_in);
        SingInGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GoogleSignInActivity sg = new GoogleSignInActivity();
                sg.onStart();
            }
        });


        loginButton = (LoginButton) findViewById(R.id.facebook_login_button);
        loginButton.setReadPermissions("email");

        // Login Button callback registration
        callbackManager = CallbackManager.Factory.create();
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                launchMainActivity();
            }

            @Override
            public void onCancel() {
            }

            @Override
            public void onError(FacebookException exception) {
                // display error
                String toastMessage = exception.getMessage();
                Toast.makeText(LoginActivity.this, toastMessage, Toast.LENGTH_LONG).show();
            }
        });

        // check for an existing access token
        com.facebook.accountkit.AccessToken accessToken = AccountKit.getCurrentAccessToken();
        com.facebook.AccessToken loginToken = com.facebook.AccessToken.getCurrentAccessToken();
        if (accessToken != null || loginToken != null) {
            // if previously logged in, proceed to the account activity
            launchMainActivity();
        }
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Forward result to the callback manager for Login Button
        callbackManager.onActivityResult(requestCode, resultCode, data);

        // For Account Kit, confirm that this response matches your request
        if (requestCode == APP_REQUEST_CODE) {
            AccountKitLoginResult loginResult = data.getParcelableExtra(AccountKitLoginResult.RESULT_KEY);
            if (loginResult.getError() != null) {
                // display login error
                String toastMessage = loginResult.getError().getErrorType().getMessage();
                Toast.makeText(this, toastMessage, Toast.LENGTH_LONG).show();
            } else if (loginResult.getAccessToken() != null) {
                // on successful login, proceed to the account activity
                launchMainActivity();
            }
        }
    }

    private void onAccountKitLogin(final String login, final LoginType loginType) {
        // create intent for the Account Kit activity
        final Intent intent = new Intent(this, AccountKitActivity.class);

        // configure login type and response type
        AccountKitConfiguration.AccountKitConfigurationBuilder configurationBuilder =
                new AccountKitConfiguration.AccountKitConfigurationBuilder(
                        loginType,
                        AccountKitActivity.ResponseType.TOKEN
                );

        if (loginType == LoginType.EMAIL) {
            configurationBuilder.setInitialEmail(login);
        }
        else {
            PhoneNumber phoneNumber = new PhoneNumber(Locale.getDefault().getCountry(), login, null);
            configurationBuilder.setInitialPhoneNumber(phoneNumber);
        }
        final AccountKitConfiguration configuration = configurationBuilder.build();

        // launch the Account Kit activity
        intent.putExtra(AccountKitActivity.ACCOUNT_KIT_ACTIVITY_CONFIGURATION, configuration);
        startActivityForResult(intent, APP_REQUEST_CODE);
    }

    private void launchMainActivity() {

        if(Registro == 1) {
            Intent intentRegister = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intentRegister);
            finish();
        }else{
            Intent intentMain = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intentMain);
            finish();
        }
    }

    private void showErrorDialog(String message) {
        new AlertDialog.Builder(this)
                .setTitle("Error")
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

   // @Override
    //public void onClick(View v) {
    //    int i = v.getId();
    //  if (i == R.id.btn_sign_in) {
    //    SignIn = new GoogleSignInActivity();
    //    SignIn.signIn();
    //  }
    //}
}