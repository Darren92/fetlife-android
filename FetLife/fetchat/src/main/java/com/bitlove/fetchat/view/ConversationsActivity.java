package com.bitlove.fetchat.view;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.bitlove.fetchat.R;
import com.bitlove.fetchat.model.pojos.Conversation;
import com.bitlove.fetchat.model.service.FetLifeApiIntentService;
import com.raizlabs.android.dbflow.runtime.FlowContentObserver;
import com.raizlabs.android.dbflow.structure.BaseModel;
import com.raizlabs.android.dbflow.structure.Model;

public class ConversationsActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private FlowContentObserver conversationsModelObserver;
    private ConversationsAdapter conversationsAdapter;

    public static void startActivity(Context context) {
        context.startActivity(new Intent(context, ConversationsActivity.class));
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
                Snackbar.make(view, "Here you will be able to start new conversation", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        final ListView conversationList = (ListView) findViewById(R.id.list_view);
        conversationList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Conversation conversation = conversationsAdapter.getItem(position);
                MessagesActivity.startActivity(ConversationsActivity.this, conversation.getId());
            }
        });

        conversationsAdapter = new ConversationsAdapter();
        conversationList.setAdapter(conversationsAdapter);

        View navHeaderView = navigationView.getHeaderView(0);

        TextView headerText = (TextView) navHeaderView.findViewById(R.id.nav_header_text);
        headerText.setText(getFetLifeApplication().getMe().getNickname());
        headerText = (TextView) navHeaderView.findViewById(R.id.nav_header_subtext);
        headerText.setText(getFetLifeApplication().getMe().getId());
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
