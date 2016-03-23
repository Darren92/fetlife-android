package com.bitlove.fetchat.view;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.bitlove.fetchat.R;
import com.bitlove.fetchat.model.pojos.Conversation;
import com.bitlove.fetchat.model.pojos.Message;
import com.bitlove.fetchat.model.service.FetLifeApiIntentService;
import com.raizlabs.android.dbflow.StringUtils;
import com.raizlabs.android.dbflow.runtime.FlowContentObserver;
import com.raizlabs.android.dbflow.structure.BaseModel;
import com.raizlabs.android.dbflow.structure.Model;

import java.util.UUID;

public class MessagesActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String EXTRA_CONVERSATION_ID = "com.bitlove.fetchat.extra.conversation_id";

    private FlowContentObserver messagesModelObserver;
    private MessagesAdapter messagesAdapter;

    private String conversationId;
    private Handler handler;
    private boolean isVisible;
    private volatile boolean refreshRuns;

    public static void startActivity(Context context, String conversationId) {
        Intent intent = new Intent(context, MessagesActivity.class);
        intent.putExtra(EXTRA_CONVERSATION_ID, conversationId);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversations);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        final EditText messageText = (EditText) findViewById(R.id.new_message);
                        String text = messageText.getText().toString();
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
                                messageText.setText("");
                            }
                        });
                        FetLifeApiIntentService.startApiCall(MessagesActivity.this, FetLifeApiIntentService.ACTION_APICALL_NEW_MESSAGE);
                    }
                }).start();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        conversationId = getIntent().getStringExtra(EXTRA_CONVERSATION_ID);

        final ListView messageList = (ListView) findViewById(R.id.list_view);
        messageList.setDividerHeight(0);
        messageList.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        messageList.setStackFromBottom(true);

        findViewById(R.id.new_message_layout).setVisibility(View.VISIBLE);

        messagesAdapter = new MessagesAdapter(conversationId);
        messageList.setAdapter(messagesAdapter);

        handler = new Handler();

        View navHeaderView = navigationView.getHeaderView(0);

        TextView headerText = (TextView) navHeaderView.findViewById(R.id.nav_header_text);
        headerText.setText(getFetLifeApplication().getMe().getNickname());
        headerText = (TextView) navHeaderView.findViewById(R.id.nav_header_subtext);
        headerText.setText(getFetLifeApplication().getMe().getId());
    }

    @Override
    protected void onStart() {
        super.onStart();
        isVisible = true;
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

        FetLifeApiIntentService.startApiCall(this, FetLifeApiIntentService.ACTION_APICALL_MESSAGES, conversationId);

        if (!refreshRuns) {
            setUpNextCall();
        }
    }

    private void setUpNextCall() {
        if (!isVisible) {
            refreshRuns = false;
            return;
        }
        refreshRuns = true;
        FetLifeApiIntentService.startApiCall(MessagesActivity.this, FetLifeApiIntentService.ACTION_APICALL_MESSAGES, conversationId);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                setUpNextCall();
            }
        }, 3000);
    }

    @Override
    protected void onStop() {
        messagesModelObserver.unregisterForContentChanges(this);
        isVisible = false;
        super.onStop();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.conversations, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_logout) {
            LoginActivity.startLogout(this);
        } else if (id == R.id.nav_feedback) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


}
