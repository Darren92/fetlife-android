package com.bitlove.fetchat.model.service;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.util.Log;

import com.bitlove.fetchat.FetLifeApplication;
import com.bitlove.fetchat.model.api.FetLifeApi;
import com.bitlove.fetchat.model.api.FetLifeService;
import com.bitlove.fetchat.model.db.FetChatDatabase;
import com.bitlove.fetchat.model.pojos.Conversation;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.runtime.TransactionManager;
import com.raizlabs.android.dbflow.sql.language.Delete;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;

import retrofit.Call;
import retrofit.Response;

public class FetLifeApiIntentService extends IntentService {

    public static final String ACTION_APICALL_CONVERSATIONS = "com.bitlove.fetchat.action.apicall.cpnversations";
    public static final String ACTION_APICALL_MESSAGES = "com.bitlove.fetchat.action.apicall.messages";
    private static final String EXTRA_METHOD = "com.bitlove.fetchat.extra.METHOD";
    private static final String EXTRA_PARAMS = "com.bitlove.fetchat.extra.PARAMS";

    public FetLifeApiIntentService() {
        super("FetLifeApiIntentService");
    }

    public static void startApiCall(Context context, String action, Serializable... params) {
        Intent intent = new Intent(context, FetLifeApiIntentService.class);
        intent.setAction(action);
        intent.putExtra(EXTRA_PARAMS, params);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            switch (action) {
                case ACTION_APICALL_CONVERSATIONS:
                    retriveConversations();
            }
        }
    }

    private void retriveConversations() {
        try {
            Call<List<Conversation>> getConversationsCall = getFetLifeApi().getConversations(FetLifeService.AUTH_HEADER_PREFIX + getFetLifeApplication().getAccessToken());
            Response<List<Conversation>> conversationsResponse = getConversationsCall.execute();
            if (conversationsResponse.isSuccess()) {
                final List<Conversation> conversations = conversationsResponse.body();
//                Delete.table(Conversation.class);
//                for (Conversation conversation: conversations) {
//                    conversation.save();
//                }
                TransactionManager.transact(FlowManager.getDatabase(FetChatDatabase.NAME).getWritableDatabase(), new Runnable() {
                    @Override
                    public void run() {
                        Delete.table(Conversation.class);
                        for (Conversation conversation : conversations) {
                            conversation.save();
                        }
                    }
                });
            } else {
                //TODO: error handling
            }
        } catch (IOException e) {
            Log.e("FETCHAT","eception",e);
        }
    }

    protected FetLifeApplication getFetLifeApplication() {
        return (FetLifeApplication) getApplication();
    }

    protected FetLifeApi getFetLifeApi() {
        return getFetLifeApplication().getFetLifeService().getFetLifeApi();
    }
}
