package com.landersaavedra.FollowApp;

import android.app.ProgressDialog;
import android.support.annotation.VisibleForTesting;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by Lander on 21/02/2018.
 */

public class BaseActivity extends AppCompatActivity {

    @VisibleForTesting
    public ProgressDialog mPrograessDialog;

    public void showProgressDialog(){

        if(mPrograessDialog == null){
            mPrograessDialog = new ProgressDialog(this);
            mPrograessDialog.setMessage("Cargando...");
            mPrograessDialog.setIndeterminate(true);
        }
        mPrograessDialog.show();
    }

    public void hideProgressDialog(){
     if(mPrograessDialog != null&& mPrograessDialog.isShowing()){
         mPrograessDialog.dismiss();
     }
    }

    @Override
    public void onStop(){
        super.onStop();
        hideProgressDialog();
    }
}
