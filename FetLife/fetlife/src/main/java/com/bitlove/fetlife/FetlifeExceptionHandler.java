package com.bitlove.fetlife;

import com.bitlove.fetlife.view.ErrorReportActivity;

/**
 * Created by Titan on 6/14/2016.
 */
public class FetlifeExceptionHandler implements Thread.UncaughtExceptionHandler {

    private final FetLifeApplication fetLifeApplication;

    public FetlifeExceptionHandler(FetLifeApplication fetLifeApplication) {
        this.fetLifeApplication = fetLifeApplication;
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        ErrorReportActivity.startActivity(fetLifeApplication, ex);
    }
}
