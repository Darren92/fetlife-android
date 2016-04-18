package com.bitlove.fetchat.inbound;

import android.app.TaskStackBuilder;
import android.os.Build;

import com.bitlove.fetchat.FetLifeApplication;
import com.bitlove.fetchat.event.NewMessageEvent;
import com.bitlove.fetchat.view.ConversationsActivity;
import com.bitlove.fetchat.view.MessagesActivity;
import com.onesignal.OneSignal;

import org.json.JSONException;
import org.json.JSONObject;

public class OnNotificationOpenedHandler implements OneSignal.NotificationOpenedHandler {
    @Override
    public void notificationOpened(String message, JSONObject additionalData, boolean isActive) {
        FetLifeApplication application = FetLifeApplication.getInstance();
        if (isActive) {
            try {
                application.getEventBus().post(new NewMessageEvent(additionalData.getString("side_id")));
            } catch (JSONException e) {
                //TODO: Error handling
            }
        } else {
            //TODO: if the messages are from the same conversation, open the conversation
            if (additionalData.has("stacked_notifications")) {
                ConversationsActivity.startActivity(application);
            } else {
                try {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        TaskStackBuilder.create(application).addNextIntent(ConversationsActivity.createIntent(application)).addNextIntent(MessagesActivity.createIntent(application, additionalData.getString("side_id"), true)).startActivities();
                    } else {
                        ConversationsActivity.startActivity(application);
                        MessagesActivity.startActivity(application, additionalData.getString("side_id"), true);
                    }
                } catch (JSONException e) {
                    //TODO: Error handling
                }
            }
        }
    }
}
