package com.bitlove.fetlife.model.pojos;

import com.bitlove.fetlife.model.db.FetLifeDatabase;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

@Table(databaseName = FetLifeDatabase.NAME)
public class Friend extends BaseModel {

    @Column
    @PrimaryKey(autoincrement = false)
    @JsonProperty("id")
    private String id;

    @Column
    @JsonProperty("nickname")
    private String nickname;

    @JsonProperty("avatar")
    private Avatar avatarVariants;

    @Column
    @JsonIgnore
    private String avatar;

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

    public Avatar getAvatarVariants() {
        return avatarVariants;
    }

    public void setAvatarVariants(Avatar avatarVariants) {
        this.avatarVariants = avatarVariants;
        if (avatarVariants != null) {
            setAvatar(avatarVariants.getVariants().getIconUrl());
        }
    }

    @JsonIgnore
    public String getAvatar() {
        return avatar;
    }

    @JsonIgnore
    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
}
