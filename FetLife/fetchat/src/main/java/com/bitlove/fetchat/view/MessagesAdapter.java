package com.bitlove.fetchat.view;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.Looper;
import android.text.Html;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bitlove.fetchat.FetLifeApplication;
import com.bitlove.fetchat.R;
import com.bitlove.fetchat.model.pojos.Conversation;
import com.bitlove.fetchat.model.pojos.Message;
import com.bitlove.fetchat.model.pojos.Message$Table;
import com.raizlabs.android.dbflow.list.FlowCursorList;
import com.raizlabs.android.dbflow.sql.builder.Condition;
import com.raizlabs.android.dbflow.sql.language.Select;
import com.raizlabs.android.dbflow.structure.cache.ModelCache;
import com.raizlabs.android.dbflow.structure.cache.ModelLruCache;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class MessagesAdapter extends BaseAdapter {

    private final String conversationId;
    private List<Message> itemList;

    public MessagesAdapter(String conversationId) {
        this.conversationId = conversationId;
        loadItems();
    }

    @Override
    public int getCount() {
        return itemList.size();
    }

    @Override
    public Message getItem(int position) {
        return itemList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //TODO: OPTIMIZE

        Context context = parent.getContext();

        View view = LayoutInflater.from(context).inflate(R.layout.listitem_message, parent, false);

        Message message = itemList.get(position);
//        Message previousMessage = position != 0 ? itemList.get(position-1) : null;
//        Message nextMessage = position != itemList.size()-1 ? itemList.get(position+1) : null;

        boolean myMessage = message.getSenderId().equals(getFetLifeApplication(context).getMe().getId());

        TextView dateText = (TextView) view.findViewById(R.id.message_sub);
        dateText.setGravity(myMessage ? Gravity.RIGHT : Gravity.LEFT);
        dateText.setText(message.getSenderNickname() +  context.getResources().getString(R.string.message_sub_separator) + SimpleDateFormat.getDateTimeInstance().format(new Date(message.getDate())));
        dateText.setVisibility(View.VISIBLE);

        LinearLayout messageContainer = (LinearLayout) view.findViewById(R.id.message_container);
        messageContainer.setGravity(myMessage ? Gravity.RIGHT : Gravity.LEFT);

        int hPadding = (int) context.getResources().getDimension(R.dimen.listitem_horizontal_margin);
        int vPadding = (int) context.getResources().getDimension(R.dimen.listitem_vertical_margin);

        if (myMessage) {
            messageContainer.setPadding(hPadding*10, vPadding, hPadding, vPadding);
        } else {
            messageContainer.setPadding(hPadding, vPadding, hPadding*10, vPadding);
        }

        TextView messageText = (TextView) view.findViewById(R.id.message_text);

        messageText.setText(Html.fromHtml(message.getBody().trim()));
        if (message.getPending()) {
            messageText.setAlpha(0.5f);
        }

        return view;
    }

    public void refresh() {
        loadItems();
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                notifyDataSetInvalidated();
            }
        });
    }

    private void loadItems() {
        itemList = new Select().from(Message.class).where(Condition.column(Message$Table.CONVERSATIONID).eq(conversationId)).orderBy(true,Message$Table.DATE).queryList();
    }

    protected FetLifeApplication getFetLifeApplication(Context context) {
        return (FetLifeApplication) context.getApplicationContext();
    }


}
