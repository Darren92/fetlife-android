package com.bitlove.fetlife.model.pojos;

import com.bitlove.fetlife.model.db.FetLifeDatabase;
import com.bitlove.fetlife.util.DateUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

import java.util.UUID;

@Table(database = FetLifeDatabase.class)
public class Conversation extends BaseModel {

    private static final String PREFIX_LOCAL = "%" + Conversation.class.getName() + ".PREFIX_LOCAL%";

    public static boolean isLocal(String conversationId) {
        return conversationId.startsWith(PREFIX_LOCAL);
    }

    public static String createLocalConversation(Member member) {
        Conversation conversation = new Conversation();
        conversation.setId(PREFIX_LOCAL + UUID.randomUUID().toString());
        String dateString = DateUtil.toString(System.currentTimeMillis());
        conversation.setCreatedAt(dateString);
        conversation.setUpdatedAt(dateString);
        conversation.setContainNewMessage(false);
        conversation.setMember(member);
        conversation.save();
        return conversation.getId();
    }

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
    @JsonProperty("updated_at")
    private String updatedAt;

    @Column
    @JsonIgnore
    private long date;

    @Column
    @JsonProperty("has_new_messages")
    private boolean containNewMessage;

    @JsonProperty("member")
    private Member member;

    @Column
    @JsonIgnore
    private String nickname;

    @Column
    @JsonIgnore
    private String avatarLink;

    @Column
    @JsonIgnore
    private String memberLink;

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
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
        if (updatedAt != null) {
            try {
                setDate(DateUtil.parseDate(updatedAt));
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

    public boolean isContainNewMessage() {
        return getContainNewMessage();
    }

    public boolean getContainNewMessage() {
        return containNewMessage;
    }

    public void setContainNewMessage(boolean containNewMessage) {
        this.containNewMessage = containNewMessage;
    }

    public Member getMember() {
        return member;
    }

    public void setMember(Member member) {
        this.member = member;
        if (member != null) {
            setMemberId(member.getId());
            setNickname(member.getNickname());
            setAvatarLink(member.getAvatarLink());
            setMemberLink(member.getLink());
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
    public String getAvatarLink() {
        return avatarLink;
    }

    @JsonIgnore
    public void setAvatarLink(String avatarLink) {
        this.avatarLink = avatarLink;
    }

    @JsonIgnore
    public String getMemberId() {
        return memberId;
    }

    @JsonIgnore
    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }

    public String getMemberLink() {
        return memberLink;
    }

    public void setMemberLink(String memberLink) {
        this.memberLink = memberLink;
    }
}
