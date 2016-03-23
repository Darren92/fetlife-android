package com.bitlove.fetchat.view;

import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.bitlove.fetchat.model.pojos.Conversation;
import com.bitlove.fetchat.model.pojos.Message;
import com.bitlove.fetchat.model.pojos.Message$Table;
import com.raizlabs.android.dbflow.list.FlowCursorList;
import com.raizlabs.android.dbflow.sql.builder.Condition;
import com.raizlabs.android.dbflow.sql.language.Select;
import com.raizlabs.android.dbflow.structure.cache.ModelCache;
import com.raizlabs.android.dbflow.structure.cache.ModelLruCache;

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
        TextView textView = new TextView(parent.getContext());
        textView.setText(itemList.get(position).getBody());
        return textView;
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
        itemList = new Select().from(Message.class).where(Condition.column(Message$Table.CONVERSATIONID).eq(conversationId)).queryList();
    }

}
