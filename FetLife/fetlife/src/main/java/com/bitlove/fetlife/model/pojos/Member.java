package com.bitlove.fetlife.model.pojos;

import com.bitlove.fetlife.model.db.FetLifeDatabase;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

@Table(databaseName = FetLifeDatabase.NAME)
public class Member extends BaseModel {

    @JsonProperty("id")
    @Column
    @PrimaryKey(autoincrement = false)
    private String id;

    @JsonProperty("nickname")
    @Column
    private String nickname;

    @JsonProperty("notification_token")
    @Column
    private String notificationToken;

    @JsonProperty("avatar")
    private Avatar avatar;

    @JsonProperty("url")
    @Column
    private String link;

    @JsonIgnore
    @Column
    private String avatarLink;

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNotificationToken() {
        return notificationToken;
    }

    public void setNotificationToken(String notificationToken) {
        this.notificationToken = notificationToken;
    }

    public Avatar getAvatar() {
        return avatar;
    }

    public void setAvatar(Avatar avatar) {
        this.avatar = avatar;
        if (avatar != null) {
            Variants variants = avatar.getVariants();
            if (variants != null) {
                setAvatarLink(variants.getIconUrl());
            }
        }
    }

    @JsonIgnore
    public String getAvatarLink() {
        return avatarLink;
    }

    @JsonIgnore
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
