package com.bitlove.fetchat;

import android.app.Application;
import android.util.Log;

import com.bitlove.fetchat.model.pojos.Member;
import com.bitlove.fetchat.model.api.FetLifeService;
import com.bitlove.fetchat.view.OnNotificationOpenedHandler;
import com.onesignal.OneSignal;
import com.raizlabs.android.dbflow.config.FlowManager;

import org.json.JSONException;
import org.json.JSONObject;

public class FetLifeApplication extends Application {

    public static final String CONSTANT_BUNDLE_JSON = "com.bitlove.fetchat.bundle.json";

    private static FetLifeApplication instance;

    public static FetLifeApplication getInstance() {
        return instance;
    }

    private FetLifeService fetLifeService;
    private String accessToken;
    private Member me;

    @Override
    public void onCreate() {
        super.onCreate();

        instance = this;

        OneSignal.startInit(this).setNotificationOpenedHandler(new OnNotificationOpenedHandler()).init();

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

    public void setMe(Member me) {
        this.me = me;
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("version",1);
            jsonObject.put("nickname",me.getNickname());
            jsonObject.put("member_token",me.getNotificationToken());
            OneSignal.sendTags(jsonObject);
        } catch (JSONException e) {
            //TODO: error handling
        }
    }

    public Member getMe() {
        return me;
    }
}
