package com.bitlove.fetchat.view;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;

import com.bitlove.fetchat.FetLifeApplication;
import com.bitlove.fetchat.R;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

    protected void showProgress(final boolean blocking) {
        if (blocking) {
            showDialogProgress();
        } else {
            showToolbarProgress();
        }
    }

    private void showSnackbarProgress() {
        if (progressSnackbar == null || !progressSnackbar.isShownOrQueued()) {
            progressSnackbar = Snackbar.make(getRootView(), R.string.snakckbar_loading, Snackbar.LENGTH_INDEFINITE);
            //Snackbar.SnackbarLayout snackbarLayout = (Snackbar.SnackbarLayout) progressSnackbar.getView();
            progressSnackbar.show();
        }
    }

    private void dismissSnackbarProgress() {
        if (progressSnackbar != null) {
            progressSnackbar.dismiss();
            progressSnackbar = null;
        }
    }

    private void showToolbarProgress() {
        View progressView = findViewById(R.id.toolbar_progress_bar);
        if (progressView != null) {
            progressView.setVisibility(View.VISIBLE);
        }
    }

    private void dismissToolbarProgress() {
        View progressView = findViewById(R.id.toolbar_progress_bar);
        if (progressView != null) {
            progressView.setVisibility(View.INVISIBLE);
        }
    }

    private void showDialogProgress() {
        if (progressDialog == null || !progressDialog.isShowing()) {
            progressDialog =  new ProgressDialog(this);
            progressDialog.setCancelable(false);
            progressDialog.show();
        }
    }

    private void dismissDialogProgress() {
        if (progressDialog != null) {
            progressDialog.dismiss();
            progressDialog = null;
        }
    }

    protected void dismissProgress() {
            dismissDialogProgress();
            dismissSnackbarProgress();
            dismissToolbarProgress();
    }

    protected abstract View getRootView();

}
