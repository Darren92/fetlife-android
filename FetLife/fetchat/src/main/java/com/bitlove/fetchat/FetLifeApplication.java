package com.bitlove.fetchat;

import android.app.Application;

import com.bitlove.fetchat.model.pojos.Me;
import com.bitlove.fetchat.model.api.FetLifeService;
import com.raizlabs.android.dbflow.config.FlowManager;

public class FetLifeApplication extends Application {

    private FetLifeService fetLifeService;
    private String accessToken;
    private Me me;

    @Override
    public void onCreate() {
        super.onCreate();

        fetLifeService = new FetLifeService();

        FlowManager.init(this);
    }

    public FetLifeService getFetLifeService() {
        return fetLifeService;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setMe(Me me) {
        this.me = me;
    }

    public Me getMe() {
        return me;
    }
}
