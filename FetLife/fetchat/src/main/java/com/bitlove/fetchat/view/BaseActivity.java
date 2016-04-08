package com.bitlove.fetchat.view;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ProgressBar;

import com.bitlove.fetchat.FetLifeApplication;
import com.bitlove.fetchat.R;
import com.bitlove.fetchat.model.api.FetLifeApi;
import com.bitlove.fetchat.model.pojos.Member;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

public abstract class BaseActivity extends AppCompatActivity {

    private static final String STATE_PROGRESS = "com.bitlove.fetchat.STATE_PROGRESS";
    private static final int PROGRESS_NONE = 0;
    private static final int PROGRESS_BLOCKING = PROGRESS_NONE + 10;
    private static final int PROGRESS_NONBLOCKING = PROGRESS_BLOCKING + 10;

    protected Snackbar progressSnackbar;
    protected ProgressDialog progressDialog;

    protected FetLifeApplication getFetLifeApplication() {
        return (FetLifeApplication) getApplication();
    }

    protected void verifyUser() {

        if (getFetLifeApplication().getAccessToken() != null) {
            return;
        }

        AccountManager accountManager = AccountManager.get(getFetLifeApplication());

        Account[] accounts = accountManager.getAccounts();
        if (accounts.length == 0) {
            LoginActivity.startLogout(this);
            return;
        }

        Account account = accounts[0];

        try {
            String meAsJson = accountManager.getUserData(account, FetLifeApplication.CONSTANT_BUNDLE_JSON);
            Member me = new ObjectMapper().readValue(meAsJson, Member.class);
            getFetLifeApplication().setMe(me);
        } catch (IOException e) {
            LoginActivity.startLogout(this);
            return;
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        int progressState;

        if (progressDialog != null && progressDialog.isShowing()) {
                progressState = PROGRESS_BLOCKING;
                progressDialog.dismiss();
        } else if (progressSnackbar != null && progressSnackbar.isShownOrQueued()) {
            progressState = PROGRESS_NONBLOCKING;
        } else {
            progressState = PROGRESS_BLOCKING;
        }

        outState.putInt(STATE_PROGRESS, progressState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        int progressState = savedInstanceState.getInt(STATE_PROGRESS, PROGRESS_NONE);

        if (progressState != PROGRESS_NONE) {
            showProgress(progressState == PROGRESS_BLOCKING);
        }
    }

    protected void showProgress(boolean blocking) {
        if (blocking) {
            showDialogProgress();
        } else {
            showSnackbarProgress();
        }
    }

    private void showSnackbarProgress() {
        if (progressSnackbar == null || !progressSnackbar.isShownOrQueued()) {
            progressSnackbar = Snackbar.make(getRootView(), R.string.snakckbar_loading, Snackbar.LENGTH_INDEFINITE);
            //Snackbar.SnackbarLayout snackbarLayout = (Snackbar.SnackbarLayout) progressSnackbar.getView();
            progressSnackbar.show();
        }
    }

    private void showDialogProgress() {
        if (progressDialog == null || !progressDialog.isShowing()) {
            progressDialog =  new ProgressDialog(this);
            progressDialog.setCancelable(false);
            progressDialog.show();
        }
    }

    protected void dismissProgress() {
        if (progressSnackbar != null) {
            progressSnackbar.dismiss();
            progressSnackbar = null;
        }
        if (progressDialog != null) {
            progressDialog.dismiss();
            progressDialog = null;
        }
    }

    protected abstract View getRootView();

}
