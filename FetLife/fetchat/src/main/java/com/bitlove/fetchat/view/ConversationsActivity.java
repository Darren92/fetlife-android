package com.bitlove.fetchat.view;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.widget.AdapterView;

import com.bitlove.fetchat.model.pojos.Conversation;
import com.bitlove.fetchat.model.service.FetLifeApiIntentService;
import com.raizlabs.android.dbflow.runtime.FlowContentObserver;
import com.raizlabs.android.dbflow.structure.BaseModel;
import com.raizlabs.android.dbflow.structure.Model;

public class ConversationsActivity extends RecyclerActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private FlowContentObserver conversationsModelObserver;
    private ConversationsAdapter conversationsAdapter;

    public static void startActivity(Context context) {
        Intent intent = new Intent(context, ConversationsActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        verifyUser();

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Here you will be able to start new conversation", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        recyclerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Conversation conversation = conversationsAdapter.getItem(position);
                MessagesActivity.startActivity(ConversationsActivity.this, conversation.getId(), false);
            }
        });

        conversationsAdapter = new ConversationsAdapter();
        recyclerList.setAdapter(conversationsAdapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        conversationsModelObserver = new FlowContentObserver();
        conversationsModelObserver.addModelChangeListener(new FlowContentObserver.OnModelStateChangedListener() {
            @Override
            public void onModelStateChanged(Class<? extends Model> table, BaseModel.Action action) {
                conversationsAdapter.refresh();
            }
        });
        conversationsModelObserver.registerForContentChanges(this, Conversation.class);
        conversationsAdapter.refresh();
        FetLifeApiIntentService.startApiCall(this, FetLifeApiIntentService.ACTION_APICALL_CONVERSATIONS);
    }

    @Override
    protected void onStop() {
        conversationsModelObserver.unregisterForContentChanges(this);
        super.onStop();
    }

}
