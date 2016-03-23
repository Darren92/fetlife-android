package com.bitlove.fetchat.model.pojos;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Member {

    @JsonProperty("id")
    private String id;

    @JsonProperty("nickname")
    private String nickname;

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
}
