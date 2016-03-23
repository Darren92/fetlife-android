package com.bitlove.fetchat.view;

import android.graphics.Typeface;
import android.os.Handler;
import android.os.Looper;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.listitem_message, parent, false);

        Message message = itemList.get(position);

        TextView headerText = (TextView) view.findViewById(R.id.list_item_header);
        headerText.setText(SimpleDateFormat.getDateTimeInstance().format(new Date(message.getDate())));
        TextView messageText = (TextView) view.findViewById(R.id.list_item_text);
        messageText.setText(Html.fromHtml(message.getBody()));
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

}
