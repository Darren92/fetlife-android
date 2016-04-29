package com.bitlove.fetlife.model.pojos;

import com.fasterxml.jackson.annotation.JsonProperty;

public class MessageIds {

    @JsonProperty("ids")
    public String[] ids;

    public MessageIds(String... ids) {
        this.ids = ids;
    }

    public void setIds(String[] ids) {
        this.ids = ids;
    }

    public String[] getIds() {
        return ids;
    }
}
