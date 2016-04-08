package com.bitlove.fetchat.view;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.view.View;
import android.widget.ListView;

import com.bitlove.fetchat.R;
import com.bitlove.fetchat.model.pojos.Message;
import com.bitlove.fetchat.model.service.FetLifeApiIntentService;
import com.raizlabs.android.dbflow.runtime.FlowContentObserver;
import com.raizlabs.android.dbflow.structure.BaseModel;
import com.raizlabs.android.dbflow.structure.Model;

import java.util.UUID;

public class MessagesActivity extends RecyclerActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String EXTRA_CONVERSATION_ID = "com.bitlove.fetchat.extra.conversation_id";

    private FlowContentObserver messagesModelObserver;
    private MessagesAdapter messagesAdapter;

    private String conversationId;

//Polling
//    private volatile boolean refreshRuns;
//    private Handler handler = new Handler();
//    private boolean isVisible;

    public static void startActivity(Context context, String conversationId, boolean newTask) {
        Intent intent = new Intent(context, MessagesActivity.class);
        intent.putExtra(EXTRA_CONVERSATION_ID, conversationId);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
        //TODO: start alone, add backstack
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        verifyUser();

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String text = textInput.getText().toString();
                        if (text == null || text.trim().length() == 0) {
                            return;
                        }
                        Message message = new Message();
                        message.setPending(true);
                        message.setDate(System.currentTimeMillis());
                        message.setClientId(UUID.randomUUID().toString());
                        message.setConversationId(conversationId);
                        message.setBody(text);
                        message.save();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                textInput.setText("");
                            }
                        });
                        FetLifeApiIntentService.startApiCall(MessagesActivity.this, FetLifeApiIntentService.ACTION_APICALL_NEW_MESSAGE);
                    }
                }).start();
            }
        });

        conversationId = getIntent().getStringExtra(EXTRA_CONVERSATION_ID);

        recyclerList.setDividerHeight(0);
        recyclerList.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        recyclerList.setStackFromBottom(true);

        textInputLayout.setVisibility(View.VISIBLE);

        messagesAdapter = new MessagesAdapter(conversationId);
        recyclerList.setAdapter(messagesAdapter);

        FetLifeApiIntentService.startApiCall(this, FetLifeApiIntentService.ACTION_APICALL_MESSAGES, conversationId);

    }

    @Override
    protected void onStart() {
        super.onStart();
        messagesModelObserver = new FlowContentObserver();
        messagesModelObserver.addModelChangeListener(new FlowContentObserver.OnModelStateChangedListener() {
            @Override
            public void onModelStateChanged(Class<? extends Model> table, BaseModel.Action action) {
                messagesAdapter.refresh();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ListView messagesList = (ListView) findViewById(R.id.list_view);
                        messagesList.setSelection(messagesList.getCount() - 1);
                    }
                });

            }
        });
        messagesModelObserver.registerForContentChanges(this, Message.class);
        messagesAdapter.refresh();

        ListView messagesList = (ListView) findViewById(R.id.list_view);
        messagesList.setSelection(messagesList.getCount() - 1);

//Polling
//        isVisible = true;
//        if (!refreshRuns) {
//            setUpNextCall();
//        }
    }

    @Override
    protected void onStop() {
        messagesModelObserver.unregisterForContentChanges(this);
        super.onStop();
//Polling
//        isVisible = false;
    }

//Polling
//    private void setUpNextCall() {
//        if (!isVisible) {
//            refreshRuns = false;
//            return;
//        }
//        refreshRuns = true;
//        FetLifeApiIntentService.startApiCall(MessagesActivity.this, FetLifeApiIntentService.ACTION_APICALL_MESSAGES, conversationId);
//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                setUpNextCall();
//            }
//        }, 3000);
//    }

}
