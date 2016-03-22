package com.bitlove.fetchat.view;

import android.support.v7.app.AppCompatActivity;

import com.bitlove.fetchat.FetLifeApplication;

public abstract class BaseActivity extends AppCompatActivity {

    protected FetLifeApplication getFetLifeApplication() {
        return (FetLifeApplication) getApplication();
    }
}
