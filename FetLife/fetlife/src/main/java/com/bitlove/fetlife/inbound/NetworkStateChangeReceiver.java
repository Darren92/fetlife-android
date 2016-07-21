package com.bitlove.fetlife.inbound;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.bitlove.fetlife.model.service.FetLifeApiIntentService;
import com.bitlove.fetlife.util.NetworkUtil;

public class NetworkStateChangeReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(final Context context, final Intent intent) {

        int status = NetworkUtil.getConnectivityStatusString(context);
        if(status != NetworkUtil.NETWORK_STATUS_NOT_CONNECTED){
            if (!FetLifeApiIntentService.isActionInProgress(FetLifeApiIntentService.ACTION_APICALL_SEND_MESSAGES)) {
                FetLifeApiIntentService.startApiCall(context, FetLifeApiIntentService.ACTION_APICALL_SEND_MESSAGES);
            }
            if (!FetLifeApiIntentService.isActionInProgress(FetLifeApiIntentService.ACTION_APICALL_SEND_FRIENDREQUESTS)) {
                FetLifeApiIntentService.startApiCall(context, FetLifeApiIntentService.ACTION_APICALL_SEND_FRIENDREQUESTS);
            }
        }
    }
}
