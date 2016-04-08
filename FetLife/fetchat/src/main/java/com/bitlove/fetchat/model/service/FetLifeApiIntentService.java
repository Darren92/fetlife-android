package com.bitlove.fetchat.model.service;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;

import com.bitlove.fetchat.BuildConfig;
import com.bitlove.fetchat.FetLifeApplication;
import com.bitlove.fetchat.model.api.FetLifeApi;
import com.bitlove.fetchat.model.api.FetLifeService;
import com.bitlove.fetchat.model.db.FetChatDatabase;
import com.bitlove.fetchat.model.pojos.Conversation;
import com.bitlove.fetchat.model.pojos.Member;
import com.bitlove.fetchat.model.pojos.Message;
import com.bitlove.fetchat.model.pojos.Message$Table;
import com.bitlove.fetchat.model.pojos.Token;
import com.bitlove.fetchat.view.ConversationsActivity;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.runtime.TransactionManager;
import com.raizlabs.android.dbflow.sql.builder.Condition;
import com.raizlabs.android.dbflow.sql.language.Delete;
import com.raizlabs.android.dbflow.sql.language.Select;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import java.util.UUID;

import retrofit.Call;
import retrofit.Response;

public class FetLifeApiIntentService extends IntentService {

    public static final String ACTION_APICALL_CONVERSATIONS = "com.bitlove.fetchat.action.apicall.cpnversations";
    public static final String ACTION_APICALL_MESSAGES = "com.bitlove.fetchat.action.apicall.messages";
    public static final String ACTION_APICALL_NEW_MESSAGE = "com.bitlove.fetchat.action.apicall.new_messages";

    public static final String ACTION_APICALL_LOGON_USER = "com.bitlove.fetchat.action.apicall.logon_user";

    private static final String ACCOUNT_TYPE_FETCHAT = "com.bitlove.fetchat.type.account.fetchat";
    private static final String TOKEN_TYPE_REFRESH = "com.bitlove.fetchat.type.token.refresh";

    private static final String EXTRA_PARAMS = "com.bitlove.fetchat.extra.params";

    public FetLifeApiIntentService() {
        super("FetLifeApiIntentService");
    }

    public static void startApiCall(Context context, String action, String... params) {
        Intent intent = new Intent(context, FetLifeApiIntentService.class);
        intent.setAction(action);
        intent.putExtra(EXTRA_PARAMS, params);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent == null) {
            return;
        }

        final String action = intent.getAction();

        //TODO: checkForNetworkState
        try {
            boolean result = false;
            switch (action) {
                case ACTION_APICALL_LOGON_USER:
                    result = logonUser(intent.getStringArrayExtra(EXTRA_PARAMS));
                    break;
                case ACTION_APICALL_CONVERSATIONS:
                    result = retriveConversations();
                    break;
                case ACTION_APICALL_MESSAGES:
                    result = retrieveMessages(intent.getStringArrayExtra(EXTRA_PARAMS));
                    break;
                case ACTION_APICALL_NEW_MESSAGE:
                    result = sendPendingMessages();
                    break;
            }

            if (result) {
                sendLoadFinishedNotification(action);
            } else if (action != ACTION_APICALL_LOGON_USER && getFetLifeApplication().getFetLifeService().getLastResponseCode() == 403) {
                refreshToken();
                onHandleIntent(intent);
                return;
                //TODO: error handling for endless loop
            } else {
                sendLoadFailedNotification(action);
            }
        } catch (IOException ioe) {
            sendConnectionFailedNotification(action);
        }
    }

    private void refreshToken() {

    }

    private void sendLoadFinishedNotification(String action) {

    }

    private void sendLoadFailedNotification(String action) {

    }

    private void sendConnectionFailedNotification(String action) {

    }

    private boolean logonUser(String... params) throws IOException {
        Call<Token> tokenCall = getFetLifeApplication().getFetLifeService().getFetLifeApi().login(
                BuildConfig.CLIENT_ID,
                BuildConfig.CLIENT_SECRET,
                BuildConfig.REDIRECT_URL,
                FetLifeService.GRANT_TYPE_PASSWORD,
                params[0], params[1]);

        Response<Token> tokenResponse = tokenCall.execute();
        if (tokenResponse.isSuccess()) {
            getFetLifeApplication().setAccessToken(tokenResponse.body().getAccessToken());

            if (retrieveMyself()) {
                AccountManager accountManager = AccountManager.get(getFetLifeApplication());
                clearAccounts(accountManager);
                Account account = new Account(params[0],ACCOUNT_TYPE_FETCHAT);

                String meAsJson = new ObjectMapper().writeValueAsString(getFetLifeApplication().getMe());
                Bundle accountData = new Bundle();
                accountData.putString(FetLifeApplication.CONSTANT_BUNDLE_JSON, meAsJson);

                accountManager.addAccountExplicitly(account, null, accountData);
                accountManager.setAuthToken(account, TOKEN_TYPE_REFRESH, tokenResponse.body().getRefreshToken());

                return true;

            } else {
                return false;
            }

        } else {
            return false;
        }
    }

    private void clearAccounts(AccountManager accountManager) {
        for (Account account : accountManager.getAccounts()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
                accountManager.removeAccountExplicitly(account);
            } else {
                accountManager.removeAccount(account, null, null);
            }
        }
    }

    private boolean sendPendingMessages() throws IOException {
        List<Message> pendingMessages = new Select().from(Message.class).where(Condition.column(Message$Table.PENDING).eq(true)).queryList();
        for (Message message : pendingMessages) {
            if (!sendPendingMessage(message)) {
                return false;
            }
        }
        return true;
    }

    private boolean sendPendingMessage(Message pendingMessage) throws IOException {
        Call<Message> postMessagesCall = getFetLifeApi().postMessage(FetLifeService.AUTH_HEADER_PREFIX + getFetLifeApplication().getAccessToken(), pendingMessage.getConversationId(), pendingMessage.getBody());
        Response<Message> postMessageResponse = postMessagesCall.execute();
        if (postMessageResponse.isSuccess()) {
            final Message message = postMessageResponse.body();
            message.setClientId(pendingMessage.getClientId());
            message.setPending(false);
            message.setConversationId(pendingMessage.getConversationId());
            message.update();
            return true;
        } else {
            return false;
        }
    }

    private boolean retrieveMessages(String... params) throws IOException {
        final String conversationId = params[0];
        Call<List<Message>> getMessagesCall = getFetLifeApi().getMessages(FetLifeService.AUTH_HEADER_PREFIX + getFetLifeApplication().getAccessToken(), conversationId);
        Response<List<Message>> messagesResponse = getMessagesCall.execute();
        if (messagesResponse.isSuccess()) {
            final List<Message> messages = messagesResponse.body();
            TransactionManager.transact(FlowManager.getDatabase(FetChatDatabase.NAME).getWritableDatabase(), new Runnable() {
                @Override
                public void run() {
                    for (Message message : messages) {
                        Message storedMessage = new Select().from(Message.class).where(Condition.column(Message$Table.ID).eq(message.getId())).querySingle();
                        if (storedMessage != null) {
                            message.setClientId(storedMessage.getClientId());
                        } else {
                            message.setClientId(UUID.randomUUID().toString());
                        }
                        message.setConversationId(conversationId);
                        message.setPending(false);
                        message.save();
                    }
                }
            });
            return true;
        } else {
            return false;
        }
    }

    private boolean retriveConversations() throws IOException {
        Call<List<Conversation>> getConversationsCall = getFetLifeApi().getConversations(FetLifeService.AUTH_HEADER_PREFIX + getFetLifeApplication().getAccessToken());
        Response<List<Conversation>> conversationsResponse = getConversationsCall.execute();
        if (conversationsResponse.isSuccess()) {
            final List<Conversation> conversations = conversationsResponse.body();
            TransactionManager.transact(FlowManager.getDatabase(FetChatDatabase.NAME).getWritableDatabase(), new Runnable() {
                @Override
                public void run() {
                    new Delete().from(Conversation.class).queryClose();
                    for (Conversation conversation : conversations) {
                        conversation.save();
                    }
                }
            });
            return true;
        } else {
            return false;
        }
    }

    private boolean retrieveMyself() throws IOException {
        Call<Member> getMeCall = getFetLifeApi().getMe(FetLifeService.AUTH_HEADER_PREFIX + getFetLifeApplication().getAccessToken());
        Response<Member> getMeResponse = getMeCall.execute();
        if (getMeResponse.isSuccess()) {
            getFetLifeApplication().setMe(getMeResponse.body());
            return true;
        } else {
            return false;
        }
    }

    private FetLifeApplication getFetLifeApplication() {
        return (FetLifeApplication) getApplication();
    }

    private FetLifeApi getFetLifeApi() {
        return getFetLifeApplication().getFetLifeService().getFetLifeApi();
    }
}
