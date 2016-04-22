package com.bitlove.fetlife;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.bitlove.fetlife.model.pojos.Member;
import com.bitlove.fetlife.model.api.FetLifeService;
import com.bitlove.fetlife.inbound.OnNotificationOpenedHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.onesignal.OneSignal;
import com.raizlabs.android.dbflow.config.FlowManager;

import org.greenrobot.eventbus.EventBus;

public class FetLifeApplication extends Application {

    public static final String CONSTANT_PREF_KEY_ME_JSON = "com.bitlove.fetlife.bundle.json";
    public static final String CONSTANT_ONESIGNAL_TAG_VERSION = "version";
    public static final String CONSTANT_ONESIGNAL_TAG_NICKNAME = "nickname";
    public static final String CONSTANT_ONESIGNAL_TAG_MEMBER_TOKEN = "member_token";

    private static FetLifeApplication instance;

    public static FetLifeApplication getInstance() {
        return instance;
    }

    private FetLifeService fetLifeService;

    private String accessToken;
    private Member me;

    private EventBus eventBus;

    @Override
    public void onCreate() {
        super.onCreate();

        instance = this;

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String meAsJson = preferences.getString(FetLifeApplication.CONSTANT_PREF_KEY_ME_JSON, null);
        if (meAsJson != null) {
            try {
                Member me = new ObjectMapper().readValue(meAsJson, Member.class);
                this.me = me;
            } catch (Exception e) {
                preferences.edit().clear().apply();
            }
        }

        OneSignal.startInit(this).setNotificationOpenedHandler(new OnNotificationOpenedHandler()).init();
        OneSignal.enableNotificationsWhenActive(false);

        fetLifeService = new FetLifeService();

        eventBus = EventBus.getDefault();

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
    }

    public void removeMe() {
        me = null;
    }

    public Member getMe() {
        return me;
    }

    public EventBus getEventBus() {
        return eventBus;
    }

}
