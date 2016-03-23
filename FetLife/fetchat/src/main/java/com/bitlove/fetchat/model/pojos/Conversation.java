package com.bitlove.fetchat.model.pojos;

import com.bitlove.fetchat.model.db.FetChatDatabase;
import com.bitlove.fetchat.util.DateUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

@Table(databaseName = FetChatDatabase.NAME)
public class Conversation extends BaseModel {

    @Column
    @PrimaryKey(autoincrement = false)
    @JsonProperty("id")
    private String id;

    @Column
    @JsonProperty("subject")
    private String subject;

    @Column
    @JsonProperty("created_at")
    private String createdAt;

    @Column
    @JsonIgnore
    private long date;

    @Column
    @JsonProperty("has_new_messages")
    private boolean hasNewMessage;

    @JsonProperty("member")
    private Member member;

    @Column
    @JsonIgnore
    private String nickname;

    @Column
    @JsonIgnore
    private String memberId;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
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

    public boolean getHasNewMessage() {
        return hasNewMessage;
    }

    public void setHasNewMessage(boolean hasNewMessage) {
        this.hasNewMessage = hasNewMessage;
    }

    public Member getMember() {
        return member;
    }

    public void setMember(Member member) {
        this.member = member;
        if (member != null) {
            setMemberId(member.getId());
            setNickname(member.getNickname());
        }
    }

    @JsonIgnore
    public String getNickname() {
        return nickname;
    }

    @JsonIgnore
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    @JsonIgnore
    public String getMemberId() {
        return memberId;
    }

    @JsonIgnore
    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }
}
