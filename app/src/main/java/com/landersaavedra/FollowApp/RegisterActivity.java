package com.landersaavedra.FollowApp;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;



public class RegisterActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Set View to register.xml

        setContentView(R.layout.register_user);

        TextView loginScreen = (TextView) findViewById(R.id.EditTextEmail);

        // Listening to Login Screen link
        loginScreen.setOnClickListener(new View.OnClickListener() {

            public void onClick(View arg0) {
                // Closing registration screen
                // Switching to Login Screen/closing register screen
                finish();
            }
        });
    }
}
