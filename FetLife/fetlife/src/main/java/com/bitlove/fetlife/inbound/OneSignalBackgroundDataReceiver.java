package com.bitlove.fetlife.inbound;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.WakefulBroadcastReceiver;

import com.bitlove.fetlife.model.service.FetLifeApiIntentService;

import org.json.JSONObject;

public class OneSignalBackgroundDataReceiver extends WakefulBroadcastReceiver {
    public void onReceive(Context context, Intent intent) {
        Bundle dataBundle = intent.getBundleExtra("data");

        try {
            JSONObject customJSON = new JSONObject(dataBundle.getString("custom"));
            if (customJSON.has("a")) {
                JSONObject additionalData = customJSON.getJSONObject("a");
                String conversationId = additionalData.getString("side_id");
                if (conversationId != null) {
                    FetLifeApiIntentService.startApiCall(context, FetLifeApiIntentService.ACTION_APICALL_MESSAGES, conversationId);
                }
            }
        } catch (Exception e) {
            //TODO: error handling
        }
    }
}