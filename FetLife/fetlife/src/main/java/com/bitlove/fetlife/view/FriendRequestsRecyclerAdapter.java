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
import com.bitlove.fetlife.model.pojos.FriendRequest;
import com.bitlove.fetlife.model.resource.ImageLoader;

import java.util.ArrayList;
import java.util.List;

public class FriendRequestsRecyclerAdapter extends RecyclerView.Adapter<FriendRequestViewHolder> {

    private final ImageLoader imageLoader;

    public interface OnFriendRequestClickListener {
        public void onItemClick(FriendRequest FriendRequest);
        public void onAvatarClick(FriendRequest FriendRequest);
    }

    private List<FriendRequest> itemList;
    OnFriendRequestClickListener onFriendRequestClickListener;

    public FriendRequestsRecyclerAdapter(ImageLoader imageLoader) {
        this.imageLoader = imageLoader;
        loadItems();
    }

    public void setOnItemClickListener(OnFriendRequestClickListener onFriendRequestClickListener) {
        this.onFriendRequestClickListener = onFriendRequestClickListener;
    }

    private void loadItems() {
        //TODO: think of moving to separate thread with specific DB executor
        try {
//            itemList = new Select().from(FriendRequest.class).orderBy(FriendRequest_Table.nickname,true).queryList();
        } catch (Throwable t) {
            itemList = new ArrayList<>();
        }
    }

    @Override
    public FriendRequestViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.listitem_friendrequest, parent, false);
        return new FriendRequestViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(FriendRequestViewHolder FriendRequestViewHolder, int position) {

        final FriendRequest FriendRequest = itemList.get(position);

        FriendRequestViewHolder.headerText.setText(FriendRequest.getNickname());
        FriendRequestViewHolder.upperText.setText(FriendRequest.getMetaInfo());

//        FriendRequestViewHolder.dateText.setText(SimpleDateFormat.getDateTimeInstance().format(new Date(FriendRequest.getDate())));

        FriendRequestViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onFriendRequestClickListener != null) {
                    onFriendRequestClickListener.onItemClick(FriendRequest);
                }
            }
        });

        FriendRequestViewHolder.avatarImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onFriendRequestClickListener != null) {
                    onFriendRequestClickListener.onAvatarClick(FriendRequest);
                }
            }
        });

        FriendRequestViewHolder.avatarImage.setImageResource(R.drawable.dummy_avatar);
        String avatarUrl = FriendRequest.getAvatarLink();
        imageLoader.loadImage(FriendRequestViewHolder.itemView.getContext(), avatarUrl, FriendRequestViewHolder.avatarImage, R.drawable.dummy_avatar);
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

    public FriendRequest getItem(int position) {
        return itemList.get(position);
    }
}

class FriendRequestViewHolder extends RecyclerView.ViewHolder {

    ImageView avatarImage;
    TextView headerText, upperText, dateText, lowerText;

    public FriendRequestViewHolder(View itemView) {
        super(itemView);

        headerText = (TextView) itemView.findViewById(R.id.friend_header);
        upperText = (TextView) itemView.findViewById(R.id.friend_upper);
        dateText = (TextView) itemView.findViewById(R.id.friend_right);
        lowerText = (TextView) itemView.findViewById(R.id.friend_lower);
        avatarImage = (ImageView) itemView.findViewById(R.id.friend_icon);
    }
}