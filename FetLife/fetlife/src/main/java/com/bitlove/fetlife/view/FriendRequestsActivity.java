package com.bitlove.fetlife.view;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.bitlove.fetlife.R;
import com.bitlove.fetlife.event.ServiceCallFailedEvent;
import com.bitlove.fetlife.event.ServiceCallFinishedEvent;
import com.bitlove.fetlife.event.ServiceCallStartedEvent;
import com.bitlove.fetlife.model.pojos.Conversation;
import com.bitlove.fetlife.model.pojos.FriendRequest;
import com.bitlove.fetlife.model.service.FetLifeApiIntentService;
import com.raizlabs.android.dbflow.runtime.FlowContentObserver;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class FriendRequestsActivity extends ResourceActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public static final String EXTRA_FRIENDREQUEST_LIST_MODE = "com.bitlove.fetlife.extra.friendRequest_list_mode";

    public enum FriendRequestListMode {
        NEW_CONVERSATION,
        FRIENDREQUEST_PROFILE
    }

    private static final int FRIENDREQUESTS_PAGE_COUNT = 10;

    private FlowContentObserver friendRequestsModelObserver;
    private FriendRequestsRecyclerAdapter friendRequestsAdapter;

    private int requestedPage = 1;

    public static void startActivity(Context context) {
        context.startActivity(createIntent(context, FriendRequestListMode.FRIENDREQUEST_PROFILE));
    }

    public static void startActivity(Context context, FriendRequestListMode friendRequestListMode) {
        context.startActivity(createIntent(context, friendRequestListMode));
    }

    public static Intent createIntent(Context context, FriendRequestListMode friendRequestListMode) {
        Intent intent = new Intent(context, FriendRequestsActivity.class);
        intent.putExtra(EXTRA_FRIENDREQUEST_LIST_MODE, friendRequestListMode.toString());
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        floatingActionButton.setVisibility(View.GONE);

        friendRequestsAdapter = new FriendRequestsRecyclerAdapter(getFetLifeApplication().getImageLoader());
        friendRequestsAdapter.setOnItemClickListener(new FriendRequestsRecyclerAdapter.OnFriendRequestClickListener() {
            @Override
            public void onItemClick(FriendRequest friendRequest) {
            }

            @Override
            public void onAvatarClick(FriendRequest friendRequest) {
                String url = friendRequest.getLink();
                if (url != null) {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(url));
                    startActivity(intent);
                }
            }
        });
        recyclerView.setAdapter(friendRequestsAdapter);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) {
                    int visibleItemCount = recyclerLayoutManager.getChildCount();
                    int pastVisiblesItems = recyclerLayoutManager.findFirstVisibleItemPosition();
                    int lastVisiblePosition = visibleItemCount + pastVisiblesItems;

                    if (lastVisiblePosition >= (requestedPage * FRIENDREQUESTS_PAGE_COUNT)) {
                        FetLifeApiIntentService.startApiCall(FriendRequestsActivity.this, FetLifeApiIntentService.ACTION_APICALL_FRIENDREQUESTS, Integer.toString(FRIENDREQUESTS_PAGE_COUNT), Integer.toString(++requestedPage));
                    }

                }
            }
        });
    }

    private FriendRequestListMode getFriendRequestListMode() {
        return FriendRequestListMode.valueOf(getIntent().getStringExtra(EXTRA_FRIENDREQUEST_LIST_MODE));
    }

    @Override
    protected void onStart() {
        super.onStart();

        friendRequestsModelObserver = new FlowContentObserver();

        if (isFinishing()) {
            return;
        }

        friendRequestsModelObserver.registerForContentChanges(this, FriendRequest.class);
        friendRequestsAdapter.refresh();

        showProgress();
        getFetLifeApplication().getEventBus().register(this);

        if (!FetLifeApiIntentService.isActionInProgress(FetLifeApiIntentService.ACTION_APICALL_FRIENDREQUESTS)) {
            FetLifeApiIntentService.startApiCall(this, FetLifeApiIntentService.ACTION_APICALL_FRIENDREQUESTS, Integer.toString(FRIENDREQUESTS_PAGE_COUNT));
        }

        requestedPage = 1;
    }

    @Override
    protected void onStop() {
        super.onStop();

        friendRequestsModelObserver.unregisterForContentChanges(this);

        getFetLifeApplication().getEventBus().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onFriendRequestsCallFinished(ServiceCallFinishedEvent serviceCallFinishedEvent) {
        if (serviceCallFinishedEvent.getServiceCallAction() == FetLifeApiIntentService.ACTION_APICALL_FRIENDREQUESTS) {
            friendRequestsAdapter.refresh();
            dismissProgress();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onFriendRequestsCallFailed(ServiceCallFailedEvent serviceCallFailedEvent) {
        if (serviceCallFailedEvent.getServiceCallAction() == FetLifeApiIntentService.ACTION_APICALL_FRIENDREQUESTS) {
            if (serviceCallFailedEvent.isServerConnectionFailed()) {
                showToast(getResources().getString(R.string.error_connection_failed));
            } else {
                showToast(getResources().getString(R.string.error_apicall_failed));
            }
            friendRequestsAdapter.refresh();
            dismissProgress();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onFriendRequestCallStarted(ServiceCallStartedEvent serviceCallStartedEvent) {
        if (serviceCallStartedEvent.getServiceCallAction() == FetLifeApiIntentService.ACTION_APICALL_FRIENDREQUESTS) {
            showProgress();
        }
    }

}
