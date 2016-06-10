package com.bitlove.fetlife.view;

import android.os.Handler;
import android.os.Looper;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bitlove.fetlife.R;
import com.bitlove.fetlife.model.pojos.Friend;
import com.bitlove.fetlife.model.pojos.Friend$Table;
import com.bitlove.fetlife.model.resource.ImageLoader;
import com.raizlabs.android.dbflow.sql.language.Select;

import java.util.List;

public class FriendsRecyclerAdapter extends RecyclerView.Adapter<FriendViewHolder> {

    private final ImageLoader imageLoader;

    public interface OnFriendClickListener {
        public void onClick(Friend friend);
    }

    private List<Friend> itemList;
    OnFriendClickListener onFriendClickListener;

    public FriendsRecyclerAdapter(ImageLoader imageLoader) {
        this.imageLoader = imageLoader;
        loadItems();
    }

    public void setOnItemClickListener(OnFriendClickListener onFriendClickListener) {
        this.onFriendClickListener = onFriendClickListener;
    }

    private void loadItems() {
        //TODO: think of moving to separate thread with specific DB executor
        itemList = new Select().from(Friend.class).orderBy(false,Friend$Table.NICKNAME).queryList();
    }

    @Override
    public FriendViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.listitem_friend, parent, false);
        return new FriendViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(FriendViewHolder friendViewHolder, int position) {

        final Friend friend = itemList.get(position);

        friendViewHolder.headerText.setText(friend.getNickname());
//        friendViewHolder.messageText.setText(friend.getSubject());

//        friendViewHolder.dateText.setText(SimpleDateFormat.getDateTimeInstance().format(new Date(friend.getDate())));

        friendViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onFriendClickListener != null) {
                    onFriendClickListener.onClick(friend);
                }
            }
        });

        friendViewHolder.avatarImage.setImageResource(R.drawable.dummy_avatar);
        String avatarUrl = friend.getAvatar();
        if (avatarUrl != null) {
            imageLoader.loadImage(friendViewHolder.itemView.getContext(), friend.getAvatar(), friendViewHolder.avatarImage);
        }

    }

    public void refresh() {
        loadItems();
        //TODO: think of possibility of update only specific items instead of the whole list
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public Friend getItem(int position) {
        return itemList.get(position);
    }
}

class FriendViewHolder extends RecyclerView.ViewHolder {

    ImageView avatarImage;
    TextView headerText, messageText, dateText, lowerText;

    public FriendViewHolder(View itemView) {
        super(itemView);

        headerText = (TextView) itemView.findViewById(R.id.friend_header);
        messageText = (TextView) itemView.findViewById(R.id.friend_upper);
        dateText = (TextView) itemView.findViewById(R.id.friend_right);
        lowerText = (TextView) itemView.findViewById(R.id.friend_lower);
        avatarImage = (ImageView) itemView.findViewById(R.id.conversation_icon);
    }
}
