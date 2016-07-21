package com.bitlove.fetlife.model.pojos;

import com.bitlove.fetlife.model.db.FetLifeDatabase;
import com.bitlove.fetlife.util.DateUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

@Table(database = FetLifeDatabase.class)
public class Message extends BaseModel {

    @Column
    @PrimaryKey(autoincrement = false)
    @JsonIgnore
    private String clientId;

    @Column
    @JsonProperty("id")
    private String id;

    @Column
    @JsonProperty("body")
    private String body;

    @Column
    @JsonProperty("created_at")
    private String createdAt;

    @JsonProperty("member")
    private Member sender;

    @Column
    @JsonProperty("is_new")
    private boolean newMessage;

    @Column
    @JsonIgnore
    private long date;

    @Column
    @JsonIgnore
    private String conversationId;

    @Column
    @JsonIgnore
    private String senderId;

    @Column
    @JsonIgnore
    private String senderNickname;

    @Column
    @JsonIgnore
    private boolean pending;

    @Column
    @JsonIgnore
    private boolean failed;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    @JsonIgnore
    public String getSenderId() {
        return senderId;
    }

    @JsonIgnore
    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    @JsonIgnore
    public String getSenderNickname() {
        return senderNickname;
    }

    @JsonIgnore
    public void setSenderNickname(String senderNickname) {
        this.senderNickname = senderNickname;
    }

    public Member getSender() {
        return sender;
    }

    public void setSender(Member sender) {
        this.sender = sender;
        if (sender != null) {
            setSenderId(sender.getId());
            setSenderNickname(sender.getNickname());
        }
    }

    @JsonIgnore
    public String getConversationId() {
        return conversationId;
    }

    @JsonIgnore
    public void setConversationId(String conversationId) {
        this.conversationId = conversationId;
    }

    @JsonIgnore
    public boolean getPending() {
        return isPending();
    }

    public boolean isPending() {
        return pending;
    }

    @JsonIgnore
    public void setPending(boolean pending) {
        this.pending = pending;
    }

    @JsonIgnore
    public boolean getFailed() {
        return isFailed();
    }

    public boolean isFailed() {
        return failed;
    }

    @JsonIgnore
    public void setFailed(boolean failed) {
        this.failed = failed;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
        if (createdAt != null) {
            try {
                setDate(DateUtil.parseDate(createdAt));
            } catch (Exception e) {
            }
        }
    }

    @JsonIgnore
    public long getDate() {
        return date;
    }

    @JsonIgnore
    public void setDate(long date) {
        this.date = date;
    }

    public boolean getNewMessage() {
        return isNewMessage();
    }

    public boolean isNewMessage() {
        return newMessage;
    }

    public void setNewMessage(boolean isNew) {
        this.newMessage = isNew;
    }
}
