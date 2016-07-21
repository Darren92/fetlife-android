package com.bitlove.fetlife.inbound;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.Parcelable;
import android.widget.Toast;

import com.bitlove.fetlife.FetLifeApplication;
import com.bitlove.fetlife.event.FriendSuggestionAddedEvent;
import com.bitlove.fetlife.model.pojos.FriendSuggestion;
import com.fasterxml.jackson.databind.ObjectMapper;

public class NfcFriendActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        onReceive(this, getIntent());
        finish();
    }

    public void onReceive(Context context, Intent intent) {

        Parcelable[] rawMsgs = intent.getParcelableArrayExtra(
                NfcAdapter.EXTRA_NDEF_MESSAGES);
        NdefMessage msg = (NdefMessage) rawMsgs[0];

        try {
            FriendSuggestion friendSuggestion = new ObjectMapper().readValue(new String(msg.getRecords()[0].getPayload()), FriendSuggestion.class);
            friendSuggestion.save();

            getFetLifeApplication().getEventBus().post(new FriendSuggestionAddedEvent());

            //TODO replace with Android notification
            Toast.makeText(context, "You received a new Shared Profile. Check your Friend Requests page", Toast.LENGTH_LONG).show();

        } catch (Exception e) {
            //Should not happen, force a crash to get the report if it did
            throw new RuntimeException(e);
        }

    }

    private FetLifeApplication getFetLifeApplication() {
        return (FetLifeApplication) getApplication();
    }

}
