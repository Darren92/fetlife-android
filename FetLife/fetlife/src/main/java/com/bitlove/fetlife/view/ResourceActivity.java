package com.bitlove.fetlife.view;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bitlove.fetlife.FetLifeApplication;
import com.bitlove.fetlife.R;
import com.bitlove.fetlife.event.AuthenticationFailedEvent;
import com.bitlove.fetlife.model.pojos.Member;
import com.bitlove.fetlife.model.resource.ImageLoader;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class ResourceActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    protected FloatingActionButton floatingActionButton;
    protected NavigationView navigationView;
    protected View navigationHeaderView;
    protected RecyclerView recyclerView;
    protected LinearLayoutManager recyclerLayoutManager;
    protected View inputLayout;
    protected View inputIcon;
    protected EditText textInput;
    protected ProgressBar progressIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //TODO: think of moving content stuff out of this class/method
        setContentView(R.layout.activity_resource);

        floatingActionButton = (FloatingActionButton) findViewById(R.id.fab);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //getSupportActionBar().setIcon(R.mipmap.ic_launcher);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        inputLayout = findViewById(R.id.text_input_layout);
        inputIcon = findViewById(R.id.text_send_icon);
        textInput = (EditText) findViewById(R.id.text_input);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(recyclerLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        progressIndicator = (ProgressBar) findViewById(R.id.toolbar_progress_indicator);

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        navigationHeaderView = navigationView.getHeaderView(0);

        try {
            String versionPrefixText = getString(R.string.version_prefix);
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            TextView versionTextView = (TextView) navigationView.findViewById(R.id.version_text);
            versionTextView.setText(versionPrefixText + pInfo.versionName);
        } catch (PackageManager.NameNotFoundException e) {
        }

        Member me = getFetLifeApplication().getMe();
        if (me != null) {
            TextView headerTextView = (TextView) navigationHeaderView.findViewById(R.id.nav_header_text);
            headerTextView.setText(me.getNickname());
            TextView headerSubTextView = (TextView) navigationHeaderView.findViewById(R.id.nav_header_subtext);
            headerSubTextView.setText(me.getMetaInfo());
            ImageView headerAvatar = (ImageView) navigationHeaderView.findViewById(R.id.nav_header_image);
            getFetLifeApplication().getImageLoader().loadImage(this, me.getAvatarLink(), headerAvatar, R.drawable.dummy_avatar);
            final String selfLink = me.getLink();
            if (selfLink != null) {
                headerAvatar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse(selfLink));
                        startActivity(intent);
                    }
                });
            }
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
        getMenuInflater().inflate(R.menu.activity_resource, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_logout) {
            LoginActivity.logout(getFetLifeApplication());
        } else if (id == R.id.nav_conversations) {
            ConversationsActivity.startActivity(this);
        } else if (id == R.id.nav_friends) {
            FriendsActivity.startActivity(this);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    protected void showProgress() {
        progressIndicator.setVisibility(View.VISIBLE);
    }

    protected void dismissProgress() {
        progressIndicator.setVisibility(View.INVISIBLE);
    }

    protected void verifyUser() {

        if (getFetLifeApplication().getMe() == null) {
            LoginActivity.logout(getFetLifeApplication());
            finish();
            overridePendingTransition(0, 0);
            return;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAuthenticationFailed(AuthenticationFailedEvent authenticationFailedEvent) {
        showToast(getString(R.string.authentication_failed));
        LoginActivity.logout(getFetLifeApplication());
    }

    protected FetLifeApplication getFetLifeApplication() {
        return (FetLifeApplication) getApplication();
    }

    protected void showToast(final String text) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(ResourceActivity.this, text, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
