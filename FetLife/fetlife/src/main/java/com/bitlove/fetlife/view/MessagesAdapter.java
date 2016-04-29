package com.bitlove.fetlife.view;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.bitlove.fetlife.FetLifeApplication;
import com.bitlove.fetlife.R;
import com.bitlove.fetlife.model.pojos.Message;
import com.bitlove.fetlife.model.pojos.Message$Table;
import com.bitlove.fetlife.util.ColorUtil;
import com.bitlove.fetlife.util.StringUtil;
import com.raizlabs.android.dbflow.sql.builder.Condition;
import com.raizlabs.android.dbflow.sql.language.Select;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class MessagesAdapter extends BaseAdapter {

    private static final float PENDING_ALPHA = 0.5f;

    static class MessageViewHolder {
        LinearLayout messageContainer;
        TextView messageText;
        TextView subText;
    }

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

        Context context = parent.getContext();

        MessageViewHolder messageViewHolder;

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.listitem_message, parent, false);
            messageViewHolder = new MessageViewHolder();
            messageViewHolder.messageContainer = (LinearLayout) convertView.findViewById(R.id.message_container);
            messageViewHolder.messageText = (TextView) convertView.findViewById(R.id.message_text);
            messageViewHolder.messageText.setMovementMethod(LinkMovementMethod.getInstance());
            messageViewHolder.subText = (TextView) convertView.findViewById(R.id.message_sub);
            convertView.setTag(messageViewHolder);
        } else {
            messageViewHolder = (MessageViewHolder) convertView.getTag();
        }

        Message message = itemList.get(position);
        String messageBody = message.getBody().trim();

        messageViewHolder.messageText.setText(StringUtil.parseHtml(messageBody));
        messageViewHolder.subText.setText(message.getSenderNickname() + context.getResources().getString(R.string.message_sub_separator) + SimpleDateFormat.getDateTimeInstance().format(new Date(message.getDate())));

        boolean myMessage = message.getSenderId().equals(getFetLifeApplication(context).getMe().getId());
        int hPadding = (int) context.getResources().getDimension(R.dimen.listitem_horizontal_margin);
        int vPadding = (int) context.getResources().getDimension(R.dimen.listitem_vertical_margin);

        if (myMessage) {
            messageViewHolder.subText.setGravity(Gravity.RIGHT);
            messageViewHolder.messageContainer.setGravity(Gravity.RIGHT);
            messageViewHolder.messageContainer.setPadding(hPadding*10, vPadding, hPadding, vPadding);
        } else {
            messageViewHolder.subText.setGravity(Gravity.LEFT);
            messageViewHolder.messageContainer.setGravity(Gravity.LEFT);
            messageViewHolder.messageContainer.setPadding(hPadding, vPadding, hPadding*10, vPadding);
        }

        if (message.getPending()) {
            messageViewHolder.messageText.setTextColor(ColorUtil.retrieverColor(context, R.color.text_color_primary));
            messageViewHolder.messageText.setAlpha(PENDING_ALPHA);
        } else if (message.getFailed()) {
            messageViewHolder.messageText.setAlpha(1f);
            messageViewHolder.messageText.setTextColor(ColorUtil.retrieverColor(context, R.color.text_color_error));
        } else {
            messageViewHolder.messageText.setTextColor(ColorUtil.retrieverColor(context, R.color.text_color_primary));
            messageViewHolder.messageText.setAlpha(1f);
        }

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
        itemList = new Select().from(Message.class).where(Condition.column(Message$Table.CONVERSATIONID).eq(conversationId)).orderBy(true,Message$Table.DATE).queryList();
    }

    protected FetLifeApplication getFetLifeApplication(Context context) {
        return (FetLifeApplication) context.getApplicationContext();
    }

}

