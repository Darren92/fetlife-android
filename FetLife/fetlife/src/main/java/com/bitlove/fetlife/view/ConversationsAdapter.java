package com.bitlove.fetlife.view;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.bitlove.fetlife.R;
import com.bitlove.fetlife.model.pojos.Conversation;
import com.bitlove.fetlife.util.ColorUtil;
import com.raizlabs.android.dbflow.sql.language.Select;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class ConversationsAdapter extends BaseAdapter {

    static class ConversationViewHolder {
        TextView headerText;
        TextView messageText;
        TextView dateText;
        TextView lowerText;
        TextView newMessageIndicator;
    }

    private static final float CONVERSATION_READ_ALPHA = 0.5f;

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

        Context context = parent.getContext();

        ConversationViewHolder conversationViewHolder;

        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.listitem_conversation, parent, false);

            conversationViewHolder = new ConversationViewHolder();
            conversationViewHolder.newMessageIndicator = (TextView) convertView.findViewById(R.id.conversation_new_message_indicator);
            conversationViewHolder.headerText = (TextView) convertView.findViewById(R.id.conversation_header);
            conversationViewHolder.messageText = (TextView) convertView.findViewById(R.id.conversation_message_text);
            conversationViewHolder.dateText = (TextView) convertView.findViewById(R.id.conversation_date);
            conversationViewHolder.lowerText = (TextView) convertView.findViewById(R.id.conversation_lower);

            convertView.setTag(conversationViewHolder);

        } else {
            conversationViewHolder = (ConversationViewHolder) convertView.getTag();
        }

        Conversation conversation = itemList.get(position);

        conversationViewHolder.headerText.setText(conversation.getNickname());
        conversationViewHolder.messageText.setText(conversation.getSubject());

        if (conversation.getHasNewMessage()) {
            conversationViewHolder.newMessageIndicator.setVisibility(View.VISIBLE);
        } else {
            conversationViewHolder.newMessageIndicator.setVisibility(View.GONE);
        }
        conversationViewHolder.dateText.setText(SimpleDateFormat.getDateTimeInstance().format(new Date(conversation.getDate())));

        return convertView;
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
