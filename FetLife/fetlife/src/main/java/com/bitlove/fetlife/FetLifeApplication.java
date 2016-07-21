package com.bitlove.fetlife;

import android.app.Application;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.preference.PreferenceManager;

import com.bitlove.fetlife.inbound.OnNotificationOpenedHandler;
import com.bitlove.fetlife.model.api.FetLifeService;
import com.bitlove.fetlife.model.db.FetLifeDatabase;
import com.bitlove.fetlife.model.pojos.Member;
import com.bitlove.fetlife.model.resource.ImageLoader;
import com.crashlytics.android.Crashlytics;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.onesignal.OneSignal;
import com.raizlabs.android.dbflow.config.FlowConfig;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;

import io.fabric.sdk.android.Fabric;
import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

public class FetLifeApplication extends Application {

    public static final String CONSTANT_PREF_KEY_ME_JSON = "com.bitlove.fetlife.bundle.json";
    private static final String CONSTANT_PREF_KEY_DB_VERSION = "com.bitlove.fetlife.pref.db_version";

    public static final String CONSTANT_ONESIGNAL_TAG_VERSION = "version";
    public static final String CONSTANT_ONESIGNAL_TAG_NICKNAME = "nickname";
    public static final String CONSTANT_ONESIGNAL_TAG_MEMBER_TOKEN = "member_token";

    private static FetLifeApplication instance;
    private ImageLoader imageLoader;
    private String versionText;
    private int versionNumber;

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

//        Thread.currentThread().setUncaughtExceptionHandler(new FetlifeExceptionHandler(this, Thread.currentThread().getDefaultUncaughtExceptionHandler()));

        Fabric.with(this, new Crashlytics());

        instance = this;

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        //TODO: move me to database and check also for structure update
        String meAsJson = preferences.getString(FetLifeApplication.CONSTANT_PREF_KEY_ME_JSON, null);
        try {
            Member me = new ObjectMapper().readValue(meAsJson, Member.class);
            this.me = me;
        } catch (Exception e) {
            preferences.edit().remove(CONSTANT_PREF_KEY_ME_JSON);
        }

        int databaseVersion = preferences.getInt(CONSTANT_PREF_KEY_DB_VERSION, 0);
        if (databaseVersion < FetLifeDatabase.MIN_SUPPORTED_VERSION) {
            deleteDatabase();
        }
        preferences.edit().putInt(CONSTANT_PREF_KEY_DB_VERSION, FetLifeDatabase.VERSION).apply();
        FlowManager.init(new FlowConfig.Builder(this).build());

        OneSignal.startInit(this).setNotificationOpenedHandler(new OnNotificationOpenedHandler()).init();
        OneSignal.enableNotificationsWhenActive(false);

        imageLoader = new ImageLoader(this);

        try {
            fetLifeService = new FetLifeService(this);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        eventBus = EventBus.getDefault();

        try {
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            versionText = pInfo.versionName;
            versionNumber = pInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            versionText = getString(R.string.text_unknown);
        }

    }

    public FetLifeService getFetLifeService() {
        return fetLifeService;
    }

    public ImageLoader getImageLoader() {
        return imageLoader;
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

    public void deleteDatabase() {
        deleteDatabase(FetLifeDatabase.NAME + ".db");
        //DBFlow library uses .db suffix, but they mentioned they might going to change this in the future
        deleteDatabase(FetLifeDatabase.NAME);
    }

    public String getVersionText() {
        return versionText;
    }

    public int getVersionNumber() {
        return versionNumber;
    }
}
