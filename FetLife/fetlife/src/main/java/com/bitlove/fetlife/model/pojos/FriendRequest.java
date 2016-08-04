package com.bitlove.fetlife.model.pojos;

import com.bitlove.fetlife.model.db.FetLifeDatabase;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

@Table(database = FetLifeDatabase.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public class FriendRequest extends BaseModel {

    public static enum PendingState {
        NEW,
        ACCEPTED,
        REJECTED
    }

    @Column
    @PrimaryKey(autoincrement = false)
    private String clientId;

    @Column
    @JsonProperty("id")
    private String id;

    @Column
    private PendingState pendingState;

    @Column
    private boolean pending;

    @Column
    private String link;

    @Column
    private String nickname;

    @Column
    private String metaInfo;

    @Column
    private String avatarLink;

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isPending() {
        return pending;
    }

    public void setPending(boolean pending) {
        this.pending = pending;
    }

    public PendingState getPendingState() {
        return pendingState;
    }

    public void setPendingState(PendingState pendingState) {
        this.pendingState = pendingState;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getMetaInfo() {
        return metaInfo;
    }

    public void setMetaInfo(String metaInfo) {
        this.metaInfo = metaInfo;
    }

    public String getAvatarLink() {
        return avatarLink;
    }

    public void setAvatarLink(String avatarLink) {
        this.avatarLink = avatarLink;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }
}


