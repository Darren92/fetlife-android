package com.bitlove.fetlife.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.bitlove.fetlife.R;
import com.bitlove.fetlife.event.NewMessageEvent;
import com.bitlove.fetlife.event.ServiceCallFailedEvent;
import com.bitlove.fetlife.event.ServiceCallFinishedEvent;
import com.bitlove.fetlife.event.ServiceCallStartedEvent;
import com.bitlove.fetlife.model.pojos.Friend;
import com.bitlove.fetlife.model.service.FetLifeApiIntentService;
import com.raizlabs.android.dbflow.runtime.FlowContentObserver;
import com.raizlabs.android.dbflow.structure.BaseModel;
import com.raizlabs.android.dbflow.structure.Model;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class FriendsActivity extends ResourceActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public static final String RESULT_EXTRA_FRIEND_ID = "com.bitlove.fetlife.result.extra.friend_id";

    private static final int FRIENDS_PAGE_COUNT = 10;

    private FlowContentObserver friendsModelObserver;
    private FriendsRecyclerAdapter friendsAdapter;

    private int requestedPage = 1;

    public static void startActivity(Context context) {
        context.startActivity(createIntent(context));
    }

    public static void startActivityForResult(Activity activity, int requestCode) {
        activity.startActivityForResult(createIntent(activity), requestCode);
    }

    public static Intent createIntent(Context context) {
        Intent intent = new Intent(context, FriendsActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        floatingActionButton.setVisibility(View.GONE);

        friendsAdapter = new FriendsRecyclerAdapter(getFetLifeApplication().getImageLoader());
        friendsAdapter.setOnItemClickListener(new FriendsRecyclerAdapter.OnFriendClickListener() {
            @Override
            public void onClick(Friend friend) {
                Intent resultIntent = new Intent();
                resultIntent.putExtra(RESULT_EXTRA_FRIEND_ID, friend.getId());
                setResult(RESULT_OK, resultIntent);
                finish();
            }
        });
        recyclerView.setAdapter(friendsAdapter);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) {
                    int visibleItemCount = recyclerLayoutManager.getChildCount();
                    int pastVisiblesItems = recyclerLayoutManager.findFirstVisibleItemPosition();
                    int lastVisiblePosition = visibleItemCount + pastVisiblesItems;

                    if (lastVisiblePosition >= (requestedPage * FRIENDS_PAGE_COUNT)) {
                        FetLifeApiIntentService.startApiCall(FriendsActivity.this, FetLifeApiIntentService.ACTION_APICALL_FRIENDS, Integer.toString(FRIENDS_PAGE_COUNT), Integer.toString(++requestedPage));
                    }

                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        friendsModelObserver = new FlowContentObserver();

        if (isFinishing()) {
            return;
        }

        friendsModelObserver.addModelChangeListener(new FlowContentObserver.OnModelStateChangedListener() {
            @Override
            public void onModelStateChanged(Class<? extends Model> table, BaseModel.Action action) {
                friendsAdapter.refresh();
            }
        });
        friendsModelObserver.registerForContentChanges(this, Friend.class);
        friendsAdapter.refresh();

        showProgress();
        getFetLifeApplication().getEventBus().register(this);

        if (!FetLifeApiIntentService.isActionInProgress(FetLifeApiIntentService.ACTION_APICALL_FRIENDS)) {
            FetLifeApiIntentService.startApiCall(this, FetLifeApiIntentService.ACTION_APICALL_FRIENDS, Integer.toString(FRIENDS_PAGE_COUNT));
        }

        requestedPage = 1;
    }

    @Override
    protected void onStop() {
        super.onStop();

        friendsModelObserver.unregisterForContentChanges(this);

        getFetLifeApplication().getEventBus().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onFriendsCallFinished(ServiceCallFinishedEvent serviceCallFinishedEvent) {
        if (serviceCallFinishedEvent.getServiceCallAction() == FetLifeApiIntentService.ACTION_APICALL_FRIENDS) {
            dismissProgress();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onFriendsCallFailed(ServiceCallFailedEvent serviceCallFailedEvent) {
        if (serviceCallFailedEvent.getServiceCallAction() == FetLifeApiIntentService.ACTION_APICALL_FRIENDS) {
            if (serviceCallFailedEvent.isServerConnectionFailed()) {
                showToast(getResources().getString(R.string.error_connection_failed));
            } else {
                showToast(getResources().getString(R.string.error_apicall_failed));
            }
            dismissProgress();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onFriendCallStarted(ServiceCallStartedEvent serviceCallStartedEvent) {
        if (serviceCallStartedEvent.getServiceCallAction() == FetLifeApiIntentService.ACTION_APICALL_FRIENDS) {
            showProgress();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onNewMessageArrived(NewMessageEvent newMessageEvent) {
        showProgress();
        if (!FetLifeApiIntentService.isActionInProgress(FetLifeApiIntentService.ACTION_APICALL_FRIENDS)) {
            FetLifeApiIntentService.startApiCall(this, FetLifeApiIntentService.ACTION_APICALL_FRIENDS);
        }
    }

}
