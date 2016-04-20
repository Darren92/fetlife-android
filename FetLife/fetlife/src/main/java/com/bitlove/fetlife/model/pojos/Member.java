package com.bitlove.fetlife.model.pojos;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Member {

    @JsonProperty("id")
    private String id;

    @JsonProperty("nickname")
    private String nickname;

    @JsonProperty("notification_token")
    private String notificationToken;

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
}
