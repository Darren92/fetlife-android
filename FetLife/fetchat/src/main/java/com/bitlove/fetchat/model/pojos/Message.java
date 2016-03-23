package com.bitlove.fetchat.model.pojos;

import com.bitlove.fetchat.model.db.FetChatDatabase;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

@Table(databaseName = FetChatDatabase.NAME)
public class Message extends BaseModel {

    @Column
    @PrimaryKey(autoincrement = false)
    @JsonProperty("id")
    private String id;

    @Column
    private String body;

    @Column
    @JsonIgnore
    private String conversationId;

    @Column
    @JsonIgnore
    private boolean pending;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
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
        return pending;
    }

    @JsonIgnore
    public void setPending(boolean pending) {
        this.pending = pending;
    }
}
