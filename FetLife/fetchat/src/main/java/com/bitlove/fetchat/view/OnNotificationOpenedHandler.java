package com.bitlove.fetchat.view;

import android.content.Intent;
import android.util.Log;

import com.bitlove.fetchat.FetLifeApplication;
import com.onesignal.OneSignal;

import org.json.JSONException;
import org.json.JSONObject;

public class OnNotificationOpenedHandler implements OneSignal.NotificationOpenedHandler {
    @Override
    public void notificationOpened(String message, JSONObject additionalData, boolean isActive) {
        FetLifeApplication application = FetLifeApplication.getInstance();
        if (additionalData.has("stacked_notifications")) {
            ConversationsActivity.startActivity(application);
        } else {
            String conversationId = parseConversationId(additionalData);
            MessagesActivity.startActivity(application, conversationId, true);
        }
    }

    private String parseConversationId(JSONObject data) {
        try {
            String launchUrl = data.getString("launchURL");
            String[] snippets = launchUrl.split("/");
            String suffix = snippets[snippets.length-1];
            snippets = suffix.split("#");
            return snippets[0];
        } catch (JSONException e) {
            //TODO: error handling
            return null;
        }
    }
}
