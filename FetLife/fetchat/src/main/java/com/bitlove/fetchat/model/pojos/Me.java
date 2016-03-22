package com.bitlove.fetchat.model.pojos;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Me {

    @JsonProperty("nickname")
    private String nickname;

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
}
