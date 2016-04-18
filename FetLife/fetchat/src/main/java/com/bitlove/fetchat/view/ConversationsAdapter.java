package com.bitlove.fetchat.view;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.bitlove.fetchat.R;
import com.bitlove.fetchat.model.pojos.Conversation;
import com.raizlabs.android.dbflow.sql.language.Select;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class ConversationsAdapter extends BaseAdapter {
    private List<Conversation> itemList;

    public ConversationsAdapter() {
        loadItems();
    }

    @Override
    public int getCount() {
        return itemList.size();
    }

    @Override
    public Conversation getItem(int position) {
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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.listitem_conversation, parent, false);
        Conversation conversation = itemList.get(position);
        TextView headerText = (TextView) view.findViewById(R.id.conversation_header);
        headerText.setText(conversation.getNickname());
        TextView messageText = (TextView) view.findViewById(R.id.conversation_message_text);
        messageText.setText(conversation.getSubject());
        if (conversation.getHasNewMessage()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                messageText.setTextColor(context.getColor(R.color.text_color_primary));
            } else {
                messageText.setTextColor(context.getResources().getColor(R.color.text_color_primary));
            }
            messageText.setTypeface(null, Typeface.BOLD);
        }
//        TextView lowerText = (TextView) view.findViewById(R.id.conversation_lower);
//        lowerText.setText(conversation.getSubject());
        TextView dateText = (TextView) view.findViewById(R.id.conversation_date);
        dateText.setText(SimpleDateFormat.getDateTimeInstance().format(new Date(conversation.getDate())));
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
        itemList = new Select().from(Conversation.class).queryList();
    }
}
