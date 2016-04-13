package com.bitlove.fetchat.view;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.bitlove.fetchat.FetLifeApplication;
import com.bitlove.fetchat.R;
import com.bitlove.fetchat.model.pojos.Member;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.onesignal.OneSignal;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Arrays;

public class ResourceActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    protected FloatingActionButton floatingActionButton;
    protected NavigationView navigationView;
    protected View navigationHeaderView;
    protected ListView recyclerList;
    protected View textInputLayout;
    protected EditText textInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //TODO: think of moving content stuff out of this class/method
        setContentView(R.layout.activity_recycler);

        floatingActionButton = (FloatingActionButton) findViewById(R.id.fab);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        textInputLayout = findViewById(R.id.text_input_layout);
        textInput = (EditText) findViewById(R.id.text_input);

        recyclerList = (ListView) findViewById(R.id.list_view);

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        navigationHeaderView = navigationView.getHeaderView(0);

        Member me = getFetLifeApplication().getMe();
        if (me != null) {
            TextView headerTextView = (TextView) navigationHeaderView.findViewById(R.id.nav_header_text);
            headerTextView.setText(me.getNickname());
//        headerTextView = (TextView) navigationHeaderView.findViewById(R.id.nav_header_subtext);
//        headerTextView.setText(getFetLifeApplication().getMe().getId());
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        verifyUser();
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

            getFetLifeApplication().setAccessToken(null);

            //TODO: think about to move to the intent service
            PreferenceManager.getDefaultSharedPreferences(getFetLifeApplication()).edit().clear().apply();
            OneSignal.setSubscription(false);

            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put(FetLifeApplication.CONSTANT_ONESIGNAL_TAG_VERSION,1);
                jsonObject.put(FetLifeApplication.CONSTANT_ONESIGNAL_TAG_NICKNAME,getFetLifeApplication().getMe().getNickname());
                jsonObject.put(FetLifeApplication.CONSTANT_ONESIGNAL_TAG_MEMBER_TOKEN,"");
                OneSignal.sendTags(jsonObject);

                String[] tags = new String[] {
                        FetLifeApplication.CONSTANT_ONESIGNAL_TAG_VERSION,
                        FetLifeApplication.CONSTANT_ONESIGNAL_TAG_NICKNAME,
                        FetLifeApplication.CONSTANT_ONESIGNAL_TAG_MEMBER_TOKEN
                };
                OneSignal.deleteTags(Arrays.asList(tags));

            } catch (JSONException e) {
                //TODO: error handling
            }

            getFetLifeApplication().removeMe();
            LoginActivity.startLogout(this);
        } else if (id == R.id.nav_feedback) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    protected void verifyUser() {

        if (getFetLifeApplication().getMe() == null) {
            LoginActivity.startLogout(this);
            finish();
            overridePendingTransition(0, 0);
            return;
        }
    }

    @Override
    protected View getRootView() {
        return findViewById(R.id.content_layout);
    }
}
